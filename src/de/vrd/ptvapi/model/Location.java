package de.vrd.ptvapi.model;

public class Location {
	String suburb;
	String location_name;
	Double lat;
	Double lon;
	Double distance;
	String transport_type;
	Integer stop_id;
	String outlet_type;
	String business_name;
	public String getSuburb() {
		return suburb;
	}
	public void setSuburb(String suburb) {
		this.suburb = suburb;
	}
	public String getLocation_name() {
		return location_name;
	}
	public void setLocation_name(String location_name) {
		this.location_name = location_name;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLon() {
		return lon;
	}
	public void setLon(Double lon) {
		this.lon = lon;
	}
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
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
	public String getOutlet_type() {
		return outlet_type;
	}
	public void setOutlet_type(String outlet_type) {
		this.outlet_type = outlet_type;
	}
	public String getBusiness_name() {
		return business_name;
	}
	public void setBusiness_name(String business_name) {
		this.business_name = business_name;
	}
	
	
}
