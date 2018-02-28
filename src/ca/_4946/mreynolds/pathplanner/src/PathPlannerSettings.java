package ca._4946.mreynolds.pathplanner.src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca._4946.mreynolds.pathplanner.src.data.point.MagnetPoint;

public class PathPlannerSettings {

	public static final double ROBOT_LENGTH_IN = 39.375;
	public static final double ROBOT_WIDTH_IN = 34.375;
	public static final double WHEEL_WIDTH_IN = 32;

	
	// https://www.desmos.com/calculator/r25h6mn7h4
	public static final double MAX_VEL = 60; // in/s
	public static final double MAX_ACCEL = MAX_VEL / 1.0; // in/s^2 // ~1sec to accelerate
	public static final double MAX_JERK = MAX_ACCEL * 2; // in/s^3 // Must be greater than or equal to maxaccel

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
