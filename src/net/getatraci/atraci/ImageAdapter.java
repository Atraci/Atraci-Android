package net.getatraci.atraci;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    
    int cnt;
    public ImageAdapter(Context c) {
        inflater = LayoutInflater.from(c);
    }

    public int getCount() {
        return test.length-1;
    }

    public Object getItem(int position) {
        return null;
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

        picture.setImageResource(imgs[i]);
        name.setText(test[i]);

        return v;
    }
    
    private int[] imgs = {R.drawable.afro,R.drawable.enjoyed, R.drawable.portal, R.drawable.ic_launcher};
    private String[] test = {"Afro", "Enjoyed", "portal", "ic_launcher"};

}