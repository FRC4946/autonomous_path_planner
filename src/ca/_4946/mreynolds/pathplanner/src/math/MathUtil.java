package ca._4946.mreynolds.pathplanner.src.math;

public class MathUtil {

	public static final double EPSILON = 1e-8;

	public static boolean isBetween(double left, double right, double a) {
		if (left <= right)
			return left <= a && a <= right;

		return right <= a && a <= left;
	}

	public static double limit(double min, double a, double max) {
		return Math.min(max, Math.min(min, a));
	}

	public static double toRange(double in, double min, double max) {
		while (in < min)
			in += (max - min);

		while (in > max)
			in -= (max - min);
		return in;
	}

	public static double quadratic(double a, double b, double c) {
		return ((-b) + Math.sqrt(b * b - 4 * a * c)) / (2 * a);
	}

	public static double quadratic2(double a, double b, double c) {
		return ((-b) - Math.sqrt(b * b - 4 * a * c)) / (2 * a);
	}

	public static double[] cubic(double a, double b, double c, double d) {
		// https://stackoverflow.com/questions/27176423/function-to-solve-cubic-equation-analytically

		if (Math.abs(d) < EPSILON) { // Quadratic case, ax^2+bx+c=0
			d = c;
			c = b;
			b = a;
			if (Math.abs(d) < EPSILON) { // Linear case, ax+b=0
				d = c;
				c = b;
				if (Math.abs(d) < EPSILON) // Degenerate case
					return new double[0];
				return new double[] { -c / d };
			}

			double D = c * c - 4 * d * b;
			if (Math.abs(D) < EPSILON)
				return new double[] { -c / (2 * d) };
			else if (D > 0)
				return new double[] { (-c + Math.sqrt(D)) / (2 * d), (-c - Math.sqrt(D)) / (2 * d) };
			return new double[0];
		}

		// Convert to depressed cubic t^3+pt+q = 0 (subst x = t - b/3a)
		double p = (3 * d * b - c * c) / (3 * d * d);
		double q = (2 * c * c * c - 9 * d * c * b + 27 * d * d * a) / (27 * d * d * d);
		double[] roots = {};

		if (Math.abs(p) < EPSILON) { // p = 0 -> t^3 = -q -> t = -q^1/3
			roots = new double[] { Math.cbrt(-q) };
		} else if (Math.abs(q) < EPSILON) { // q = 0 -> t^3 + pt = 0 -> t(t^2+p)=0

			// roots = [0].concat(p < 0 ? [Math.sqrt(-p), -Math.sqrt(-p)] : []);
			if (p < 0)
				roots = new double[] { 0, Math.sqrt(-p), -Math.sqrt(-p) };
			else
				roots = new double[] { 0 };
		} else {
			double D = q * q / 4 + p * p * p / 27;
			if (Math.abs(D) < EPSILON) { // D = 0 -> two roots
				roots = new double[] { -1.5 * q / p, 3 * q / p };
			} else if (D > 0) { // Only one real root
				double u = Math.cbrt(-q / 2 - Math.sqrt(D));
				roots = new double[] { u - p / (3 * u) };
			} else { // D < 0, three roots, but needs to use complex numbers/trigonometric solution
				double u = 2 * Math.sqrt(-p / 3);
				double t = Math.acos(3 * q / p / u) / 3; // D < 0 implies p < 0 and acos argument in [-1..1]
				double k = 2 * Math.PI / 3;
				roots = new double[] { u * Math.cos(t), u * Math.cos(t - k), u * Math.cos(t - 2 * k) };
			}
		}

		// Convert back from depressed cubic
		for (int i = 0; i < roots.length; i++)
			roots[i] -= c / (3 * d);

		return roots;
	}
}
