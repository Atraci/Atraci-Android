package net.getatraci.atraci;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenIntentListener extends BroadcastReceiver {

    private static boolean screenOn = true;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenOn = false;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            screenOn = true;
        }
    }
    
    
    public static boolean isScreenOn() {
    	return ScreenIntentListener.screenOn;
    }
    

}
