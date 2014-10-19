package net.getatraci.atraci.interfaces;

import net.getatraci.atraci.activities.PlayerActivity;
import android.webkit.JavascriptInterface;

public class PlayerJSInterface {
	
	private PlayerActivity player;
	
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
	public int getScreenWidth() {
//		Display display = player.getWindowManager().getDefaultDisplay();
//		Point size = new Point();
//		display.getSize(size);
//		return size.x;
		
		return player.getWebView().getWidth();
	}
	
	@JavascriptInterface
	public int getScreenHeight() {
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
	}
	
	@JavascriptInterface
	public void setTitle(String title) {
		player.setActionBarTitle(title);
	}

}
