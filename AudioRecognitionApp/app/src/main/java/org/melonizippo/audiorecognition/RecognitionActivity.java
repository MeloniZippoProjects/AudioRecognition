package org.melonizippo.audiorecognition;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.melonizippo.audiorecognition.recognition.Fingerprint;
import org.melonizippo.audiorecognition.recognition.RecognitionResult;
import org.melonizippo.audiorecognition.recognition.RecognitionService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;

public class RecognitionActivity extends Activity
{

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private enum MediaRecorderState {
        PAUSED,
        RECORDING
    }
    private MediaRecorder mediaRecorder = new MediaRecorder();
    private MediaRecorderState mediaRecorderState;
    private Path recordFile;

    private Button recordButton;
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);

        mediaRecorderState = MediaRecorderState.PAUSED;

        recordButton = findViewById(R.id.recordButton);
        recordButton.setOnClickListener(view -> recordButtonListener());

        resultText = findViewById(R.id.resultText);
    }

    private void recordButtonListener()
    {
        if(mediaRecorderState == MediaRecorderState.PAUSED)
        {
            configureMediaRecorder();
            startRecording();
            recordButton.setText(R.string.stop_recording_button);
            mediaRecorderState = MediaRecorderState.RECORDING;
        }
        else
        {
            stopRecording();
            recordButton.setText(R.string.start_recording_button);
            mediaRecorderState = MediaRecorderState.PAUSED;

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
        RecognitionResult result = RecognitionService.Recognize(recordingFingerprint, this);

        if(result == null)
        {
            resultText.setText("Recognition failed");
        }
        else
        {
            StringBuilder sb = new StringBuilder();
            sb.append("Title: ").append(result.metadata.title).append("\n")
                .append("Artist: ").append(result.metadata.artist).append("\n")
                .append("with similarity: ").append(result.similarity);
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
