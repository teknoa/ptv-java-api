package de.vrd.ptvapi.model;

public class Departure {

	Platform platform;
	
	Run run;
	
	String time_table_utc;
	String time_realtime_utc;
	String flags;
	public Platform getPlatform() {
		return platform;
	}
	public void setPlatform(Platform platform) {
		this.platform = platform;
	}
	public Run getRun() {
		return run;
	}
	public void setRun(Run run) {
		this.run = run;
	}
	public String getTime_table_utc() {
		return time_table_utc;
	}
	public void setTime_table_utc(String time_table_utc) {
		this.time_table_utc = time_table_utc;
	}
	public String getTime_realtime_utc() {
		return time_realtime_utc;
	}
	public void setTime_realtime_utc(String time_realtime_utc) {
		this.time_realtime_utc = time_realtime_utc;
	}
	public String getFlags() {
		return flags;
	}
	public void setFlags(String flags) {
		this.flags = flags;
	}
	
	
}
