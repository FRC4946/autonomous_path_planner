package ca._4946.mreynolds.pathplanner.src.data.point;

/**
 * A point that {@link ControlPoint}s can snap to. May or may not have a heading
 * that will also snap the {@code ControlPoint}'s heading
 * 
 * @author Matthew
 *
 */
public class MagnetPoint extends Point {
	private boolean hasHeading = false;
	private double heading = 0;

	/**
	 * Create a {@code MagnetPoint} at (0,0) that <i>does not</i> describe a heading
	 */
	public MagnetPoint() {
		super();
		setup(false, 0);
	}

	/**
	 * Create a {@code MagnetPoint} at (x,y) that <i>does not</i> describe a heading
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 */
	public MagnetPoint(double x, double y) {
		super(x, y);
		setup(false, 0);
	}

	/**
	 * Create a {@code MagnetPoint} at (x,y) that describe a heading {@code h}
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @param h
	 *            the heading
	 */
	public MagnetPoint(double x, double y, double h) {
		super(x, y);
		setup(true, h);
	}

	/**
	 * Create a {@code MagnetPoint} at the coordinates specified by {@code point}
	 * that <i>does not</i> describe a heading
	 * 
	 * @param point
	 *            the {@link java.awt.Point} describing the coordinates
	 */
	public MagnetPoint(java.awt.Point point) {
		super(point);
		setup(false, 0);
	}

	/**
	 * Snap a {@link ControlPoint} to this {@code MagnetPoint}
	 * 
	 * @param p
	 *            the {@code ControlPoint} to snap
	 */
	public void latch(ControlPoint p) {
		p.setX(x);
		p.setY(y);
		if (hasHeading) {
			p.setHeading(heading);
			p.setMagnet(true);
		}
	}

	/**
	 * Setup the parameters of the magnet
	 */
	private void setup(boolean hasHeading, double newHeading) {
		size = 10;
		this.hasHeading = hasHeading;
		this.heading = newHeading;
	}

	/**
	 * @return {@code true} if this magnet point also describes a heading
	 */
	public boolean hasHeading() {
		return hasHeading;
	}

	/**
	 * @return the heading specified by this magnet point
	 */
	public double getHeading() {
		return heading;
	}
}
