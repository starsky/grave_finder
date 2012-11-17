package pl.itiner.model;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

class DepartedListDeserializer implements JsonDeserializer<List<DepartedImpl>> {

	private static final String FEATURES_FIELD = "features";

	@Override
	public List<DepartedImpl> deserialize(JsonElement jsonElement, Type type,
			JsonDeserializationContext context) throws JsonParseException {
		JsonArray array = jsonElement.getAsJsonObject().getAsJsonArray(FEATURES_FIELD);
		ArrayList<DepartedImpl> ret = new ArrayList<DepartedImpl>(array.size());
		for(int i = 0; i < array.size(); i++) {
			ret.add((DepartedImpl) context.deserialize(array.get(i).getAsJsonObject(),DepartedImpl.class));
		}
		return ret;
	}

}
