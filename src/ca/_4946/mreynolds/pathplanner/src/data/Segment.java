package ca._4946.mreynolds.pathplanner.src.data;

import ca._4946.mreynolds.pathplanner.src.data.point.Point;

/**
 * A representation of pose and translational motion at a given moment, and the
 * delta time between this and adjacent {@code Segment}s.
 * <p>
 * <b>Fields:</b>
 * <li>Pose: x position, y position, heading
 * <li>Motion: position travelled, velocity, acceleration, and jerk
 * <li>Other: delta time
 * 
 * @author Matthew Reynolds
 *
 */
public class Segment {

	public double pos = 0;
	public double vel = 0;
	public double accel = 0;
	public double jerk = 0;
	public double heading = 0;
	public double dt = 0;
	public double x = 0;
	public double y = 0;

	/**
	 * Create an empty segment with all parameters equal to 0
	 */
	public Segment() {
	}

	/**
	 * Create a new {@code Segment} that clones all values from the specified
	 * {@code Segment}
	 * 
	 * @param s
	 *            the {@code Segment} to clone
	 */
	public Segment(Segment s) {
		pos = s.pos;
		vel = s.vel;
		accel = s.accel;
		jerk = s.jerk;
		heading = s.heading;
		dt = s.dt;
		x = s.x;
		y = s.y;
	}

	/**
	 * Create a new {@code Segment} with all values equal to 0 except for
	 * coordinates x and y, which are clones from the specified {@link Point}
	 * 
	 * @param p
	 *            the {@code Point} to clone
	 */
	public Segment(Point p) {
		this.x = p.getX();
		this.y = p.getY();
	}

	/**
	 * Create a new {@code Segment} with the specified parameters and all other
	 * values equal to 0
	 * 
	 * @param pos
	 *            the position
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @param heading
	 *            the heading
	 */
	public Segment(double pos, double x, double y, double heading) {
		this.pos = pos;
		this.x = x;
		this.y = y;
		this.heading = heading;
	}

	@Override
	public String toString() {
		return pos + "\t" + vel + "\t" + accel + "\t" + jerk + "\t" + heading + "\t" + dt + "\t" + x + "\t" + y;
	}

	/**
	 * Create a {@link Point} representing the x and y coordinates of the
	 * {@code Segment}
	 * 
	 * @return the {@code Point}
	 */
	public Point toPt() {
		return new Point(x, y);
	}
}
