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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.client.HttpResponseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import pl.itiner.models.Departed;
import pl.itiner.models.DepartedDeserializer;
import android.content.Context;
import android.util.Log;

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
 * TODO
 * Usunąć listę statyczną, urzyć urlbuildera do budowy url,
 * usunuść parse json tylko użyć biblioteki.
 *
 */
public class GeoJSON {

	public static ArrayList<Departed> dList = new ArrayList<Departed>();
	public static final String TAG = "GeoJSON";
	// private String urlString =
	// "http://www.poznan.pl/featureserver/featureserver.cgi/groby?queryable=";
	public URL url;

	/**
	 * 
	 * @param cm_id Cementery id
	 * @param g_name Person`s name
	 * @param g_surname Persons` surenmae
	 * @param g_date_burial 
	 * @param whichDate
	 */
	public GeoJSON(String cm_id, String g_name, String g_surname,
			String g_date_burial, int whichDate) {
		dList.clear();
		url = prepeareURL(cm_id, g_name, g_surname, g_date_burial, whichDate);
	}

	public URL prepeareURL(String cm_id, String g_name, String g_surname,
			String g_date, int whichDate) {
		String tmp_String = "http://www.poznan.pl/featureserver/featureserver.cgi/groby/all.json?maxFeatures=100&queryable=g_surname,g_name,cm_id,g_date_death,g_date_birth,g_date_burial&cm_id="
				+ cm_id + "&g_surname=" + g_surname + "&g_name=" + g_name;
		switch (whichDate) {
		case -1:
			tmp_String += "&g_date_death&g_date_burial&g_date_birth";
			break;
		case 0:
			tmp_String += "&g_date_death=" + g_date;
			break;
		case 1:
			tmp_String += "&g_date_burial=" + g_date;
			break;
		case 2:
			tmp_String += "&g_date_birth=" + g_date;
			break;
		}

		// +
		// &g_date_burial=" + g_date_burial;// + "&g_date_birth="+g_date_birth;
		// http://www.poznan.pl/featureserver/featureserver.cgi/groby?queryable=g_surname,g_name,cm_id,g_date_burial,g_date_birth&cm_id=&g_surname=&g_name=micha%C5%82&g_date_burial=&g_date_birth=
		try {
			return new URL(tmp_String);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getJSON(Context ctx) {

		ByteArrayOutputStream baos;
		try {
			// set the download URL, a url that points to a file on the internet
			// this is the file to be downloaded

			// create the new connection
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

	public ArrayList<Departed> parseJSON(Context ctx, String JSON) {

		JSONObject mainJSONObject;
		JSONArray featuresArray;
		GsonBuilder g = new GsonBuilder();
		g.registerTypeAdapter(Departed.class, new DepartedDeserializer());
		final Gson gson = new Gson();
		try {
			mainJSONObject = new JSONObject(JSON);
			featuresArray = mainJSONObject.getJSONArray("features");
			for (int i = 0; i < featuresArray.length(); i++) {

				JSONObject object = featuresArray.getJSONObject(i);
				Departed dt = gson.fromJson(object.toString(), Departed.class);
				dList.add(dt);

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dList;

	}
}