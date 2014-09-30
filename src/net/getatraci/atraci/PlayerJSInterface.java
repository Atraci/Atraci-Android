package net.getatraci.atraci;

import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class PlayerJSInterface {
	
	@JavascriptInterface
	public void onYouTubeIframeAPIReady() {
		Toast.makeText(null, "API Ready!", Toast.LENGTH_LONG).show();
	}

}
