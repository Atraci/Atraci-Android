package net.getatraci.atraci.activities;

import java.util.ArrayList;

import net.getatraci.atraci.R;
import net.getatraci.atraci.data.DatabaseHelper;
import net.getatraci.atraci.data.MusicItem;
import net.getatraci.atraci.data.Playlists;
import net.getatraci.atraci.loaders.PlaylistListAdapter;
import net.getatraci.atraci.loaders.SongListAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Blake LaFleur
 *
 */

public class PlaylistSelectorFragment extends Fragment implements OnKeyListener, OnClickListener, OnItemClickListener, OnItemLongClickListener, OnFocusChangeListener, LoaderCallbacks<PlaylistListAdapter> {

	private Menu mMenu;
	private DatabaseHelper database;
	private ListView list;
	View.OnTouchListener gestureListener;

	static class ViewHolder {
		public TextView text;
	}
	
	public PlaylistSelectorFragment(){
		super();
	}

	public PlaylistSelectorFragment(DatabaseHelper db) {
		database = db;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getLoaderManager().initLoader(111, null, this);
		//list.setOnItemClickListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_playlist_selector, container, false);
		list = (ListView) v.findViewById(R.id.playlists_list);
		list.setOnItemLongClickListener(this);
		list.setOnItemClickListener(this);
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.playlist_selector, menu);
		menu.removeItem(R.id.action_search);
		mMenu = menu;
	}

	/* Listener for navbar items like search and drawer */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_add) {
			openTextField();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void openTextField() {
		getActivity().getActionBar().setTitle("");
		mMenu.findItem(R.id.action_add).setActionView(R.layout.menu_textngo);
		mMenu.findItem(R.id.action_add).getActionView().findViewById(R.id.addplaylist_button).setOnClickListener(this);
		mMenu.findItem(R.id.action_add).getActionView().findViewById(R.id.addplaylist_field).setOnFocusChangeListener(this);
		mMenu.findItem(R.id.action_add).getActionView().findViewById(R.id.addplaylist_field).setOnKeyListener(this);
		getActivity().getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	}

	@Override
	public void onClick(View v) {
		String s = ((EditText)mMenu.findItem(R.id.action_add).getActionView().findViewById(R.id.addplaylist_field)).getText().toString();
		long result = database.createPlaylist(s);
		if(result > 0) {
		getLoaderManager().getLoader(111).forceLoad();
		list.requestFocus();
		//getActivity().getActionBar().setTitle(R.string.playlists);
		mMenu.findItem(R.id.action_add).setActionView(null);
		getActivity().getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		} else {
			Toast.makeText(getActivity(), getString(R.string.playlist_exists), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(!hasFocus) {
			mMenu.findItem(R.id.action_add).setActionView(null);
			//getActivity().getActionBar().setTitle(getResources().getString(R.string.playlists));
			getActivity().getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		}		
	}

	public Loader<PlaylistListAdapter> onCreateLoader(int id, Bundle bundle) {
		return new AsyncTaskLoader<PlaylistListAdapter>(getActivity()){
			PlaylistListAdapter data;

			@Override
			public PlaylistListAdapter loadInBackground() {
				ArrayList<Playlists> data = database.getAllPlaylists();
				return new PlaylistListAdapter(getActivity(), data);
			}


			@Override
			public void deliverResult(PlaylistListAdapter data) {
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
	public void onLoadFinished(Loader<PlaylistListAdapter> loader,
			PlaylistListAdapter adapter) {

		if(adapter.getCount() == 0) {
			Toast.makeText(getActivity(), R.string.no_playlists_found, Toast.LENGTH_LONG).show();
			list.setAdapter(new PlaylistListAdapter(getActivity(), new ArrayList<Playlists>()));
		} else {
			list.setAdapter(adapter);
		}

	}

	@Override
	public void onLoaderReset(Loader<PlaylistListAdapter> loader) {		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		createDeleteDialog((Playlists)list.getAdapter().getItem(position), view).show();
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Playlists plist = ((Playlists)list.getAdapter().getItem(position));
		Toast.makeText(getActivity(), getString(R.string.loading)+ " " + plist.getName(), Toast.LENGTH_LONG).show();
		SongListActivity.show(this, Integer.toString(plist.getId()), true);
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if(event.getAction()==KeyEvent.ACTION_UP && keyCode==KeyEvent.KEYCODE_ENTER){
			onClick(null);
			return true;

		}else {
			return false;
		}
	}
	
	public Dialog createDeleteDialog(final Playlists p, final View view) {
		final Loader<SongListAdapter> loader = getLoaderManager().getLoader(111);
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle("Are you sure?");
	    builder.setMessage("Delete " + p.getName() + "?");
	    builder.setIcon(android.R.drawable.ic_dialog_alert);
	    builder.setPositiveButton("Do It!", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	    		database.deletePlaylist(p.getId());
	    		HomeActivity.getDatabase().closeConnection();
	            	Toast.makeText(getActivity(), getString(R.string.song_delete_successful), Toast.LENGTH_SHORT).show();
	                Animation animationY = new ScaleAnimation(1,0, 1, 0, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0);
	                animationY.setDuration(700);
	                animationY.setFillEnabled(true);
	                animationY.setFillAfter(true);
	                view.startAnimation(animationY);  
	                animationY = null;
	            	loader.forceLoad();
	      } });
	    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	            
	      } });
	    return builder.create();
	}
}
