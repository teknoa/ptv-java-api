package de.vrd.ptvapi.model;

public class Mode {
	public static Integer getMode(String transport_type) {
		switch(transport_type){
		case "train":
		return 0;
		case "tram":
		return 1;
		case "bus":
		return 2;
		case "vline":
		return 3;
		case "nightrider":
		return 4;
		}
		return null;
	}
}
