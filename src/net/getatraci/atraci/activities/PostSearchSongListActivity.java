package net.getatraci.atraci.activities;

import java.util.ArrayList;

import net.getatraci.atraci.R;
import net.getatraci.atraci.data.MusicItem;
import net.getatraci.atraci.data.Playlists;
import net.getatraci.atraci.json.JSONParser;
import net.getatraci.atraci.loaders.SongListAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
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

public class PostSearchSongListActivity extends Activity implements LoaderCallbacks<SongListAdapter>, OnItemClickListener, OnItemLongClickListener {

	GridView m_gridview;
	private static final int LID_PSSLA = 1;
	private ProgressDialog progress;
	private ArrayList<MusicItem> results;
	private boolean isPlaylist;
	private int playlistID;
	private String playlistName;

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(true); 
		setContentView(R.layout.activity_postsearchsonglist);

		//		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		//		mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		final Bundle bundle = getIntent().getExtras();
		isPlaylist = bundle.getBoolean("isPlaylist");
		actionBar.setTitle(bundle.getString("query"));
		m_gridview = (GridView) findViewById(R.id.gridview);
		progress = new ProgressDialog(this);
		progress.setTitle("Loading...");
		progress.setMessage("Loading tracks...\nPlease Wait!");
		getLoaderManager().initLoader(LID_PSSLA, bundle, this);
		m_gridview.setOnItemClickListener(this);
		m_gridview.setOnItemLongClickListener(this);
		
		if(isPlaylist) {
			playlistID = Integer.parseInt(bundle.getString("query"));
			playlistName = HomeActivity.getDatabase().getPlaylistByID(playlistID).getName();
			actionBar.setTitle("Playlist: " + playlistName);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.postsearch, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public Loader<SongListAdapter> onCreateLoader(int id, final Bundle bundle) {

		final String q = JSONParser.ATRACI_API_URL + bundle.getString("query").replaceAll("'", "").replaceAll(" ", "%20");
		return  new AsyncTaskLoader<SongListAdapter>(this) {
			SongListAdapter data;
			@Override
			public SongListAdapter loadInBackground() {
				try {
					if(!isPlaylist) { //Load JSON if this is not a playlist 
						String readJSON = JSONParser.getJSON(q);
						JSONArray jsonArray = new JSONArray(readJSON);
						results = JSONParser.getSongListFromJsonArray(jsonArray);
					} else {
						results = HomeActivity.getDatabase().getSongsFromPlaylist(playlistID);
						HomeActivity.getDatabase().closeConnection();
					}
					return new SongListAdapter(PostSearchSongListActivity.this, results);
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
					setProgressBarIndeterminateVisibility(true); 
					//					progress.show();
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
	
	public static void fireIntent(Context  yourClass, String query, boolean isPlaylist) {
		Intent intent = new Intent(yourClass, PostSearchSongListActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("query", query);
		bundle.putBoolean("isPlaylist", isPlaylist);
		intent.putExtras(bundle);
		yourClass.startActivity(intent);
	}

	private void startPlayerActivity(String[] songs, int pos) {
		Intent intent = new Intent(this, PlayerActivity.class);
		Bundle bundle = new Bundle();
		bundle.putStringArray("values", songs);
		bundle.putInt("position", pos);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public void onLoadFinished(Loader<SongListAdapter> loader, SongListAdapter adapter) {
		m_gridview.setAdapter(adapter);
		setProgressBarIndeterminateVisibility(false); 
		if(m_gridview.getAdapter().getCount() == 0) {
			Toast.makeText(PostSearchSongListActivity.this, "There are no songs to show!", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onLoaderReset(Loader<SongListAdapter> loader) {
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		GridView grid = (GridView)adapter.findViewById(R.id.gridview);

		String[] songs = new String[grid.getAdapter().getCount()];
		for(int i = 0; i < grid.getAdapter().getCount(); i++) {
			MusicItem mi = ((MusicItem)grid.getAdapter().getItem(i));
			songs[i] = mi.getArtist() + " - " + mi.getTrack();
		}
		startPlayerActivity(songs, position);	
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		
        Animation animationY = new ScaleAnimation(1,(float)0.5, 1,(float)0.5, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0);
        animationY.setDuration(700);
        animationY.setFillEnabled(true);
        animationY.setFillAfter(true);
        view.startAnimation(animationY);  
        
        animationY = null;
		ArrayList<Playlists> plists = HomeActivity.getDatabase().getAllPlaylists();
		String[] names = new String[plists.size()];
		
		if(plists.size() == 0) {
			Toast.makeText(this, "You do not have any playlists!", Toast.LENGTH_LONG).show();
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
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Add to Playlist:");
		builder.setItems(playlists, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int pos) {
				boolean result = HomeActivity.getDatabase().addSongToPlaylist(playlists[pos], item);
	            Animation animationY = new ScaleAnimation((float)0.5, 1, (float)0.5, 1, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0);
	            animationY.setDuration(700);
	            animationY.setFillEnabled(true);
	            animationY.setFillAfter(true);
	            view.startAnimation(animationY);  
	            animationY = null;
	            
				if(!result) {
					Toast.makeText(PostSearchSongListActivity.this, "This song already exists in the playlist!", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(PostSearchSongListActivity.this, "Song added to " + playlists[pos] + " playlist!", Toast.LENGTH_SHORT).show();
				}
			}
		});
		return builder.create();
	}
	
	public Dialog createDeleteDialog(final String pname, final MusicItem item, final View view) {
		final Loader<SongListAdapter> loader = getLoaderManager().getLoader(LID_PSSLA);
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Are you sure?");
	    builder.setMessage("Delete " + item.getTrack() + "?");
	    builder.setIcon(android.R.drawable.ic_dialog_alert);
	    builder.setPositiveButton("Do It!", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	Log.d("ATRACI", item.getYoutube());
	            int result = HomeActivity.getDatabase().deleteSongFromPlaylistByLink(pname, item.getTrack());
	            if(result > 0) {
	            	Toast.makeText(PostSearchSongListActivity.this, "Song deleted successfully!", Toast.LENGTH_SHORT).show();
	                Animation animationY = new ScaleAnimation((float)0.5,0, (float)0.5, 0, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0);
	                animationY.setDuration(700);
	                animationY.setFillEnabled(true);
	                animationY.setFillAfter(true);
	                view.startAnimation(animationY);  
	                animationY = null;
	            	loader.forceLoad();
	            } else {
	            	Toast.makeText(PostSearchSongListActivity.this, "There was an unknown error while deleting the song :(", Toast.LENGTH_LONG).show();
	            }
	      } });
	    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	            Animation animationY = new ScaleAnimation((float)0.5, 1, (float)0.5, 1, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0);
	            animationY.setDuration(700);
	            animationY.setFillEnabled(true);
	            animationY.setFillAfter(true);
	            view.startAnimation(animationY);  
	            animationY = null;
	            finish();
	      } });
	    return builder.create();
	}
	

}
