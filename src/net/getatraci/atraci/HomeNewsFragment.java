package net.getatraci.atraci;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class HomeNewsFragment extends Fragment implements LoaderCallbacks<CommitItemAdapter> {
	
	ListView lv;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.fragment_homenews, container, false);
		//return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		lv = (ListView)getActivity().findViewById(R.id.commit_list);
		
		getLoaderManager().initLoader(0, null, this);
	}



	@Override
	public Loader<CommitItemAdapter> onCreateLoader(int id, Bundle bundle) {
		final Activity a = getActivity();
		return new AsyncTaskLoader<CommitItemAdapter>(a) {
			CommitItemAdapter data;
			
			@Override
			public CommitItemAdapter loadInBackground() {
				
				ArrayList<CommitItem> commits;
					
				try{
					commits = JSONParser.getCommits();
					return new CommitItemAdapter(a, commits);

				} catch(Exception e){
					Log.e("ATRACI", e.getMessage(), e);
					e.printStackTrace();} catch (Throwable e) {
					// TODO Auto-generated catch block
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
		//progress.dismiss();
	}



	@Override
	public void onLoaderReset(Loader<CommitItemAdapter> arg0) {
		// TODO Auto-generated method stub
		
	}


}
