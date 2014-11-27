package net.getatraci.atraci.loaders;

import net.getatraci.atraci.R;
import net.getatraci.atraci.data.MusicItem;
import net.getatraci.atraci.data.MusicTypeCategories;
import net.getatraci.atraci.layouthelpers.RoundedImageView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LFMArrayAdapter extends BaseAdapter {

	private MusicTypeCategories m_data;
	LayoutInflater layoutInflater;
	Context mContext;
	
	private final int TYPE_ARTIST_HEADER = 0;
	private final int TYPE_ARTIST = 1;
	private final int TYPE_ALBUM_HEADER = 2;
	private final int TYPE_ALBUM = 3;
	private final int TYPE_SONG_HEADER = 4;
	private final int TYPE_SONG = 5;
	
    public LFMArrayAdapter(Context context, MusicTypeCategories data) {
      super();
      mContext = context;
      m_data = data;
      layoutInflater = LayoutInflater.from(context);
    }


	@Override
	public int getCount() {
		return m_data.getTotalSize()+3;
	}

	@Override
	public MusicItem getItem(int position) {
        switch(getItemViewType(position)) {
        case TYPE_SONG:
        	return m_data.getSong(Math.abs((m_data.getTotalSize()-m_data.getSongCount())-(position-3)));
        case TYPE_ALBUM:
        	return m_data.getAlbum(position-m_data.getArtistCount()-2);
        case TYPE_ARTIST:
        	return m_data.getArtist(position-1);
        }
        return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	
	
	@Override
	public int getItemViewType(int position) {
		if(position == 0) {
			return TYPE_ARTIST_HEADER;
		}
		if(position == m_data.getArtistCount() + 1) {
			return TYPE_ALBUM_HEADER;
		}
		if(position == m_data.getArtistCount() + m_data.getAlbumCount() + 2) {
			return TYPE_SONG_HEADER;
		}
		if(position <= m_data.getArtistCount()) {
			return TYPE_ARTIST;
		}
		if(position <= m_data.getArtistCount() + m_data.getAlbumCount()) {
			return TYPE_ALBUM;
		}
		return TYPE_SONG;
	}


	@Override
	public int getViewTypeCount() {
		return 6;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		
		if(type == TYPE_ARTIST_HEADER || type == TYPE_ALBUM_HEADER || type == TYPE_SONG_HEADER) {
			if(convertView == null) {
				convertView = getHeaderView();
				convertView.setBackgroundResource(android.R.color.background_dark);
			}
			String text = type == TYPE_ARTIST_HEADER ? "Artists" : type == TYPE_ALBUM_HEADER ? "Albums" : "Songs";
			((TextView) ((ViewGroup) convertView).getChildAt(0)).setText(text);
			return convertView;
		}
		
		if(convertView == null)
			convertView = layoutInflater.inflate(R.layout.searchlist_item, parent, false);
		
		TextView txt1 = (TextView) convertView.findViewById(R.id.item1);
		TextView txt2 = (TextView) convertView.findViewById(R.id.item2);
		RoundedImageView img = (RoundedImageView) convertView.findViewById(R.id.album_art);
		String text = type == TYPE_ARTIST ? "Artists" : type == TYPE_ALBUM ? "Albums" : "Songs";
		if(m_data.getTotalSize() < 1)
			return convertView;
		
		try {
		Log.d("ATRACI", "Type: " + text + "  Position: " + position + " Artist Size: "+m_data.getArtistCount() + "Album Size: " + m_data.getAlbumCount() + "Song Size: "+ m_data.getSongCount() + " Total Size: " + m_data.getTotalSize());
        switch(type) {
        case TYPE_SONG:
			new ImageDownloader(img,m_data.getSong(position - (m_data.getArtistCount() + m_data.getAlbumCount() + 3)).getImage_lrg());
//        	txt.setText(m_data.getSong(Math.abs(position - m_data.getAlbumCount()- m_data.getArtistCount()-3)).getTrack());
			txt1.setText(m_data.getSong(position - (m_data.getArtistCount() + m_data.getAlbumCount() + 3)).getTrack());
			txt2.setText(m_data.getSong(position - (m_data.getArtistCount() + m_data.getAlbumCount() + 3)).getArtist());
        	break;
        case TYPE_ALBUM:
        	new ImageDownloader(img,m_data.getAlbum(position-(m_data.getArtistCount()+2)).getImage_lrg());
        	txt1.setText(m_data.getAlbum(position-m_data.getArtistCount()-2).getAlbum());
        	txt2.setText(m_data.getAlbum(position-m_data.getArtistCount()-2).getArtist());
        	break;
        case TYPE_ARTIST:
        	new ImageDownloader(img,m_data.getArtist(position-1).getImage_lrg());
        	txt1.setText(m_data.getArtist(position-1).getArtist());
        	break;
        }
		} catch (Exception e) {
			Log.e("ATRACI", ""+e.getMessage(), e);
			return convertView;
		} 
		
		
        Animation animationY = new ScaleAnimation(0, 1, 1, 1, Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_PARENT, 1);
        animationY.setDuration(200);
        convertView.startAnimation(animationY);  
        animationY = null;
		
		return convertView;
	}
	
	private ViewGroup getHeaderView() {
		LinearLayout layout = new LinearLayout(mContext);
		TextView tv = new TextView(mContext);
		tv.setBackgroundResource(android.R.color.background_dark);
		tv.setTextColor(Color.WHITE);
		tv.setShadowLayer(1, 0, 0, Color.BLACK);
		layout.addView(tv);
		return layout;
	}	
}