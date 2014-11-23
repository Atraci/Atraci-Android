package net.getatraci.atraci.data;

import org.json.JSONException;

import net.getatraci.atraci.json.JSONParser;
import android.os.AsyncTask;

public class AsyncYoutubeGetter extends AsyncTask<String, Void, String> {

	public AsyncYoutubeGetter(String song) {
		execute(song);
	}

	@Override
	protected String doInBackground(String... params) {
		String song = params[0];
		try {
			return JSONParser.parseYoutube(song);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}
