package de.vrd.ptvapi.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.codec.net.URLCodec;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

import de.vrd.ptvapi.deserialiser.DepartureDeserializer;
import de.vrd.ptvapi.deserialiser.DirectionDeserializer;
import de.vrd.ptvapi.deserialiser.PlatformDeserializer;
import de.vrd.ptvapi.model.Departure;
import de.vrd.ptvapi.model.Direction;
import de.vrd.ptvapi.model.Line;
import de.vrd.ptvapi.model.Platform;
import de.vrd.ptvapi.model.Result;
import de.vrd.ptvapi.model.Stop;

public class PTVAPI {

	private static final String BASEURL = "http://timetableapi.ptv.vic.gov.au";
	private static final String VERSION = "/v2/";
	private static final String API_HEALTHCHECK = "healthcheck";

	String devId;
	String key;

	Crypto crypto;

	URLCodec codec;

	Gson gson;
	
	public PTVAPI(String devId, String key) 
	throws NullPointerException{
		if(devId == null || key == null)
			throw new NullPointerException("PTVAPI constructor requires the developerID and the developer key");
			
		this.devId = devId;
		this.key = key;
		init();
		
		
	}


	void init() {
		crypto = new Crypto(key);
		codec = new URLCodec();
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Platform.class, new PlatformDeserializer());
		builder.registerTypeAdapter(Direction.class, new DirectionDeserializer());		
		builder.registerTypeAdapter(Departure.class, new DepartureDeserializer());
		gson = builder.create();
	}

	


	/* ***************************************
	 * 
	 * 
	 * 
	 * 	API Implementation starts here
	 * 
	 * 
	 * 
	 *************************************** */
	

	public String getStatus() {
		String ret = null;
		List<KeyVal> params = new ArrayList<>();
		params.add(new KeyVal("timestamp", Helper.getCurrentTimeAsUTC(null)));
		ret = executeQueryForJsonResult(getQueryURL(API_HEALTHCHECK, null, params));

		return ret;
	}
	
	
	public List<Result> getStopsNearby(Double latitude, Double longitude) {
		List<Result> resultlist = new ArrayList<Result>();

		String ret = null;
		List<KeyVal> params = new ArrayList<>();
		
		params.add(new KeyVal("nearme", null));
		params.add(new KeyVal("latitude", latitude.toString()));
		params.add(new KeyVal("longitude", longitude.toString()));
		
		String queryurl = getQueryURL(null, params, null);
		ret = executeQueryForJsonResult(queryurl);

		JsonElement resultvalue = null;
		JsonStreamParser parser = new JsonStreamParser(ret);
		while(parser.hasNext()) {
			JsonElement next = parser.next();
			if(next.isJsonObject()) {
				System.err.println("result is no object: "+next.toString());
			}
			if(next.isJsonArray()) {
				JsonArray array = next.getAsJsonArray();
				for(JsonElement elem : array){
					
					JsonObject resultentry = elem.getAsJsonObject();
					resultvalue = resultentry.get("result");
					String type = resultentry.get("type").getAsString();
					if(type.equals("stop")){
						Stop stop = gson.fromJson(resultvalue, Stop.class);
						resultlist.add(stop);
					}

					if(type.equals("line")){
						Line line = gson.fromJson(resultvalue, Line.class);
						resultlist.add(line);
					}
				}
			}
		}
		
		return resultlist;
	}
	
	public List<Result> getStationInfo(String name) {
		List<Result> resultlist = new ArrayList<Result>();
		
		String ret = null;
		List<KeyVal> params = new ArrayList<>();
		params.add(new KeyVal("search", name));
		ret = executeQueryForJsonResult(getQueryURL(null, params, null));
		
		JsonElement resultvalue = null;
		
		JsonStreamParser parser = new JsonStreamParser(ret);
		while(parser.hasNext()) {
			JsonElement next = parser.next();
			if(next.isJsonObject()) {
				System.err.println("result is no object: "+next.toString());
			}
			if(next.isJsonArray()) {
				JsonArray array = next.getAsJsonArray();
				for(JsonElement elem : array){
					
					JsonObject resultentry = elem.getAsJsonObject();
					resultvalue = resultentry.get("result");
					String type = resultentry.get("type").getAsString();
					if(type.equals("stop")){
						Stop stop = gson.fromJson(resultvalue, Stop.class);
						resultlist.add(stop);
					}

					if(type.equals("line")){
						Line line = gson.fromJson(resultvalue, Line.class);
						resultlist.add(line);
					}
				}
			}
		}
		return resultlist; 
	}
	
	public List<Stop> getStopsOnLine(Line line) {
		
		List<Stop> stops = new ArrayList<>();
		
		List<KeyVal> params = new ArrayList<>();
		
		params.add(new KeyVal("mode", Helper.convertTransportTypeToModeId(line.getTransport_type()).toString()));
		params.add(new KeyVal("line", line.getLine_id().toString()));
		
		String queryURLREST = getQueryURL("stops-for-line", params, null);
		String queryresult = executeQueryForJsonResult(queryURLREST);
		JsonStreamParser parser = new JsonStreamParser(queryresult);
		while(parser.hasNext()) {
			JsonElement next = parser.next();
			if(next.isJsonArray()){
				for(JsonElement curelement : next.getAsJsonArray()) {
					Stop stop = gson.fromJson(curelement, Stop.class);
					stops.add(stop);
				}
			}
		}
		
		if( ! stops.isEmpty()) {
			line.setStops(stops);
			return stops;
		} else
			return null;
		
	}
	
	public List<Departure> getStoppingPattern(Departure departure, Date time) {
		return getStoppingPattern(departure, Helper.getCurrentTimeAsUTC(time));
	}


	public List<Departure> getStoppingPattern(Departure departure, String currentTimeAsUTC) {

		List<KeyVal> params = new ArrayList<>();
		params.add(new KeyVal("mode", Helper.convertTransportTypeToModeId(departure.getRun().getTransport_type()).toString()));
		params.add(new KeyVal("run", departure.getRun().getRun_id().toString()));
		params.add(new KeyVal("stop", departure.getPlatform().getStop().getStop_id().toString()));

		List<KeyVal> params2 = new ArrayList<>();
		params2.add(new KeyVal("for_utc", currentTimeAsUTC));

		
		String queryURLREST = getQueryURL("stopping-pattern", params, params2);
		String queryresult = executeQueryForJsonResult(queryURLREST);
		JsonStreamParser parser = new JsonStreamParser(queryresult);
		
		while(parser.hasNext()) {
			JsonElement values = parser.next();
			
			return getDepartures(values);
			
		}
		
		return null;
	}
	
	
	public List<Departure> getBroadNextDepartures(Stop stop, Integer maxResults) {
		
		
		List<KeyVal> params = new ArrayList<>();
		params.add(new KeyVal("mode", Helper.convertTransportTypeToModeId(stop.getTransport_type()).toString()));
		params.add(new KeyVal("stop", stop.getStop_id().toString()));
		params.add(new KeyVal("departures", "by-destination"));
		params.add(new KeyVal("limit", maxResults.toString()));
		
		String queryURLREST = getQueryURL(null, params, null);
		String queryresult = executeQueryForJsonResult(queryURLREST);
		JsonStreamParser parser = new JsonStreamParser(queryresult);
		
		while(parser.hasNext()) {
			JsonElement values = parser.next();
			
			return getDepartures(values);
			
		}
		
		
		return null;
	}

	public List<Departure> getSpecificNextDepartures(Departure departure, Integer maxResults, Date time) {
		return getSpecificNextDepartures(departure, maxResults, Helper.getCurrentTimeAsUTC(time));
	}


	public List<Departure> getSpecificNextDepartures(Departure departure, Integer maxResults, String currentTimeAsUTC) {
		
		
		List<KeyVal> params = new ArrayList<>();
		params.add(new KeyVal("mode", Helper.convertTransportTypeToModeId(departure.getPlatform().getStop().getTransport_type()).toString()));
		params.add(new KeyVal("line", departure.getPlatform().getDirection().getLine().getLine_id().toString()));
		params.add(new KeyVal("stop", departure.getPlatform().getStop().getStop_id().toString()));
		params.add(new KeyVal("directionid", departure.getPlatform().getDirection().getDirection_id().toString()));
		params.add(new KeyVal("departures", "all"));
		params.add(new KeyVal("limit", maxResults.toString()));

		List<KeyVal> params2 = new ArrayList<>();
		params2.add(new KeyVal("for_utc", currentTimeAsUTC));

		
		String queryURLREST = getQueryURL(null, params, params2);
		String queryresult = executeQueryForJsonResult(queryURLREST);
		JsonStreamParser parser = new JsonStreamParser(queryresult);
		
		while(parser.hasNext()) {
			JsonElement values = parser.next();
			
			return getDepartures(values);
			
		}
		
		
		return null;
	}

	public void getJourneys(String origin, String destination) {
		Locale localLocale = Locale.US;
		Object[] arrayOfObject = new Object[4];
	    arrayOfObject[0] = origin;
	    arrayOfObject[1] = destination;
//	    arrayOfObject[2] = getJourneyPrefsAPIString();
	    arrayOfObject[2] = getMetlinkAPIToken();
	    String url = String.format(localLocale, "http://api.ptv.vic.gov.au/v2/journey-planner/o/%s/d/%s?token=%s", arrayOfObject);
	    
	    String executeQueryForJsonResult = executeQueryForJsonResult(url);
	    System.out.println();
	}
	
