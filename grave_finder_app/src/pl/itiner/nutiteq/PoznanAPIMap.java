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

package pl.itiner.nutiteq;

import javax.awt.geom.Point2D;

import com.mgmaps.utils.Tools;
import com.nutiteq.components.MapPos;
import com.nutiteq.maps.GeoMap;
import com.nutiteq.maps.UnstreamedMap;
import com.nutiteq.utils.Utils;

public class PoznanAPIMap extends EPSG2177 implements GeoMap, UnstreamedMap {

	private final String baseurl;
	private final String format;
	private final String style;
	private final String request;

	/**
	 * Constructor for the simple WMS implementation
	 * 
	 * @param baseurl
	 *            base URL for the service
	 * @param tileSize
	 *            map tile size
	 * @param minZoom
	 *            minimum zoom for the map
	 * @param maxZoom
	 *            maximum zoom for the map
	 * @param layer
	 *            LAYERS parameter
	 * @param format
	 *            FORMAT parameter
	 * @param style
	 *            STYLE parameter
	 * @param request
	 *            REQUEST parameter
	 * @param copyright
	 *            copyright string displayed on map
	 */

	public PoznanAPIMap(final String baseurl, final int tileSize,
			final int minZoom, final int maxZoom, final String layer,
			final String format, final String style, final String request,
			final String copyright, double[] resolutions, double minEpsgX,
			double minEpsgY) {
		super(copyright, tileSize, minZoom, maxZoom, resolutions, minEpsgX,
				minEpsgY);
		this.format = format;
		this.request = request;
		this.style = style;

		final String epsgCode = "EPSG%3A2177";
		final StringBuffer base = new StringBuffer(
				Utils.prepareForParameters(baseurl));
		base.append("LAYERS=").append(Tools.urlEncode(layer));
		base.append("&FORMAT=").append(Tools.urlEncode(format));
		base.append("&BGCOLOR=0x000000");
		base.append("&TRANSPARENT=FALSE");
		base.append("&SERVICE=WMS&VERSION=1.1.1");
		base.append("&REQUEST=").append(Tools.urlEncode(request));
		base.append("&STYLES=").append(Tools.urlEncode(style));
		base.append("&EXCEPTIONS=").append(
				Tools.urlEncode("application/vnd.ogc.se_inimage"));
		base.append("&SRS=");
		base.append(epsgCode);
		base.append("&BBOX=");
		this.baseurl = base.toString();
	}

	public String buildPath(final int mapX, final int mapY, final int zoom) {
		return buildPath(mapX, mapY, zoom, baseurl);
	}

	public String buildPath(final int mapX, final int mapY, final int zoom,
			String url) {
		final StringBuffer result = new StringBuffer(url);

		final MapPos minPos = new MapPos(mapX, mapY + getTileSize(), zoom);
		final MapPos maxPos = new MapPos(mapX + getTileSize(), mapY, zoom);

		Point2D.Double leftDown = mapPosToEpsg(minPos);
		Point2D.Double topRight = mapPosToEpsg(maxPos);

		result.append(leftDown.x).append(",").append(leftDown.y).append(",");
		result.append(topRight.x).append(",").append(topRight.y);

		result.append("&WIDTH=").append(getTileSize()).append("&HEIGHT=")
				.append(getTileSize());
		return result.toString();
	}

	public String createBaseUrl(String baseUrl, String layer) {
		final String epsgCode = "EPSG%3A2177";
		final StringBuffer base = new StringBuffer(
				Utils.prepareForParameters(baseUrl));
		base.append("LAYERS=").append(Tools.urlEncode(layer));
		base.append("&FORMAT=").append(Tools.urlEncode(format));
		base.append("&BGCOLOR=0x000000");
		base.append("&TRANSPARENT=FALSE");
		base.append("&SERVICE=WMS&VERSION=1.1.1");
		base.append("&REQUEST=").append(Tools.urlEncode(request));
		base.append("&STYLES=").append(Tools.urlEncode(style));
		base.append("&EXCEPTIONS=").append(
				Tools.urlEncode("application/vnd.ogc.se_inimage"));
		base.append("&SRS=");
		base.append(epsgCode);
		base.append("&BBOX=");
		return base.toString();
	}

}