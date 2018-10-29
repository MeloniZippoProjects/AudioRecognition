package org.melonizippo.audiorecognition;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.melonizippo.audiorecognition.database.FingerprintsDatabaseAdapter;
import org.melonizippo.audiorecognition.database.SongMetadata;
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
            int songId = b.getInt("song_id");
            loadSongMetadata(songId);
        }
    }

    private void loadSongMetadata(int songId)
    {
        SongMetadata metadata = FingerprintsDatabaseAdapter.getMetadata(songId, this);
        
        TextView title = findViewById(R.id.titleText);
        ImageView cover = findViewById(R.id.coverView);
        TextView artist = findViewById(R.id.artistText);
        TextView album = findViewById(R.id.albumText);
        TextView year = findViewById(R.id.yearText);
        TextView genre = findViewById(R.id.genreText);

        if(metadata.title != null)
        	title.setText(metadata.title);
        if(metadata.artist != null)
        	artist.setText(metadata.artist);
        if(metadata.album != null)
        	album.setText(metadata.album);
        if(metadata.year != null)
        	year.setText(metadata.year);
        if(metadata.genre != null)
        	genre.setText(metadata.genre);
        if(metadata.cover != null)
            cover.setImageBitmap(metadata.cover);
    }
}
