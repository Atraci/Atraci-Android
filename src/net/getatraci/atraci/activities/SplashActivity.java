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
		setContentView(R.layout.activity_splashscreen);
		getActionBar().hide();
	       new Handler().postDelayed(new Runnable() {
	    	   
	            /*
	             * Showing splash screen with a timer.
	             */
	 
	            @Override
	            public void run() {
	                Intent i = new Intent(SplashActivity.this, HomeActivity.class);
	                startActivity(i);
	                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	 
	                // close this activity
	                finish();
	            }
	        }, 2000);
	}

}
