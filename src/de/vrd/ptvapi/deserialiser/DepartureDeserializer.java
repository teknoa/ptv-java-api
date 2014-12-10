package de.vrd.ptvapi.deserialiser;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import de.vrd.ptvapi.model.Departure;
import de.vrd.ptvapi.model.Platform;
import de.vrd.ptvapi.model.Run;

public class DepartureDeserializer implements JsonDeserializer<Departure>{

	@Override
	public Departure deserialize(JsonElement element, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {

		Departure departure = new Departure();
		JsonObject curDeparture = element.getAsJsonObject();
		
		JsonObject curPlatform = curDeparture.get("platform").getAsJsonObject();
		Platform platform = context.deserialize(curPlatform, Platform.class);
		departure.setPlatform(platform);
		
		JsonObject curRun = curDeparture.getAsJsonObject().get("run").getAsJsonObject();
		Run run = context.deserialize(curRun, Run.class);
		departure.setRun(run);
		
		departure.setFlags(curDeparture.get("flags").getAsString());
		//not yet implemented by PTV
		JsonElement jsonElement = curDeparture.get("time_realtime_utc");
		if(! (jsonElement instanceof JsonNull))
			departure.setTime_realtime_utc(curDeparture.get("time_realtime_utc").getAsString());
		departure.setTime_table_utc(curDeparture.get("time_timetable_utc").getAsString());
		
		return departure;
	}

	
}
