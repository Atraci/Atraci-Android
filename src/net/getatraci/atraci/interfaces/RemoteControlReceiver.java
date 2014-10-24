package net.getatraci.atraci.interfaces;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

public class RemoteControlReceiver extends BroadcastReceiver {
	
	public RemoteControlReceiver() {
		super();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (KeyEvent.KEYCODE_MEDIA_PLAY == event.getKeyCode()) {
                Log.d("ATRACI", "Media play button");
            }
            else if(KeyEvent.KEYCODE_MEDIA_PAUSE == event.getKeyCode()) {
            	Log.d("ATRACI", "Media pause button");
            }
            else if(KeyEvent.KEYCODE_MEDIA_NEXT == event.getKeyCode()) {
            }
            else if(KeyEvent.KEYCODE_MEDIA_PREVIOUS == event.getKeyCode()) {
            }
            else if(KeyEvent.KEYCODE_MEDIA_STOP == event.getKeyCode()) {
            	Log.d("ATRACI", "Media stop button");
            }
        }
		
	}

}
