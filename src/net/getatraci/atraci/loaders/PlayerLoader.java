package net.getatraci.atraci.loaders;

import net.getatraci.atraci.activities.PlayerActivity;
import net.getatraci.atraci.json.JSONParser;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * The loader for the PlayerActivity. This loader creates an Asynctask and
 * pulls all JSON information. It then creates the HTML file for the webview
 * and plays it.
 * @author Blake LaFleur
 *
 */

public class PlayerLoader implements LoaderCallbacks<String[]> {
	
	private PlayerActivity activity;
	private WebView wv;
	
	/**
	 * 
	 * @param player the instance of the player class that called the loader
	 */
	public PlayerLoader(PlayerActivity player) {
		activity = player;
	}
	
	/**
	 * Create a new AsyncTaskLoader that loads all relevent video information.
	 * This includes: Video ID, Lyrics String, Full content URL
	 */
	public Loader<String[]> onCreateLoader(int id, Bundle args) {
		return new AsyncTaskLoader<String[]>(activity.getActivity()) {
			String[] data;
			@Override
			public String[] loadInBackground() {
			String[] results = new String[3];
				try {
					//Get the ID from the full URL
					results[0] = JSONParser.parseYoutube(activity.getQuery()[activity.getPosition()]);
					//Get the lyrics 
					//results[1] = JSONParser.getLyrics(artist, title);
					//Get the full Youtube URL
					results[2] = activity.getQuery()[activity.getPosition()]; 
					return results;
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return null;
			}
			
			
			@Override
			public void deliverResult(String[] data) {
				this.data = data;
				if(activity.isVideoOver()) {
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
		wv = activity.getWebView();
		//Load the YTHtml.html file into the webview to create YouTube player
		wv.loadDataWithBaseURL("http://localhost:8000/", activity.getHtml(data[0]), "text/html", "utf-8", null);
		//Set the lyrics and make them scrollable
//		TextView tv = (TextView)activity.findViewById(R.id.lyrics_box);
//        tv.setMovementMethod(new ScrollingMovementMethod());
//        tv.setText(data[1]);  
        //Create a notification with the song information
        activity.showNotification(data[2], "Now Playing", data[2]);
        activity.setVideoOver(false);
	}

	@Override
	public void onLoaderReset(Loader<String[]> arg0) {
		
	}

}
