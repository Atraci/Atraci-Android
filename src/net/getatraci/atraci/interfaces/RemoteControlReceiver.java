package net.getatraci.atraci.interfaces;

import net.getatraci.atraci.activities.HomeActivity;
import net.getatraci.atraci.activities.PlayerFragment;
import net.getatraci.atraci.loaders.PagerFragmentAdapter;
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
		final PlayerFragment player = (PlayerFragment)((PagerFragmentAdapter)HomeActivity.pager.getAdapter()).getRegisteredFragment(1);
		if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
			KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
			Log.d("ATRACI", "Media Key Pressed: " +event.getKeyCode());
			if (KeyEvent.KEYCODE_MEDIA_PLAY == event.getKeyCode()) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					Log.d("ATRACI", "Media play button");
					player.playVideo();
				}
			}
			else if(KeyEvent.KEYCODE_MEDIA_PAUSE == event.getKeyCode()) {
				Log.d("ATRACI", "Media pause button");
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					player.pauseVideo();
				}
			}
			else if(KeyEvent.KEYCODE_MEDIA_NEXT == event.getKeyCode()) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					player.skipToItemByIndexOffset(1);
				}
			}
			else if(KeyEvent.KEYCODE_MEDIA_PREVIOUS == event.getKeyCode()) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					player.skipToItemByIndexOffset(-1);
				}
			}
			else if(KeyEvent.KEYCODE_MEDIA_STOP == event.getKeyCode()) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					Log.d("ATRACI", "Media stop button");
					player.stopVideo();
				}
			}
			else if(KeyEvent.KEYCODE_HEADSETHOOK == event.getKeyCode()) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if(player.isPlaying()){
						player.pauseVideo();
					} else {
						player.playVideo();
					}
				}
			}
			else if(KeyEvent.KEYCODE_CALL == event.getKeyCode()) {
				player.pauseVideo();
			}
		}
	}

}
