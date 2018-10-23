package org.melonizippo.audiorecognition;



import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;

import android.app.Application;
import android.util.Log;

public class AudioRecognitionApp extends Application {

    private static final String LOG_TAG = "AudioRecognitionApp";

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidAudioConverter.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                Log.i(LOG_TAG, "FFMpeg loaded succesfully");
            }
            @Override
            public void onFailure(Exception error) {
                Log.e(LOG_TAG, "Cannot load FFMpeg, library not supported");
                System.exit(0);
            }
        });
    }
}
