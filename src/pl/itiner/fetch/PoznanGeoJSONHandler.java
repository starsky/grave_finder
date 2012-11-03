/*
 * This file is part of the Lokalizator grobów project.
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation v3; 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA
 */
package pl.itiner.fetch;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.itiner.grave.R;
import pl.itiner.model.Departed;
import pl.itiner.model.DepartedDeserializer;
import pl.itiner.model.DepartedListDeserializer;
import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * 
 */

/**
 * TODO złap gdzieś wyjątek parsowania
 * 
 */
public final class PoznanGeoJSONHandler implements DataHandler<List<Departed>> {

	private static DataHandler<List<Departed>> prototype = new PoznanGeoJSONHandler();
	
	public synchronized static void setPrototype(DataHandler<List<Departed>> newPrototype) {
		prototype = newPrototype;
	}
	
	public synchronized static DataHandler<List<Departed>> createInstance(
			QueryParams params, Context ctx) {
		return prototype.clone(params, ctx);
	}

	private static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String TAG = "GeoJSON";

	static final int MAX_FETCH_SIZE = 5;
	private static final Type COLLECTION_TYPE = new TypeToken<List<Departed>>() {
	}.getType();

	private static GsonBuilder g;

	static {
		g = new GsonBuilder();
		g.setDateFormat(DATE_FORMAT);
		g.registerTypeAdapter(Departed.class, new DepartedDeserializer());
		g.registerTypeAdapter(COLLECTION_TYPE, new DepartedListDeserializer());
	}

	private QueryParams params;
	private String serverUri;
	private ResponseHandler<List<Departed>> responseHandler;

	private PoznanGeoJSONHandler() {

	}

	private PoznanGeoJSONHandler(QueryParams params, Context ctx) {
		this.params = params;
		serverUri = ctx.getResources().getString(
				R.string.poznan_feature_server_uri);
	}

	private List<Departed> executeQuery() throws IOException {
		Uri uri = prepeareURL();
		String resp = HttpDownloadTask.getResponse(uri);
		return parseJSON(resp);
	}

	private List<Departed> parseJSON(String JSON) {
		Gson gson = g.create();
		return gson.fromJson(JSON, COLLECTION_TYPE);
	}

	private static Map<String, String> createQueryParamsMap(QueryParams params) {
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		Map<String, String> map = new HashMap<String, String>();
		if (params.cmId != null) {
			map.put("cm_id", params.cmId.toString());
		}
		if (filledStr(params.name)) {
			map.put("g_name", cleanStr(params.name));
		}
		if (filledStr(params.surename)) {
			map.put("g_surname", cleanStr(params.surename));
		}
		if (params.deathDate != null) {
			map.put("g_date_death", formatter.format(params.deathDate));
		}
		if (params.burialDate != null) {
			map.put("g_date_burial", formatter.format(params.burialDate));
		}
		if (params.birthDate != null) {
			map.put("g_date_birth", formatter.format(params.birthDate));
		}
		return map;
	}

	private static boolean filledStr(String str) {
		return str != null && !str.equals("");
	}

	private static String cleanStr(String str) {
		if (filledStr(str))
			return str.toLowerCase().trim();
		else
			return str;
	}

	private Uri prepeareURL() {
		Map<String, String> paramsMap = createQueryParamsMap(params);
		Uri.Builder b = Uri.parse(serverUri).buildUpon();
		b.appendQueryParameter("maxFeatures", MAX_FETCH_SIZE + "");
		StringBuilder queryableParam = new StringBuilder();
		for (String p : paramsMap.keySet()) {
			queryableParam.append(p).append(",");
			b.appendQueryParameter(p, paramsMap.get(p));
		}
		if (queryableParam.length() > 0)
			queryableParam.deleteCharAt(queryableParam.length() - 1);
		b.appendQueryParameter("queryable", queryableParam.toString());
		return b.build();
	}

	@Override
	public void run() {
		try {
			List<Departed> result = executeQuery();
			if (responseHandler != null) {
				responseHandler.handleResponse(result);
			}
		} catch (IOException e) {
		}
	}

	@Override
	public void setResponseHandler(ResponseHandler<List<Departed>> handler) {
		responseHandler = handler;
	}

	@Override
	public DataHandler<List<Departed>> clone(QueryParams params, Context ctx) {
		return new PoznanGeoJSONHandler(params, ctx);
	}

}