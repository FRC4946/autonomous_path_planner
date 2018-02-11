package ca._4946.mreynolds.pathplanner.src.math.catmullrom;

import ca._4946.mreynolds.pathplanner.src.data.point.Point;

public class CatmullRomSpline2D {

	private CatmullRomSpline splineXVals, splineYVals;

	public CatmullRomSpline2D(Point p0, Point p1, Point p2, Point p3) {
		assert p0 != null : "p0 cannot be null";
		assert p1 != null : "p1 cannot be null";
		assert p2 != null : "p2 cannot be null";
		assert p3 != null : "p3 cannot be null";

		splineXVals = new CatmullRomSpline(p0.getX(), p1.getX(), p2.getX(), p3.getX());
		splineYVals = new CatmullRomSpline(p0.getY(), p1.getY(), p2.getY(), p3.getY());
	}

	public Point q(float t) {
		return new Point(splineXVals.q(t), splineYVals.q(t));
	}
}
