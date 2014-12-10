package de.vrd.ptvapi.test;

import java.util.List;

import de.vrd.ptvapi.connector.PTVAPI;
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
		testStation();
	}
	
	public void testStatus() {
		System.out.println(api.getStatus());
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
}