/*	  
	public  String getJourneyPrefsAPIString()
	{
		Object[] arrayOfObject = new Object[14];
		arrayOfObject[0] = Boolean.valueOf(true);
		arrayOfObject[1] = MyDateTime.getAPIDateTime(paramJourneyPreference.getDepartOrArrivalTime());
		arrayOfObject[2] = paramJourneyPreference.walkingSpeed;
		arrayOfObject[3] = paramJourneyPreference.transferMethod;
		arrayOfObject[4] = paramJourneyPreference.maxTransferTime;
		arrayOfObject[5] = paramJourneyPreference.tripPreference;
		arrayOfObject[6] = Boolean.valueOf(paramJourneyPreference.showAccessibleServices);
		arrayOfObject[7] = Boolean.valueOf(paramJourneyPreference.showAccessibleStops);
		arrayOfObject[8] = Boolean.valueOf(paramJourneyPreference.preferredTransport.contains(PreferredTransport.Trains));
		arrayOfObject[9] = Boolean.valueOf(paramJourneyPreference.preferredTransport.contains(PreferredTransport.Trams));
		arrayOfObject[10] = Boolean.valueOf(paramJourneyPreference.preferredTransport.contains(PreferredTransport.Buses));
		arrayOfObject[11] = Boolean.valueOf(paramJourneyPreference.preferredTransport.contains(PreferredTransport.RegionalTrains));
		arrayOfObject[12] = Boolean.valueOf(paramJourneyPreference.preferredTransport.contains(PreferredTransport.RegionalCoaches));
		arrayOfObject[13] = Boolean.valueOf(paramJourneyPreference.preferredTransport.contains(PreferredTransport.Skybus));
		return String.format("departFrom=%b&time_utc=%s&transferSpeed=%s&transferMethod=%s&transferMaxTime=%s&routeType=%s&wheelchair=%b&noSolidStairs=%b&inclTrain=%s&inclTram=%s&inclBus=%s&inclVline=%s&inclRegCoach=%s&inclSkybus=%s", arrayOfObject);
	}	
*/	
	  public  String getAPITokenTimeMessage()
	  {
	    return formatTimeUTC("yyyyMMddHH", System.currentTimeMillis());
	  }
	  public  String getMetlinkAPIToken()
	  {
	    return crypto.hmacSha1(getAPITokenTimeMessage(), "931df831-d4ee-11e3-89f1-00fffdfe3ac2");
	  }
	
	  private  String formatTimeUTC(String paramString, long paramLong)
	  {
	    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(paramString, Locale.ENGLISH);
	    TimeZone localTimeZone = TimeZone.getTimeZone("UTC");
	    Date localDate = new Date(paramLong);
	    localSimpleDateFormat.setTimeZone(localTimeZone);
	    return localSimpleDateFormat.format(localDate);
	  }
	
	/*
	 * 
	 * 	Private helper methods
	 * 
	 * 
	 */
	private String getQueryURL(String apicall, List<KeyVal> paramsURL, List<KeyVal> paramsParameters) {
		StringBuffer paramstring = new StringBuffer();
		if(paramsURL != null) {
			Iterator<KeyVal> iterator = paramsURL.iterator();
			while(iterator.hasNext()){
				KeyVal keyval = iterator.next();
				paramstring.append(keyval.key);
				paramstring.append("/");
				
				if(keyval.value != null) {
					String val;
					try {
						val = codec.encode(keyval.value, "UTF-8").replace("+","%20");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						val = keyval.value;
					}
					paramstring.append(val);
					if(iterator.hasNext())
						paramstring.append("/");
				}
			}
		} 
		if(apicall != null){
			if(paramsURL != null)
				paramstring.append("/");
			paramstring.append(apicall);
		}
		
		paramstring.append("?devid="+devId);
		
		if(paramsParameters != null)
			for(KeyVal keyval: paramsParameters){
				paramstring.append("&");
				paramstring.append(keyval.key);
				paramstring.append("=");
				String val = keyval.value;
				/*
				try {
					val = codec.encode(keyval.value, "UTF-8").replace("+","%20");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					val = keyval.value;
				}
				*/
				paramstring.append(val);
			}
		

		String queryurl = VERSION + paramstring.toString();
		String signature = crypto.hmacSha1(queryurl, null);
		String ret = BASEURL + queryurl + "&signature=" + signature;
		return ret;
	}
	
	private static String executeQueryForJsonResult(String queryurl) {
		String ret = null;
		System.out.println(queryurl);
		try {
			URL url = new URL(queryurl);
			URLConnection openConnection = url.openConnection();
			InputStream inputStream = openConnection.getInputStream();

			StringBuffer strbuf = new StringBuffer();

			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while((line = reader.readLine()) != null) {
				strbuf.append(line);
			}
			ret = strbuf.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	private List<Departure> getDepartures(JsonElement values) {
		List<Departure> listDepartures = new ArrayList<Departure>();
		JsonArray asJsonArray = values.getAsJsonObject().get("values").getAsJsonArray();
		
		for(JsonElement curVal : asJsonArray){
			
			Departure departure = gson.fromJson(curVal, Departure.class);
			
			listDepartures.add(departure);
		}
		return listDepartures;
	}
	
}
