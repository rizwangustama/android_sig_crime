package com.rizwan.sigcrime.utilities;

import com.rizwan.sigcrime.dao.Users;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilClass {

	public static String serverUri ="http://rizwangustama.my.id/sig/";
//	public static String serverUri ="http://192.168.1.5/sig/";

	// TODO: PREFERENCE_FILE_KEY
	public static String prefKeyUserID ="Sig-user-id";
	public static String urlUpdateProfileWithImage = "Assets/image/uploaderprofile.php";
	public static String NewsCreatID="?";

	public static String currentLatitude="0";
	public static String currentLongitude="0";

	public static String stringToDate(String aDate, String aFormat, String newFormat) {

		if(aDate==null) return null;
		ParsePosition pos = new ParsePosition(0);
		SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
		Date stringDate = simpledateformat.parse(aDate, pos);
		simpledateformat = new SimpleDateFormat(newFormat);
		String xDate = simpledateformat.format(stringDate);

		return xDate;
	}

	public static Date ConvertToDate(String aDate, String newFormat) {

		if(aDate==null) return null;
		ParsePosition pos = new ParsePosition(0);
		SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd");
		Date stringDate = simpledateformat.parse(aDate, pos);

		return stringDate;
	}

	public static String kmlKecamatan = "?";
	public static String kecamatanID ="?";
	public static String userID = "?";
	public  static Users userLogin = new Users();
	public static String kecLat = "0";
	public  static String kecLang ="0";
}
