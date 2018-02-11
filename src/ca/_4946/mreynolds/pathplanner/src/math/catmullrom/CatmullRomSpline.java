package ca._4946.mreynolds.pathplanner.src.math.catmullrom;

public class CatmullRomSpline {

	private double p0, p1, p2, p3;

	public CatmullRomSpline(double d, double e, double f, double g) {
		this.p0 = d;
		this.p1 = e;
		this.p2 = f;
		this.p3 = g;
	}

	public double q(double t) {
		return 0.5f * ((2 * p1) + (p2 - p0) * t + (2 * p0 - 5 * p1 + 4 * p2 - p3) * t * t
				+ (3 * p1 - p0 - 3 * p2 + p3) * t * t * t);
	}

	/**
	 * Example implementation
	 */
	public static void main(String[] args) {
		CatmullRomSpline crs = new CatmullRomSpline(1f, 2f, 2f, 1f);
		System.out.println(crs.q(0f));
		System.out.println(crs.q(0.25f));
		System.out.println(crs.q(0.5f));
		System.out.println(crs.q(0.75f));
		System.out.println(crs.q(1f));
	}

	/**
	 * @return the p0
	 */
	public double getP0() {
		return p0;
	}

	/**
	 * @param p0
	 *            the p0 to set
	 */
	public void setP0(double p0) {
		this.p0 = p0;
	}

	/**
	 * @return the p1
	 */
	public double getP1() {
		return p1;
	}

	/**
	 * @param p1
	 *            the p1 to set
	 */
	public void setP1(double p1) {
		this.p1 = p1;
	}

	/**
	 * @return the p2
	 */
	public double getP2() {
		return p2;
	}

	/**
	 * @param p2
	 *            the p2 to set
	 */
	public void setP2(double p2) {
		this.p2 = p2;
	}

	/**
	 * @return the p3
	 */
	public double getP3() {
		return p3;
	}

	/**
	 * @param p3
	 *            the p3 to set
	 */
	public void setP3(double p3) {
		this.p3 = p3;
	}

}
