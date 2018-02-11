package ca._4946.mreynolds.pathplanner.src.math;

import java.util.ArrayList;
import java.util.List;

import ca._4946.mreynolds.pathplanner.src.PathPlanner;
import ca._4946.mreynolds.pathplanner.src.data.LinAngSegment;
import ca._4946.mreynolds.pathplanner.src.data.Segment;
import ca._4946.mreynolds.pathplanner.src.data.actions.DriveAction;
import ca._4946.mreynolds.pathplanner.src.data.point.Point;
import ca._4946.mreynolds.pathplanner.src.data.point.Waypoint;
import ca._4946.mreynolds.pathplanner.src.math.bezier.CubicBezier;

public class PathParser {

	public static final double MAX_JERK = 30; // in/s^2
	public static final double MAX_ACCEL = 15; // in/s^2
	public static final double MAX_VEL = 15; // in/s
	public static final double SAMPLE_PERIOD = 0.02; // 20ms

	public static ArrayList<LinAngSegment> fillPath(List<Waypoint> list) {
		ArrayList<LinAngSegment> fill = new ArrayList<>();

		double dist = 0;
		for (int i = 0; i < list.size() - 1; i++) {
			CubicBezier curve = new CubicBezier(list.get(i), list.get(i + 1));

			double spacing = 1; // 5in

			for (double pos = 0; pos < curve.length(); pos += spacing) {
				LinAngSegment seg = curve.getSegOnCurve(pos / curve.length());
				seg.lin.pos = dist + pos;
				fill.add(seg);
			}

			dist += curve.length();
		}

		return fill;
	}

	public static ArrayList<LinAngSegment> smoothPath(List<Waypoint> list) {

		ArrayList<LinAngSegment> fill = fillPath(list);
		ArrayList<LinAngSegment> smooth = new ArrayList<>();

		TrapezoidMotionProfile profile = new TrapezoidMotionProfile(fill.get(fill.size() - 1).lin.pos, MAX_VEL,
				MAX_ACCEL, MAX_JERK);
		double time = 0;
		int lastSeg = 0;
		while (time < profile.time[7]) {

			LinAngSegment s = new LinAngSegment(profile.getSeg(time));

			for (int i = lastSeg; i < fill.size() - 1; i++) {

				// If we are between two points...
				if (fill.get(i).lin.pos <= s.lin.pos && fill.get(i + 1).lin.pos >= s.lin.pos) {
					lastSeg = i;

					double dp = fill.get(i + 1).lin.pos - fill.get(i).lin.pos;
					double dx = fill.get(i + 1).lin.x - fill.get(i).lin.x;
					double dy = fill.get(i + 1).lin.y - fill.get(i).lin.y;
					double dap = fill.get(i + 1).ang.pos - fill.get(i).ang.pos;
					double dav = fill.get(i + 1).ang.vel - fill.get(i).ang.vel;
					double daa = fill.get(i + 1).ang.accel - fill.get(i).ang.accel;
					double daj = fill.get(i + 1).ang.jerk - fill.get(i).ang.jerk;

					if (dap > Math.PI)
						dap = -2 * Math.PI + dap;
					if (dap < -Math.PI)
						dap = 2 * Math.PI + dap;

					double percent = (s.lin.pos - fill.get(i).lin.pos) / dp;
					// System.out.println(percent + "\t" + percent * percent);

					s.lin.dt = SAMPLE_PERIOD;
					s.lin.x = fill.get(i).lin.x + dx * percent;
					s.lin.y = fill.get(i).lin.y + dy * percent;
					s.ang.pos = fill.get(i).ang.pos + dap * percent;
					s.ang.vel = fill.get(i).ang.vel + dav * percent;
					s.ang.accel = fill.get(i).ang.accel + daa * percent;
					s.ang.jerk = fill.get(i).ang.jerk + daj * percent;

					s.lin.heading = Math.toDegrees(s.ang.pos);
					smooth.add(s);
					break;
				}

			}

			time += SAMPLE_PERIOD;
		}

		return smooth;
	}

	public static DriveAction generatePath(ArrayList<LinAngSegment> list) {
		if (list.size() < 3)
			return new DriveAction();

		DriveAction path = new DriveAction();

		Segment l, r, lastL, lastR;
		double botRadius = PathPlanner.WHEEL_WIDTH_IN / 2.0;
		double perp = MathUtil.toRange(list.get(0).ang.pos - (Math.PI / 2), 0, 2 * Math.PI);
		l = new Segment(list.get(0).lin);
		r = new Segment(list.get(0).lin);

		l.x += Math.cos(perp) * botRadius;
		l.y += Math.sin(perp) * botRadius;
		r.x -= Math.cos(perp) * botRadius;
		r.y -= Math.sin(perp) * botRadius;

		// TODO: Fix last
		for (int i = 1; i < list.size() - 1; i++) {

			lastL = l;
			lastR = r;

			LinAngSegment s = list.get(i);

			perp = MathUtil.toRange(s.ang.pos - (Math.PI / 2), 0, 2 * Math.PI);
			l = new Segment(s.lin);
			r = new Segment(s.lin);

			// l.heading = r.heading = Math.toDegrees(s.ang.pos);
			l.x += Math.cos(perp) * botRadius;
			l.y += Math.sin(perp) * botRadius;
			r.x -= Math.cos(perp) * botRadius;
			r.y -= Math.sin(perp) * botRadius;

			// Negative change is turning CW (L+, R-)
			// Positive change is turning CCW (L-, R+)
			double dtheta = (list.get(i + 1).ang.pos - s.ang.pos);
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
