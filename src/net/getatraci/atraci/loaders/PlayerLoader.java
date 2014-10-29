package net.getatraci.atraci.loaders;

import net.getatraci.atraci.activities.HomeActivity;
import net.getatraci.atraci.activities.PlayerFragment;
import net.getatraci.atraci.data.MusicItem;
import net.getatraci.atraci.json.JSONParser;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

/**
 * The loader for the PlayerActivity. This loader creates an Asynctask and
 * pulls all JSON information. It then creates the HTML file for the webview
 * and plays it.
 * @author Blake LaFleur
 *
 */

public class PlayerLoader implements LoaderCallbacks<String[]> {
	
	private PlayerFragment mPlayer;
	private WebView wv;
	
	/**
	 * 
	 * @param player the instance of the player class that called the loader
	 */
	public PlayerLoader(PlayerFragment player) {
		mPlayer = player;
	}
	
	/**
	 * Create a new AsyncTaskLoader that loads all relevent video information.
	 * This includes: Video ID, Lyrics String, Full content URL
	 */
	public Loader<String[]> onCreateLoader(int id, Bundle args) {
		return new AsyncTaskLoader<String[]>(mPlayer.getActivity()) {
			String[] data;
			@Override
			public String[] loadInBackground() {
			String[] results = new String[3];
				try {
					MusicItem item = mPlayer.getQuery().get(mPlayer.getPosition());
					HomeActivity.getDatabase().addToHistory(item);
					//Get the ID from the full URL
					results[0] = JSONParser.parseYoutube(item.getTrack() + " - " + item.getArtist());
					//Get the lyrics 
					//results[1] = JSONParser.getLyrics(artist, title);
					results[2] = item.getTrack() + " - " + item.getArtist();
					return results;
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return null;
			}
			
			
			@Override
			public void deliverResult(String[] data) {
				this.data = data;
				if(mPlayer.isVideoOver()) {
					onLoadFinished(null, data);
					//super.deliverResult(data);
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

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onLoadFinished(Loader<String[]> loader, String[] data) {
		mPlayer.getQueue_list().setAdapter(new QueueListAdapter(mPlayer.getActivity(), mPlayer.getQuery(), mPlayer.getPosition()));
		Log.d("ATRACI", "load finished");
		if(mPlayer.getPosition() > 3){
		mPlayer.getQueue_list().setSelection(mPlayer.getPosition()-2);
		}
		wv = mPlayer.getWebView();
		//Load the YTHtml.html file into the webview to create YouTube player
		wv.loadDataWithBaseURL("http://localhost:8000/", mPlayer.getHtml(data[0]), "text/html", "utf-8", null);
        mPlayer.setVideoOver(false);
        if(mPlayer.isPlaying()){
        	mPlayer.playVideo();
        }
	}

	@Override
	public void onLoaderReset(Loader<String[]> arg0) {
		
	}

}
