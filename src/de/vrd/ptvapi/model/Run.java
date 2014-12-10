package de.vrd.ptvapi.model;

public class Run {

	String transport_type;
	Integer run_id;
	Integer num_skipped;
	Integer destination_id;
	String destination_name;
	public String getTransport_type() {
		return transport_type;
	}
	public void setTransport_type(String transport_type) {
		this.transport_type = transport_type;
	}
	public Integer getRun_id() {
		return run_id;
	}
	public void setRun_id(Integer run_id) {
		this.run_id = run_id;
	}
	public Integer getNum_skipped() {
		return num_skipped;
	}
	public void setNum_skipped(Integer num_skipped) {
		this.num_skipped = num_skipped;
	}
	public Integer getDestination_id() {
		return destination_id;
	}
	public void setDestination_id(Integer destination_id) {
		this.destination_id = destination_id;
	}
	public String getDestination_name() {
		return destination_name;
	}
	public void setDestination_name(String destination_name) {
		this.destination_name = destination_name;
	}
	
	
}
