package net.getatraci.atraci.loaders;

import net.getatraci.atraci.data.Top100Genres;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

public class GenreAdapter extends BaseAdapter {

	private String[] genres;
	private LayoutInflater inflater;
	
	public GenreAdapter(Context c) {
		inflater = LayoutInflater.from(c);
		genres = Top100Genres.allGenres();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return genres.length;
	}

	@Override
	public Object getItem(int position) {
		return genres[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
        View v = view;

        if(v == null)
        {
           v = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }
        
        CheckedTextView ctv = (CheckedTextView) v.findViewById(android.R.id.text1);
        ctv.setTextColor(Color.BLACK);
        v.setBackgroundColor(Color.RED);
        ctv.setText(Top100Genres.getGenreNameById((String)getItem(position)));
        
        return v;
	}

}
