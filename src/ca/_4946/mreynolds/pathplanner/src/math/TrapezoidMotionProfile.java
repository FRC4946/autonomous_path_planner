package ca._4946.mreynolds.pathplanner.src.math;

import ca._4946.mreynolds.pathplanner.src.data.Segment;

public class TrapezoidMotionProfile {

	// These store the maximums that were specified during construction
	private double jmax_param;
	private double amax_param;
	private double vmax_param;

	// These are the true limits that are calculated during the path planning
	private double jmax;
	private double amax;
	private double vmax;

	// The parameters describing the parameters of each phase
	public double[] time = new double[8];
	private double[] vel = new double[8];
	private double[] accel = new double[8];
	private double[] jerk = new double[7];

	private double pathLength;

	public TrapezoidMotionProfile(double dist, double maxVelocity, double maxAcceleration, double maxJerk) {

		jmax_param = maxJerk;
		amax_param = maxAcceleration;
		vmax_param = maxVelocity;
		pathLength = dist;

		calc();

		accel = new double[] { 0, amax, amax, 0, 0, -amax, -amax, 0 };
		jerk = new double[] { jmax, 0, -jmax, 0, -jmax, 0, jmax, 0 };

		vel = new double[8];
		vel[0] = 0;
		for (int i = 1; i < 8; i++)
			vel[i] = vel(accel[i - 1], jerk[i - 1], time[i] - time[i - 1]) + vel[i - 1];
	}

	private void calc() {
		jmax = jmax_param;
		amax = amax_param;
		vmax = vmax_param;

		// Parameters of phase 1
		double t1 = amax / jmax;
		double d1 = pos(0, 0, jmax, t1);
		double v1 = vel(0, jmax, t1);

		// Parameters of phase 2
		double t2 = (vmax - 2 * v1) / amax;
		double d2 = pos(v1, amax, 0, t2);
		double v2 = vel(amax, 0, t2) + v1;

		// Parameters of phase 3
		double d3 = pos(v2, amax, -jmax, t1);
		double v3 = vel(amax, -jmax, t1) + v2;

		// Parameters of phase 4
		double t4 = Math.max((pathLength - 2 * d1 - 2 * d2 - 2 * d3) / vmax, 0);
		// double d4 = pos(v3, 0, 0, t4);

		// Trim phase 2
		double _a = amax;
		double _b = 3 * amax * amax / jmax;
		double _c = 2 * (amax * amax * amax) / (jmax * jmax) - pathLength;
		double newT2 = Math.max(MathUtil.quadratic(_a, _b, _c), 0);
		if (newT2 < t2) {
			t2 = newT2;

			d2 = pos(v1, amax, 0, t2);
			v2 = vel(amax, 0, t2) + v1;
			d3 = pos(v2, amax, -jmax, t1);
			v3 = vel(amax, -jmax, t1) + v2;

			vmax = v3;
		}

		// Trim phase 1
		double newT1 = Math.cbrt(pathLength / (2 * jmax));
		if (newT1 < t1) {
			t1 = newT1;
			t2 = 0;
			t4 = 0;

			// Calculate the max a
			amax = accel(jmax, t1);

			d1 = pos(0, 0, jmax, t1);
			v1 = vel(0, jmax, t1);

			d2 = pos(v1, amax, 0, t2); // 0
			v2 = vel(amax, 0, t2) + v1; // 0+v1

			d3 = pos(v2, amax, -jmax, t1);
			v3 = vel(amax, -jmax, t1) + v2;

			vmax = v3;
		}

		time[0] = 0;
		time[1] = t1;
		time[2] = t1 + t2;
		time[3] = t1 + t2 + t1;
		time[4] = t1 + t2 + t1 + t4;
		time[5] = t1 + t2 + t1 + t4 + t1;
		time[6] = t1 + t2 + t1 + t4 + t1 + t2;
		time[7] = t1 + t2 + t1 + t4 + t1 + t2 + t1;
	}

	/**
	 * Get the instantaneous velocity desired after having travelled a certain
	 * distance on the path
	 * 
	 * @param distTraveled
	 * @return
	 */
	public Segment getSeg(double t) {

		Segment s = new Segment();

		for (int i = 0; i < 7 && t > time[i]; i++) {
			double dt = t - time[i];

			if (dt > time[i + 1] - time[i])
				dt = time[i + 1] - time[i];

			s.pos += pos(vel[i], accel[i], jerk[i], dt);
			s.vel += vel(accel[i], jerk[i], dt);
			s.accel += accel(jerk[i], dt);
			s.jerk = jerk[i];
		}
		return s;
	}

	public double timeToTravel(double d) {

		double time;

		for (time = 0; getSeg(time).pos < (d-0.5); time += 0.001)
			;

		return time;
	}

	private static double pos(double vel, double accel, double jerk, double t) {
		return (vel * t) + (accel * t * t / 2) + (jerk * t * t * t / 6);
	}

	private static double vel(double accel, double jerk, double t) {
		return pos(accel, jerk, 0, t);
	}

	private static double accel(double jerk, double t) {
		return vel(jerk, 0, t);
	}

}
