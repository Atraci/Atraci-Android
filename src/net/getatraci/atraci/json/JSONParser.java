package net.getatraci.atraci.json;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import net.getatraci.atraci.data.CommitItem;
import net.getatraci.atraci.data.MusicItem;
import net.getatraci.atraci.data.MusicTypeCategories;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {


	public static final String  LASTFM_API_URL = "http://www.last.fm/search/autocomplete?q=";
	public static final String ATRACI_API_URL = "http://api.getatraci.net/search/";
	public static final String YOUTUBE_API_URL_RTSP = "http://gdata.youtube.com/feeds/api/videos?format=6&alt=json&max-results=1&q=";
	public static final String YOUTUBE_API_URL = "http://gdata.youtube.com/feeds/api/videos?alt=json&format=5&restriction=US&max-results=1&q=";
	public static final String LYRICS_WIKIA_API_URL = "http://lyrics.wikia.com/api.php?fmt=realjson&func=getSong&artist=%s&song=%s";
	public static final String TOP_100_LIST_URL = "http://itunes.apple.com/rss/topsongs/limit=100/explicit=true/json";
	
	public static final int ATRACI = 0;
	public static final int LFM = 1;
	public static final int LYRICS_WIKIA = 2;

	public static String getJSON(String address) throws Throwable {
		StringBuilder builder = new StringBuilder();
		
		try{
		HttpURLConnection urlConnection = (HttpURLConnection) new URL(address).openConnection();
		urlConnection.setRequestMethod("GET");
		urlConnection.connect();
		InputStream is = new BufferedInputStream(urlConnection.getInputStream());
		if(urlConnection.getResponseCode() != 200)
			throw new Throwable("The server did not respond as expected! Please try again later.");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				is, "iso-8859-1"), 8);
		String line = null;
		while ((line = reader.readLine()) != null) {
			builder.append(line + "\n");
		}
		is.close();
		urlConnection.disconnect();
		}catch(ClientProtocolException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		return builder.toString();
	}
	
	
	public static String extractYoutubeId(String url) throws MalformedURLException {
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
	
    public static MusicTypeCategories getLastFMListFromJsonArray(JSONArray jsonArray) {
        MusicTypeCategories lfm = new MusicTypeCategories();
        // fill the list
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
            	MusicItem lfi = new MusicItem();
                JSONObject jo = (JSONObject) jsonArray.get(i);
                // fill map
                Iterator<?> iter = jo.keys();
                while(iter.hasNext()) {
                    String currentKey = (String) iter.next();
                    	parseLFM(currentKey, jo, lfi);
                }

                lfi.setType();

                switch(lfi.getType()) {
	                case MusicItem.TRACK:
	                	lfm.addSong(lfi);
	                	break;
	                case MusicItem.ALBUM:
	                	lfm.addAlbum(lfi);
	                	break;
	                case MusicItem.ARTIST:
	                	lfm.addArtist(lfi);
	                	break;
                }
            } catch (JSONException e) {
                Log.e("JSON", e.getLocalizedMessage());
            }
        }
        return lfm;
    }
    
    public static ArrayList<MusicItem> getSongListFromJsonArray(JSONArray jsonArray) {
        ArrayList<MusicItem> items = new ArrayList<MusicItem>();
        // fill the list
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
            	MusicItem lfi = new MusicItem();
                JSONObject jo = (JSONObject) jsonArray.get(i);
                // fill map
                Iterator<?> iter = jo.keys();
                while(iter.hasNext()) {
                    String currentKey = (String) iter.next();
                    	parseATRACI(currentKey, jo, lfi);
                }

                lfi.setType();
                
                	items.add(lfi);
            } catch (JSONException e) {
                Log.e("JSON", e.getLocalizedMessage());
            }
        }
        return items;
    }
    
    public static ArrayList<MusicItem> getTop100FromJsonArray(JSONArray jsonArray) {
        ArrayList<MusicItem> items = new ArrayList<MusicItem>();
        // fill the list
        for (int i = 0; i < jsonArray.length(); i++) {
            	MusicItem lfi = new MusicItem();
            	try {
					lfi.setTrack(jsonArray.getJSONObject(i).getJSONObject("im:name").getString("label"));
					lfi.setImage_med(jsonArray.getJSONObject(i).getJSONArray("im:image").getJSONObject(1).getString("label"));
					lfi.setImage_lrg(jsonArray.getJSONObject(i).getJSONArray("im:image").getJSONObject(2).getString("label"));
					lfi.setArtist(jsonArray.getJSONObject(i).getJSONObject("im:artist").getString("label"));
					lfi.setAlbum(jsonArray.getJSONObject(i).getJSONObject("im:collection").getJSONObject("im:name").getString("label"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	items.add(lfi);
            
        }
        return items;
    }
    
    public static String getLyrics(String artist, String song) {
    	String lyrics = "";
    	String query = String.format(JSONParser.LYRICS_WIKIA_API_URL, artist.replaceAll(" ", "_"), song.replaceAll(" ", "_"));
		try {
			String json = JSONParser.getJSON(query);
			JSONObject jsonObject = new JSONObject(json);
			lyrics = jsonObject.getString("lyrics");
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return lyrics;
    }
    
    public static ArrayList<CommitItem> getCommits() {
    	
    	ArrayList<CommitItem> commits = new ArrayList<CommitItem>();
    	try {
			String json = JSONParser.getJSON("https://api.github.com/repos/Atraci/Atraci-Android/commits");
			JSONArray array = new JSONArray(json);
			for(int i = 0; i < array.length(); i++) {
				JSONObject jo = array.getJSONObject(i).getJSONObject("commit");
				CommitItem ci = new CommitItem();
				String s = jo.getString("message");
				ci.setMessage(s);
				commits.add(ci);
			}
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return commits;
    }
    
    private static void parseLFM(String currentKey, JSONObject jo, MusicItem lfi) throws JSONException {
        if(currentKey.equals("track")) {
        	lfi.setTrack(jo.getString(currentKey));
        }
        else if(currentKey.equals("album")) {
        	lfi.setAlbum(jo.getString(currentKey));
        }
        else if(currentKey.equals("artist")) {
        	lfi.setArtist(jo.getString(currentKey));
        }
        else if(currentKey.equals("image")) {
        	lfi.setImage_med("http://userserve-ak.last.fm/serve/34s/"+""+jo.getString(currentKey));
        	lfi.setImage_lrg("http://userserve-ak.last.fm/serve/64/"+""+jo.getString(currentKey));
        }
    }
    
    private static void parseATRACI(String currentKey, JSONObject jo, MusicItem lfi) throws JSONException {
        if(currentKey.equals("title")) {
        	lfi.setTrack(jo.getString(currentKey));
        	return;
        }
        if(currentKey.equals("artist")) {
        	lfi.setArtist(jo.getString(currentKey));
        	return;
        }
        if(currentKey.equals("cover_url_large")) {
        	lfi.setImage_lrg(jo.getString(currentKey));
        	return;
        }
        if(currentKey.equals("cover_url_medium")) {
        	lfi.setImage_med(jo.getString(currentKey));
        	return;
        }
    }
    
    public static String parseYoutubeRTSP(String q) throws JSONException {
    	try {
    		q = q.replaceAll(" ", "%20");
			String json = getJSON(YOUTUBE_API_URL_RTSP+q);
			JSONObject ja = new JSONObject(json).getJSONObject("feed").getJSONArray("entry").getJSONObject(0).getJSONObject("media$group");

			String link = ja.getJSONArray("media$content").getJSONObject(2).getString("url");
			return link;
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			Log.e("ATRACI", e.getMessage(), e);
		}
    	return null;
    }
    
    public static String parseYoutube(String q) throws JSONException {
    	try {
    		q = q.replaceAll(" ", "%20");
			String json = getJSON(YOUTUBE_API_URL+q);
			JSONArray ja = new JSONObject(json).getJSONObject("feed").getJSONArray("entry").getJSONObject(0).getJSONArray("link");

			String link = ja.getJSONObject(0).getString("href");
			return link;
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			Log.e("ATRACI", e.getMessage(), e);
		}
    	return null;
    }

}

