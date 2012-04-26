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
package pl.itiner.grave;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import pl.itiner.models.Departed;
import pl.itiner.models.DepartedDeserializer;
import pl.itiner.models.DepartedListDeserializer;
import android.content.Context;
import android.net.Uri;
import android.net.http.AndroidHttpClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * 
 */

/**
 * TODO Usunąć listę statyczną, złap gdzieś wyjątek parsowania
 * 
 */
public class GeoJSON {

	private static final String USER_AGENT = "Grave-finder (www.itiner.pl)";
	private static final int MAX_FETCH_SIZE = 5;
	public static List<Departed> dList = new ArrayList<Departed>();
	public static final String TAG = "GeoJSON";
	private static final Type COLLECTION_TYPE = new TypeToken<List<Departed>>() {
	}.getType();
	private GsonBuilder g = new GsonBuilder();
	private Uri uri;

	public GeoJSON(String cm_id, String g_name, String g_surname,
			String g_date_burial, int whichDate) {
		dList.clear();
		switch (whichDate) {
		case -1:
			uri = prepeareURL(cm_id, g_name, g_surname, null, null, null);
			break;
		case 0:
			uri = prepeareURL(cm_id, g_name, g_surname, g_date_burial, null,
					null);
			break;
		case 1:
			uri = prepeareURL(cm_id, g_name, g_surname, null, g_date_burial,
					null);
			break;
		case 2:
			uri = prepeareURL(cm_id, g_name, g_surname, null, null,
					g_date_burial);
			break;
		}
		g = new GsonBuilder();
		g.registerTypeAdapter(Departed.class, new DepartedDeserializer());
		g.registerTypeAdapter(COLLECTION_TYPE, new DepartedListDeserializer());
	}

	public Uri prepeareURL(String cmId, String name, String surname,
			String deathDate, String birthDate, String burialDate) {
		Map<String, String> paramsMap = createQueryParamsMap(cmId, name,
				surname, deathDate, birthDate, burialDate);
		Uri.Builder b = Uri.parse(
				"http://www.poznan.pl/featureserver/featureserver.cgi/groby")
				.buildUpon();
		b.appendQueryParameter("maxFeatures", MAX_FETCH_SIZE + "");
		StringBuilder queryableParam = new StringBuilder();
		for (String p : paramsMap.keySet()) {
			queryableParam.append(p).append(",");
			b.appendQueryParameter(p, paramsMap.get(p));
		}
		queryableParam.deleteCharAt(queryableParam.length() - 1);
		b.appendQueryParameter("queryable", queryableParam.toString());
		return b.build();
	}

	public String getJSON(Context ctx) throws IOException {

		AndroidHttpClient client = AndroidHttpClient
				.newInstance(USER_AGENT);
		HttpResponse resp = client.execute(new HttpGet(uri.toString()));
		OutputStream os = new ByteArrayOutputStream();
		resp.getEntity().writeTo(os);
		return os.toString();
	}

	public List<Departed> parseJSON(Context ctx, String JSON) {
		Gson gson = g.create();
		dList = gson.fromJson(JSON, COLLECTION_TYPE);
		return dList;
	}

	private static Map<String, String> createQueryParamsMap(String cmId,
			String name, String surname, String deathDate, String birthDate,
			String burialDate) {
		Map<String, String> map = new HashMap<String, String>();
		if (cmId != null) {
			map.put("cm_id", cmId);
		}
		if (name != null) {
			map.put("g_name", name);
		}
		if (surname != null) {
			map.put("g_surname", surname);
		}
		if (deathDate != null) {
			map.put("g_date_death", deathDate);
		}
		if (burialDate != null) {
			map.put("g_date_burial", burialDate);
		}
		if (birthDate != null) {
			map.put("g_date_birth", birthDate);
		}
		return map;
	}

}