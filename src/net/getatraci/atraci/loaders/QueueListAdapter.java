package net.getatraci.atraci.loaders;

import java.util.ArrayList;

import net.getatraci.atraci.R;
import net.getatraci.atraci.data.MusicItem;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class QueueListAdapter extends BaseAdapter{
	
	private ArrayList<MusicItem> queue;
	private int pos;
	private LayoutInflater inflater;
	
	public QueueListAdapter(Context c, ArrayList<MusicItem> q, int p) {
		super();
		queue = q;
		pos = p;
		Log.d("ATRACI", "Queue Size: "+ q.size());
		inflater = LayoutInflater.from(c);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		Log.d("ATRACI", "Queued Size: "+ queue.size());
		return queue.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return queue.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
        View v = view;
        TextView name;

        if(v == null)
        {
           v = inflater.inflate(R.layout.queue_item, parent, false);
           v.setTag(R.id.song, v.findViewById(R.id.song));
        }
        
        name = (TextView)v.getTag(R.id.song);
        name.setText(queue.get(position).getTrack());
        
        if(pos == position){
        	v.setBackgroundColor(Color.GRAY);
        } else {
        	v.setBackgroundColor(Color.WHITE);
        }
        return v;
	}

	
	
	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return true;
	}

	public ArrayList<MusicItem> getQueue() {
		return queue;
	}

	public void setQueue(ArrayList<MusicItem> queue) {
		this.queue = queue;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}
	
	

}
