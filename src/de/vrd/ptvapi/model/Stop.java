package de.vrd.ptvapi.model;

public class Stop extends Location implements Result{
	
	String departureTime;
	
	String transport_type;
	Integer stop_id;
	
	public String getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}

	public String getTransport_type() {
		return transport_type;
	}
	public void setTransport_type(String transport_type) {
		this.transport_type = transport_type;
	}
	public Integer getStop_id() {
		return stop_id;
	}
	public void setStop_id(Integer stop_id) {
		this.stop_id = stop_id;
	}

	public String toString() {
		return "Stop["+ stop_id + "], "+location_name+"{"+transport_type + "}, departure:"+departureTime+", suburb:'"+suburb+ "', coord[lat("+lat+ ") lon(" +lon+ ")]";
	}
}
