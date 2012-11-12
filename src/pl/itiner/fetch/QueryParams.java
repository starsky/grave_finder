package pl.itiner.fetch;

import java.text.ParseException;
import java.util.Date;

import android.net.Uri;

import com.google.common.base.Strings;

public final class QueryParams {
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

	public QueryParams(Uri uri) throws ParseException, NumberFormatException {
		name = Strings.emptyToNull(cleanStr(uri
				.getQueryParameter(GraveFinderProvider.NAME_QUERY_PARAM)));
		surename = Strings.emptyToNull(cleanStr(uri
				.getQueryParameter(GraveFinderProvider.SURENAME_QUERY_PARAM)));
		cmId = uri
				.getQueryParameter(GraveFinderProvider.CEMENTARY_ID_QUERY_PARAM) == null ? null
				: Long.valueOf(uri
						.getQueryParameter(GraveFinderProvider.CEMENTARY_ID_QUERY_PARAM));
		birthDate = uri
				.getQueryParameter(GraveFinderProvider.BIRTH_DATE_QUERY_PARAM) == null ? null
				: GraveFinderProvider.dateFormat
						.parse(uri
								.getQueryParameter(GraveFinderProvider.BIRTH_DATE_QUERY_PARAM));
		burialDate = uri
				.getQueryParameter(GraveFinderProvider.BURIAL_DATE_QUERY_PARAM) == null ? null
				: GraveFinderProvider.dateFormat
						.parse(uri
								.getQueryParameter(GraveFinderProvider.BURIAL_DATE_QUERY_PARAM));
		deathDate = uri
				.getQueryParameter(GraveFinderProvider.DEATH_DATE_QUERY_PARAM) == null ? null
				: GraveFinderProvider.dateFormat
						.parse(uri
								.getQueryParameter(GraveFinderProvider.DEATH_DATE_QUERY_PARAM));
	}

	private static String cleanStr(String str) {
		if (!Strings.isNullOrEmpty(str))
			return str.toLowerCase().trim();
		else
			return str;
	}

	@Override
	/*
	 * Generated (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((birthDate == null) ? 0 : birthDate.hashCode());
		result = prime * result
				+ ((burialDate == null) ? 0 : burialDate.hashCode());
		result = prime * result + ((cmId == null) ? 0 : cmId.hashCode());
		result = prime * result
				+ ((deathDate == null) ? 0 : deathDate.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((surename == null) ? 0 : surename.hashCode());
		return result;
	}

	/*
	 * Generated (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QueryParams other = (QueryParams) obj;
		if (birthDate == null) {
			if (other.birthDate != null)
				return false;
		} else if (!birthDate.equals(other.birthDate))
			return false;
		if (burialDate == null) {
			if (other.burialDate != null)
				return false;
		} else if (!burialDate.equals(other.burialDate))
			return false;
		if (cmId == null) {
			if (other.cmId != null)
				return false;
		} else if (!cmId.equals(other.cmId))
			return false;
		if (deathDate == null) {
			if (other.deathDate != null)
				return false;
		} else if (!deathDate.equals(other.deathDate))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (surename == null) {
			if (other.surename != null)
				return false;
		} else if (!surename.equals(other.surename))
			return false;
		return true;
	}

}