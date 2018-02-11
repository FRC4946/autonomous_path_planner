package ca._4946.mreynolds.pathplanner.src.data.point;

public class MagnetPoint extends Point {

	private boolean hasHeading = false;
	private double heading = 0;

	public boolean hasHeading() {
		return hasHeading;
	}

	public double getHeading() {
		return heading;
	}

	public MagnetPoint() {
		super();
		setup(false, 0);
	}

	public MagnetPoint(double x, double y) {
		super(x, y);
		setup(false, 0);
	}

	public MagnetPoint(double x, double y, double heading) {
		super(x, y);
		setup(true, heading);
	}

	public MagnetPoint(java.awt.Point point) {
		super(point);
		setup(false, 0);
	}

	private void setup(boolean hasHeading, double newHeading) {
		size = 10;
		this.hasHeading = hasHeading;
		this.heading = newHeading;
	}

}
