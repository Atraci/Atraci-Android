package net.getatraci.atraci.loaders;

import java.util.ArrayList;

import net.getatraci.atraci.R;
import net.getatraci.atraci.data.MusicItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SongListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<MusicItem> items;
	private ImageDownloader[] images;
	int cnt;

	public SongListAdapter(Context c, ArrayList<MusicItem> arr) {
		inflater = LayoutInflater.from(c);
		this.items = arr;
		images = new ImageDownloader[items.size()];
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
	public View getView(int position, View view, ViewGroup viewGroup)
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
		picture.setImageResource(R.drawable.record);

		if(images[position] != null && position != 0){
			images[position].cancel(true);
			picture.setImageBitmap(images[position].getImage());
		} else {
			images[position] = new ImageDownloader(picture, items.get(position).getImage_lrg());
		}
		name.setText(items.get(position).getTrack()+"\n"+items.get(position).getArtist());


		doAnimation(v);
		return v;
	}

	private static void doAnimation(View view){
		Animation animation = null;
		animation = new ScaleAnimation(0,1,0,1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
		animation.setDuration(400);
		animation.setFillEnabled(true);
		animation.setFillAfter(true);
		view.startAnimation(animation); 
	}

	public void cancelAllImageLoads(){
		for(ImageDownloader i : images){
			if(i != null){
				i.cancel(true);
			}
		}
	}

}
