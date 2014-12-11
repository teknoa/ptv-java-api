package de.vrd.ptvapi.model;

public class Direction {
	Integer linedir_id;
	Integer direction_id;
	String direction_name;
	
	Line line;
	
	public Integer getLinedir_id() {
		return linedir_id;
	}
	public void setLinedir_id(Integer linedir_id) {
		this.linedir_id = linedir_id;
	}
	public Integer getDirection_id() {
		return direction_id;
	}
	public void setDirection_id(Integer direction_id) {
		this.direction_id = direction_id;
	}
	public String getDirection_name() {
		return direction_name;
	}
	public void setDirection_name(String direction_name) {
		this.direction_name = direction_name;
	}
	public Line getLine() {
		return line;
	}
	public void setLine(Line line) {
		this.line = line;
	}
	
	public String toString() {
		return "Direction id["+direction_id+ "] Name:"+direction_name+ " linedirectionId: "+linedir_id;
	}
	
	public String toStringRecursive() {
		return toString()
				+"\n\tLine Details:\n" + line.toStringRecursive();
	}
}
