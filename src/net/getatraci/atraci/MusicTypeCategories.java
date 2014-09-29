package net.getatraci.atraci;

import java.util.ArrayList;

public class MusicTypeCategories {
	
	private ArrayList<MusicItem> artists = new ArrayList<MusicItem>();
	private ArrayList<MusicItem> albums = new ArrayList<MusicItem>();
	private ArrayList<MusicItem> songs = new ArrayList<MusicItem>();
	
	public MusicItem getArtist(int i) {
		return artists.get(i);
	}
	
	public MusicItem getAlbum(int i) {
		return albums.get(i);
	}
	
	public MusicItem getSong(int i) {
		return songs.get(i);
	}
	
	public void addArtist(MusicItem i) {
		artists.add(i);
	}

	public void addAlbum(MusicItem i) {
		albums.add(i);
	}
	
	public void addSong(MusicItem i) {
		songs.add(i);
	}
	
	public int getArtistCount() {
		return artists.size();
	}
	
	public int getAlbumCount() {
		return albums.size();
	}
	
	public int getSongCount() {
		return songs.size();
	}
	
	public int getTotalSize() {
		return artists.size() + albums.size() + songs.size();
	}
	
	
	@Override
	public String toString() {
		return artists.toString() + "\n" + albums.toString() + "\n" + songs.toString();
	}

}
