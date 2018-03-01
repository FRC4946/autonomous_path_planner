package ca._4946.mreynolds.pathplanner.src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca._4946.mreynolds.pathplanner.src.data.point.MagnetPoint;

public class PathPlannerSettings {

	public static final double ROBOT_LENGTH_IN = 39.375;
	public static final double ROBOT_WIDTH_IN = 34.375;
	public static final double WHEEL_WIDTH_IN = 28;

	// desmos.com/calculator/bovxrwsidp
	private static final double kApproxAccelTime = 0.75; // ~0.75 sec to accelerate to max vel
	private static final double kJerkMultiplier = 3.0; // Must be greater than 1.0. Larger = more aggresive jerk
	public static final double MAX_VEL = 120; // in/s
	public static final double MAX_ACCEL = MAX_VEL / kApproxAccelTime; // in/s^2
	public static final double MAX_JERK = MAX_ACCEL / kApproxAccelTime * kJerkMultiplier; // in/s^3

	public static final double SAMPLE_PERIOD = 0.02; // 20ms

	private static ArrayList<MagnetPoint> magnets = new ArrayList<>();

	private static void setupMagnets() {
		magnets.add(new MagnetPoint(-116, ROBOT_LENGTH_IN / 2, 90));
		magnets.add(new MagnetPoint(-73, ROBOT_LENGTH_IN / 2, 90));
		magnets.add(new MagnetPoint(2, ROBOT_LENGTH_IN / 2, 90));
		magnets.add(new MagnetPoint(44, ROBOT_LENGTH_IN / 2, 90));
		magnets.add(new MagnetPoint(73, ROBOT_LENGTH_IN / 2, 90));
		magnets.add(new MagnetPoint(116, ROBOT_LENGTH_IN / 2, 90));
	}

	public static List<MagnetPoint> getMagnets() {
		if (magnets.isEmpty())
			setupMagnets();
		return Collections.unmodifiableList(magnets);
	}

}
