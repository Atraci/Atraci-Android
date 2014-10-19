package net.getatraci.atraci.data;

public class Songs {
	
	private int id;
	private String link;
	private String artist; 
	private String title;
	private String cover_large;
	private String cover_med;
	
	public Songs(int id) {
		this.id = id;
	}
	
	public Songs(int id, String link, String artist, String title, String cover_large, String cover_med) {
		this.id = id;
		this.link = link;
		this.artist = artist;
		this.title = title;
		this.cover_large = cover_large;
		this.cover_med = cover_med;
	}
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCover_large() {
		return cover_large;
	}
	public void setCover_large(String cover_large) {
		this.cover_large = cover_large;
	}
	public String getCover_med() {
		return cover_med;
	}
	public void setCover_med(String cover_med) {
		this.cover_med = cover_med;
	}
	

}
