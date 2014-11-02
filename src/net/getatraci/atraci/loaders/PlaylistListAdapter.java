package net.getatraci.atraci.loaders;

import java.util.ArrayList;

import net.getatraci.atraci.data.Playlists;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PlaylistListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<Playlists> items;
    
    int cnt;
    
    public PlaylistListAdapter(Context c, ArrayList<Playlists> arr) {
        inflater = LayoutInflater.from(c);
        this.items = arr;
    }

    public int getCount() {
        return items.size();
    }

    public Playlists getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        View v = view;
        TextView name;

        if(v == null)
        {
           v = inflater.inflate(android.R.layout.simple_list_item_1, viewGroup, false);
        }

        name = (TextView)v.findViewById(android.R.id.text1);
        name.setText(items.get(i).getName());
        Log.d("ATRACI", items.get(i).getName());

        return v;
    }

}
