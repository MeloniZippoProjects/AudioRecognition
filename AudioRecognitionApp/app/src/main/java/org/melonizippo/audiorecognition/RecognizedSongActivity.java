package org.melonizippo.audiorecognition;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.melonizippo.audiorecognition.recognition.RecognitionResult;
import org.melonizippo.audiorecognition.recognition.RecognitionService;

public class RecognizedSongActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognized_song);

        Bundle b = getIntent().getExtras();
        if(b != null)
        {
            RecognitionResult recognitionResult = (RecognitionResult) b.get("recognition_result");
            loadSongMetadata(recognitionResult);
        }
    }

    private void loadSongMetadata(RecognitionResult recognitionResult)
    {
        TextView title = findViewById(R.id.titleText);
        ImageView cover = findViewById(R.id.coverView);
        TextView artist = findViewById(R.id.artistText);
        TextView album = findViewById(R.id.albumText);
        TextView year = findViewById(R.id.yearText);
        TextView genre = findViewById(R.id.genreText);

        if(recognitionResult.metadata.title != null)
        	title.setText(recognitionResult.metadata.title);
        if(recognitionResult.metadata.artist != null)
        	artist.setText(recognitionResult.metadata.artist);
        if(recognitionResult.metadata.album != null)
        	album.setText(recognitionResult.metadata.album);
        if(recognitionResult.metadata.year != null)
        	year.setText(recognitionResult.metadata.year);
        if(recognitionResult.metadata.genre != null)
        	genre.setText(recognitionResult.metadata.genre);
        if(recognitionResult.metadata.cover != null)
            cover.setImageBitmap(recognitionResult.metadata.cover);
    }
}
