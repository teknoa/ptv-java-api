package de.vrd.ptvapi.test;

import java.util.Date;
import java.util.List;

import de.vrd.ptvapi.connector.PTVAPI;
import de.vrd.ptvapi.model.Departure;
import de.vrd.ptvapi.model.Line;
import de.vrd.ptvapi.model.Result;
import de.vrd.ptvapi.model.Stop;

public class TestPTVAPI {
	PTVAPI api;
	public TestPTVAPI(PTVAPI api) {
		this.api = api;
	}
	
	public void performTests() {
		testStatus();
//		testStopsNearby();
//		testStation();
//		testBroadNextDepartures();
//		testSpecificNextDepartures();
		testJourney();
//		testStoppingPatternForDeparture();
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
		System.out.println("printing out broad departures for query 'Pakenham'");
		for(Departure dep : listDepartures)
			System.out.println(dep.toStringRecursive());
	}
	
	public void testSpecificNextDepartures() {
		String search = "Clayton Station";
		List<Result> stationInfo = api.getStationInfo(search);
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
			
			System.out.println("printing out specific departures for query '"+search+"'");
			
			for(Departure dep : specificNextDepartures)
				System.out.println(dep.toStringRecursive());
			
			
		}
	}

	public void testStoppingPatternForDeparture() {
		String search = "Clayton Station";
		List<Result> stationInfo = api.getStationInfo(search);
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
			
			System.out.println("printing out specific departure (listindex 0) for query '"+search+"'");
			System.out.println(listDepartures.get(0).toStringRecursive());
			
			List<Departure> stoppingPattern = api.getStoppingPattern(listDepartures.get(0), (Date)null);
			
			System.out.println("\n\n\n================================\n\n\n");
			System.out.println("printing out stopping pattern for (listindex 0) for query '"+search+"'");
			for(Departure dep : stoppingPattern)
				System.out.println(dep.toStringRecursive());
			
			
		}
	}

	public void testJourney() {
		api.getJourneys("Clayton", "Flindern");
	}
}
