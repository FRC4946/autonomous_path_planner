package ca._4946.mreynolds.pathplanner.src.math;

import java.util.ArrayList;
import java.util.List;

import ca._4946.mreynolds.pathplanner.src.PathPlanner;
import ca._4946.mreynolds.pathplanner.src.data.Segment;
import ca._4946.mreynolds.pathplanner.src.data.actions.DriveAction;
import ca._4946.mreynolds.pathplanner.src.data.point.Point;
import ca._4946.mreynolds.pathplanner.src.math.bezier.CubicBezier;

public class PathParser {

	public static final double MAX_JERK = 30; // in/s^2
	public static final double MAX_ACCEL = 15; // in/s^2
	public static final double MAX_VEL = 15; // in/s
	public static final double SAMPLE_PERIOD = 0.02; // 20ms

	public static ArrayList<Segment> fillPath(List<CubicBezier> list) {

		ArrayList<Segment> fill = new ArrayList<>();

		double dist = 0;
		for (CubicBezier c : list) {

			double spacing = 1; // 5in

			for (double pos = 0; pos < c.length(); pos += spacing) {
				Segment seg = c.getSegOnCurve(pos / c.length());
				seg.pos = dist + pos;
				fill.add(seg);
			}

			dist += c.length();
		}

		for (int i = 0; i < fill.size(); i++) {
			Segment prev = fill.get(Math.max(i - 1, 0));
			Segment next = fill.get(Math.min(i + 1, fill.size() - 1));
			fill.get(i).heading = Math.atan2(prev.y - next.y, prev.x - next.x);
		}

		return fill;
	}

	public static ArrayList<Segment> smoothPath(DriveAction action) {

		ArrayList<Segment> fill = fillPath(action.curves);
		ArrayList<Segment> smooth = new ArrayList<>();

		TrapezoidMotionProfile profile = new TrapezoidMotionProfile(fill.get(fill.size() - 1).pos, MAX_VEL, MAX_ACCEL,
				MAX_JERK);
		double time = 0;
		int lastSeg = 0;
		while (time < profile.time[7]) {

			Segment s = new Segment(profile.getSeg(time));

			for (int i = lastSeg; i < fill.size() - 1; i++) {

				// If we are between two points...
				if (fill.get(i).pos <= s.pos && fill.get(i + 1).pos >= s.pos) {
					lastSeg = i;

					double dp = fill.get(i + 1).pos - fill.get(i).pos;
					double dx = fill.get(i + 1).x - fill.get(i).x;
					double dy = fill.get(i + 1).y - fill.get(i).y;
					double dh = fill.get(i + 1).heading - fill.get(i).heading;

					if (dh > Math.PI)
						dh = -2 * Math.PI + dh;
					if (dh < -Math.PI)
						dh = 2 * Math.PI + dh;

					double percent = (s.pos - fill.get(i).pos) / dp;

					s.dt = SAMPLE_PERIOD;
					s.x = fill.get(i).x + dx * percent;
					s.y = fill.get(i).y + dy * percent;
					s.heading = Math.toDegrees(fill.get(i).heading + dh * percent);
					smooth.add(s);
					break;
				}

			}

			time += SAMPLE_PERIOD;
		}

		return smooth;
	}

