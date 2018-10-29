package org.melonizippo.audiorecognition;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.melonizippo.audiorecognition.database.FingerprintsDatabaseAdapter;
import org.melonizippo.audiorecognition.database.HistoryEntry;
import org.melonizippo.audiorecognition.database.SongMetadata;

import java.util.List;

public class HistoryEntryAdapter extends ArrayAdapter<HistoryEntry>
{
    Context mContext;

    HistoryEntryAdapter(List<HistoryEntry> dataSet, Context context)
    {
        super(context, R.layout.history_entry_view, dataSet);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        HistoryEntry historyEntry = getItem(position);
        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.history_entry_view, parent, false);
        }

        SongMetadata metadata = FingerprintsDatabaseAdapter.getMetadata(historyEntry.songId, getContext());

        TextView titleView = (TextView) convertView.findViewById(R.id.titleText);
        titleView.setText(metadata.title);

        if(metadata.artist != null)
        {
            TextView artistView = (TextView) convertView.findViewById(R.id.artistText);
            artistView.setText(metadata.artist);
        }

        TextView dateView = (TextView) convertView.findViewById(R.id.dateText);
        dateView.setText(historyEntry.timestamp.toString());

        if(metadata.cover != null)
        {
            ImageView coverView = (ImageView) convertView.findViewById(R.id.coverIcon);
            coverView.setImageBitmap(metadata.cover);
        }

        convertView.requestLayout();
        return convertView;
    }
}

