package org.melonizippo.audiorecognition;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.melonizippo.audiorecognition.database.HistoryDatabaseAdapter;
import org.melonizippo.audiorecognition.database.HistoryEntry;

import java.util.List;

public class HistoryViewActivity extends Activity
{
    private HistoryEntryAdapter historyEntryAdapter;
    private List<HistoryEntry> history;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_view);

        history = HistoryDatabaseAdapter.getHistory(this);
        historyEntryAdapter = new HistoryEntryAdapter(history, this);

        ListView historyView = findViewById(R.id.historyView);
        historyView.setAdapter(historyEntryAdapter);

        historyView.setOnItemClickListener((parent, view, position, id) -> onEntryClick(position));
    }

    private void onEntryClick(int position)
    {
        HistoryEntry historyEntry = history.get(position);

        Intent intent = new Intent(HistoryViewActivity.this, RecognizedSongActivity.class);
        Bundle b = new Bundle();
        b.putInt("song_id", historyEntry.songId);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }
}
