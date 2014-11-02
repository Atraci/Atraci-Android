package net.getatraci.atraci.activities;

import java.util.ArrayList;

import net.getatraci.atraci.R;
import net.getatraci.atraci.data.MusicItem;
import net.getatraci.atraci.data.Playlists;
import net.getatraci.atraci.data.Top100Genres;
import net.getatraci.atraci.json.JSONParser;
import net.getatraci.atraci.loaders.GenreAdapter;
import net.getatraci.atraci.loaders.SongListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.Toast;

/**
 * This activity displays a list of songs that the user searched for in the
 * search activity. Once a song is selected, the list is sent to the player
 * with the index that was selected to begin playing.
 * @author Blake LaFleur
 *
 */

public class SongListFragment extends Fragment implements LoaderCallbacks<SongListAdapter>, OnItemClickListener, OnItemLongClickListener, ActionBar.OnNavigationListener {

	GridView m_gridview;
	private static final int LID_PSSLA = 1;
	private ArrayList<MusicItem> results;
	private boolean isPlaylist;
	private int playlistID;
	public static final String QUERY_TOP100 = "Top 100 Songs";
	private String genre = Top100Genres.ALL;
	public static final String QUERY_HISTORY = "History";
	private Bundle bundle;
	private GenreAdapter genreAdapter;
	private String query;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the fragment layout
		View view = inflater.inflate(R.layout.fragment_postsearchsonglist,
				container,
				false); 
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		bundle = getArguments();
		if(bundle == null){
			return view;
		}
		isPlaylist = bundle.getBoolean("isPlaylist");

		m_gridview = (GridView) view.findViewById(R.id.gridview);
		getLoaderManager().initLoader(LID_PSSLA, bundle, this);
		m_gridview.setOnItemClickListener(this);
		m_gridview.setOnItemLongClickListener(this);

