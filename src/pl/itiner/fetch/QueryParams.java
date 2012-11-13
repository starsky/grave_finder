package pl.itiner.fetch;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Strings;

public final class QueryParams implements Parcelable {
	private final String name;
	private final String surename;
	private final long cmId;
	private final long birthDateTime;
	private final long burialDateTime;
	private final long deathDateTime;

	private QueryParams(Parcel in) {
		name = in.readString();
		surename = in.readString();
		cmId = in.readLong();
		burialDateTime = in.readLong();
		birthDateTime = in.readLong();
		deathDateTime = in.readLong();
	}

	public static final Parcelable.Creator<QueryParams> CREATOR = new Parcelable.Creator<QueryParams>() {
		public QueryParams createFromParcel(Parcel in) {
			return new QueryParams(in);
		}

		public QueryParams[] newArray(int size) {
			return new QueryParams[size];
		}
	};

	public QueryParams(String name, String surename, Long cmId, Date birthDate,
			Date burialDate, Date deathDate) {
		this.name = cleanStr(Strings.nullToEmpty(name));
		this.surename = cleanStr(Strings.nullToEmpty(surename));
		this.cmId = cmId == null ? Long.MIN_VALUE : cmId;
		this.birthDateTime = birthDate == null ? Long.MIN_VALUE : birthDate
				.getTime();
		this.burialDateTime = burialDate == null ? Long.MIN_VALUE : burialDate
				.getTime();
		this.deathDateTime = deathDate == null ? Long.MIN_VALUE : deathDate
				.getTime();
	}

	public String getName() {
		if (!isFilledName()) {
			throw new IllegalStateException("Name is not filled.");
		}
		return name;
	}

	public String getSurename() {
		if (!isFilledSurename()) {
			throw new IllegalStateException("Surename is not filled.");
		}
		return surename;
	}

	public long getCmId() {
		if (!isFilledCmId()) {
			throw new IllegalStateException("Cmid is not filled.");
		}
		return cmId;
	}

	public Date getBirthDate() {
		if (!isFilledBirthDate()) {
			throw new IllegalStateException("Birthday date is not filled.");
		}
		return new Date(birthDateTime);
	}

	public Date getBurialDate() {
		if (!isFilledBurialDate()) {
			throw new IllegalStateException("Burial date is not filled.");
		}
		return new Date(burialDateTime);
	}

	public Date getDeathDate() {
		if (!isFilledDeathDate()) {
			throw new IllegalStateException("Death date is not filled.");
		}
		return new Date(deathDateTime);
	}

	public boolean isFilledName() {
		return !Strings.isNullOrEmpty(name);
	}

	public boolean isFilledSurename() {
		return !Strings.isNullOrEmpty(surename);
	}

	public boolean isFilledCmId() {
		return cmId != Long.MIN_VALUE;
	}

	public boolean isFilledBirthDate() {
		return birthDateTime != Long.MIN_VALUE;
	}

	public boolean isFilledBurialDate() {
		return burialDateTime != Long.MIN_VALUE;
	}

	public boolean isFilledDeathDate() {
		return deathDateTime != Long.MIN_VALUE;
	}

	// public QueryParams(Uri uri) throws ParseException, NumberFormatException
	// {
	// name = Strings.emptyToNull(cleanStr(uri
	// .getQueryParameter(GraveFinderProvider.NAME_QUERY_PARAM)));
	// surename = Strings.emptyToNull(cleanStr(uri
	// .getQueryParameter(GraveFinderProvider.SURENAME_QUERY_PARAM)));
	// cmId = uri
	// .getQueryParameter(GraveFinderProvider.CEMENTARY_ID_QUERY_PARAM) == null
	// ? null
	// : Long.valueOf(uri
	// .getQueryParameter(GraveFinderProvider.CEMENTARY_ID_QUERY_PARAM));
	// birthDate = uri
	// .getQueryParameter(GraveFinderProvider.BIRTH_DATE_QUERY_PARAM) == null ?
	// null
	// : GraveFinderProvider.dateFormat
	// .parse(uri
	// .getQueryParameter(GraveFinderProvider.BIRTH_DATE_QUERY_PARAM));
	// burialDate = uri
	// .getQueryParameter(GraveFinderProvider.BURIAL_DATE_QUERY_PARAM) == null ?
	// null
	// : GraveFinderProvider.dateFormat
	// .parse(uri
	// .getQueryParameter(GraveFinderProvider.BURIAL_DATE_QUERY_PARAM));
	// deathDate = uri
	// .getQueryParameter(GraveFinderProvider.DEATH_DATE_QUERY_PARAM) == null ?
	// null
	// : GraveFinderProvider.dateFormat
	// .parse(uri
	// .getQueryParameter(GraveFinderProvider.DEATH_DATE_QUERY_PARAM));
	// }

	private static String cleanStr(String str) {
		if (!Strings.isNullOrEmpty(str))
			return str.toLowerCase().trim();
		else
			return str;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(surename);
		dest.writeLong(cmId);
		dest.writeLong(burialDateTime);
		dest.writeLong(birthDateTime);
		dest.writeLong(deathDateTime);
	}

}