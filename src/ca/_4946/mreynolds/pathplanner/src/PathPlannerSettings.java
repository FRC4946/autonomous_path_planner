package ca._4946.mreynolds.pathplanner.src;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ini4j.Ini;

import ca._4946.mreynolds.pathplanner.src.data.point.MagnetPoint;
import ca._4946.mreynolds.pathplanner.src.io.FileIO;

public class PathPlannerSettings {

	public static final double ROBOT_LENGTH_IN = 39.375;
	public static final double ROBOT_WIDTH_IN = 34.375;
	public static final double WHEEL_WIDTH_IN = 28;

	// Fast:
	// approxTime = 0.75
	// jerk: 3.0
	// Max vel: 120

	// Slow:
	// Approx time = 1.0
	// jerk = 2.0
	// Max vel = 60

	// desmos.com/calculator/bovxrwsidp
	private static final double kApproxAccelTime = 1; // ~0.75 sec to accelerate to max vel
	private static final double kJerkMultiplier = 2.0; // Must be greater than 1.0. Larger = more aggresive jerk
	public static final double MAX_VEL = 60; // in/s
	public static final double MAX_ACCEL = MAX_VEL / kApproxAccelTime; // in/s^2
	public static final double MAX_JERK = MAX_ACCEL / kApproxAccelTime * kJerkMultiplier; // in/s^3

	public static final double SAMPLE_PERIOD = 0.02; // 20ms
	public static final String APP_VERSION = "1.0.1";

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

	public static void saveSettings() {

		try {
			File f = new File(FileIO.PROFILE_DIR + "/test.ini");
			if (!f.exists())
				f.createNewFile();

			Ini ini = new Ini(f);
			System.out.println(ini.values());
			System.out.println(ini.fetch("Motion Profile", "max vel"));

			ini.put("Motion Profile", "algorithm", "Constant Jerk");
			ini.put("Motion Profile", "max vel", MAX_VEL);
			ini.put("Motion Profile", "max accel", MAX_ACCEL);
			ini.put("Motion Profile", "max jerk", MAX_JERK);
			ini.store();

		} catch (IOException e2) {
			e2.printStackTrace();
		}

	}

}
