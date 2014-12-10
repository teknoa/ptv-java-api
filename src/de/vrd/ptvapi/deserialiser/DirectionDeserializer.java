package de.vrd.ptvapi.deserialiser;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import de.vrd.ptvapi.model.Direction;
import de.vrd.ptvapi.model.Line;

public class DirectionDeserializer implements JsonDeserializer<Direction>{

	@Override
	public Direction deserialize(JsonElement element, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {

		JsonObject curDirection = element.getAsJsonObject();
		
		Direction direction = new Direction();
		direction.setLinedir_id(curDirection.get("linedir_id").getAsInt());
		direction.setDirection_id(curDirection.get("direction_id").getAsInt());
		direction.setDirection_name(curDirection.get("direction_name").getAsString());
		
		Line line = context.deserialize(curDirection.get("line"), Line.class);
		direction.setLine(line);
		
		return direction;
	}

	
}
