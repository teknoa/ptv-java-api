package de.vrd.ptvapi.model;

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
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.codec.net.URLCodec;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

public class PTVAPI {

	private static final String BASEURL = "http://timetableapi.ptv.vic.gov.au";
	private static final String VERSION = "/v2/";
	private static final String API_HEALTHCHECK = "healthcheck";

	String devId;
	String key;

	Crypto crypto;

	URLCodec codec;

	public PTVAPI(String devId, String key) {
		this.devId = devId;
		this.key = key;
		init();
	}


	void init() {
		crypto = new Crypto(key);
		codec = new URLCodec();
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
	private String getQueryURLREST(List<KeyVal> params) {
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
	private String getQueryResult(String queryurl) {
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

	public String getStation(String name) {
		String ret = null;
		List<KeyVal> params = new ArrayList<>();
		params.add(new KeyVal("search", name));
		ret = getQueryResult(getQueryURLREST(params));

		return ret;
	}
	static void printJSONObject(JsonObject job) {
		Set<Entry<String,JsonElement>> entrySet = job.entrySet();
		for(Entry<String,JsonElement> elem : entrySet)
			System.out.println(elem.getKey()+" "+elem.getValue());
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PTVAPI api = new PTVAPI("1000317", "7e39cff6-7aad-11e4-a34a-0665401b7368");
//		System.out.println(api.getStatus());
		JsonStreamParser parser = new JsonStreamParser(api.getStation("Pakenham"));
		while(parser.hasNext()) {
			JsonElement next = parser.next();
			if(next.isJsonObject()) {
				printJSONObject(next.getAsJsonObject());
			}
			if(next.isJsonArray()) {
				JsonArray array = next.getAsJsonArray();
				for(JsonElement elem : array)
					printJSONObject(elem.getAsJsonObject());
			}
		}
//		System.out.println(api.getStation("Alamein"));
	}

}
