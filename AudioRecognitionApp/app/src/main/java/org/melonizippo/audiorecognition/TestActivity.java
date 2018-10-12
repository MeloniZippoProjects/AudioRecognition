package org.melonizippo.audiorecognition;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.TextView;

import com.musicg.wave.Wave;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;

public class TestActivity extends AppCompatActivity {
    String filePath = Environment.getExternalStorageDirectory() + "/recorded_audio.wav";
    private static final int REQUEST_PERMISSIONS = 1;

    private static String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WAKE_LOCK
    };

    public static boolean verifyPermissions(Activity activity) {
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
            return false;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        boolean state = verifyPermissions(this);

        if(!state)
            return;

        try {
            Files.createFile(Paths.get(filePath));
        }
        catch(Exception ex)
        {
            //cannot create
            System.out.println("cannot create");
        }

        int color = getResources().getColor(R.color.colorPrimaryDark);
        int requestCode = 0;
        AndroidAudioRecorder.with(this)
                // Required
                .setFilePath(filePath)
                .setColor(color)
                .setRequestCode(requestCode)

                // Optional
                .setSource(AudioSource.MIC)
                .setChannel(AudioChannel.STEREO)
                .setSampleRate(AudioSampleRate.HZ_48000)
                .setAutoStart(false)
                .setKeepDisplayOn(true)

                // Start recording
                .record();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                // Great! User has recorded and saved the audio file
                Wave wave = new Wave(filePath);
                System.out.println("Prova prova: " + Base64.encodeToString(wave.getFingerprint(),0));
            } else if (resultCode == RESULT_CANCELED) {
                // Oops! User has canceled the recording
            }
        }
    }
}
