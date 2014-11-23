package net.getatraci.atraci.data;

import java.util.ArrayList;
import java.util.Collections;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Atraci";
    private static final int DATABASE_VERSION = 7;
	
    private static final String TABLE_PLAYLISTS = "playlists";
    private static final String TABLE_SONGS = "songs";
    private static final String TABLE_HISTORY = "history";
    
    //Primary Keys
    private static final String KEY_ID = "id";
    
    //Column Tables
    private static final String PLAYLISTS_NAME = "name";
    private static final String SONGS_LINK = "link";
    private static final String SONGS_ARTIST = "artist";
    private static final String SONGS_TITLE = "title";
    private static final String SONGS_COVER_LARGE = "cover_large";
    private static final String SONGS_COVER_MED = "cover_med";
    private static final String SONGS_TIME_ADDED = "time_added";
    private static final String HISTORY_LINK = "link";
    private static final String HISTORY_ARTIST = "artist";
    private static final String HISTORY_TITLE = "title";
    private static final String HISTORY_COVER_LARGE = "cover_large";
    private static final String HISTORY_COVER_MED = "cover_med";
    private static final String HISTORY_TIME_ADDED = "time_added";
    
    //Table creation strings
    private static final String CREATE_TABLE_PLAYLISTS = "CREATE TABLE " 
    										+ TABLE_PLAYLISTS + "("
    										+ KEY_ID + " INTEGER PRIMARY KEY,"
    										+ PLAYLISTS_NAME + " TEXT UNIQUE,"
    										 + "FOREIGN KEY("+ KEY_ID + ") REFERENCES "+ TABLE_SONGS +"("+ KEY_ID +  "));";
    private static final String CREATE_TABLE_SONGS = "CREATE TABLE " 
    										+ TABLE_SONGS + "(" 
    										+ KEY_ID + " INTEGER," 
    										+ SONGS_LINK + " TEXT," 
    										+ SONGS_ARTIST + " TEXT," 
    										+ SONGS_TITLE + " TEXT,"
    										+ SONGS_COVER_LARGE + " TEXT,"
    										+ SONGS_COVER_MED + " TEXT,"
    										+ SONGS_TIME_ADDED + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL"
    										+/* "FOREIGN KEY("+ KEY_ID + ") REFERENCES "+ TABLE_PLAYLISTS +"("+ KEY_ID +  ")*/");";
    
    private static final String CREATE_TABLE_HISTORY = "CREATE TABLE " + TABLE_HISTORY + "("
											    		+ HISTORY_LINK + " TEXT," 
														+ HISTORY_ARTIST + " TEXT," 
														+ HISTORY_TITLE + " TEXT,"
														+ HISTORY_COVER_LARGE + " TEXT,"
														+ HISTORY_COVER_MED + " TEXT,"
														+ HISTORY_TIME_ADDED + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,"
														+ "PRIMARY KEY ("+HISTORY_ARTIST+","+HISTORY_TITLE+"));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PLAYLISTS);
        db.execSQL(CREATE_TABLE_SONGS);
        db.execSQL(CREATE_TABLE_HISTORY);
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        //Recreate the tables
        onCreate(db);
		
	}
	
	public void deleteHistory(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_HISTORY, null, null);
	}
	
	public void addToHistory(MusicItem item){
		if(item.getYoutube().length() < 1){ //If the youtube URL is missing, do not add to history.
			return;
		}
		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(HISTORY_ARTIST, item.getArtist());
		values.put(HISTORY_TITLE, item.getTrack());
		values.put(HISTORY_LINK, item.getYoutube());
		values.put(HISTORY_COVER_MED, item.getImage_med());
		values.put(HISTORY_COVER_LARGE, item.getImage_lrg());
		db.replace(TABLE_HISTORY, null, values);
	}
	
	public ArrayList<MusicItem> getHistory() {
		ArrayList<MusicItem> songs = new ArrayList<MusicItem>();
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT * FROM " + TABLE_HISTORY + " ORDER BY " + HISTORY_TIME_ADDED;
		
		Cursor c = db.rawQuery(query, null);
		if(!c.moveToFirst()) {
			return songs;
		}
		
		do {
			MusicItem item = new MusicItem();
			item.setYoutube(c.getString(c.getColumnIndex(SONGS_LINK)));
			item.setArtist(c.getString(c.getColumnIndex(SONGS_ARTIST)));
			item.setTrack(c.getString(c.getColumnIndex(SONGS_TITLE)));
			item.setYoutube(c.getString(c.getColumnIndex(SONGS_LINK)));
			item.setImage_lrg(c.getString(c.getColumnIndex(SONGS_COVER_LARGE)));
			item.setImage_med(c.getString(c.getColumnIndex(SONGS_COVER_MED)));
			songs.add(item);
		} while(c.moveToNext());
		Collections.reverse(songs);
		return songs;
	}
	
	public long createPlaylist(String playlist) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(PLAYLISTS_NAME, playlist);
		long result = db.insert(TABLE_PLAYLISTS, null, values);
		return result;
	}
	
	public void deletePlaylist(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_SONGS, KEY_ID + " = ?", new String[]{Integer.toString(id)});
		db.delete(TABLE_PLAYLISTS, KEY_ID + " = ?", new String[]{Integer.toString(id)});

	}
	
	public int deleteSongFromPlaylistByLink(String id, String title) {
		SQLiteDatabase db = this.getWritableDatabase();
		int result = db.delete(TABLE_SONGS, SONGS_TITLE + " = ? AND " + KEY_ID + " = ?", new String[]{title, id});
		return result;
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
		String query = "SELECT * FROM " + TABLE_SONGS + " WHERE " + KEY_ID + " = ? ORDER BY " + SONGS_TIME_ADDED;
		
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
		db.close();
		return (result > 0 ? true : false);
	}
	
	public void closeDatabaseConnection() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.close();
		db = this.getReadableDatabase();
		db.close();
	}
}
