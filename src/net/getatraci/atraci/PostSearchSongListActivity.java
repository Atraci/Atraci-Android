package net.getatraci.atraci;

import java.util.ArrayList;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class PostSearchSongListActivity extends Activity implements LoaderCallbacks<SongListAdapter>, OnItemClickListener {

	GridView m_gridview;
	private static final int LID_PSSLA = 1;
	private ProgressDialog progress;
	private ArrayList<MusicItem> results;

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_home);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		final Bundle bundle = getIntent().getExtras();
		
		actionBar.setTitle(bundle.getString("query"));

		m_gridview = (GridView) findViewById(R.id.gridview);
		progress = new ProgressDialog(this);
		progress.setTitle("Loading...");
		progress.setMessage("Loading tracks...\nPlease Wait!");
		
		getLoaderManager().initLoader(LID_PSSLA, bundle, this);
		
		m_gridview.setOnItemClickListener(this);


	}
	
	@Override
	public Loader<SongListAdapter> onCreateLoader(int id, Bundle bundle) {
		
		final String q = JSONParser.ATRACI_API_URL + bundle.getString("query").replaceAll(" ", "%20");
		return  new AsyncTaskLoader<SongListAdapter>(this) {
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
			}

			@Override
			protected void onStartLoading() {
				progress.show();
				forceLoad();
			}
		};
	}
	
	private void startPlayerActivity(String link, String name) {
		Intent intent = new Intent(this, PlayerActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("query", link);
		bundle.putString("title", name);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public void onLoadFinished(Loader<SongListAdapter> loader, SongListAdapter adapter) {
		progress.dismiss();
		m_gridview.setAdapter(adapter);

	}

	@Override
	public void onLoaderReset(Loader<SongListAdapter> loader) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		GridView grid = (GridView)adapter.findViewById(R.id.gridview);
		MusicItem mi = ((MusicItem)grid.getAdapter().getItem(position));
		startPlayerActivity(mi.getYoutube(), mi.getTrack());
		
	}

}
