package net.getatraci.atraci;

import android.os.Parcel;
import android.os.Parcelable;

public class MusicItem implements Parcelable {

	private double weight;
	private String image;
	private String artist = "";
	private String album = "";
	private String track = "";
	private String youtube = "";
	private int type = -1;
	
	public static final int ARTIST = 0;
	public static final int ALBUM = 1;
	public static final int TRACK = 2;
	public static final int UNDEFINED = -1;
	
	
	public MusicItem(){
		
	}
	
	public MusicItem(int w, String i, String item, int type) {
		weight = w;
		image = i;
		
		switch(type){
		case 0:
			artist = item;
			break;
		case 1:
			album = item;
			break;
		case 2:
			track = item;
			break;
		}
	}
	
	
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public String getTrack() {
		return track;
	}
	public void setTrack(String track) {
		this.track = track;
	}


	public int getType() {
		return type;
	}


	public void setType() {
		if(track!="")
			type = TRACK;
		else if(album != "")
			type = ALBUM;
		else if(artist != "")
			type = ARTIST;
		else
			type = UNDEFINED;
			
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeDouble(weight);
		arg0.writeString(image);
		arg0.writeString(artist);
		arg0.writeString(album);
		arg0.writeString(track);
		arg0.writeInt(type);
		
	}

	public String getYoutube() {
		return youtube;
	}

	public void setYoutube(String youtube) {
		this.youtube = youtube;
	}
	
}
