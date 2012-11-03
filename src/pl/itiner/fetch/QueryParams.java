package pl.itiner.fetch;

import java.text.ParseException;
import java.util.Date;

import android.net.Uri;

import com.google.common.base.Strings;

final class QueryParams {
	public final String name;
	public final String surename;
	public final Long cmId;
	public final Date birthDate;
	public final Date burialDate;
	public final Date deathDate;

	
	
	public QueryParams(String name, String surename, Long cmId, Date birthDate,
			Date burialDate, Date deathDate) {
		super();
		this.name = Strings.emptyToNull(cleanStr(name));
		this.surename = Strings.emptyToNull(cleanStr(surename));
		this.cmId = cmId;
		this.birthDate = birthDate;
		this.burialDate = burialDate;
		this.deathDate = deathDate;
	}

	public QueryParams(Uri uri) throws ParseException,
			NumberFormatException {
		name = Strings.emptyToNull(cleanStr(uri
				.getQueryParameter(GraveFinderProvider.NAME_QUERY_PARAM)));
		surename = Strings.emptyToNull(cleanStr(uri
				.getQueryParameter(GraveFinderProvider.SURENAME_QUERY_PARAM)));
		cmId = uri.getQueryParameter(GraveFinderProvider.CEMENTARY_ID_QUERY_PARAM) == null ? null
				: Long.valueOf(uri
						.getQueryParameter(GraveFinderProvider.CEMENTARY_ID_QUERY_PARAM));
		birthDate = uri.getQueryParameter(GraveFinderProvider.BIRTH_DATE_QUERY_PARAM) == null ? null
				: GraveFinderProvider.dateFormat.parse(uri
						.getQueryParameter(GraveFinderProvider.BIRTH_DATE_QUERY_PARAM));
		burialDate = uri.getQueryParameter(GraveFinderProvider.BURIAL_DATE_QUERY_PARAM) == null ? null
				: GraveFinderProvider.dateFormat.parse(uri
						.getQueryParameter(GraveFinderProvider.BURIAL_DATE_QUERY_PARAM));
		deathDate = uri.getQueryParameter(GraveFinderProvider.DEATH_DATE_QUERY_PARAM) == null ? null
				: GraveFinderProvider.dateFormat.parse(uri
						.getQueryParameter(GraveFinderProvider.DEATH_DATE_QUERY_PARAM));
	}

	private static String cleanStr(String str) {
		if (!Strings.isNullOrEmpty(str))
			return str.toLowerCase().trim();
		else
			return str;
	}

}