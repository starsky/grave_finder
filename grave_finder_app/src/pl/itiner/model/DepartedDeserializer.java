package pl.itiner.model;

import java.lang.reflect.Type;

import android.location.Location;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

class DepartedDeserializer implements JsonDeserializer<DepartedImpl> {
	
	private static final int LON = 0;
	private static final int LAT = 1;
	private static final String COORDINATES_FIELD = "coordinates";
	private static final String GEOMETRY_FIELD = "geometry";
	private static final String ID_FIELD = "id";
	private static final String PROPERTIES_FIELD = "properties";

	@Override
	public DepartedImpl deserialize(JsonElement jsonElement, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		JsonObject j = jsonElement.getAsJsonObject();
		DepartedProperties properties = context.deserialize(j.getAsJsonObject(PROPERTIES_FIELD),
				DepartedProperties.class);
		long id = j.get(ID_FIELD).getAsLong();
		double lat = j.getAsJsonObject(GEOMETRY_FIELD)
				.getAsJsonArray(COORDINATES_FIELD).get(LAT).getAsDouble();
		double lon = j.getAsJsonObject(GEOMETRY_FIELD)
				.getAsJsonArray(COORDINATES_FIELD).get(LON).getAsDouble();
		Location location = new Location(Departed.GRAVE_LOCATION_PROVIDER);
		location.setLatitude(lat);
		location.setLongitude(lon);
		return new DepartedImpl(properties, id, location);
	}

}
