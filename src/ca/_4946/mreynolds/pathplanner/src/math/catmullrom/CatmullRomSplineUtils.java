package ca._4946.mreynolds.pathplanner.src.math.catmullrom;

import ca._4946.mreynolds.pathplanner.src.data.point.Point;

public class CatmullRomSplineUtils {

	/**
	 * Creates catmull spline curves between the points array.
	 *
	 * @param points
	 *            The current 2D points array
	 * @param subdivisions
	 *            The number of subdivisions to add between each of the points.
	 *
	 * @return A larger array with the points subdivided.
	 */
	public static Point[] subdividePoints(Point[] points, int subdivisions) {
		assert points != null;
		assert points.length >= 3;

		Point[] subdividedPoints = new Point[((points.length - 1) * subdivisions) + 1];

		float increments = 1f / (float) subdivisions;

		for (int i = 0; i < points.length - 1; i++) {
			Point p0 = i == 0 ? points[i] : points[i - 1];
			Point p1 = points[i];
			Point p2 = points[i + 1];
			Point p3 = (i + 2 == points.length) ? points[i + 1] : points[i + 2];

			CatmullRomSpline2D crs = new CatmullRomSpline2D(p0, p1, p2, p3);

			for (int j = 0; j <= subdivisions; j++) {
				subdividedPoints[(i * subdivisions) + j] = crs.q(j * increments);
			}
		}

		return subdividedPoints;
	}
}
