package de.vrd.ptvapi.test;

import java.util.Date;
import java.util.List;

import de.vrd.ptvapi.api.HighLevelAPI;
import de.vrd.ptvapi.model.Departure;
import de.vrd.ptvapi.model.Result;
import de.vrd.ptvapi.model.Stop;

public class TestHighLevelAPI {
	HighLevelAPI api;
	public TestHighLevelAPI(HighLevelAPI api) {
		this.api = api;
	}
	
	public void performTests() {
		testStatus();
		testGetStopsForSpecificDeparture();
	}
	
	public void testStatus() {
		System.out.println(api.getStatus());
	}
	public void testGetStopsForSpecificDeparture() {
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
			
			Departure departureWithStops = api.getSpecificDepartureWithStops(listDepartures.get(0));
			
			System.out.println("printing out specific departure (listindex 0) for query '"+search+"'");
			System.out.println(departureWithStops.toStringRecursive());
			
			
		}
	}

}
