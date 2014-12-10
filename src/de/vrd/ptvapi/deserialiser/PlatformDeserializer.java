package de.vrd.ptvapi.deserialiser;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import de.vrd.ptvapi.model.Direction;
import de.vrd.ptvapi.model.Platform;
import de.vrd.ptvapi.model.Stop;

public class PlatformDeserializer implements JsonDeserializer<Platform>{

	@Override
	public Platform deserialize(JsonElement element, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {

		JsonObject curPlatform = element.getAsJsonObject();
		Platform platform = new Platform();
		platform.setRealtime_id(curPlatform.get("realtime_id").getAsString());
		
		Stop stop = (Stop)context.deserialize(curPlatform.get("stop"), Stop.class);
		platform.setStop(stop);
		
		Direction direction = (Direction) context.deserialize(curPlatform.get("direction"), Direction.class);
		platform.setDirection(direction);
		
		return platform;
	}

	
}
