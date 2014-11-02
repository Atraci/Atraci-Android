package net.getatraci.atraci.data;

public class Top100Genres {
	
	public static final String ALL = "0";
	public static final String BLUES = "2";
	public static final String CHILDRENS = "4";
	public static final String CLASSICAL = "5";
	public static final String COUNTRY = "6";
	public static final String ELECTRONIC = "7";
	public static final String HOLIDAY = "8";
	public static final String JAZZ = "11";
	public static final String LATINO = "12";
	public static final String POP = "14";
	public static final String RnB = "15";
	public static final String SOUNDTRACK = "16";
	public static final String DANCE = "17";
	public static final String HIPHOP = "18";
	public static final String WORLD = "19";
	public static final String ALTERNATIVE = "20";
	public static final String ROCK = "21";
	public static final String CHRISTIAN = "22";
	public static final String ANIME = "29";
	public static final String INSTRUMENTAL = "53";


	
	public static String[] allGenres(){
		String[] genres = new String[20];
		genres[0] = ALL;
		genres[1] = BLUES;
		genres[2] = CHILDRENS;
		genres[3] = CLASSICAL;
		genres[4] = COUNTRY;
		genres[5] = ELECTRONIC;
		genres[6] = HOLIDAY;
		genres[7] = JAZZ;
		genres[8] = LATINO;
		genres[9] = POP;
		genres[10] = RnB;
		genres[11] = SOUNDTRACK;
		genres[12] = DANCE;
		genres[13] = HIPHOP;
		genres[14] = WORLD;
		genres[15] = ALTERNATIVE;
		genres[16] = ROCK;
		genres[17] = CHRISTIAN;
		genres[18] = ANIME;
		genres[19] = INSTRUMENTAL;
		return genres;
	}
	
	public static String getGenreNameById(String id){
		String genre = "UNKNOWN";
		switch(id){
		case CLASSICAL:
			genre = "Classical";
			break;
		case COUNTRY:
			genre = "Country";
			break;
		case CHILDRENS:
			genre = "Childrens";
			break;
		case ELECTRONIC:
			genre = "Electronic";
			break;
		case HOLIDAY:
			genre = "Holiday";
			break;
		case JAZZ:
			genre = "Jazz";
			break;
		case LATINO:
			genre = "Latino";
			break;
		case POP:
			genre = "Pop";
			break;
		case ALL:
			genre = "All";
			break;
		case RnB:
			genre = "R&B";
			break;
		case DANCE:
			genre = "Dance";
			break;
		case HIPHOP:
			genre = "Hip-Hop/Rap";
			break;
		case ALTERNATIVE:
			genre = "Alternative";
			break;
		case ROCK:
			genre = "Rock";
			break;
		case CHRISTIAN:
			genre = "Christian";
			break;	
		case INSTRUMENTAL:
			genre = "Instrumental";
			break;
		case ANIME:
			genre = "Anime";
			break;
		case WORLD:
			genre = "World";
			break;
		case SOUNDTRACK:
			genre = "Soundtrack";
			break;
		case BLUES:
			genre = "Blues";
			break;
		}
		return genre;
	}

}
