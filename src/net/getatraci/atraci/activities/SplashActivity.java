package net.getatraci.atraci.activities;

import net.getatraci.atraci.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstance){
		super.onCreate(savedInstance);
		setContentView(R.layout.splashscreen_layout);
		getActionBar().hide();
	       new Handler().postDelayed(new Runnable() {
	    	   
	            /*
	             * Showing splash screen with a timer. This will be useful when you
	             * want to show case your app logo / company
	             */
	 
	            @Override
	            public void run() {
	                // This method will be executed once the timer is over
	                // Start your app main activity
	                Intent i = new Intent(SplashActivity.this, HomeActivity.class);
	                startActivity(i);
	 
	                // close this activity
	                finish();
	            }
	        }, 2000);
	}

}
