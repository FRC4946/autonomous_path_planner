package ca._4946.mreynolds.pathplanner.src.data.profiles;

import java.util.HashMap;
import java.util.Map;

import ca._4946.mreynolds.customSwing.ErrorPopup;
import ca._4946.mreynolds.pathplanner.src.data.Segment;
import ca._4946.mreynolds.util.MathUtil;

public class ConstantJerkProfile extends MotionProfile {

	public ConstantJerkProfile() {
		// Fast:
		// approxTime = 0.75
		// jerk: 3.0
		// Max vel: 120

		// Slow:
		// Approx time = 1.0
		// jerk = 2.0
		// Max vel = 60

		// Default tunings
		updateRelativeTunings(60, 1, 2);
	}

	// desmos.com/calculator/bovxrwsidp
	private boolean m_tuneAbs = false;
	private double m_approxAccelTime = 1;
	private double m_jerkMultiplier = 2.0; // Must be greater than 1.0. Larger = more aggresive jerk

	// These store the maximums that were specified during construction
	private double jmax_param;
	private double amax_param;
	private double vmax_param;

	// These are the true limits that are calculated during the path planning
	private double jmax;
	private double amax;
	private double vmax;

	// The parameters describing the parameters of each phase
	private double[] time = new double[8];
	private double[] vel = new double[8];
	private double[] accel = new double[8];
	private double[] jerk = new double[7];

	private double pathLength;

	public void setMaxVel(double maxVel) {
		if (m_tuneAbs)
			updateAbsoluteTunings(maxVel, amax_param, jmax_param);
		else
			updateRelativeTunings(maxVel, m_approxAccelTime, m_jerkMultiplier);
	}

	public void setMaxAccel(double maxAccel) {
		updateAbsoluteTunings(vmax_param, maxAccel, jmax_param);
	}

	public void setMaxJerk(double maxJerk) {
		updateAbsoluteTunings(vmax_param, amax_param, maxJerk);
	}

	public void setAccelTime(double time) {
		updateRelativeTunings(vmax_param, time, m_jerkMultiplier);
	}

	public void setJerkMultplier(double multiplier) {
		updateRelativeTunings(vmax_param, m_approxAccelTime, multiplier);
	}

	public double getMaxVel() {
		return vmax_param;
	}

	public double getMaxAccel() {
		return amax_param;
	}

	public double getMaxJerk() {
		return jmax_param;
	}

	public double getAccelTime() {
		return m_approxAccelTime;
	}

	public double getJerkMultiplier() {
		return m_jerkMultiplier;
	}

	public boolean tuningIsAbs() {
		return m_tuneAbs;
	}

	private void updateAbsoluteTunings(double maxVel, double maxAccel, double maxJerk) {
		m_tuneAbs = true;
		vmax_param = maxVel;
		amax_param = maxAccel;
		jmax_param = maxJerk;

		m_approxAccelTime = vmax_param / amax_param;
		m_jerkMultiplier = jmax_param * m_approxAccelTime / amax_param;
	}

	private void updateRelativeTunings(double maxVel, double accelTime, double jerkMultiplier) {
		m_tuneAbs = false;
		vmax_param = maxVel;
		m_approxAccelTime = accelTime;
		m_jerkMultiplier = jerkMultiplier;

		amax_param = vmax_param / m_approxAccelTime;
		jmax_param = amax_param / m_approxAccelTime * m_jerkMultiplier;
	}

	@Override
	public Map<String, String> exportProfile() {
		Map<String, String> map = new HashMap<String, String>();

		map.put("algorithm", toString());
		map.put("max vel", "" + Math.abs(vmax_param));
		if (m_tuneAbs) {
			map.put("max accel", "" + Math.abs(amax_param));
			map.put("max jerk", "" + Math.abs(jmax_param));
			map.remove("accel time");
			map.remove("jerk multiplier");
		} else {
			map.remove("max accel");
			map.remove("max jerk");
			map.put("accel time", "" + Math.abs(m_approxAccelTime));
			map.put("jerk multiplier", "" + Math.abs(m_jerkMultiplier));
		}

		return map;
	}

	@Override
	public void importProfile(Map<String, String> data) {
		try {
			vmax_param = Double.parseDouble(data.get("max vel"));

			m_tuneAbs = data.get("max accel") != null;
			if (m_tuneAbs) {
				amax_param = Double.parseDouble(data.get("max accel"));
				jmax_param = Double.parseDouble(data.get("max jerk"));
				updateAbsoluteTunings(vmax_param, amax_param, jmax_param);
			} else {
				m_approxAccelTime = Double.parseDouble(data.get("accel time"));
				m_jerkMultiplier = Double.parseDouble(data.get("jerk multiplier"));
				updateRelativeTunings(vmax_param, m_approxAccelTime, m_jerkMultiplier);
			}
		} catch (Exception e) {
			ErrorPopup.createPopup("Error loading profile", e);
		}
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
		double newT2 = Math.max(Math.max(MathUtil.quadratic(_a, _b, _c), MathUtil.quadratic2(_a, _b, _c)), 0);
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

	private static double pos(double vel, double accel, double jerk, double t) {
		return (vel * t) + (accel * t * t / 2) + (jerk * t * t * t / 6);
	}

	private static double vel(double accel, double jerk, double t) {
		return pos(accel, jerk, 0, t);
	}

	private static double accel(double jerk, double t) {
		return vel(jerk, 0, t);
	}

	@Override
	public String toString() {
		return "Constant Jerk";
	}

	@Override
	public double duration() {
		return time[7];
	}

	@Override
	public void setPathLength(double dist) {

		jmax_param = Math.copySign(jmax_param, dist);
		amax_param = Math.copySign(amax_param, dist);
		vmax_param = Math.copySign(vmax_param, dist);
		pathLength = dist;

		calc();

		accel = new double[] { 0, amax, amax, 0, 0, -amax, -amax, 0 };
		jerk = new double[] { jmax, 0, -jmax, 0, -jmax, 0, jmax, 0 };

		vel = new double[8];
		vel[0] = 0;
		for (int i = 1; i < 8; i++)
			vel[i] = vel(accel[i - 1], jerk[i - 1], time[i] - time[i - 1]) + vel[i - 1];
	}

}
