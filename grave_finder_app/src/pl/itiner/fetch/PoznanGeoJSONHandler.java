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
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.itiner.grave.R;
import pl.itiner.model.Departed;
import pl.itiner.model.DepartedFactory;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;

/**
 * 
 */
public final class PoznanGeoJSONHandler {

	public static final String TAG = "GeoJSON";
	private static final String DATE_FORMAT = "yyyy-MM-dd";

	static final int MAX_FETCH_SIZE = 25;

	private final QueryParams params;
	private final String serverUri;

	public PoznanGeoJSONHandler(QueryParams params, Context ctx) {
		this.params = params;
		serverUri = ctx.getResources().getString(
				R.string.poznan_feature_server_uri);
	}

	public List<? extends Departed> executeQuery() throws IOException {
		Uri uri = prepeareURL();
		String resp = HttpDownloadTask.getResponse(uri);
		return DepartedFactory.parseJson(resp);
	}

	@SuppressLint("SimpleDateFormat")
	private static Map<String, String> createQueryParamsMap(QueryParams params) {
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		Map<String, String> map = new HashMap<String, String>();
		if (params.isFilledCmId()) {
			map.put("cm_id", params.getCmId() + "");
		}
		if (params.isFilledName()) {
			map.put("g_name", cleanStr(params.getName()));
		}
		if (params.isFilledSurename()) {
			map.put("g_surname", cleanStr(params.getSurename()));
		}
		if (params.isFilledDeathDate()) {
			map.put("g_date_death", formatter.format(params.getDeathDate()));
		}
		if (params.isFilledBurialDate()) {
			map.put("g_date_burial", formatter.format(params.getBurialDate()));
		}
		if (params.isFilledBirthDate()) {
			map.put("g_date_birth", formatter.format(params.getBirthDate()));
		}
		return map;
	}

	private static boolean filledStr(String str) {
		return str != null && !str.equals("");
	}

	@SuppressLint("DefaultLocale")
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

}