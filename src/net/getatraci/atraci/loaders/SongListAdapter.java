package net.getatraci.atraci.loaders;

import java.util.ArrayList;

import net.getatraci.atraci.R;
import net.getatraci.atraci.data.MusicItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SongListAdapter extends BaseAdapter {
	
    private LayoutInflater inflater;
    private ArrayList<MusicItem> items;
    
    int cnt;
    public SongListAdapter(Context c, ArrayList<MusicItem> arr) {
        inflater = LayoutInflater.from(c);
        this.items = arr;
    }

    public int getCount() {
        return items.size();
    }

    public MusicItem getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        View v = view;
        ImageView picture;
        TextView name;

        if(v == null)
        {
           v = inflater.inflate(R.layout.musicgrid_item, viewGroup, false);
           v.setTag(R.id.picture, v.findViewById(R.id.picture));
           v.setTag(R.id.text, v.findViewById(R.id.text));
        }

        picture = (ImageView)v.getTag(R.id.picture);
        name = (TextView)v.getTag(R.id.text);
        

        
		new ImageDownloader(picture, items.get(i).getImage_lrg());
        name.setText(items.get(i).getTrack()+"\n"+items.get(i).getArtist());
        
        Animation animationY = new TranslateAnimation(0, 0, viewGroup.getHeight()/4, 0);
        animationY.setDuration(500);
        v.startAnimation(animationY);  
        animationY = null; 
        return v;
    }

}
