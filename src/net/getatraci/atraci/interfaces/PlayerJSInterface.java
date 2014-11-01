package net.getatraci.atraci.interfaces;

import net.getatraci.atraci.R;
import net.getatraci.atraci.activities.PlayerFragment;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.webkit.JavascriptInterface;

public class PlayerJSInterface {
	
	private static PlayerFragment player;
	
	public PlayerJSInterface(PlayerFragment context) {
		player = context;
	}
	
	@JavascriptInterface
	public void onVideoComplete() {
		player.setVideoOver(true);
		player.skipToItemByIndexOffset(1);
	}
	
	@JavascriptInterface
	public void onVideoPaused() {
		player.pauseVideo();
	}
	
	@JavascriptInterface
	public void onVideoUnstarted() {

	}
	
	@JavascriptInterface
	public void onVideoPlaying() {
		player.playVideo();
		
	}
	
	@JavascriptInterface
	public void onPlayerReady() {
	}
	
	@JavascriptInterface
	public void setHTMLLoaded(){
		player.setHTMLLoaded(true);
	}
	
	@JavascriptInterface
	public boolean isHTMLLoaded(){
		Log.d("ATRACI", Boolean.toString(player.isHTMLLoaded()));
		return player.isHTMLLoaded();
	}
	
	@JavascriptInterface
	public static int getScreenWidth() {
		Display display = player.getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		//return player.getWv_frame().getWidth();
		
		return player.getWebView().getWidth() - 25;
	}
	
	@JavascriptInterface
	public static int getScreenHeight() {
		Display display = player.getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		//return player.getWv_frame().getHeight();
		return player.getWebView().getHeight();
	}
	
	@JavascriptInterface
	public void setSeekbarLength(int time) {
		player.getSeekBar().setMax(time);
		player.setTotalTimeViewTime(time);
	}
	
	@JavascriptInterface
	public void setVideoTime(int time) {
		player.getSeekBar().setProgress(time);
		player.setTimeViewTime(time);
		player.setTimePlayed(time);
	}
	
	@JavascriptInterface
	public void setTitle(String title) {
		player.setActionBarTitle(title);
	}
	
	@JavascriptInterface
	public String getQualityLevel() {
		return PreferenceManager.getDefaultSharedPreferences(player.getActivity()).getString("quality", player.getResources().getStringArray(R.array.quality_values)[2] );
	}
	
	@JavascriptInterface
	public void pushToLog(String text){
		Log.d("ATRACI", text);
	}

}
