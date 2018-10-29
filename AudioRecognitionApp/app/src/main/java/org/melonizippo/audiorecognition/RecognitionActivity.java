package org.melonizippo.audiorecognition;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.melonizippo.audiorecognition.database.HistoryDatabaseAdapter;
import org.melonizippo.audiorecognition.database.HistoryEntry;
import org.melonizippo.audiorecognition.recognition.Fingerprint;
import org.melonizippo.audiorecognition.recognition.RecognitionResult;
import org.melonizippo.audiorecognition.recognition.RecognitionService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;

public class RecognitionActivity extends Activity
{
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private static final int REQUEST_PERMISSIONS = 1;

    private static String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WAKE_LOCK
    };

    public static void verifyPermissions(Activity activity) {
        // Check if we have write permission

        List<String> permissionsRequiredList = new ArrayList<>();


        for(String permission : PERMISSIONS)
        {
            int granted = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (granted != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                permissionsRequiredList.add(permission);
            }
        }

        if(!permissionsRequiredList.isEmpty())
        {
            ActivityCompat.requestPermissions(
                    activity,
                    permissionsRequiredList.toArray(new String[]{}),
                    REQUEST_PERMISSIONS
            );
        }
    }



    private enum MediaRecorderState {
        PAUSED,
        RECORDING
    }
    private MediaRecorder mediaRecorder = new MediaRecorder();
    private MediaRecorderState mediaRecorderState;
    private Path recordFile;

    private ImageView recordButton;
    private ProgressBar progressSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);

        mediaRecorderState = MediaRecorderState.PAUSED;

        recordButton = findViewById(R.id.recordButton);
        recordButton.setOnClickListener(view -> recordButtonListener());

        progressSpinner = findViewById(R.id.progressSpinner);

        verifyPermissions(this);
    }

    private void recordButtonListener()
    {
        if(mediaRecorderState == MediaRecorderState.PAUSED)
        {
            configureMediaRecorder();
            startRecording();
            recordButton.setImageDrawable(getDrawable(R.drawable.stop_circle));
            mediaRecorderState = MediaRecorderState.RECORDING;
        }
        else
        {
            stopRecording();
            mediaRecorderState = MediaRecorderState.PAUSED;
            progressSpinner.setVisibility(View.VISIBLE);

            recordButton.setImageDrawable(getDrawable(R.drawable.record_rec));
            //todo: code for recognition and showing RecognizedSongActivity

            //convert recording in wav PCM 16-bit
            AndroidAudioConverter.with(this)
                    .setInputFile(new File(recordFile.toString()))
                    .setFormat(AudioFormat.WAV)
                    .setCallback(convertCallback)
                    .convert();
        }
    }

    private IConvertCallback convertCallback = new IConvertCallback() {
        @Override
        public void onSuccess(File convertedFile) {
            Log.i(LOG_TAG, "Recorded audio succesfully converted");
            recognitionCallback(convertedFile);
        }

        @Override
        public void onFailure(Exception error) {
            Log.e(LOG_TAG, "Error converting recorded audio to WAV");
            error.printStackTrace();
        }
    };

    private void recognitionCallback(File convertedFile)
    {
        Fingerprint recordingFingerprint = new Fingerprint(convertedFile.getAbsolutePath());
        RecognitionResult result = RecognitionService.recognize(recordingFingerprint, this);

        progressSpinner.setVisibility(View.GONE);

        if(result == null)
        {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.recognitionCoordinatorLayout), R.string.failedRecognition, Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
        else
        {
            HistoryEntry historyEntry = new HistoryEntry();
            historyEntry.songId = result.songId;
            historyEntry.timestamp = LocalDate.now();
            HistoryDatabaseAdapter.addEntry(this, historyEntry);

            Intent intent = new Intent(RecognitionActivity.this, RecognizedSongActivity.class);
            Bundle b = new Bundle();
            b.putInt("song_id", result.songId);
            intent.putExtras(b);
            startActivity(intent);
            //finish();
        }
    }

    private void configureMediaRecorder()
    {
        Log.d(LOG_TAG, "Setting up MediaRecorder");

        recordFile = null;
        try {
            recordFile = Files.createTempFile("recorded_", "." + AudioFormat.M4A.toString());
            Log.d(LOG_TAG, "Recording in " + recordFile.toString());
        }
        catch(IOException ex)
        {
            Log.e(LOG_TAG, "Cannot create temporary recording file");
            System.exit(0);
        }

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(recordFile.toString());

        try {
            Log.d(LOG_TAG, "Preparing MediaRecorder");
            mediaRecorder.prepare();
        }
        catch(IOException ex)
        {
            Log.e(LOG_TAG, "Cannot prepare MediaRecorder");
            System.exit(0);
        }
        catch(IllegalStateException ex)
        {
            Log.e(LOG_TAG, "Prepare called when MediaRecorder was not set up properly");
        }
    }

    private void startRecording()
    {
        Log.d(LOG_TAG, "Start recording");
        mediaRecorder.start();
    }

    private void stopRecording()
    {
        Log.d(LOG_TAG, "Stop recording");
        mediaRecorder.stop();
    }
}
