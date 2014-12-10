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
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.net.URLCodec;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

import de.vrd.ptvapi.model.Line;
import de.vrd.ptvapi.model.Mode;
import de.vrd.ptvapi.model.Result;
import de.vrd.ptvapi.model.Stop;
import de.vrd.ptvapi.model.StoppingPattern;

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
		gson = new GsonBuilder().create();
	}

	private String getQueryURLParams(String apicall, List<KeyVal> params) {
		StringBuffer paramstring = new StringBuffer();
		paramstring.append("?devid="+devId);
		if(params != null)
			for(KeyVal keyval: params){
				paramstring.append("&");
				paramstring.append(keyval.key);
				paramstring.append("=");
				String val;
				try {
					val = codec.encode(keyval.value, "UTF-8").replace("+","%20");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					val = "";
				}
				paramstring.append(val);
			}

		String queryurl = VERSION + apicall + paramstring.toString();
		String signature = crypto.hmacSha1(queryurl, null);
		String ret = BASEURL + queryurl + "&signature=" + signature;
		return ret;
	}
	private String getQueryURLREST(String apicall, List<KeyVal> params) {
		StringBuffer paramstring = new StringBuffer();
		if(params != null)
			for(KeyVal keyval: params){
				paramstring.append(keyval.key);
				paramstring.append("/");
				String val;
				try {
					val = codec.encode(keyval.value, "UTF-8").replace("+","%20");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					val = "";
				}
				paramstring.append(val);
			}
		paramstring.append("?devid="+devId);

		
		String queryurl = VERSION + paramstring.toString();
		String signature = crypto.hmacSha1(queryurl, null);
		String ret = BASEURL + queryurl + "&signature=" + signature;
		return ret;
	}
	
	
	private String getQueryURL(String apicall, List<KeyVal> paramsURL, List<KeyVal> paramsParameters) {
		StringBuffer paramstring = new StringBuffer();
		if(paramsURL != null) {
			Iterator<KeyVal> iterator = paramsURL.iterator();
			while(iterator.hasNext()){
				KeyVal keyval = iterator.next();
				paramstring.append(keyval.key);
				paramstring.append("/");
				String val;
				try {
					val = codec.encode(keyval.value, "UTF-8").replace("+","%20");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					val = keyval.value;
				}
				paramstring.append(val);
				if(iterator.hasNext())
					paramstring.append("/");
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
				String val;
				try {
					val = codec.encode(keyval.value, "UTF-8").replace("+","%20");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					val = keyval.value;
				}
				paramstring.append(val);
			}
		

		String queryurl = VERSION + paramstring.toString();
		String signature = crypto.hmacSha1(queryurl, null);
		String ret = BASEURL + queryurl + "&signature=" + signature;
		return ret;
	}
	
	private static String getQueryResult(String queryurl) {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	public String getStatus() {
		String ret = null;
		List<KeyVal> params = new ArrayList<>();
		params.add(new KeyVal("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())));
		ret = getQueryResult(getQueryURLParams(API_HEALTHCHECK, params));

		return ret;
	}

	
	public List<Result> getStationInfo(String name) {
		List<Result> resultlist = new ArrayList<Result>();
		
		String ret = null;
		List<KeyVal> params = new ArrayList<>();
		params.add(new KeyVal("search", name));
		ret = getQueryResult(getQueryURL(null, params, null));
		
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
		
		params.add(new KeyVal("mode", Mode.getMode(line.getTransport_type()).toString()));
		params.add(new KeyVal("line", line.getLine_id().toString()));
		
		String queryURLREST = getQueryURL("stops-for-line", params, null);
		String queryresult = getQueryResult(queryURLREST);
		JsonStreamParser parser = new JsonStreamParser(queryresult);
		while(parser.hasNext()) {
			JsonElement next = parser.next();
			if(next.isJsonArray()){
				for(JsonElement curelement : next.getAsJsonArray()) {
					JsonObject curObj = (JsonObject)curelement.getAsJsonObject();
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
	
	public StoppingPattern getStoppingPattern(String transport_type, Integer runId, Integer stopId, Date time) {
		return getStoppingPattern(transport_type, runId, stopId, Helper.getCurrentTimeAsUTC(time));
	}


	private StoppingPattern getStoppingPattern(String transport_type, Integer runId, Integer stopId,
			String currentTimeAsUTC) {

		List<KeyVal> params = new ArrayList<>();
		params.add(new KeyVal("mode", Mode.getMode(transport_type).toString()));
		params.add(new KeyVal("run", runId.toString()));
		params.add(new KeyVal("stop", stopId.toString()));

		List<KeyVal> params2 = new ArrayList<>();
		params2.add(new KeyVal("for_utc", currentTimeAsUTC));

		
		String queryURLREST = getQueryURL("stopping-pattern", params, params2);
		String queryresult = getQueryResult(queryURLREST);
		JsonStreamParser parser = new JsonStreamParser(queryresult);
		return null;
	}
}
