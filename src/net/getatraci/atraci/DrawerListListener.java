package net.getatraci.atraci;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class DrawerListListener  implements OnItemClickListener{

	private PlaylistSelectorFragment mContext;
	
	public DrawerListListener(PlaylistSelectorFragment context) {
		mContext = context;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Fragment sel = new PlaylistSelectorFragment();
		FragmentManager manager = mContext.getFragmentManager();
		manager.beginTransaction().replace(R.id.content_frame, mContext).commit();
	}

}
