package net.getatraci.atraci.interfaces;

import net.getatraci.atraci.activities.PlayerActivity;
import android.graphics.Point;
import android.view.Display;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class PlayerJSInterface {
	
	private PlayerActivity player;
	
	public PlayerJSInterface(PlayerActivity context) {
		player = context;
	}
	
	@JavascriptInterface
	public void onVideoComplete() {
		player.skipToItemByIndexOffset(1);
		Toast.makeText(player, "Video Over!", Toast.LENGTH_LONG).show();
	}
	
	@JavascriptInterface
	public int getScreenWidth() {
		Display display = player.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size.x;
	}

}
