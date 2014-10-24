package net.getatraci.atraci.interfaces;

import net.getatraci.atraci.activities.PlayerActivity;
import android.graphics.Point;
import android.view.Display;
import android.webkit.JavascriptInterface;

public class PlayerJSInterface {
	
	private static PlayerActivity player;
	
	public PlayerJSInterface(PlayerActivity context) {
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
		
	}
	
	@JavascriptInterface
	public void onPlayerReady() {
		player.playVideo();
	}
	
	@JavascriptInterface
	public static int getScreenWidth() {
		Display display = player.getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return player.getWv_frame().getWidth();
		
		//return player.getWebView().getWidth();
	}
	
	@JavascriptInterface
	public static int getScreenHeight() {
		Display display = player.getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return player.getWv_frame().getHeight();
	}
	
	@JavascriptInterface
	public void setSeekbarLength(int time) {
		player.getSeekBar().setMax(time);
		player.setTotalTimeViewTime(time);
	}
	
	@JavascriptInterface
	public int getTimePlayed() {
		return player.getTimePlayed();
	}
	
	@JavascriptInterface
	public void setVideoTime(int time) {
		player.getSeekBar().setProgress(time);
		player.setTimePlayed(time);
		player.setTimeViewTime(time);
	}
	
	@JavascriptInterface
	public void setTitle(String title) {
		player.setActionBarTitle(title);
	}

}