		if(isPlaylist) {
			playlistID = Integer.parseInt(bundle.getString("query"));
		}
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstance) {
		setHasOptionsMenu(true);
		super.onCreate(savedInstance);
		getActivity().setProgressBarIndeterminateVisibility(true); 
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.d("ATRACI", "createOptions");
		getActivity().getActionBar().setDisplayShowTitleEnabled(false);
		if(QUERY_TOP100.equals(bundle.getString("query"))){
			getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		} else {
			getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		}
		inflater.inflate(R.menu.postsearch, menu);
		genreAdapter = new GenreAdapter(getActivity());
		getActivity().getActionBar().setListNavigationCallbacks(genreAdapter, this);
		//super.onCreateOptionsMenu(menu, inflater);
	}

	public void setBundle(Bundle bundle){
		if(QUERY_TOP100.equals(query)){
			getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		} else {
			getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		}
		this.bundle = bundle;
		getLoaderManager().restartLoader(LID_PSSLA, this.bundle, this);
	}

	@Override
	public Loader<SongListAdapter> onCreateLoader(int id, final Bundle bundle) {

		final String q = JSONParser.ATRACI_API_URL + bundle.getString("query").replaceAll("'", "").replaceAll(" ", "%20");
		return  new AsyncTaskLoader<SongListAdapter>(getActivity()) {
			SongListAdapter data;
			@Override
			public SongListAdapter loadInBackground() {
				try {
					Log.d("ATRACI", bundle.getString("query"));
					SongListFragment.this.query = bundle.getString("query");
					if(QUERY_TOP100.equals(SongListFragment.this.query)) {
						String json = JSONParser.getJSON(JSONParser.TOP_100_LIST_URL.replaceAll("%@", genre));
						JSONObject obj = new JSONObject(json);
						JSONArray array = obj.getJSONObject("feed").getJSONArray("entry");
						results = JSONParser.getTop100FromJsonArray(array);
					}
					else if(QUERY_HISTORY.equals(SongListFragment.this.query)){
						results = HomeActivity.getDatabase().getHistory();
					}
					else if(!isPlaylist) { //Load JSON if this is not a playlist 
						String readJSON = JSONParser.getJSON(q);
						JSONArray jsonArray = new JSONArray(readJSON);
						results = JSONParser.getSongListFromJsonArray(jsonArray);
					}
					else {
						results = HomeActivity.getDatabase().getSongsFromPlaylist(playlistID);
					}
					return new SongListAdapter(getActivity(), results);
				} catch (JSONException e) {
					Log.e("ATRACI", e.getMessage(), e);
				} catch (Throwable e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			public void deliverResult(SongListAdapter data) {
				super.deliverResult(data);
				this.data = data;
			}

			@Override
			protected void onStartLoading() {
				if(data !=null) {
					deliverResult(data);
				}
				if(data == null){
					getActivity().setProgressBarIndeterminateVisibility(true); 
					forceLoad();
				}
			}

			@Override
			protected void onStopLoading() {
				// Attempt to cancel the current load task, if possible.
				cancelLoad();
			}

			@Override
			protected void onReset() {
				super.onReset();
				// Ensure that the loader is stopped.
				onStopLoading();
				data = null;
			}
		};
	}


	@Override
	public void onLoadFinished(Loader<SongListAdapter> loader, SongListAdapter adapter) {
		m_gridview.setAdapter(adapter);
		getActivity().setProgressBarIndeterminateVisibility(false); 
		if(adapter == null || adapter.getCount() == 0) {
			Toast.makeText(getActivity(), getString(R.string.no_songs), Toast.LENGTH_LONG).show();
		}
	}

	public static void show(Fragment yourClass, String query, boolean isPlaylist) {
		Bundle bundle = new Bundle();
		bundle.putString("query", query);
		bundle.putBoolean("isPlaylist", isPlaylist);
		SongListFragment sla = new SongListFragment();
		sla.setArguments(bundle);
		FragmentTransaction ft = yourClass.getFragmentManager().beginTransaction();
		ft.replace(R.id.root_frame, sla);
		ft.addToBackStack(null);
		ft.commit();

	}

	private void startPlayerActivity(ArrayList<MusicItem> songs, int pos) {
		Bundle bundle = new Bundle();
		//bundle.putStringArray("values", songs);
		bundle.putParcelableArrayList("values", songs);
		bundle.putInt("position", pos);
		((PlayerFragment)HomeActivity.pageAdapter.getRegisteredFragment(1)).setHTMLLoaded(true);
		((PlayerFragment)HomeActivity.pageAdapter.getRegisteredFragment(1)).loadNewBundle(bundle);
		HomeActivity.pager.setCurrentItem(1);
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		GridView grid = (GridView)adapter.findViewById(R.id.gridview);

		//		ArrayList<MusicItem> songs = new ArrayList<MusicItem>();
		//		for(int i = 0; i < grid.getAdapter().getCount(); i++) {
		//			MusicItem mi = ((MusicItem)grid.getAdapter().getItem(i));
		//			songs.add(mi);
		//		}
		HomeActivity.getDatabase().addToHistory((MusicItem)grid.getAdapter().getItem(position));
		startPlayerActivity(results, position);	
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {

		doAnimation(view, false);
		ArrayList<Playlists> plists = HomeActivity.getDatabase().getAllPlaylists();
		String[] names = new String[plists.size()];

		if(plists.size() == 0) {
			Toast.makeText(getActivity(), getString(R.string.no_playlists_found), Toast.LENGTH_LONG).show();
			doAnimation(view, true);
			return true;
		}

		for(int i = 0; i < plists.size(); i++) {
			names[i] = plists.get(i).getName();
		}

		Dialog dialog;

		if(!isPlaylist) {
			dialog = createPlaylistDialog(names, (MusicItem)m_gridview.getAdapter().getItem(position), view);
			dialog.show();

		} else {
			dialog = createDeleteDialog(Integer.toString(playlistID), (MusicItem)m_gridview.getAdapter().getItem(position), view);
			dialog.show();
		}
		return true;
	}

	public Dialog createPlaylistDialog(final String[] playlists, final MusicItem item, final View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.add_to_playlist);
		builder.setItems(playlists, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int pos) {
				boolean result = HomeActivity.getDatabase().addSongToPlaylist(playlists[pos], item);
				doAnimation(view, true);

				if(!result) {
					Toast.makeText(getActivity(), getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getActivity(), getString(R.string.song_added_to) + " " + playlists[pos] +"!", Toast.LENGTH_SHORT).show();
				}
			}
		});
		return builder.create();
	}

	public Dialog createDeleteDialog(final String pname, final MusicItem item, final View view) {
		final Loader<SongListAdapter> loader = getLoaderManager().getLoader(LID_PSSLA);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Are you sure?");
		builder.setMessage("Delete " + item.getTrack() + "?");
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setPositiveButton("Do It!", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.d("ATRACI", item.getYoutube());
				int result = HomeActivity.getDatabase().deleteSongFromPlaylistByLink(pname, item.getTrack());
				if(result > 0) {
					Toast.makeText(getActivity(), getString(R.string.song_delete_successful), Toast.LENGTH_SHORT).show();
					Animation animationY = new ScaleAnimation((float)0.5,0, (float)0.5, 0, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0);
					animationY.setDuration(700);
					animationY.setFillEnabled(true);
					animationY.setFillAfter(true);
					view.startAnimation(animationY);  
					animationY = null;
					loader.forceLoad();
				} else {
					Toast.makeText(getActivity(), getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
				}
			} });
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				doAnimation(view, true);
			} });
		return builder.create();
	}

	private static void doAnimation(View view, boolean zoomOut){
		Animation animation = null;
		if(!zoomOut){
			animation = new ScaleAnimation(1,(float)0.5, 1,(float)0.5, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0);
		} else {
			animation = new ScaleAnimation((float)0.5, 1, (float)0.5, 1, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0);
		}
		animation.setDuration(700);
		animation.setFillEnabled(true);
		animation.setFillAfter(true);
		view.startAnimation(animation); 
	}

	@Override
	public void onLoaderReset(Loader<SongListAdapter> loader) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		if(QUERY_TOP100.equals(query)){
			Log.d("ATRACI", "item clicked in navbar");
			genre = (String)genreAdapter.getItem(itemPosition);
			getLoaderManager().restartLoader(LID_PSSLA, bundle, this);
		}
		return true;
	}


}
