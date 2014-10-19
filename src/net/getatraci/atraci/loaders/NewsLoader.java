package net.getatraci.atraci.loaders;

import java.util.ArrayList;

import net.getatraci.atraci.data.CommitItem;
import net.getatraci.atraci.json.JSONParser;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class NewsLoader implements LoaderCallbacks<CommitItemAdapter> {
	
	private Activity activity;
	private ListView lv;

	public NewsLoader(Activity activity, ListView lv) {
		this.activity = activity;
		this.lv = lv;
	}
	
	@Override
	public Loader<CommitItemAdapter> onCreateLoader(int id, Bundle args) {
		return new AsyncTaskLoader<CommitItemAdapter>(activity) {
			CommitItemAdapter data;
			
			@Override
			public CommitItemAdapter loadInBackground() {
				ArrayList<CommitItem> commits;
				try{
					commits = JSONParser.getCommits();
					return new CommitItemAdapter(activity, commits);

				} catch(Exception e){
					Log.e("ATRACI", e.getMessage(), e);
					e.printStackTrace();} catch (Throwable e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public void deliverResult(CommitItemAdapter data) {
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
	public void onLoadFinished(Loader<CommitItemAdapter> loader, CommitItemAdapter adapter) {
		lv.setAdapter(adapter);
	}

	@Override
	public void onLoaderReset(Loader<CommitItemAdapter> arg0) {
		
	}


}
