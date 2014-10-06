package net.getatraci.atraci.activities;

import net.getatraci.atraci.R;
import net.getatraci.atraci.data.MusicItem;
import net.getatraci.atraci.data.MusicTypeCategories;
import net.getatraci.atraci.json.JSONParser;
import net.getatraci.atraci.loaders.SongListAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class PostSearchSongListActivity extends Activity implements LoaderCallbacks<SongListAdapter>, OnItemClickListener {

	GridView m_gridview;
	private static final int LID_PSSLA = 1;
	private ProgressDialog progress;
	private MusicTypeCategories results;
	private Menu optionsMenu;

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_postsearchsonglist);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		final Bundle bundle = getIntent().getExtras();
		
		actionBar.setTitle("Results");

		m_gridview = (GridView) findViewById(R.id.gridview);
		progress = new ProgressDialog(this);
		progress.setTitle("Loading...");
		progress.setMessage("Loading tracks...\nPlease Wait!");
		setRefreshActionButtonState(true);
		getLoaderManager().initLoader(LID_PSSLA, bundle, this);
		
		m_gridview.setOnItemClickListener(this);


	}
	
	public void setRefreshActionButtonState(final boolean refreshing) {
	    if (optionsMenu != null) {
	        final MenuItem refreshItem = optionsMenu
	            .findItem(R.id.menuRefresh);
	        if (refreshItem != null) {
	            if (refreshing) {
	                refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
	            } else {
	                refreshItem.setActionView(null);
	            }
	        }
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    this.optionsMenu = menu;
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.postsearch, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public Loader<SongListAdapter> onCreateLoader(int id, Bundle bundle) {
		
		final String q = JSONParser.ATRACI_API_URL + bundle.getString("query").replaceAll("'", "").replaceAll(" ", "%20");
		return  new AsyncTaskLoader<SongListAdapter>(this) {
			SongListAdapter data;
			@Override
			public SongListAdapter loadInBackground() {
				try {
					Log.d("ATRACI", q);
					String readJSON = JSONParser.getJSON(q);
					JSONArray jsonArray = new JSONArray(readJSON);
					results = JSONParser.getListFromJsonArray(jsonArray, JSONParser.ATRACI);
					return new SongListAdapter(PostSearchSongListActivity.this, results);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.e("ATRACI", e.getMessage(), e);
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return null;
			}

			@Override
			public void deliverResult(SongListAdapter data) {
				// TODO Auto-generated method stub
				super.deliverResult(data);
				this.data = data;
				progress.dismiss();
			}

			@Override
			protected void onStartLoading() {
				if(data !=null) {
					deliverResult(data);
				}
				if(data == null){
					progress.show();
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
		setRefreshActionButtonState(false);
	}

	@Override
	public void onLoaderReset(Loader<SongListAdapter> loader) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		GridView grid = (GridView)adapter.findViewById(R.id.gridview);
		
		String[] songs = new String[grid.getAdapter().getCount()];
		Toast.makeText(this, grid.getAdapter().getCount()+"", Toast.LENGTH_SHORT).show();
		for(int i = 0; i < grid.getAdapter().getCount(); i++) {
			MusicItem mi = ((MusicItem)grid.getAdapter().getItem(i));
			songs[i] = mi.getArtist() + " - " + mi.getTrack();
		}
		startPlayerActivity(songs, position);
		
	}

}
