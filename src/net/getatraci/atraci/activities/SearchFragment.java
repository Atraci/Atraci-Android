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

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.SearchManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;

public class SearchFragment extends Fragment implements OnItemClickListener, LoaderCallbacks<LFMArrayAdapter>, OnQueryTextListener, OnCloseListener {

	private ListView list;
	private Timer timer = new Timer();
	private final long SEARCH_TRIGGER_DELAY_IN_MS = 600;
	private static final int LID_LFM = 0;
	private SongListFragment songlist;
	private EditText searchField;

	public SearchFragment(SongListFragment sl) {
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
	}

	public void launchSongList(Bundle bundle) {
		//if(songlist == null){
			songlist = new SongListFragment();
	//	}
		getFragmentManager().beginTransaction().replace(R.id.root_frame, songlist).commit();
		songlist.setArguments(bundle);
		hideKeyBoard(searchField.getApplicationWindowToken(), getActivity());
		
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getActivity().getMenuInflater().inflate(R.menu.searchview, menu);
		menu.removeItem(R.id.action_search);

		SearchManager searchManager =
				(SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView =
				(SearchView) menu.findItem(R.id.action_searchview).getActionView();
		searchView.setSearchableInfo(
				searchManager.getSearchableInfo(getActivity().getComponentName()));
		searchView.setIconified(false);
		searchView.setOnQueryTextListener(this);
		searchView.setOnCloseListener(this);

		View searchPlate = searchView.findViewById(searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null));
		searchPlate.setBackgroundResource(R.drawable.textfield_searchview);
		searchField = (EditText) searchView.findViewById(searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null));
		searchField.setTextColor(Color.WHITE);
		searchField.setHintTextColor(Color.GRAY);
		searchField.setHint(getResources().getString(R.string.seach_hint));
		searchField.setFocusable(true);
		searchField.requestFocus();
		showKeyBoard(getActivity());
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		LFMArrayAdapter adapt = (LFMArrayAdapter) list.getAdapter();
		MusicItem mi = adapt.getItem(position);
		Bundle extras = new Bundle();
		extras.putString("query", ("" + mi.getArtist() + " " + mi.getAlbum() + " " + mi.getTrack())+"");
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
				if(data != null) {
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
						getLoaderManager().restartLoader(LID_LFM, bundle, SearchFragment.this);
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
		hideKeyBoard(searchField.getApplicationWindowToken(), getActivity());
		return true;
	}

	@Override
	public boolean onClose() {
		if (SearchFragment.this.isVisible()) {
			hideKeyBoard(searchField.getApplicationWindowToken(), getActivity());
			getFragmentManager().popBackStackImmediate();
		}
		return true;
	}
	
	public static void hideKeyBoard(IBinder token, Activity activity){
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(token ,0);
	}
	
	public static void showKeyBoard(Activity activity){
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
	}
}
