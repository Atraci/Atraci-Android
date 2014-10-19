package net.getatraci.atraci.activities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import net.getatraci.atraci.R;
import net.getatraci.atraci.interfaces.RemoteControlReceiver;
import net.getatraci.atraci.json.JSONParser;
import net.getatraci.atraci.loaders.PlayerLoader;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * This activity is the player for all media received by the application.
 * Media is sent to player in a bundle containing a list of songs, and
 * the position in the list to begin playing at.
 * @author Blake LaFleur
 *
 */
public class PlayerActivity extends Activity{

	private int position = 0;  //The position to be stored from the bundle
	private ImageButton play_button,prev_button, next_button, shuffle_button, repeat_button;	//Buttonbar buttons
	private SeekBar seekBar;
	private TextView time, total_time;
	private WebView wv;	// The webview that contains the HTML file of the video viewer 
	private String[] query; // The list to be stored from the bundle containing songs
	private Notification notification; // Notification for the notification drawer
	private NotificationManager mNotifyMgr; // Manager that handles the notification
	private static final int NOTIFY_ID=1; //ID of the notification
	private boolean videoOver = true;
	private PlayerLoader loader;
	private Timer timer;
	private boolean playing = false;
	private boolean shuffle = false;
	private boolean repeat = false;
	private boolean isSeeking = false;
	private ActionBar actionBar;
	private AudioManager am;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		//		this.moveTaskToBack(true);
		//Set up the action bar
		actionBar = getActionBar();
		setTitle("");
		loader = new PlayerLoader(this);
		mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		wv = (WebView)findViewById(R.id.youtube_view);
		//Get data from the bundle
		Bundle bundle = getIntent().getExtras();
		query = bundle.getStringArray("values");
		position = bundle.getInt("position");

		play_button = (ImageButton)findViewById(R.id.playbut);
		prev_button = (ImageButton)findViewById(R.id.prevbut);
		next_button = (ImageButton)findViewById(R.id.nextbut);
		shuffle_button = (ImageButton)findViewById(R.id.shufflebut);
		repeat_button = (ImageButton)findViewById(R.id.repeatbut);
		seekBar = (SeekBar)findViewById(R.id.player_seekbar);

		time = (TextView)findViewById(R.id.play_time);
		total_time = (TextView)findViewById(R.id.total_time);

