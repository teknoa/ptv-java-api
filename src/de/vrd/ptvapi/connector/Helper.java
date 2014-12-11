package de.vrd.ptvapi.connector;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Helper {

	private static final String PATTERN = "yyyy-MM-dd'T'hh:mm:ss'Z'";

	public static String getCurrentTimeAsUTC(Date date) {
		if(date == null)
			date = new Date();
		
		SimpleDateFormat formatter = new SimpleDateFormat(PATTERN);
		formatter.setTimeZone(TimeZone.getTimeZone("EDT"));
		return formatter.format(date);
		
	}
	
	public static Integer convertTransportTypeToModeId(String transport_type) {
		switch(transport_type){
		case "train":
		return 0;
		case "tram":
		return 1;
		case "bus":
		return 2;
		case "vline":
		return 3;
		case "nightrider":
		return 4;
		}
		return null;
	}

	public static void main(String[] args) {
		System.out.println(getCurrentTimeAsUTC(null));
	}
}
