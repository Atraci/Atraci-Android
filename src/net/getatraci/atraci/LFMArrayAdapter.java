package net.getatraci.atraci;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LFMArrayAdapter extends BaseAdapter {

	private ArrayList<MusicItem> m_data;
	LayoutInflater layoutInflater;
	Context m_Context;
	
    public LFMArrayAdapter(Context context, ArrayList<MusicItem> data) {
      super();
      m_Context = context;
      m_data = data;
      layoutInflater = LayoutInflater.from(context);
    }


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(m_data.size() != 0)
			return m_data.size();
		else
			return 1;
	}

	@Override
	public MusicItem getItem(int position) {
		if(m_data.size() != 0)
			return m_data.get(position);
		else
			return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView == null)
			convertView = layoutInflater.inflate(R.layout.searchlist_item, parent, false);
		
		TextView txt = (TextView) convertView.findViewById(R.id.item1);
		ImageView img = (ImageView) convertView.findViewById(R.id.album_art);
		
		if(m_data.size() == 0) {
			img.setImageResource(R.drawable.ic_action_cancel);
			txt.setText("No Results :(");
			return convertView;
		}
		
		try {
			new ImageDownloader(img,m_data.get(position).getImage()).get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(m_data.get(position).getTrack() != "")
			txt.setText(m_data.get(position).getTrack());
		else if(m_data.get(position).getAlbum() != "")
			txt.setText(m_data.get(position).getAlbum());
		else 
			txt.setText(m_data.get(position).getArtist());
		
		return convertView;
	}
	
	
}