	public static DriveAction generatePath(ArrayList<Segment> list) {
		if (list.size() < 2)
			return new DriveAction();

		DriveAction path = new DriveAction();

		Segment l, r, lastL, lastR;
		double botRadius = PathPlanner.WHEEL_WIDTH_IN / 2.0;
		double perp = MathUtil.toRange(Math.toRadians(list.get(0).heading) - (Math.PI / 2), 0, 2 * Math.PI);
		l = new Segment(list.get(0));
		r = new Segment(list.get(0));

		l.x += Math.cos(perp) * botRadius;
		l.y += Math.sin(perp) * botRadius;
		r.x -= Math.cos(perp) * botRadius;
		r.y -= Math.sin(perp) * botRadius;

		// TODO: Fix last
		for (int i = 1; i < list.size() - 1; i++) {

			lastL = l;
			lastR = r;

			Segment s = list.get(i);

			perp = MathUtil.toRange(Math.toRadians(list.get(i).heading) - (Math.PI / 2), 0, 2 * Math.PI);
			l = new Segment(s);
			r = new Segment(s);

			// l.heading = r.heading = Math.toDegrees(s.ang.pos);
			l.x += Math.cos(perp) * botRadius;
			l.y += Math.sin(perp) * botRadius;
			r.x -= Math.cos(perp) * botRadius;
			r.y -= Math.sin(perp) * botRadius;

			// Negative change is turning CW (L+, R-)
			// Positive change is turning CCW (L-, R+)
			double dtheta = Math.toRadians(list.get(i + 1).heading - s.heading);
			if (dtheta > Math.PI)
				dtheta -= Math.PI * 2;
			else if (dtheta < -Math.PI)
				dtheta += Math.PI * 2;
			double omega = dtheta / SAMPLE_PERIOD;
			l.pos = lastL.pos + l.toPt().distance(lastL.toPt());
			r.pos = lastR.pos + r.toPt().distance(lastR.toPt());

			l.vel += -omega * (botRadius);
			r.vel += omega * (botRadius);

			path.addSegment(true, l);
			path.addSegment(false, r);
		}

		return path;
	}

	public static void smoothAccelJerk(ArrayList<Segment> path) {
		int smoothSize = 5;

		for (int i = smoothSize; i < path.size() - smoothSize; i++) {
			double dv = path.get(i + smoothSize).vel - path.get(i - smoothSize).vel;
			path.get(i).accel = dv / (SAMPLE_PERIOD * smoothSize * 2);
		}

		// for (int i = smoothSize; i < path.size() - smoothSize; i++) {
		// double da = path.get(i + smoothSize).accel - path.get(i - smoothSize).accel;
		// path.get(i).jerk = da / (SAMPLE_PERIOD * smoothSize * 2);
		// }
	}

	@SuppressWarnings("unused")
	private static Point getIntersection(Segment a, Segment b) {
		double ma = Math.tan(Math.toRadians(a.heading - 90));
		double ba = a.y - a.x * ma;

		double mb = Math.tan(Math.toRadians(b.heading - 90));
		double bb = b.y - b.x * mb;

		double cx = (ba - bb) / (mb - ma);
		double cy = ma * cx + ba;

		return new Point(cx, cy);
	}

	// public static ArrayList<Point> smoothPath_Spline(ArrayList<? extends Point>
	// path) {
	// ArrayList<Point> smooth = new ArrayList<>();
	// double maxSpeed = 10; // The max pixel spacing
	// ArrayList<Point> spline = new ArrayList<>();
	// spline.addAll(Arrays.asList(CatmullRomSplineUtils.subdividePoints(path.toArray(new
	// Point[path.size()]), 100)));
	//
	// smooth.add(spline.get(0));
	//
	// for (int i = 1; i < spline.size(); i++) {
	// double dist = 0;
	//
	// do {
	// Point cur = spline.get(i);
	// Point last = smooth.get(smooth.size() - 1);
	//
	// // Distance from this point to the last one in smooth
	// dist = cur.distance(last);
	// double slope = (double) (cur.getY() - last.getY()) / (cur.getX() -
	// last.getX());
	//
	// if (dist > maxSpeed * 1.25) {
	// if (Double.isInfinite(Math.abs(slope)))
	// smooth.add(new Point(cur.getX(), last.getY() + Math.signum(slope) *
	// maxSpeed));
	// else {
	// double dX = (Math.sqrt((maxSpeed * maxSpeed) / (slope * slope + 1)));
	// if (cur.getX() < last.getX())
	// dX *= -1;
	//
	// double newY = (dX) * slope + last.getY();
	// Point interpolate = new Point(dX + last.getX(), newY);
	// smooth.add(interpolate);
	// }
	// }
	// } while (dist > maxSpeed * 1.25);
	// }
	//
	// return smooth;
	// }
}
