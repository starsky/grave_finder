package pl.itiner.commons;

import com.google.common.base.Strings;

public final class Commons {
	private Commons() {
	}

	public static String capitalizeFirstLetter(String str) {
		if (!Strings.isNullOrEmpty(str)) {
			return str.substring(0, 1).toUpperCase() + str.substring(1);
		}
		return str;
	}
}
