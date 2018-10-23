package org.melonizippo.audiorecognition;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

    Button recordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);

        mediaRecorderState = MediaRecorderState.PAUSED;

        recordButton = findViewById(R.id.recordButton);
        recordButton.setOnClickListener(view -> recordButtonListener());
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
        }
    }

    private void configureMediaRecorder()
    {
        Log.d(LOG_TAG, "Setting up MediaRecorder");

        Path recFile = null;
        try {
            recFile = Files.createTempFile("", ".rec");
            Log.d(LOG_TAG, "Recording in " + recFile.toString());
        }
        catch(IOException ex)
        {
            Log.e(LOG_TAG, "Cannot create temporary recording file");
            System.exit(0);
        }

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(recFile.toString());

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
