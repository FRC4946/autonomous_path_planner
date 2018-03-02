package ca._4946.mreynolds.pathplanner.src.math;

import java.util.ArrayList;
import java.util.List;

import ca._4946.mreynolds.pathplanner.src.PathPlannerSettings;
import ca._4946.mreynolds.pathplanner.src.data.Segment;
import ca._4946.mreynolds.pathplanner.src.data.actions.DriveAction;
import ca._4946.mreynolds.pathplanner.src.data.point.Point;
import ca._4946.mreynolds.util.MathUtil;

public class PathParser {

	/**
	 * Create one continuous path of segments from the specified list of curves
	 * 
	 * @param list
	 *            The curves used to generate the path
	 * @return the path
	 */
	public static ArrayList<Segment> fillPath(List<CubicBezier> list) {
		ArrayList<Segment> fill = new ArrayList<>();
		double spacing = 1;

		// Generate the list
		double dist = 0;
		for (CubicBezier c : list) {
			for (double pos = 0; pos < c.length(); pos += spacing) {
				Segment seg = c.getSegOnCurve(pos / c.length());
				seg.pos = dist + pos;
				fill.add(seg);
			}

			dist += c.length();
		}

		// Calc all the headings
		for (int i = 0; i < fill.size(); i++) {
			Segment prev = fill.get(Math.max(i - 1, 0));
			Segment next = fill.get(Math.min(i + 1, fill.size() - 1));
			fill.get(i).heading = Math.atan2(prev.y - next.y, prev.x - next.x);
		}

		return fill;
	}

	/**
	 * Smooth out the path with proper distances as specified by the
	 * TrapezoidMotionProfile
	 * 
	 * @param action
	 *            the action to smooth
	 * @return The list of center points
	 */
	public static ArrayList<Segment> smoothPath(DriveAction action) {
		boolean isReverse = action.getData() == 1;

		ArrayList<Segment> fill = fillPath(action.getCurves());
		ArrayList<Segment> smooth = new ArrayList<>();

		double dist = fill.get(fill.size() - 1).pos;
		if (isReverse)
			dist *= -1;

		// Create the motion profile
		TrapezoidMotionProfile profile = new TrapezoidMotionProfile(dist, PathPlannerSettings.MAX_VEL, PathPlannerSettings.MAX_ACCEL, PathPlannerSettings.MAX_JERK);
		double time = 0;
		int lastSeg = 0;

		// Iterate through every timestamp
		while (time < profile.time[7]) {

			// Load the current segment's pos, vel, accel, jerk
			Segment s = new Segment(profile.getSeg(time));
			for (int i = lastSeg; i < fill.size() - 1; i++) {

				// Find the two surrounding points on the 2D path to add the x, y, and heading
				// data from the curve to the final path
				if (MathUtil.isBetween(fill.get(i).pos, fill.get(i + 1).pos, Math.abs(s.pos))) {
					lastSeg = i;

					// Linearly interpolate between the vars
					double dp = fill.get(i + 1).pos - fill.get(i).pos;
					double dx = fill.get(i + 1).x - fill.get(i).x;
					double dy = fill.get(i + 1).y - fill.get(i).y;
					double dh = fill.get(i + 1).heading - fill.get(i).heading;

					if (dh > Math.PI)
						dh = -2 * Math.PI + dh;
					if (dh < -Math.PI)
						dh = 2 * Math.PI + dh;

					double percent = (Math.abs(s.pos) - fill.get(i).pos) / dp;

					s.dt = PathPlannerSettings.SAMPLE_PERIOD;
					s.x = fill.get(i).x + dx * percent;
					s.y = fill.get(i).y + dy * percent;
					s.heading = Math.toDegrees(fill.get(i).heading + dh * percent);
					if (isReverse)
						s.heading -= 180;
					smooth.add(s);
					break;
				}

			}

			time += PathPlannerSettings.SAMPLE_PERIOD;
		}

		return smooth;
	}

	/**
	 * Turn the path into a path pair, for the left and right sides of the bot
	 * 
	 * @param list
	 *            The center path
	 * @return An Action containing the pair
	 */
	public static DriveAction generatePath(ArrayList<Segment> list) {
		if (list.size() < 2)
			return new DriveAction();
		DriveAction path = new DriveAction();

		Segment l, r, lastL, lastR;
		double botRadius = PathPlannerSettings.WHEEL_WIDTH_IN / 2;
		double perp = MathUtil.toRange(Math.toRadians(list.get(0).heading) - (Math.PI / 2), 0, 2 * Math.PI);
		l = new Segment(list.get(0));
		r = new Segment(list.get(0));

		// Offset the coords by the bot radius
		l.x += Math.cos(perp) * botRadius;
		l.y += Math.sin(perp) * botRadius;
		r.x -= Math.cos(perp) * botRadius;
		r.y -= Math.sin(perp) * botRadius;

		// Iterate through every segment
		for (int i = 1; i < list.size() - 1; i++) {
			lastL = l;
			lastR = r;

			Segment s = list.get(i);
			perp = MathUtil.toRange(Math.toRadians(list.get(i).heading) - (Math.PI / 2), 0, 2 * Math.PI);
			l = new Segment(s);
			r = new Segment(s);

			// Offset the coords by the bot radius
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

			// Calc pos and vel as a result of rotation
			double omega = dtheta / PathPlannerSettings.SAMPLE_PERIOD;
			l.pos = lastL.pos + Math.copySign(l.toPt().distance(lastL.toPt()), s.pos);
			r.pos = lastR.pos + Math.copySign(r.toPt().distance(lastR.toPt()), s.pos);

			l.vel += -omega * (botRadius);
			r.vel += omega * (botRadius);

			path.addSegment(true, l);
			path.addSegment(false, r);
		}

		// TODO: Fix last pt

		return path;
	}

	/**
	 * Smooth out the acceleration and jerk on the specified path
	 * 
	 * @param path
	 *            The path to smooth
	 */
	public static void smoothAccelJerk(ArrayList<Segment> path) {
		int smoothSize = 5;

		for (int i = smoothSize; i < path.size() - smoothSize; i++) {
			double dv = path.get(i + smoothSize).vel - path.get(i - smoothSize).vel;
			path.get(i).accel = dv / (PathPlannerSettings.SAMPLE_PERIOD * smoothSize * 2);
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
}
