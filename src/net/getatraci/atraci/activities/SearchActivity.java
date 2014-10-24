package net.getatraci.atraci.activities;

import java.util.Timer;
import java.util.TimerTask;

import net.getatraci.atraci.R;
import net.getatraci.atraci.data.MusicItem;
import net.getatraci.atraci.data.MusicTypeCategories;
import net.getatraci.atraci.json.JSONParser;
import net.getatraci.atraci.loaders.LFMArrayAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.SearchManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class SearchActivity extends Fragment implements OnItemClickListener, LoaderCallbacks<LFMArrayAdapter>, OnQueryTextListener {

	private ListView list;
	private Timer timer = new Timer();
	private final long SEARCH_TRIGGER_DELAY_IN_MS = 600;
	private static final int LID_LFM = 0;
	private SongListActivity songlist;

	public SearchActivity() {

	}

	public SearchActivity(SongListActivity sl) {
		songlist = sl;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the fragment layout
		View view = inflater.inflate(R.layout.activity_search,
				container,
				false); 
		list = (ListView) view.findViewById(R.id.search_list);
		list.setOnItemClickListener(this);

		getLoaderManager().initLoader(LID_LFM, null, this);

		return view;
	}

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setHasOptionsMenu(true);
		//setContentView(R.layout.activity_search);


	}

	public void launchSongList(Bundle bundle) {
		if(songlist.getActivity() == null){
			songlist.setArguments(bundle);
		} else {
			songlist.setBundle(bundle);
		}
		getFragmentManager().beginTransaction().replace(R.id.root_frame, songlist).commit();
		//songlist.onNewBundle(bundle);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getActivity().getMenuInflater().inflate(R.menu.searchview, menu);
		menu.findItem(R.id.action_search).setActionView(new android.widget.SearchView(getActivity()));
		SearchManager searchManager =
				(SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView =
				(SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setSearchableInfo(
				searchManager.getSearchableInfo(getActivity().getComponentName()));

		searchView.setOnQueryTextListener(this);

		searchView.setIconifiedByDefault(false);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		LFMArrayAdapter adapt = (LFMArrayAdapter) list.getAdapter();
		MusicItem mi = adapt.getItem(position);
		Bundle extras = new Bundle();
		extras.putString("query", ("" + mi.getArtist() + " " + mi.getAlbum() + " " + mi.getTrack()).trim()+"");
		extras.putBoolean("isPlaylist", false);
		launchSongList(extras);
	}

	@Override
	public Loader<LFMArrayAdapter> onCreateLoader(int id, Bundle bundle) {
		final String q = (bundle == null ? null : bundle.getString("query"));
		return new AsyncTaskLoader<LFMArrayAdapter>(getActivity()) {
			LFMArrayAdapter data;

			@Override
			public LFMArrayAdapter loadInBackground() {

				if(q == null || q.length() == 0) {
					return new LFMArrayAdapter(getActivity(), new MusicTypeCategories());
				}

				try{
					String readJSON = JSONParser.getJSON(JSONParser.LASTFM_API_URL+ q);
					JSONObject jsonObject = new JSONObject(readJSON);
					JSONArray array = jsonObject.getJSONObject("response").getJSONArray("docs");
					MusicTypeCategories data = JSONParser.getLastFMListFromJsonArray(array);
					LFMArrayAdapter adapt = new LFMArrayAdapter(getActivity(), data);
					return adapt;

				} catch(Exception e){
					Log.e("ATRACI", e.getMessage(), e);
					e.printStackTrace();} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				return null;
			}

			@Override
			public void deliverResult(LFMArrayAdapter data) {
				this.data = data;
				if(isStarted()) {
					super.deliverResult(data);
				}
			}

			@Override
			protected void onStartLoading() {
				if(data !=null) {
					deliverResult(data);
				}
				if(data == null){
					forceLoad();
				}
			}

			@Override
			protected void onStopLoading() {
				//Attempt to cancel the current load task, if possible.
				cancelLoad();
			}

			@Override
			protected void onReset() {
				super.onReset();
				//Ensure that the loader is stopped.
				onStopLoading();
				data = null;
			}
		};
	}

	@Override
	public void onLoadFinished(Loader<LFMArrayAdapter> loader, LFMArrayAdapter adapter) {
		list.setAdapter(adapter);
		//progress.dismiss();
	}

	@Override
	public void onLoaderReset(Loader<LFMArrayAdapter> loader) {
		list.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_expandable_list_item_1, new String[] {}));
	}

	@Override
	public boolean onQueryTextChange(final String newText) {

		timer.cancel();
		timer = new Timer();
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Bundle bundle = new Bundle();
						String text = newText.replaceAll(" ", "%20");
						bundle.putString("query", text);
						getLoaderManager().restartLoader(LID_LFM, bundle, SearchActivity.this);
					}});		
			}

		}, SEARCH_TRIGGER_DELAY_IN_MS);

		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		String text = query.replaceAll(" ", "%20");
		Bundle bundle = new Bundle();
		bundle.putString("query", text);
		bundle.putBoolean("isPlaylist", false);
		launchSongList(bundle);
		return true;
	}
}
