package net.getatraci.atraci.activities;

import net.getatraci.atraci.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

public class SplashActivity extends ActionBarActivity {
	
	Toolbar mToolbar;
	TextView statusText;
	
	@Override
	public void onCreate(Bundle savedInstance){
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_splashscreen);
		mToolbar = (Toolbar)findViewById(R.id.toolbar);
		statusText = (TextView)findViewById(R.id.statusText);
		//getSupportActionBar().hide();
	       new Handler().postDelayed(new Runnable() {
	    	   
	            /*
	             * Showing splash screen with a timer.
	             */
	 
	            @Override
	            public void run() {
	                Intent i = new Intent(SplashActivity.this, HomeActivity.class);
	                statusText.setText("Checking for internet connection...");
	                if(!isNetworkAvailable()){
	                	createInternetDialog().show();
	                	Log.d("ATRACI", "internet dialog");
	                	return;
	                }
	                statusText.setText("Loading d	atabase information...");
	                try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                statusText.setText("Starting Atraci!");
	                startActivity(i);
	                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	                // close this activity
	                finish();
	            }
	        }, 2000);
	}
	
	private boolean isNetworkAvailable() {
	    NetworkInfo activeNetworkInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	public Dialog createInternetDialog() {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setCancelable(false);
	    builder.setTitle("Error");
	    builder.setMessage("No internet connection was detected! Please restart application when a connection is available.");
	    builder.setIcon(android.R.drawable.ic_dialog_alert);
	    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	System.exit(0);
	      } });
	    return builder.create();
	}

}
