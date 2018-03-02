package ca._4946.mreynolds.pathplanner.src.data;

import ca._4946.mreynolds.pathplanner.src.data.point.ControlPoint;
import ca._4946.mreynolds.pathplanner.src.data.point.Point;
import ca._4946.mreynolds.util.MathUtil;

/**
 * An implementation of a cubic bezier curve
 * 
 * @author Matthew Reynolds
 *
 */
public class CubicBezier {

	// A = start
	// B = start ctrl
	// C = end ctrl
	// D = end
	private Point a, b, c, d;
	private int mapSize = 100;
	private double[] lenMap;
	private double length;

	/**
	 * Create a {@code CubicBezier} from the specified {@link ControlPoint}s
	 * 
	 * @param start
	 *            the starting point
	 * @param end
	 *            the ending point
	 */
	public CubicBezier(ControlPoint start, ControlPoint end) {
		this(start, end, start.getHandle(), end.getFlipHandle());
	}

	/**
	 * Create a {@code CubicBezier} from a start, end, and two control
	 * {@link Point}s
	 * 
	 * @param start
	 *            the start of the curve
	 * @param end
	 *            the end of the curve
	 * @param startCtrl
	 *            the first control point
	 * @param endCtrl
	 *            the second control point
	 */
	public CubicBezier(Point start, Point end, Point startCtrl, Point endCtrl) {
		this.a = start;
		this.d = end;
		this.b = startCtrl;
		this.c = endCtrl;

		calcMap();
	}

	/**
	 * Update the start of the curve to the specified {@link ControlPoint}
	 * 
	 * @param start
	 *            the new starting point
	 */
	public void updateStart(ControlPoint start) {
		this.a = start;
		this.b = start.getHandle();
		calcMap();
	}

	/**
	 * Update the end of the curve to the specified {@link ControlPoint}
	 * 
	 * @param start
	 *            the new end point
	 */
	public void updateEnd(ControlPoint end) {
		this.d = end;
		this.c = end.getFlipHandle();
		calcMap();
	}

	/**
	 * Arc length parameterization
	 */
	private void calcMap() {
		lenMap = new double[mapSize + 1];

		double lastX = x(0);
		double lastY = y(0);
		double curLen = 0;
		for (int i = 1; i <= mapSize; i++) {

			double x = x((double) (i) / mapSize);
			double y = y((double) (i) / mapSize);

			double dx = lastX - x;
			double dy = lastY - y;

			curLen += Math.sqrt(dx * dx + dy * dy);
			lenMap[i] = curLen;
			lastX = x;
			lastY = y;
		}
		length = curLen;
	}

	/**
	 * Map a percentage of the length of the curve `p` to the corresponding
	 * parameter `t`
	 * 
	 * @param p
	 *            the percentage length of the curve
	 * @return the curve parameter t
	 */
	private double map(double p) {
		p = Math.max(0.0, p);
		p = Math.min(1.0, p);

		double targetLength = p * length;

		// Binary search to find index of the map value most close to the desired val
		int low = 0, high = mapSize, index = 0;
		while (low < high) {
			index = low + ((high - low) / 2);
			if (lenMap[index] < targetLength)
				low = index + 1;
			else
				high = index;
		}

		// Ensure we have the value immediately before
		if (this.lenMap[index] > targetLength)
			index--;

		// double lengthBefore = lenMap[index];
		// if (lengthBefore == targetLength)
		// return index / mapSize;
		// else
		return (index + (targetLength - lenMap[index]) / (lenMap[index + 1] - lenMap[index])) / mapSize;
	}

	/**
	 * Get the x coordinate of the point specified by the parameter {@code t}
	 * 
	 * @param t
	 *            the percentage length of the curve
	 * @return the x position
	 */
	private double x(double t) {
		return a.getX() + 3 * (-a.getX() + b.getX()) * t + 3 * (a.getX() - 2 * b.getX() + c.getX()) * t * t
				+ (-a.getX() + 3 * b.getX() - 3 * c.getX() + d.getX()) * t * t * t;
	}

	/**
	 * Get the y coordinate of the point specified by the parameter {@code t}
	 * 
	 * @param t
	 *            the percentage length of the curve
	 * @return the y position
	 */
	private double y(double t) {
		return a.getY() + 3 * (-a.getY() + b.getY()) * t + 3 * (a.getY() - 2 * b.getY() + c.getY()) * t * t
				+ (-a.getY() + 3 * b.getY() - 3 * c.getY() + d.getY()) * t * t * t;
	}

	/**
	 * Calculate the percentage length {@code t} at the specified x value. Note that
	 * due to the cubic nature of the curve, there can be 0, 1, or 3 results
	 * 
	 * @param x
	 *            the x value to solve for
	 * @return the t values
	 */
	private double[] tAtX(double x) {
		double p = a.getX() - x;
		double q = 3 * (b.getX() - a.getX());
		double r = 3 * (a.getX() - 2 * b.getX() + c.getX());
		double s = -a.getX() + 3 * b.getX() - 3 * c.getX() + d.getX();

		return MathUtil.cubic(p, q, r, s);
	}

