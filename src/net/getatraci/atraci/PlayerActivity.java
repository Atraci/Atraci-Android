package net.getatraci.atraci;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.ActionBar;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

public class PlayerActivity extends YouTubeBaseActivity implements LoaderCallbacks<String[]>, YouTubePlayer.OnInitializedListener {

	String query;
	String artist;
	String title;
	YouTubePlayerView ytpv;
	VideoView myVideoView;
	YouTubePlayer m_player;
	
	private static final String YOUTUBE_API_KEY = "AIzaSyCqB8IccdaZbaWp7tp-Xjcm5J9IOpj8bFs";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		ActionBar actionBar = getActionBar();
		Bundle bundle = getIntent().getExtras();
		query = bundle.getString("query");
		artist = bundle.getString("artist");
		title = bundle.getString("title");
		getLoaderManager().initLoader(123, bundle, this);
		actionBar.setTitle("Now Playing");
//	    myVideoView = (VideoView)findViewById(R.id.myvideoview);
//	    myVideoView.setVideoURI(Uri.parse(query));
//	    myVideoView.setMediaController(new MediaController(this));
//	    myVideoView.requestFocus();
//	    myVideoView.start();
		//ytpv = (YouTubePlayerView) findViewById(R.id.youtube_view);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.player, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public static String getUrlVideoRTSP(String urlYoutube) {
	    try {
	        String gdy = "http://gdata.youtube.com/feeds/api/videos/";
	        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        String id = extractYoutubeId(urlYoutube);
	        URL url = new URL(gdy + id);
	        Log.i(PlayerActivity.class.getSimpleName(), url.toString());
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        Document doc = documentBuilder.parse(connection.getInputStream());
	        Element el = doc.getDocumentElement();
	        NodeList list = el.getElementsByTagName("media:content");///media:content
	        String cursor = urlYoutube;
	        for (int i = 0; i < list.getLength(); i++) {
	            Node node = list.item(i);
	            if (node != null) {
	                NamedNodeMap nodeMap = node.getAttributes();
	                HashMap<String, String> maps = new HashMap<String, String>();
	                for (int j = 0; j < nodeMap.getLength(); j++) {
	                    Attr att = (Attr) nodeMap.item(j);
	                    maps.put(att.getName(), att.getValue());
	                }
	                if (maps.containsKey("yt:format")) {
	                    String f = maps.get("yt:format");
	                    if (maps.containsKey("url")) {
	                        cursor = maps.get("url");
	                    }
	                    if (f.equals("1"))
	                        return cursor;
	                }
	            }
	        }
	        return cursor;
	    } catch (Exception ex) {
	        Log.e("Get Url Video RTSP Exception======>>", ex.toString());
	    }
	    return urlYoutube;

	}

	private static String extractYoutubeId(String url) throws MalformedURLException {
	    String id = null;
	    try {
	        String query = new URL(url).getQuery();
	        if (query != null) {
	            String[] param = query.split("&");
	            for (String row : param) {
	                String[] param1 = row.split("=");
	                if (param1[0].equals("v")) {
	                    id = param1[1];
	                }
	            }
	        } else {
	            if (url.contains("embed")) {
	                id = url.substring(url.lastIndexOf("/") + 1);
	            }
	        }
	    } catch (Exception ex) {
	        Log.e("Exception", ex.toString());
	    }
	    return id;
	}

	@Override
	public Loader<String[]> onCreateLoader(int id, Bundle args) {
		final String query = args.getString("query");
		return new AsyncTaskLoader<String[]>(this) {
			String[] data;
			@Override
			public String[] loadInBackground() {
			String[] results = new String[2];
				try {
					results[0] = JSONParser.parseYoutube(query);
					results[1] = JSONParser.getLyrics(artist, title);
					return results;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
			
			@Override
			public void deliverResult(String[] data) {
				this.data = data;
				if(isStarted()) {
					super.deliverResult(data);
				}
			}

			@Override
			protected void onStartLoading() {
				if(data !=null) {
					deliverResult(data);
				}
				if(data == null){
					forceLoad();
				}
			}
			
			@Override
			protected void onStopLoading() {
				//Attempt to cancel the current load task, if possible.
				cancelLoad();
			}
			
			@Override
			protected void onReset() {
				super.onReset();
				//Ensure that the loader is stopped.
				onStopLoading();
				data = null;
			}
			
		};
	}

	@Override
	public void onLoadFinished(Loader<String[]> arg0, String[] arg1) {
		Toast.makeText(this, arg1[0], Toast.LENGTH_LONG).show();
		this.query = arg1[0];
//	    myVideoView = (VideoView)findViewById(R.id.myvideoview);
//	    myVideoView.setVideoURI(Uri.parse(query));
//	    myVideoView.setMediaController(new MediaController(this));
//	    myVideoView.requestFocus();
//	    myVideoView.start();
		//ytpv.initialize(YOUTUBE_API_KEY, this);
		WebView wv = (WebView)findViewById(R.id.youtube_view);
		try {
			WebSettings websettings = wv.getSettings();
			websettings.setJavaScriptEnabled(true);
			websettings.setDomStorageEnabled(true);
			websettings.setDatabaseEnabled(true);
			wv.setWebViewClient(new WebViewClient());
			wv.setWebChromeClient(new WebChromeClient());
			wv.setPadding(0, 0, 0, 0);
			wv.getSettings().setLoadWithOverviewMode(true);
			wv.getSettings().setUseWideViewPort(true);
			wv.setVerticalScrollBarEnabled(false);
			wv.setHorizontalScrollBarEnabled(false);
			wv.loadData("<iframe id=\"ytplayer\" type=\"text/html\" width=\"640\" height=\"390\" src=\"http://www.youtube.com/embed/"+extractYoutubeId(query)+"?autoplay=1 frameborder=\"0\"/>", "text/html", "utf-8");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TextView tv = (TextView)findViewById(R.id.lyrics_box);
        tv.setMovementMethod(new ScrollingMovementMethod());

        tv.setText(arg1[1]);
	}

	@Override
	public void onLoaderReset(Loader<String[]> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInitializationFailure(Provider arg0,
			YouTubeInitializationResult error) {
		Toast.makeText(this, "Oh no! "+error.toString(),
				Toast.LENGTH_LONG).show();
		
	}

	@Override
	public void onInitializationSuccess(Provider arg0, YouTubePlayer player,
			boolean arg2) {
		try {
			m_player = player;
			m_player.loadVideo(extractYoutubeId(query));
			m_player.play();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
