package pl.itiner.models;

import java.lang.reflect.Type;

import android.location.Location;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class DepartedDeserializer implements JsonDeserializer<Departed> {

	private static final String GRAVE_LOCATION_PROVIDER = "GRAVE_LOCATION_PROVIDER";
	private static final String COORDINATES_FIELD = "coordinates";
	private static final String GEOMETRY_FIELD = "geometry";
	private static final String ID_FIELD = "id";
	private static final String PROPERTIES_FIELD = "properties";

	@Override
	public Departed deserialize(JsonElement jsonElement, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		JsonObject j = jsonElement.getAsJsonObject();
		j.getAsJsonObject(PROPERTIES_FIELD);
		DepartedProperties properties = context.deserialize(j,
				DepartedProperties.class);
		String id = j.get(ID_FIELD).getAsString();
		double lat = j.getAsJsonObject(GEOMETRY_FIELD)
				.getAsJsonArray(COORDINATES_FIELD).get(0).getAsDouble();
		double lon = j.getAsJsonObject(GEOMETRY_FIELD)
				.getAsJsonArray(COORDINATES_FIELD).get(1).getAsDouble();
		Location location = new Location(GRAVE_LOCATION_PROVIDER);
		location.setLatitude(lat);
		location.setLongitude(lon);
		return new Departed(properties, id, location);
	}

}
