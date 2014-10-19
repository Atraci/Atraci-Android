package net.getatraci.atraci.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Atraci";
    private static final int DATABASE_VERSION = 3;
	
    private static final String TABLE_PLAYLISTS = "playlists";
    private static final String TABLE_SONGS = "songs";
    
    //Primary Keys
    private static final String KEY_ID = "id";
    
    //Column Tables
    private static final String PLAYLISTS_NAME = "name";
    private static final String SONGS_LINK = "link";
    private static final String SONGS_ARTIST = "artist";
    private static final String SONGS_TITLE = "title";
    private static final String SONGS_COVER_LARGE = "cover_large";
    private static final String SONGS_COVER_MED = "cover_med";
    
    //Table creation strings
    private static final String CREATE_TABLE_PLAYLISTS = "CREATE TABLE " 
    										+ TABLE_PLAYLISTS + "("
    										+ KEY_ID + " INTEGER PRIMARY KEY,"
    										+ PLAYLISTS_NAME + " TEXT,"
    										 + "FOREIGN KEY("+ KEY_ID + ") REFERENCES "+ TABLE_SONGS +"("+ KEY_ID +  "));";
    private static final String CREATE_TABLE_SONGS = "CREATE TABLE " 
    										+ TABLE_SONGS + "(" 
    										+ KEY_ID + " INTEGER," 
    										+ SONGS_LINK + " TEXT," 
    										+ SONGS_ARTIST + " TEXT," 
    										+ SONGS_TITLE + " TEXT,"
    										+ SONGS_COVER_LARGE + " TEXT,"
    										+ SONGS_COVER_MED + " TEXT"
    										+/* "FOREIGN KEY("+ KEY_ID + ") REFERENCES "+ TABLE_PLAYLISTS +"("+ KEY_ID +  ")*/");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PLAYLISTS);
        db.execSQL(CREATE_TABLE_SONGS);
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        //Recreate the tables
        onCreate(db);
		
	}
	
	public long createPlaylist(String playlist) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(PLAYLISTS_NAME, playlist);
		
		return db.insert(TABLE_PLAYLISTS, null, values);
	}
	
	public void deletePlaylist(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_SONGS, KEY_ID + " = ?", new String[]{Integer.toString(id)});
		db.delete(TABLE_PLAYLISTS, KEY_ID + " = ?", new String[]{Integer.toString(id)});
	}
	
	public Playlists getPlaylistByName(String playlist_name) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		String query = "SELECT * FROM " + TABLE_PLAYLISTS + " WHERE " + PLAYLISTS_NAME + " = '" + playlist_name + "'";
		
		Cursor c = db.rawQuery(query, null);
		
		if(c == null || !c.moveToFirst()) {
			return new Playlists();
		}
		
		Playlists playlist = new Playlists(c.getInt(c.getColumnIndex(KEY_ID)), c.getString(c.getColumnIndex(PLAYLISTS_NAME)));
		return playlist;
	}
	
	public Playlists getPlaylistByID(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		String query = "SELECT * FROM " + TABLE_PLAYLISTS + " WHERE " + KEY_ID + " = " + id;
		
		Cursor c = db.rawQuery(query, null);
		
		if(c == null || !c.moveToFirst()) {
			return new Playlists();
		}
		
		Playlists playlist = new Playlists(c.getInt(c.getColumnIndex(KEY_ID)), c.getString(c.getColumnIndex(PLAYLISTS_NAME)));
		return playlist;
	}
	
	public ArrayList<Playlists> getAllPlaylists() {
		ArrayList<Playlists> playlists = new ArrayList<Playlists>();
		SQLiteDatabase db = this.getReadableDatabase();
		
		String query = "SELECT * FROM " + TABLE_PLAYLISTS;
		
		Cursor c = db.rawQuery(query, null);
		
		if(c == null || !c.moveToFirst()) {
			return playlists;
		}
		
		do {
			Playlists p = new Playlists(c.getInt(c.getColumnIndex(KEY_ID)), c.getString(c.getColumnIndex(PLAYLISTS_NAME)));
			playlists.add(p);
		} while(c.moveToNext());
		
		return playlists;
	}
	
	public ArrayList<MusicItem> getSongsFromPlaylist(int id) {
		ArrayList<MusicItem> playlists = new ArrayList<MusicItem>();
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT * FROM " + TABLE_SONGS + " WHERE " + KEY_ID + " = ?";
		
		Cursor c = db.rawQuery(query, new String[]{Integer.toString(id)});
		if(!c.moveToFirst()) {
			return playlists;
		}
		
		do {
			MusicItem item = new MusicItem();
			item.setYoutube(c.getString(c.getColumnIndex(SONGS_LINK)));
			item.setArtist(c.getString(c.getColumnIndex(SONGS_ARTIST)));
			item.setTrack(c.getString(c.getColumnIndex(SONGS_TITLE)));
			item.setYoutube(c.getString(c.getColumnIndex(SONGS_LINK)));
			item.setImage_lrg(c.getString(c.getColumnIndex(SONGS_COVER_LARGE)));
			item.setImage_med(c.getString(c.getColumnIndex(SONGS_COVER_MED)));
			playlists.add(item);
		} while(c.moveToNext());

		return playlists;
	}
	
	public boolean addSongToPlaylist(String name, MusicItem item) {
		SQLiteDatabase db = this.getWritableDatabase();
		Playlists plist = getPlaylistByName(name);
		ContentValues values = new ContentValues();
		values.put(KEY_ID, plist.getId());
		values.put(SONGS_LINK, item.getYoutube());
		values.put(SONGS_ARTIST, item.getArtist());
		values.put(SONGS_TITLE, item.getTrack());
		values.put(SONGS_COVER_LARGE, item.getImage_lrg());
		values.put(SONGS_COVER_MED, item.getImage_med());
		
		long result = db.insert(TABLE_SONGS, null, values);
		closeConnection(); //Close the connection we opened in this function
		return (result > 0 ? true : false);
	}
	
	public void closeConnection() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen()) {
			db.close();
		}
	}
}