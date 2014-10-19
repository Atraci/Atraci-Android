package net.getatraci.atraci.interfaces;

import net.getatraci.atraci.activities.PlayerActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

public class RemoteControlReceiver extends BroadcastReceiver {
	
	private PlayerActivity activity;
	
	public RemoteControlReceiver(PlayerActivity player) {
		activity = player;
	}
	
	public RemoteControlReceiver() {
		super();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (KeyEvent.KEYCODE_MEDIA_PLAY == event.getKeyCode()) {
                activity.playVideo();
                Log.d("ATRACI", "Media play button");
            }
            else if(KeyEvent.KEYCODE_MEDIA_PAUSE == event.getKeyCode()) {
            	activity.pauseVideo();
            	Log.d("ATRACI", "Media pause button");
            }
            else if(KeyEvent.KEYCODE_MEDIA_NEXT == event.getKeyCode()) {
            	activity.skipToItemByIndexOffset(1);
            }
            else if(KeyEvent.KEYCODE_MEDIA_PREVIOUS == event.getKeyCode()) {
            	activity.skipToItemByIndexOffset(-1);
            }
            else if(KeyEvent.KEYCODE_MEDIA_STOP == event.getKeyCode()) {
            	activity.stopVideo();
            	Log.d("ATRACI", "Media stop button");
            }
        }
		
	}

}
