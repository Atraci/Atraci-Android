package net.getatraci.atraci.data;

public class Playlists {
	
	private int id;
	private String name;
	
	
	public Playlists() {
		
	}
	
	/**
	 * 
	 * @param name the playlist name
	 */
	public Playlists(String name) {
		this.setName(name);
	}
	
	/**
	 * 
	 * @param id the unique id column
	 * @param name the playlist name
	 */
	public Playlists(int id, String name) {
		this.setId(id);
		this.setName(name);
	}
	
	
	
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
