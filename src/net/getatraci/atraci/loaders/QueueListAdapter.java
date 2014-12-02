package net.getatraci.atraci.loaders;

import java.util.ArrayList;

import net.getatraci.atraci.R;
import net.getatraci.atraci.data.MusicItem;
import net.getatraci.atraci.layouthelpers.RoundedImageView;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class QueueListAdapter extends BaseAdapter{
	
	private ArrayList<MusicItem> queue;
	private int pos;
	private LayoutInflater inflater;
	private ImageDownloader[] images;
	
	public QueueListAdapter(Context c, ArrayList<MusicItem> q, int p) {
		super();
		queue = q;
		pos = p;
		inflater = LayoutInflater.from(c);
		images = new ImageDownloader[q.size()];
	}

	@Override
	public int getCount() {
		return queue.size();
	}

	@Override
	public Object getItem(int position) {
		return queue.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
        View v = view;
        
		TextView txt1;
		TextView txt2;
		RoundedImageView img;

        if(v == null)
        {
           v = inflater.inflate(R.layout.searchlist_item, parent, false);
           v.setTag(R.id.item1, v.findViewById(R.id.item1));
           v.setTag(R.id.item2, v.findViewById(R.id.item2));
           v.setTag(R.id.album_art, v.findViewById(R.id.album_art));
        }
        
        txt1 = (TextView)v.getTag(R.id.item1);
        txt2 = (TextView)v.getTag(R.id.item2);
        img = (RoundedImageView)v.getTag(R.id.album_art);
        
        txt1.setText(queue.get(position).getTrack());
        txt2.setText(queue.get(position).getArtist());
        if(images[position] != null && images[position].getImage() != null){
        	img.setImageBitmap(images[position].getImage());
        } else {
        	img.setImageResource(R.drawable.record);
			images[position] = new ImageDownloader(img, queue.get(position).getImage_lrg());
		}
        
        if(pos == position){
        	v.setBackgroundColor(Color.BLACK);
        	txt1.setTextColor(Color.RED);
        	txt2.setTextColor(Color.RED);
        	ScaleAnimation anim = new ScaleAnimation(0, 1, 1, 1, Animation.RELATIVE_TO_SELF, Animation.RELATIVE_TO_SELF);
    		anim.setDuration(800);
    		anim.setFillEnabled(true);
    		anim.setFillAfter(true);
        	v.setAnimation(anim);
        	anim.start();
        } else {
        	v.setBackgroundColor(Color.WHITE);
        	txt1.setTextColor(Color.BLACK);
        	txt2.setTextColor(Color.BLACK);
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
