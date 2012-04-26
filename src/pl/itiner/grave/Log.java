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
/**
 * TODO Do wyrzucenia
 */
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