package ca._4946.mreynolds.pathplanner.src.data.point;

public class Waypoint extends Point {

	private boolean isMagnet = false;
	private boolean automaticHeading = true;
	private double heading = 90;
	private double r = 25;

	public Waypoint() {
		super();
		size = 6;
	}

	public Waypoint(double x, double y) {
		super(x, y);
		size = 6;
	}

	public Waypoint(java.awt.Point point) {
		super(point);
		size = 6;
	}

	public Waypoint(Waypoint point) {
		super(point);
		isMagnet = point.isMagnet;
		automaticHeading = point.automaticHeading;
		heading = point.heading;
		r = point.r;
		size = 6;
	}

	public Waypoint(Point point) {
		super(point);
		size = 6;
	}

	public double updateAutoHeading(Waypoint prev, Waypoint next) {
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

	public Point getHandle() {
		Point p = new Point(x, y);

		p.x += r * Math.cos(Math.toRadians(heading));
		p.y += r * Math.sin(Math.toRadians(heading));
		p.size = 6;

		return p;
	}

	public Point getFlipHandle() {
		Point p = new Point(x, y);

		p.x -= r * Math.cos(Math.toRadians(heading));
		p.y -= r * Math.sin(Math.toRadians(heading));
		p.size = 6;

		return p;
	}

	/**
	 * @return the isMagnet
	 */
	public boolean isMagnet() {
		return isMagnet;
	}

	/**
	 * @param isMagnet
	 *            the isMagnet to set
	 */
	public void setMagnet(boolean isMagnet) {
		this.isMagnet = isMagnet;
		fireElementChanged();
	}

	/**
	 * @return the automaticHeading
	 */
	public boolean isAutomaticHeading() {
		return automaticHeading;
	}

	/**
	 * @param automaticHeading
	 *            the automaticHeading to set
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
	 * @return the r
	 */
	public double getR() {
		return r;
	}

	/**
	 * @param r
	 *            the r to set
	 */
	public void setR(double r) {
		this.r = r;
		fireElementChanged();
	}

	@Override
	public Waypoint clone() {
		return new Waypoint(this);
	}

}
