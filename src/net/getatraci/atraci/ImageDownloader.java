package net.getatraci.atraci;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

	private ImageView iv;
	public ImageDownloader(ImageView view, String url) {
		iv = view;
		execute(url);
	}
	
	@Override
	protected Bitmap doInBackground(String... params) {
	       String urldisplay = params[0];
	        Bitmap mIcon11 = null;
	        try {
	            InputStream in = new java.net.URL(urldisplay).openStream();
	            mIcon11 = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	            Log.e("Error", ""+e.getMessage());
	            e.printStackTrace();
	        }
	        return mIcon11;
	}
	
	@Override
	protected void onPostExecute(Bitmap result) {
		iv.setImageBitmap(result);
	}

}
