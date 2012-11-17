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

import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionFactory;
import com.nutiteq.components.MapPos;
import com.nutiteq.components.Point;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.maps.BaseMap;

public class EPSG2177 extends BaseMap {

	private static final String[] ARGS = { "+proj=tmerc", "+lat_0=0",
			"+lon_0=18", "+k=0.999923", "+x_0=6500000", "+y_0=0",
			"+ellps=GRS80", "+units=m", "+no_defs" };
	private final Projection projection;
	private final double[] resolutions;
	private double minEpsgX;
	private double minEpsgY;

	public EPSG2177(String copyright, int tileSize, int minZoom, int maxZoom,
			double[] resolutions, double minEpsgX, double minEpsgY) {
		super(copyright, tileSize, minZoom, maxZoom);
		projection = ProjectionFactory.fromPROJ4Specification(ARGS);
		this.resolutions = resolutions;
		this.minEpsgX = minEpsgX;
		this.minEpsgY = minEpsgY;
	}

	@Override
	public Point mapPosToWgs(MapPos mapPos) {
		Point2D.Double epsg = mapPosToEpsg(mapPos);
		Point2D.Double src = new Point2D.Double(epsg.getX(), epsg.getY());
		Point2D.Double dst = new Point2D.Double();
		projection.inverseTransform(src, dst);
		return new WgsPoint(dst.getX(), dst.getY()).toInternalWgs();
	}

	@Override
	public MapPos wgsToMapPos(Point point, int zoom) {
		double resolution = resolutions[zoom - getMinZoom()];
		double minxPx = minEpsgX / resolution;
		double minyPx = minEpsgY / resolution;
		int mapHeight = getTileSize() * (2 << (zoom - 1));
		Point2D.Double epsg = wgsToEpsg(point);
		int xPx = (int) ((epsg.getX() / resolution) - minxPx);
		int yPx = (int) (mapHeight - (epsg.getY() / resolution - minyPx));
		return new MapPos(xPx, yPx, zoom);
	}

	public Point2D.Double mapPosToEpsg(MapPos mapPos) {
		int zoomIndex = mapPos.getZoom() - getMinZoom();
		double resolution = resolutions[zoomIndex];
		double mapHeightMeters = getTileSize() * (2 << (mapPos.getZoom() - 1))
				* resolution;
		double metersX = (mapPos.getX() * resolution + minEpsgX);
		double metersY = ((mapHeightMeters - (mapPos.getY() * resolution)) + minEpsgY);
		return new Point2D.Double(metersX, metersY);
	}

	public Point2D.Double wgsToEpsg(Point point) {
		WgsPoint wgsPoint = point.toWgsPoint();
		Point2D.Double src = new Point2D.Double(wgsPoint.getLon(),
				wgsPoint.getLat());
		Point2D.Double dst = new Point2D.Double();
		projection.transform(src, dst);
		return dst;
	}
}
