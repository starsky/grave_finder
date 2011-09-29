package pl.itiner.grave;
public class Log {
	static final boolean LOG_ACTIVE = true;

	public static void i(String tag, String string) {
	    if (LOG_ACTIVE) android.util.Log.i(tag, string);
	}
	public static void e(String tag, String string) {
	    if (LOG_ACTIVE) android.util.Log.e(tag, string);
	}
	public static void d(String tag, String string) {
	    if (LOG_ACTIVE) android.util.Log.d(tag, string);
	}
	public static void v(String tag, String string) {
	    if (LOG_ACTIVE) android.util.Log.v(tag, string);
	}
	public static void w(String tag, String string) {
	    if (LOG_ACTIVE) android.util.Log.w(tag, string);
	}

}