package net.getatraci.atraci.loaders;

import java.util.ArrayList;

import net.getatraci.atraci.R;
import net.getatraci.atraci.data.CommitItem;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CommitItemAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<CommitItem> items;
	
	public CommitItemAdapter(Context context, ArrayList<CommitItem> data) {
		inflater = LayoutInflater.from(context);
		items = data;
	}
	
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
        if(view == null)
        {
           return inflater.inflate(R.layout.commit_item, parent, false);
        }
        
        TextView tv = (TextView) view.findViewById(R.id.commit_message);
        tv.setText(items.get(position).getMessage());
        Log.d("ATRACI", items.size() + " | " + items.get(position).getMessage());
        
		return view;
	}


}