	/**
	 * Calculate the percentage length {@code t} at the specified y value. Note that
	 * due to the cubic nature of the curve, there can be 0, 1, or 3 results
	 * 
	 * @param y
	 *            the y value to solve for
	 * @return the t values
	 */
	private double[] tAtY(double y) {
		double p = a.getY() - y;
		double q = 3 * (b.getY() - a.getY());
		double r = 3 * (a.getY() - 2 * b.getY() + c.getY());
		double s = -a.getY() + 3 * b.getY() - 3 * c.getY() + d.getY();

		return MathUtil.cubic(p, q, r, s);
	}

	/**
	 * Find the {@link Point} on the curve at the percentage length {@code t}
	 * 
	 * @param t
	 *            the percentage length of the curve
	 * @return the {@code Point}
	 */
	public Point getPtOnCurve(double t) {
		t = Math.max(0.0, t);
		t = Math.min(1.0, t);

		t = map(t);

		Point p = new Point();

		p.setX(x(t));
		p.setY(y(t));

		return p;
	}

	/**
	 * Determine whether the specified {@link Point} lies on the curve
	 * 
	 * @param p
	 *            the {@code Point} to check
	 * @param tol
	 *            the distance tolerance
	 * @return {@code true} if the {@code Point} lies on the curve
	 */
	public boolean ptIsOnCurve(Point p, double tol) {
		double[] t_x = tAtX(p.getX());
		double[] t_y = tAtY(p.getY());

		for (double x : t_x) {
			if (x < -0.05 || 1.05 < x)
				continue;
			for (double y : t_y) {
				if (y < -0.05 || 1.05 < y)
					continue;
				if (Math.abs(x - y) < tol)
					return true;
			}
		}

		return false;
	}

	/**
	 * @return the length of the curve
	 */
	public double length() {
		return length;
	}

	@Override
	public CubicBezier clone() {
		return new CubicBezier(a, d, b, c);
	}

	@Override
	public String toString() {
		String s = "A: " + a.toString() + "\tB: " + b.toString() + "\nC: " + c.toString() + "\tD: " + d.toString();
		return s;
	}

	// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
	// * This section is used for calculating the angle, angular
	// * velocity, etc of the curve. However I did it wrong so it's not
	// * currently used
	// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=

	// private double dx(double t) {
	// return (3.0 * (1 - t) * (1 - t) * (b.getX() - a.getX())) + (6.0 * t * (1 - t)
	// * (c.getX() - b.getX()))
	// + (3.0 * t * t * (d.getX() - c.getX()));
	// }
	//
	// private double dy(double t) {
	// return (3.0 * (1 - t) * (1 - t) * (b.getY() - a.getY())) + (6.0 * t * (1 - t)
	// * (c.getY() - b.getY()))
	// + (3.0 * t * t * (d.getY() - c.getY()));
	// }
	//
	// private double ddx(double t) {
	// return 6 * (a.getX() - 2 * b.getX() + c.getX()) + 6 * (-a.getX() + 3 *
	// b.getX() - 3 * c.getX() + d.getX()) * t;
	// }
	//
	// private double ddy(double t) {
	// return 6 * (a.getY() - 2 * b.getY() + c.getY()) + 6 * (-a.getY() + 3 *
	// b.getY() - 3 * c.getY() + d.getY()) * t;
	// }
	//
	// private double dddx() {
	// return 6 * (-a.getX() + 3 * b.getX() - 3 * c.getX() + d.getX());
	// }
	//
	// private double dddy() {
	// return 6 * (-a.getY() + 3 * b.getY() - 3 * c.getY() + d.getY());
	// }
	//
	// private Segment calcAngular(double t) {
	// Segment s = new Segment();
	//
	// // Since I did this wrt t instead of time, beyond the heading these values
	// are
	// // pretty useless
	//
	// // The right thing to do would be to fix the derivatives to get proper
	// angular
	// // kinematics, but we don't need to do this for our application since it is
	// // good enough to simply calculate these from our dtheta on the generated
	// path
	//
	// double bx = dx(t);
	// double cx = ddx(t);
	// double dx = dddx();
	//
	// double by = dy(t);
	// double cy = ddy(t);
	// double dy = dddy();
	//
	// double p = bx * bx + by * by;
	// double dp = 2 * (bx * cx + by * cy);
	// double ddp = 2 * (bx * dx + cx * cx + by * dy + cy * cy);
	//
	// double q = cx * by - bx * cy;
	// double dq = dx * by - bx * dy;
	// double ddq = by + dx * cy - cx * dy - bx;
	//
	// double r1 = (ddq * p - dq * dp) / (p * p);
	// double r2 = dq * dp + q * ddp;
	//
	// double heading = MathUtil.toRange(Math.atan2(dy(t), dx(t)), 0, 2 * Math.PI);
	// double angVel = q / p;
	// double angAccel = dq / p - (q * dp) / (p * p);
	// double angJerk = r1 - (r2 * p * p - 2 * q * dp * dp * p) / (p * p * p * p);
	//
	// s.pos = heading;
	// s.vel = angVel;
	// s.accel = angAccel;
	// s.jerk = angJerk;
	//
	// return s;
	// }
}