		//Load button action listeners
		setPlayButtonOnClick();
		setPrevButtonOnClick();
		setNextButtonOnClick();
		setShuffleButtonOnClick();
		setRepeatButtonOnClick();
		setSeekbarOnChange();
		//Load the song to being playing
		getLoaderManager().initLoader(123, bundle, loader);
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		enableMediaKeys();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		if(intent.getExtras() != null) {
			Bundle bundle = intent.getExtras();
			query = bundle.getStringArray("values");
			position = bundle.getInt("position");
			stopVideo();
			getLoaderManager().restartLoader(123, bundle, loader);
		}
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		disableMediaKeys();
	}

	private void enableMediaKeys() {
		am.registerMediaButtonEventReceiver(new ComponentName(this, RemoteControlReceiver.class.getName()));
	}
	
	private void disableMediaKeys() {
		am.unregisterMediaButtonEventReceiver(new ComponentName(this, RemoteControlReceiver.class));
	}

	public void setSeekbarOnChange() {
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(final SeekBar seekBar) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								wv.loadUrl("javascript:player.seekTo("+seekBar.getProgress()+", true);");
							}});
					}
				});
				isSeeking = false;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				isSeeking = true;

			}

			@Override
			public void onProgressChanged(final SeekBar seekBar, int progress,
					boolean fromUser) {
				time.setText(String.format("%d:%02d", (progress/60), progress % 60 ));
			}
		});
	}

	/**
	 * Sets the onClickListener for the play button
	 */
	public void setPlayButtonOnClick() {
		play_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// If the video is paused, start it.
				if(!playing){
					playVideo();
				}
				else {
					pauseVideo();
				}

			}
		});
	}

	/**
	 * Sets the onClickListener for the previous button
	 */
	public void setPrevButtonOnClick() {
		prev_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				skipToItemByIndexOffset(-1);
			}
		});
	}

	/**
	 * Sets the onClickListener for the next button
	 */
	public void setNextButtonOnClick() {
		next_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				skipToItemByIndexOffset(1);				
			}
		});
	}

	/**
	 * Sets the onClickListener for the shuffle button
	 */
	public void setShuffleButtonOnClick() {
		shuffle_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				shuffle = (shuffle ? false : true);
				shuffle_button.setImageResource((shuffle ? R.drawable.ic_action_shuffle_on : R.drawable.ic_action_shuffle_off));
			}
		});
	}

	/**
	 * Sets the onClickListener for the repeat button
	 */
	public void setRepeatButtonOnClick() {
		repeat_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				repeat = (repeat ? false : true);
				repeat_button.setImageResource((repeat ? R.drawable.ic_action_repeat_on : R.drawable.ic_action_repeat_off));
			}
		});
	}

	/**
	 * @param offset the offset to skip to in the array
	 */
	public void skipToItemByIndexOffset(final int offset) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				//Stop the current song from playing
				stopVideo();
				if(!shuffle) {
					position = position + offset;
				} else {
					position = getRandomIndex();
				}

				if(position < 0) {
					position = 0;
				} else if(repeat && position >= query.length) {
					position = 0;
				} else if(!repeat && position >= query.length) {
					return;
				}
				//Get the new song and begin playing
				getLoaderManager().getLoader(123).forceLoad();
			}
		});
	}

	/**
	 * Pauses the player via the JavascriptInterface and resets the UI
	 */
	public void pauseVideo() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				wv.loadUrl("javascript:pauseVideo()");
				play_button.setImageResource(R.drawable.ic_action_play);
				cancelNotification();
				timer.cancel();
				playing = false;
			}});
	}

	/**
	 * Stops the player via the JavascriptInterface and resets the UI
	 */
	public void stopVideo() {
		videoOver = true;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				wv.loadUrl("javascript:stopVideo()");
				play_button.setImageResource(R.drawable.ic_action_play);
				cancelNotification();
				timer.cancel();
				playing = false;
			}});
	}

	/**
	 * Starts the player via the JavascriptInterface and resets the UI
	 */
	public void playVideo() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				wv.loadUrl("javascript:playVideo()");
				play_button.setImageResource(R.drawable.ic_action_pause);
				reshowLastNotification();
				startTimer();
				playing = true;
			}});
	}

	public void startTimer() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {	
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						wv.loadUrl("javascript:JSInt.setVideoTime(getCurrentTime())");
					}});

			}

		},0, 1000);

	}

	public void setActionBarTitle(final String text) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				actionBar.setTitle(text);
			}});
	}

	/**
	 * Callback that handles when the user presses the back button.
	 * We want to stop the video and destroy the activity if the user
	 * goes back to the song list so we don't end up playing 2 songs
	 * at once.
	 */
	@Override
	public void onBackPressed() {
		stopVideo();
		cancelNotification();
		finish();
	}

	/**
	 * Create a notification and display it in the notification drawer that is 
	 * relevant to what is currently playing.
	 *  
	 * @param ticker the title displayed when the notification first appears
	 * @param title the title text displayed on the notification
	 * @param content the details displayed under the title text
	 */
	public void showNotification(String ticker, String title, String content) {
		//Create an intent that will bring the last activity back to the front 
		//if the application is in the background.
		Intent notIntent = new Intent(this, PlayerActivity.class);
		notIntent.setAction(Intent.ACTION_MAIN);
		notIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		notIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT|
				Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pendInt = PendingIntent.getActivity(this,
				0,
				notIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		//Create our notification
		Notification.Builder builder = new Notification.Builder(this);
		builder.setContentIntent(pendInt);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setTicker(ticker);
		builder.setOngoing(true);
		builder.setContentTitle(title);
		builder.setContentText(content);
		//Build the notification
		notification = builder.build();
		//Create the notification or update it if it already exists
		mNotifyMgr.notify(NOTIFY_ID, notification);
	}

	/**
	 * Removes the notification from the notification drawer
	 */
	public void cancelNotification() {
		mNotifyMgr.cancel(NOTIFY_ID);
	}

	/**
	 * Reshows last notification that was created
	 */
	public void reshowLastNotification() {
		mNotifyMgr.notify(NOTIFY_ID, notification);
	}

	/**
	 * 
	 * @param q the video ID to be injected into the html file
	 * @return string containing the player-ready HTML code
	 */
	public String getHtml(String q) {
		InputStream is;
		String str = "";
		try {
			is = getAssets().open("YTPlayer.html");
			int size = is.available();

			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();

			str = new String(buffer);
			str = str.replace("videoId: '%@'", "videoId: '"+JSONParser.extractYoutubeId(q)+"'");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return str;
	}


	/**************************************************************************
	 * Getters/Setters														  * 
	 *************************************************************************/
	/**
	 * Gets the current position in the list of songs
	 * @return
	 */
	public int getPosition() {
		return position;
	}

	public WebView getWebView() {
		return wv;
	}

	/**
	 * Gets the list of songs
	 * @return
	 */
	public String[] getQuery() {
		return query;
	}

	public boolean isVideoOver() {
		return videoOver;
	}

	public void setVideoOver(boolean videoOver) {
		this.videoOver = videoOver;
	}

	public SeekBar getSeekBar() {
		return seekBar;
	}

	public TextView getTimeView() {
		return time;
	}

	public TextView getTotalTimeView() {
		return total_time;
	}

	public void setTimeViewTime(final int time) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(!isSeeking) {
					getTimeView().setText(String.format("%d:%02d", (time/60), time % 60 ));
				}
			}});
	}

	public void setTotalTimeViewTime(final int time) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				getTotalTimeView().setText(String.format("/ %d:%02d", (time/60), time % 60 ));
			}});
	}

	private int getRandomIndex() {
		Random r = new Random();
		r.setSeed(System.nanoTime()*System.currentTimeMillis());
		int i = r.nextInt(query.length-1);

		if(i != position) {
			return i;
		}

		return getRandomIndex();
	}

}
