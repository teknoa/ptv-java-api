package de.vrd.ptvapi.api;

import java.util.ArrayList;
import java.util.List;

import de.vrd.ptvapi.connector.PTVAPI;
import de.vrd.ptvapi.model.Departure;
import de.vrd.ptvapi.model.Stop;

public class HighLevelAPI extends PTVAPI {


	public HighLevelAPI(String devId, String key) 
	throws NullPointerException{
		super(devId, key);
		
	}

	
	public Departure getSpecificDepartureWithStops(Departure departure) {
		List<Departure> stoppingPattern = getStoppingPattern(departure, departure.getTime_table_utc());
		
		List<Stop> listStops = new ArrayList<>();
		for(Departure depStopPat : stoppingPattern) {
			Stop stop = depStopPat.getPlatform().getStop();
			stop.setDepartureTime(depStopPat.getTime_table_utc());
			listStops.add(stop);
		}
		departure.getPlatform().getDirection().getLine().setStops(listStops);
		
		return departure;
	}
}
