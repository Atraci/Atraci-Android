package net.getatraci.atraci.activities;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import net.getatraci.atraci.R;
import net.getatraci.atraci.data.AsyncYoutubeGetter;
import net.getatraci.atraci.data.MusicItem;
import net.getatraci.atraci.interfaces.PlayerJSInterface;
import net.getatraci.atraci.interfaces.RemoteControlReceiver;
import net.getatraci.atraci.json.JSONParser;
import net.getatraci.atraci.loaders.QueueListAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
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
@SuppressLint("SetJavaScriptEnabled")
public class PlayerFragment extends Fragment implements OnItemClickListener{

	private int position = 0;  //The position to be stored from the bundle
	private ImageButton play_button,prev_button, next_button, shuffle_button, repeat_button;	//Buttonbar buttons
	private SeekBar seekBar;
	private TextView time, total_time;
	private ListView queue_list;
	private FrameLayout wv_frame;
	private WebView wv;	// The webview that contains the HTML file of the video viewer 
	private ArrayList<MusicItem> query; // The list to be stored from the bundle containing songs
	private Notification notification; // Notification for the notification drawer
	private NotificationManager mNotifyMgr; // Manager that handles the notification
	private static final int NOTIFY_ID=1; //ID of the notification
	private boolean videoOver = true;
	private Timer timer;
	private boolean playing = false;
	private boolean shuffle = false;
	private boolean repeat = false;
	private boolean isSeeking = false;
	private QueueListAdapter q_adapter;
	private Bundle bundle;
	private boolean HTMLLoaded = false;
	private int timePlayed = 0;
	private Stack<Integer> playQueue;
	MediaSessionCompat mMediaSession;
	MediaControllerCompat mMediaController;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the fragment layout
		View view = inflater.inflate(R.layout.fragment_player,
				container,
				false); 
		//manager = getLoaderManager();
		//loader = new PlayerLoader(this);
		mNotifyMgr = (NotificationManager) getActivity().getSystemService(Activity.NOTIFICATION_SERVICE);
		playQueue = new Stack<Integer>();
		//wv = (CustomWebView)view.findViewById(R.id.youtube_view);
		wv = new WebView(getActivity());
		setUpWebview();
		play_button = (ImageButton)view.findViewById(R.id.playbut);
		prev_button = (ImageButton)view.findViewById(R.id.prevbut);
		next_button = (ImageButton)view.findViewById(R.id.nextbut);
		shuffle_button = (ImageButton)view.findViewById(R.id.shufflebut);
		repeat_button = (ImageButton)view.findViewById(R.id.repeatbut);
		seekBar = (SeekBar)view.findViewById(R.id.player_seekbar);
		time = (TextView)view.findViewById(R.id.play_time);
		total_time = (TextView)view.findViewById(R.id.total_time);
		queue_list = (ListView)view.findViewById(R.id.queue_list);
		wv_frame = (FrameLayout) view.findViewById(R.id.webview_frame);
		wv.setBackgroundColor(Color.BLACK);
		wv_frame.addView(wv);
		//Load button action listeners
		setPlayButtonOnClick();
		setPrevButtonOnClick();
		setNextButtonOnClick();
		setShuffleButtonOnClick();
		setRepeatButtonOnClick();
		setSeekbarOnChange();
		//Get data from the bundle
		bundle = getArguments();
		if(bundle == null && savedInstanceState == null){ //If nothing has been sent to the player, lets simply load their history
			query = HomeActivity.getDatabase().getHistory();
			position = 0;
			queue_list.setOnItemClickListener(this);
			bundle = new Bundle();
			bundle.putParcelableArrayList("values", query);
			bundle.putInt("position", position);
			if(query.size() > 0){
				loadNewBundle(bundle);
			}
			return view;
		}
		else if(bundle == null){
			bundle = savedInstanceState;
		}
		query = bundle.getParcelableArrayList("values");
		position = bundle.getInt("position");
		queue_list.setOnItemClickListener(this);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		AudioManager am = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
		// Start listening for button presses
//		mMediaSession = new MediaSessionCompat(getActivity(), "123");
//		mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
//		mMediaSession.setCallback(new MediaSessionCallbacks());
//		mMediaSession.setActive(true);
//		try {
//			mMediaController = new MediaControllerCompat(getActivity(), mMediaSession.getSessionToken());
//			
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		getActivity().registerReceiver(new RemoteControlReceiver(), new IntentFilter(Intent.ACTION_HEADSET_PLUG));
	}
	
	

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState = this.bundle;
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroyView() {
		AudioManager am = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
		// Stop listening for button presses
		pauseVideo();
		wv.destroy();
		super.onDestroyView();
	};
	
	public void loadNewBundle(Bundle bundle) {
		this.bundle = bundle;
		playQueue.clear();
		if(bundle != null) {
			query = bundle.getParcelableArrayList("values");
			position = bundle.getInt("position");
			stopVideo();
			q_adapter = new QueueListAdapter(getActivity(), query, position);
			queue_list.setAdapter(q_adapter);
		}
		MusicItem song = query.get(position);
		String ytLink = "";
		try {
			ytLink = new AsyncYoutubeGetter(song.getTrack() + " - " + song.getArtist()).get();
			song.setYoutube(ytLink);
			wv.loadDataWithBaseURL("http://localhost:8080/", getHtml(ytLink), "text/html", "utf-8", null);
			HomeActivity.getDatabase().addToHistory(song);
			setTimePlayed(0);
			if(!HTMLLoaded){
				pauseVideo();
			} else {
				playVideo();
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void loadNewVideoAtCurrentPosition(){
		MusicItem song = query.get(position);
		q_adapter.setPos(position);
		q_adapter.notifyDataSetChanged();
		String ytLink = "";
		try {
			ytLink = new AsyncYoutubeGetter(song.getTrack() + " - " + song.getArtist()).get();
			if(null != ytLink){
				song.setYoutube(ytLink);
				Log.d("ATRACI", ytLink);
				HomeActivity.getDatabase().addToHistory(song);
				wv.loadUrl("javascript:player.loadVideoById(\""+ JSONParser.extractYoutubeId(ytLink)+"\", 0, \"large\");");
				setTimePlayed(0);
				playVideo();
			} else {
				skipToItemByIndexOffset(1); //If youtube URL is missing or malformed, load the next song if available
			}
		} catch (InterruptedException | ExecutionException | MalformedURLException e) {
			e.printStackTrace();
		}
			queue_list.smoothScrollToPosition(position);
	}
	
	public void setSeekbarOnChange() {
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(final SeekBar seekBar) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getActivity().runOnUiThread(new Runnable() {
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
				playQueue.clear(); //Remove the play queue since the user is wanting to restart shuffling
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
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				//If video has been playing for a bit
				//make prev button take track back to 
				//the beginning on first click
				if(offset == -1 && timePlayed > 5){ 
					wv.loadUrl("javascript:player.seekTo(0, true);");
					timePlayed = 0;
				} else {

					//Stop the current song from playing
					stopVideo();
					if(!shuffle) {
						position = position + offset;
					} else {
						position = getRandomIndex();
					}

					if(position < 0) {
						position = 0;
					} else if(repeat && position >= query.size()) {
						position = 0;
					} else if(!repeat && position >= query.size()) {
						return;
					}
					//Get the new song and begin playing
					loadNewVideoAtCurrentPosition();
					//playVideo();
				}
			}
		});
	}

	public void updateQueueListPosition(){

	}

	/**
	 * Pauses the player via the JavascriptInterface and resets the UI
	 */
	public void pauseVideo() {
		if(getActivity() == null){
			return;
		}
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				wv.loadUrl("javascript:pauseVideo()");
				play_button.setImageResource(R.drawable.ic_action_play);
				cancelNotification();
				if(timer != null) {
					timer.cancel();
				}
				playing = false;
			}});
	}

	/**
	 * Stops the player via the JavascriptInterface and resets the UI
	 */
	public void stopVideo() {
		videoOver = true;
		if(getActivity() == null){
			return;
		}

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				wv.loadUrl("javascript:stopVideo()");
				play_button.setImageResource(R.drawable.ic_action_play);
				cancelNotification();
				if(timer != null) {
					timer.cancel();
				}
				playing = false;
			}});
	}

	/**
	 * Starts the player via the JavascriptInterface and resets the UI
	 */
	public void playVideo() {
		if(getActivity() == null){
			return;
		}
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Log.d("ATRACI", "playVideo()");
				wv.loadUrl("javascript:playVideo()");
				play_button.setImageResource(R.drawable.ic_action_pause);
				reshowLastNotification();
				startTimer();
				playing = true;
				//Create a notification with the song information
				MusicItem current = query.get(position);
				showNotification(current.toString(), "Now Playing", current.toString());
			}});
	}

	public void startTimer() {
		if(timer != null){
			timer.cancel();
		}
		if(getActivity() == null){
			return;
		}
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {	
				if(getActivity() == null) {
					return;
				}
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						wv.loadUrl("javascript:JSInt.setVideoTime(getCurrentTime())");
					}});

			}

		},0, 1000);

	}

	public void setActionBarTitle(final String text) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				//actionBar.setTitle(text);
			}});
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
		Intent notIntent = new Intent(getActivity(), SplashActivity.class);
		notIntent.setAction(Intent.ACTION_MAIN);
		notIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		notIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT|
				Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pendInt = PendingIntent.getActivity(getActivity(),
				0,
				notIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		//Create our notification
		Notification.Builder builder = new Notification.Builder(getActivity());
		builder.setContentIntent(pendInt);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setLargeIcon(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.ic_launcher));
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
		if(notification != null){
			mNotifyMgr.notify(NOTIFY_ID, notification);
		}
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
			is = getActivity().getAssets().open("YTPlayer.html");
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
	public ArrayList<MusicItem> getQuery() {
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
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(!isSeeking) {
					getTimeView().setText(String.format("%d:%02d", (time/60), time % 60 ));
				}
			}});
	}

	public void setTotalTimeViewTime(final int time) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				getTotalTimeView().setText(String.format("%d:%02d", (time/60), time % 60 ));
			}});
	}

	private int getRandomIndex() {
		Random r = new Random();
		r.setSeed(System.nanoTime()*System.currentTimeMillis());
		int i = r.nextInt(query.size()-1);

		if(!playQueue.contains(i)) {
			playQueue.add(i);
			
			if(playQueue.size() > (query.size() / 2)) //If the queue grows larger than half the queue, remove the lowest element so the song can be used again
				playQueue.remove(0);
			return i;
		}

		return getRandomIndex();
	}

	private void setUpWebview() {
		//Set the websettings to allow javascript injection, and media playback
		// without the need for the user to tap play on the webview
		WebSettings websettings = wv.getSettings();
		websettings.setJavaScriptEnabled(true);
		websettings.setDomStorageEnabled(true);
		websettings.setDatabaseEnabled(true);
		websettings.setMediaPlaybackRequiresUserGesture(false);

		//Set the webclients to support HTML5
		//wv.setWebViewClient(new WebViewClient());
		wv.setWebChromeClient(new WebChromeClient());
		//Set the javascript interface
		wv.addJavascriptInterface(new PlayerJSInterface(this), "JSInt");
		wv.setPadding(0, 0, 0, 0);
		wv.getSettings().setLoadWithOverviewMode(true);
		//Use html defined sizes over webview defaults
		wv.getSettings().setUseWideViewPort(true);
		//Disable the user from being able to move the video around
		wv.setVerticalScrollBarEnabled(false);
		wv.setHorizontalScrollBarEnabled(false);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		this.position = position;
		q_adapter.setPos(position);
		q_adapter.notifyDataSetChanged();
		loadNewVideoAtCurrentPosition();
	}

	public FrameLayout getWv_frame() {
		return wv_frame;
	}

	public void setWv_frame(FrameLayout wv_frame) {
		this.wv_frame = wv_frame;
	}

	public ListView getQueue_list() {
		return queue_list;
	}

	public void setQueue_list(ListView queue_list) {
		this.queue_list = queue_list;
	}

	public boolean isPlaying(){
		return playing;
	}

	public boolean isHTMLLoaded() {
		return HTMLLoaded;
	}

	public void setHTMLLoaded(boolean hTMLLoaded) {
		HTMLLoaded = hTMLLoaded;
	}

	public int getTimePlayed() {
		return timePlayed;
	}

	public void setTimePlayed(int timePlayed) {
		this.timePlayed = timePlayed;
	}

}
