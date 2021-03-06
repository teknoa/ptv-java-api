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
		//System.out.println(queryurl);
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
