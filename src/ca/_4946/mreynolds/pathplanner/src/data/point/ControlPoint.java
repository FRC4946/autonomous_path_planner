package ca._4946.mreynolds.pathplanner.src.data.point;

/**
 * A {@link Point} that contains a heading and radius that can be locked to
 * {@link MagnetPoint}s
 * 
 * @author Matthew
 *
 */
public class ControlPoint extends Point {
	private boolean isMagnet = false;
	private boolean automaticHeading = true;
	private double heading = 90;
	private double r = 25;

	/**
	 * Create a {@code ControlPoint} with default heading 90, radius 25, and
	 * coordinates (0, 0)
	 */
	public ControlPoint() {
		super();
		size = 6;
	}

	/**
	 * Create a {@code ControlPoint} with default heading 90, radius 25, and
	 * coordinates (x, y)
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 */
	public ControlPoint(double x, double y) {
		super(x, y);
		size = 6;
	}

	/**
	 * Create a {@code ControlPoint} with default heading 90, radius 25, and
	 * coordinates specified by {@code point}
	 * 
	 * @param point
	 *            the {@link java.awt.Point} to clone coordinates from
	 */
	public ControlPoint(java.awt.Point point) {
		super(point);
		size = 6;
	}

	/**
	 * Create a {@code ControlPoint} with all values cloned from the specified
	 * {@code ControlPoint}
	 * 
	 * @param point
	 *            the {@code ControlPoint} to clone
	 */
	public ControlPoint(ControlPoint point) {
		super(point);
		isMagnet = point.isMagnet;
		automaticHeading = point.automaticHeading;
		heading = point.heading;
		r = point.r;
		size = point.size;
	}

	/**
	 * Create a {@code ControlPoint} with default heading 90, radius 25, and
	 * coordinates specified by {@code point}
	 * 
	 * @param point
	 *            the {@link Point} to clone coordinates from
	 */
	public ControlPoint(Point point) {
		super(point);
		size = 6;
	}

	/**
	 * Calculate the heading for this control point. If this point is on a magnet or
	 * has been manually set ({@code isMagnet = true} or
	 * {@code automatidHeading = false}), do nothing. Otherwise, set the heading to
	 * be parallel to the angle between the previous point's handle, and the next
	 * points reverse handle
	 * 
	 * @param prev
	 *            the previous point
	 * @param next
	 *            the next point
	 * @return the calculated heading
	 */
	public double updateAutoHeading(ControlPoint prev, ControlPoint next) {
		if (isMagnet)
			return heading;

		if (!automaticHeading)
			return heading;

		double dx = next.getFlipHandle().x - prev.getHandle().x;
		double dy = next.getFlipHandle().y - prev.getHandle().y;

		if (prev == this) {
			dx = next.getFlipHandle().x - prev.x;
			dy = next.getFlipHandle().y - prev.y;
		} else if (next == this) {
			dx = next.x - prev.getHandle().x;
			dy = next.y - prev.getHandle().y;
		}

		heading = Math.toDegrees(Math.atan2(dy, dx));

		return heading;
	}

	/**
	 * Generate a {@link Point} representing the handle of the control point. The
	 * handle lies at angle {@code heading} and distance {@code radius}
	 * 
	 * @return the handle
	 */
	public Point getHandle() {
		Point p = new Point(x, y);

		p.x += r * Math.cos(Math.toRadians(heading));
		p.y += r * Math.sin(Math.toRadians(heading));
		p.size = 6;

		return p;
	}

	/**
	 * Generate a {@link Point} representing the reverse handle of the control
	 * point. The handle lies at angle {@code -heading} and distance {@code radius}
	 * 
	 * @return the handle
	 */
	public Point getFlipHandle() {
		Point p = new Point(x, y);

		p.x -= r * Math.cos(Math.toRadians(heading));
		p.y -= r * Math.sin(Math.toRadians(heading));
		p.size = 6;

		return p;
	}

	/**
	 * @return {@code true} if the control point is locked to a magnet
	 */
	public boolean isMagnet() {
		return isMagnet;
	}

	/**
	 * @param isMagnet
	 *            whether the control point is locked the a magnet
	 */
	public void setMagnet(boolean isMagnet) {
		this.isMagnet = isMagnet;
		fireElementChanged();
	}

	/**
	 * @return {@code true} if the point should automatically calculate heading
	 */
	public boolean isAutomaticHeading() {
		return automaticHeading;
	}

	/**
	 * @param automaticHeading
	 *            whether to automatically calculate this control point's heading
	 */
	public void setAutomaticHeading(boolean automaticHeading) {
		this.automaticHeading = automaticHeading;
		fireElementChanged();
	}

	/**
	 * @return the heading
	 */
	public double getHeading() {
		return heading;
	}

	/**
	 * @param heading
	 *            the heading to set
	 */
	public void setHeading(double heading) {
		this.heading = heading;
		fireElementChanged();
	}

	/**
	 * @return the radius
	 */
	public double getR() {
		return r;
	}

	/**
	 * @param r
	 *            the radius to set
	 */
	public void setR(double r) {
		this.r = r;
		fireElementChanged();
	}

	@Override
	public ControlPoint clone() {
		return new ControlPoint(this);
	}

}
