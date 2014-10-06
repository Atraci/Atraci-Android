package net.getatraci.atraci.activities;

import net.getatraci.atraci.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlaylistSelectorFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.activity_playlist_selector, container, false);
		//return super.onCreateView(inflater, container, savedInstanceState);
	}

	
	
}
