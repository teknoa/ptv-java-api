package de.vrd.ptvapi.test;

import java.util.Date;
import java.util.List;

import de.vrd.ptvapi.connector.PTVAPI;
import de.vrd.ptvapi.model.Departure;
import de.vrd.ptvapi.model.Line;
import de.vrd.ptvapi.model.Result;
import de.vrd.ptvapi.model.Stop;

public class TestSearch {
	PTVAPI api;
	public TestSearch(PTVAPI api) {
		this.api = api;
	}
	
	public void performTests() {
		testStatus();
		testStopsNearby();
//		testStation();
//		testBroadNextDepartures();
//		testSpecificNextDepartures();
	}
	
	public void testStatus() {
		System.out.println(api.getStatus());
	}
	
	public void testStopsNearby() {
		List<Result> stationInfo = api.getStopsNearby(-38.02489, 145.501724);
		
		System.out.println("found " + stationInfo.size() + " stations nearby");
		for(Result res : stationInfo){
			Stop stop = (Stop) res;
			System.out.println(stop.getSuburb() + " " + stop.getLocation_name());
		}
	}
	
	
	public void testStation() {
		List<Result> stationInfo = api.getStationInfo("Pakenham");
		for(Result curres : stationInfo) {
			if(curres instanceof Line){
				List<Stop> stops = api.getStopsOnLine((Line)curres);
				System.out.println(((Line) curres).getTransport_type() + " with " +stops.size() + " stops on line " + ((Line)curres).getLine_name());
			}
		}
	}
	
	public void testBroadNextDepartures() {
		List<Result> stationInfo = api.getStationInfo("Pakenham");
		Stop stop = null;
		for(Result curres : stationInfo) {
			if(curres instanceof Stop) {
				stop = (Stop) curres;
				break;
			}
		}
		List<Departure> listDepartures = null;
		if(stop != null)
			listDepartures = api.getBroadNextDepartures(stop, 10);
		System.out.println(listDepartures.size());
	}
	
	public void testSpecificNextDepartures() {
		List<Result> stationInfo = api.getStationInfo("Pakenham");
		Stop stop = null;
		for(Result curres : stationInfo) {
			if(curres instanceof Stop) {
				stop = (Stop) curres;
				break;
			}
		}
		List<Departure> listDepartures = null;
		if(stop != null)
			listDepartures = api.getBroadNextDepartures(stop, 10);
		
		if( ! listDepartures.isEmpty()) {
			List<Departure> specificNextDepartures = api.getSpecificNextDepartures(listDepartures.get(0), 10, (Date)null);
			System.out.println(specificNextDepartures.size());
		}
	}
}
