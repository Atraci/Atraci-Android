package net.getatraci.atraci.activities;

import net.getatraci.atraci.R;
import net.getatraci.atraci.loaders.NewsLoader;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * A fragment that is displayed when the application is started that shows a 
 * list of commits from the Github repo
 * @author Blake LaFleur
 */
public class HomeNewsFragment extends Fragment {

	private ListView lv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the fragment layout
		return inflater.inflate(R.layout.fragment_homenews,
				container,
				false); 
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Get the ListView from the layout to pass to the loader
		lv = (ListView)getActivity().findViewById(R.id.commit_list); 
		// Start the loader to get information from Github
		getLoaderManager().initLoader(0, null, new NewsLoader(getActivity(), lv));
	}
}
