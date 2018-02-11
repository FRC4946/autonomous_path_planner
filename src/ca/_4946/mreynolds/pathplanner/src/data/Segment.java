package ca._4946.mreynolds.pathplanner.src.data;

import ca._4946.mreynolds.pathplanner.src.data.point.Point;

public class Segment {

	public double pos;
	public double vel;
	public double accel;
	public double jerk;
	public double heading;
	public double dt;
	public double x;
	public double y;

	public Segment() {
	}

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

	public Segment(Point p) {
		this.x = p.getX();
		this.y = p.getY();
	}

	public Segment(double pos, double x, double y, double heading) {
		this.pos = pos;
		this.x = x;
		this.y = y;
		this.heading = heading;
	}

	public void print() {
		System.out.println(
				pos + "\t" + vel + "\t" + accel + "\t" + jerk + "\t" + heading + "\t" + dt + "\t" + x + "\t" + y);
	}

	public Point toPt() {
		return new Point(x, y);
	}
}
