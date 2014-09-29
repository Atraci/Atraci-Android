package net.getatraci.atraci;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

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
	
	public static final int ATRACI = 0;
	public static final int LFM = 1;
	public static final int LYRICS_WIKIA = 2;

	public static String getJSON(String address) throws Throwable {
		StringBuilder builder = new StringBuilder();
		
		try{
		HttpURLConnection urlConnection = (HttpURLConnection) new URL(address).openConnection();
		InputStream is = new BufferedInputStream(urlConnection.getInputStream());
		if(urlConnection.getResponseCode() != 200)
			throw new Throwable("The server did not respond as expected! Please try again later.");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				is, "iso-8859-1"), 8);
		String line = null;
		while ((line = reader.readLine()) != null) {
			builder.append(line + "n");
		}
		is.close();
		}catch(ClientProtocolException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		return builder.toString();
	}
	
    protected static MusicTypeCategories getListFromJsonArray(JSONArray jsonArray, int mode) {
        MusicTypeCategories lfm = new MusicTypeCategories();
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
                    switch(mode){
                    case ATRACI:
                    	parseATRACI(currentKey, jo, lfi);
                    	break;
                    case LFM:
                    	parseLFM(currentKey, jo, lfi);
                    	break;
                    }

                }

                lfi.setType();

                //                if(mode == ATRACI) {
                //                	parseYoutube(lfi, lfi.getTrack() + " - " + lfi.getArtist());
                //                }

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
                
                if(lfi.getImage() != null )
                	items.add(lfi);
            } catch (JSONException e) {
                Log.e("JSON", e.getLocalizedMessage());
            }
        }
        Log.d("ATRACI", lfm.toString());
        return lfm;
    }
    
    public static String getLyrics(String artist, String song) {
    	String lyrics = "";
    	String query = String.format(JSONParser.LYRICS_WIKIA_API_URL, artist.replaceAll(" ", "_"), song.replaceAll(" ", "_"));
		Log.d("ATRACI", query);
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
    
    private static void parseLFM(String currentKey, JSONObject jo, MusicItem lfi) throws JSONException {
        if(currentKey.equals("track"))
        	lfi.setTrack(jo.getString(currentKey));
        else if(currentKey.equals("album"))
        	lfi.setAlbum(jo.getString(currentKey));
        else if(currentKey.equals("artist"))
        	lfi.setArtist(jo.getString(currentKey));
        else if(currentKey.equals("weight"))
        	lfi.setWeight(jo.getDouble(currentKey));
        else if(currentKey.equals("image"))
        	lfi.setImage("http://userserve-ak.last.fm/serve/34s/"+""+jo.getString(currentKey));
    }
    
    private static void parseATRACI(String currentKey, JSONObject jo, MusicItem lfi) throws JSONException {
        if(currentKey.equals("title"))
        	lfi.setTrack(jo.getString(currentKey));
        else if(currentKey.equals("artist"))
        	lfi.setArtist(jo.getString(currentKey));
        else if(currentKey.equals("cover_url_medium"))
        	lfi.setImage(jo.getString(currentKey));
    }
    
    public static String parseYoutubeRTSP( String q) throws JSONException {
    	try {
    		q = q.replaceAll(" ", "%20");
			String json = getJSON(YOUTUBE_API_URL_RTSP+q);
			JSONObject ja = new JSONObject(json).getJSONObject("feed").getJSONArray("entry").getJSONObject(0).getJSONObject("media$group");

			String link = ja.getJSONArray("media$content").getJSONObject(2).getString("url");
			Log.d("ATRACI", link);
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
			Log.d("ATRACI", link);
			return link;
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			Log.e("ATRACI", e.getMessage(), e);
		}
    	return null;
    }

}

