package net.getatraci.atraci.data;

import android.os.Parcel;
import android.os.Parcelable;

public class MusicItem implements Parcelable {

	private String image_med;
	private String image_lrg;
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
	
	public MusicItem(Parcel p){
		image_med = p.readString();
		image_lrg = p.readString();
		youtube = p.readString();
		artist = p.readString();
		album = p.readString();
		track = p.readString();
		type = p.readInt();
	}
	
	public MusicItem(String i, String item, int type) {
		image_med = i;
		
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
	
	
	public String getImage_med() {
		return image_med;
	}
	public void setImage_med(String image) {
		this.image_med = image;
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
		if(!"".equals(track))
			type = TRACK;
		else if(!"".equals(album))
			type = ALBUM;
		else if(!"".equals(artist))
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
		arg0.writeString(image_med);
		arg0.writeString(image_lrg);
		arg0.writeString(youtube);
		arg0.writeString(artist);
		arg0.writeString(album);
		arg0.writeString(track);
		arg0.writeInt(type);
		
	}
	
	public static final Parcelable.Creator<MusicItem> CREATOR = new Parcelable.Creator<MusicItem>() {
	    public MusicItem createFromParcel(Parcel in) {
	        return new MusicItem(in);
	    }

	    public MusicItem[] newArray(int size) {
	        return new MusicItem[size];
	    }
	};

	public String getYoutube() {
		return youtube;
	}

	public void setYoutube(String youtube) {
		this.youtube = youtube;
	}
	
	@Override
	public String toString() {
		return track + " - " + artist;
	}

	public String getImage_lrg() {
		return image_lrg;
	}

	public void setImage_lrg(String image_lrg) {
		this.image_lrg = image_lrg;
	}
	
}
