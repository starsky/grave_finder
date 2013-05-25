package pl.itiner.model;

import static android.provider.BaseColumns._ID;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_CEMENTERY_ID;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_DATE_BIRTH;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_DATE_BURIAL;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_DATE_DEATH;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_FAMILY;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_FIELD;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_LAT;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_LON;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_NAME;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_PLACE;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_QUARTER;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_ROW;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_SIZE;
import static pl.itiner.db.GraveFinderProvider.Columns.COLUMN_SURENAME;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.location.Location;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

public final class DepartedFactory {
	private static final String DATE_FORMAT = "yyyy-MM-dd";

	private static final Type COLLECTION_TYPE = new TypeToken<List<DepartedImpl>>() {
	}.getType();

	private static GsonBuilder g;

	static {
		g = new GsonBuilder();
		g.setDateFormat(DATE_FORMAT);
		g.registerTypeAdapter(DepartedImpl.class, new DepartedDeserializer());
		g.registerTypeAdapter(COLLECTION_TYPE, new DepartedListDeserializer());
	}

	private DepartedFactory() {
	}

	public static List<? extends Departed> parseJson(String json) {
		final Gson gson = g.create();
		final List<? extends Departed> result = gson.fromJson(json,
				COLLECTION_TYPE);
		if (result == null) {
			throw new JsonParseException("Result is null.");
		}
		return result;
	}

	public static List<? extends Departed> parseElements(String responseBody) {
		ArrayList<DepartedImpl> list = new ArrayList<DepartedImpl>();
		Element doc = Jsoup.parse(responseBody);
		Elements even = doc.select("table[class=list]").select("tr.even");
		for (Element el : even) {
			DepartedImpl p = parseDeparted(el);
			if (p != null)
				list.add(p);
		}
		Elements odd = doc.select("table[class=list]").select("tr.odd");
		for (Element el : odd) {
			DepartedImpl p = parseDeparted(el);
			if (p != null)
				list.add(p);
		}
		return list;
	}

	private static Date parseToDate(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dateParsed = null;
		try {
			dateParsed = sdf.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dateParsed;
	}

	public static DepartedImpl parseDeparted(Element elem) {

		Elements elems = elem.select("a");// .first().attr("href");
		String href = elems.first().attr("href");
		String id;
		int personId = 0;
		if (href != null) {
			id = href.trim().substring(href.indexOf("=") + 1, href.length());
			personId = Integer.parseInt(id);
		}
		DepartedProperties dp = new DepartedProperties(1, cleanStr(elem
				.select("span[class=link]").get(0).html()), cleanStr(elem
				.select("span[class=link]").get(1).html()), parseToDate(elem
				.select("span[class=link]").get(5).html()), parseToDate(elem
				.select("span[class=link]").get(4).html()), parseToDate(elem
				.select("span[class=link]").get(3).html()), href);
		return new DepartedImpl(dp, personId, new Location("dummy"));
	}

	public static ContentValues asContentValues(Departed departed) {
		ContentValues values = new ContentValues();
		values.put(_ID, departed.getId());
		values.put(COLUMN_DATE_BIRTH,
				departed.getBirthDate() != null ? departed.getBirthDate()
						.getTime() : null);
		values.put(COLUMN_DATE_BURIAL,
				departed.getBurialDate() != null ? departed.getBurialDate()
						.getTime() : null);
		values.put(COLUMN_DATE_DEATH,
				departed.getDeathDate() != null ? departed.getDeathDate()
						.getTime() : null);
		values.put(COLUMN_FAMILY, departed.getFamily());
		values.put(COLUMN_FIELD, departed.getField());
		if (departed.getLocation() != null) {
			values.put(COLUMN_LAT, departed.getLocation().getLatitude());
			values.put(COLUMN_LON, departed.getLocation().getLongitude());
		}
		values.put(COLUMN_NAME, departed.getName());
		values.put(COLUMN_PLACE, departed.getPlace());
		values.put(COLUMN_QUARTER, departed.getQuater());
		values.put(COLUMN_ROW, departed.getRow());
		values.put(COLUMN_SIZE, departed.getSize());
		values.put(COLUMN_SURENAME, departed.getSurname());
		values.put(COLUMN_CEMENTERY_ID, departed.getCmId());
		return values;

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

}
