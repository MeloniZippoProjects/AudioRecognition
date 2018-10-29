package org.melonizippo.audiorecognition;

import android.app.Activity;
import android.os.Bundle;
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
    }
}
