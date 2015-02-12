package net.getatraci.atraci.interfaces;

import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

public class MediaSessionCallbacks extends MediaSessionCompat.Callback {
	
	public MediaSessionCallbacks() {
		Log.d("ATRACI", "MediaSessionStarted");
	}
	
	@Override
	public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
		Log.d("ATRACI", "Got MBE  " + mediaButtonEvent.getAction());
		return super.onMediaButtonEvent(mediaButtonEvent);
	}

	@Override
	public void onCommand(String command, Bundle extras, ResultReceiver cb) {
		Log.d("ATRACI", "Got command!  " + command);
		super.onCommand(command, extras, cb);
	}
	
	

}
