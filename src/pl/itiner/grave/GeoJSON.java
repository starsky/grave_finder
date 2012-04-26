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
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpResponseException;

import pl.itiner.models.Departed;
import pl.itiner.models.DepartedDeserializer;
import pl.itiner.models.DepartedListDeserializer;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * http://www.poznan.pl/featureserver/featureserver.cgi/groby/all.json?queryable
 * =cm_id,g_surname,g_date_burial&g_surname=m%C4%85kowski&cm_id=1
 * http://www.poznan
 * .pl/featureserver/featureserver.cgi/groby?queryable=g_name,g_surname
 * ,cm_id,g_date_burial&g_name=j%C3%B3zef&g_surname=piechowiak&cm_id
 * 
 * @author mihoo
 * 
 */

/**
 * TODO Usunąć listę statyczną, urzyć urlbuildera do budowy url, złap gdzieś
 * wyjątek parsowania
 * 
 */
public class GeoJSON {

	public static List<Departed> dList = new ArrayList<Departed>();
	public static final String TAG = "GeoJSON";
	private static final Type COLLECTION_TYPE = new TypeToken<List<Departed>>() {
	}.getType();
	// private String urlString =
	// "http://www.poznan.pl/featureserver/featureserver.cgi/groby?queryable=";
	private GsonBuilder g = new GsonBuilder();
	private Uri uri;

	/**
	 * 
	 * @param cm_id
	 *            Cementery id
	 * @param g_name
	 *            Person`s name
	 * @param g_surname
	 *            Persons` surenmae
	 * @param g_date_burial
	 * @param whichDate
	 */
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

	public Uri prepeareURL(String cmId, String name, String surname,
			String deathDate, String birthDate, String burialDate) {
		Map<String, String> paramsMap = createQueryParamsMap(cmId, name,
				surname, deathDate, birthDate, burialDate);
		Uri.Builder b = Uri.parse(
				"http://www.poznan.pl/featureserver/featureserver.cgi/groby")
				.buildUpon();
		b.appendQueryParameter("maxFeatures", 5 + "");
		StringBuilder queryableParam = new StringBuilder();
		for (String p : paramsMap.keySet()) {
			queryableParam.append(p).append(",");
			b.appendQueryParameter(p, paramsMap.get(p));
		}
		queryableParam.deleteCharAt(queryableParam.length() - 1);
		b.appendQueryParameter("queryable", queryableParam.toString());
		return b.build();
	}

	public String getJSON(Context ctx) {

		ByteArrayOutputStream baos;
		try {
			// set the download URL, a url that points to a file on the internet
			// this is the file to be downloaded

			// create the new connection
			URL url = new URL(uri.toString());
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();

			// set up some things on the connection
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);

			// and connect!
			urlConnection.connect();

			// this will be used to write the downloaded data into the file we
			// created
			// FileOutputStream fileOutput = ctx.openFileOutput(FILENAME,
			// ctx.MODE_PRIVATE);
			// this will be used in reading the data from the internet
			InputStream inputStream = null;

			try {
				inputStream = urlConnection.getInputStream();
			} catch (IOException ioe) {
				Log.e(TAG, ioe.getMessage());
				return null;
			}

			// variable to store total downloaded bytes
			int downloadedSize = 0;

			// create a buffer...
			byte[] buffer = new byte[1024];
			int bufferLength = 0; // used to store a temporary size of the
			// buffer
			baos = new ByteArrayOutputStream();
			// now, read through the input buffer and write the contents to the
			// file
			while ((bufferLength = inputStream.read(buffer)) > 0) {
				// add the data in the buffer to the file in the file output
				// stream (the file on the sd card
				baos.write(buffer, 0, bufferLength);
				// fileOutput.write(buffer, 0, bufferLength);
				// add up the size so we know how much is downloaded
				downloadedSize += bufferLength;
				// this is where you would do something to report the prgress,
				// like this maybe
				// updateProgress(downloadedSize, totalSize);

			}

			// close the output stream when done
			// catch some possible errors...
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (HttpResponseException httpe) {
			httpe.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		Log.i(TAG, baos.toString());
		return baos.toString();
	}

	public List<Departed> parseJSON(Context ctx, String JSON) {
		Gson gson = g.create();
		dList = gson.fromJson(JSON, COLLECTION_TYPE);
		return dList;
	}
}