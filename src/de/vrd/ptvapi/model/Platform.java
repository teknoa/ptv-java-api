package de.vrd.ptvapi.model;

public class Platform {
	String realtime_id;
	
	Stop stop;

	Direction direction;
	
	public String getRealtime_id() {
		return realtime_id;
	}

	public void setRealtime_id(String realtime_id) {
		this.realtime_id = realtime_id;
	}

	public Stop getStop() {
		return stop;
	}

	public void setStop(Stop stop) {
		this.stop = stop;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	
	
	
}
