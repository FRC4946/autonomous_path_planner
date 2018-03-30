package ca._4946.mreynolds.pathplanner.src;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ini4j.Ini;

import ca._4946.mreynolds.pathplanner.src.data.point.MagnetPoint;
import ca._4946.mreynolds.pathplanner.src.data.profiles.ConstantJerkProfile;
import ca._4946.mreynolds.pathplanner.src.data.profiles.MotionProfile;
import ca._4946.mreynolds.pathplanner.src.io.FileIO;

public class PathPlannerSettings {

	public static final double ROBOT_LENGTH_IN = 39.375;
	public static final double ROBOT_WIDTH_IN = 34.375;
	public static double WHEEL_WIDTH_IN = 28;

	public static final double SAMPLE_PERIOD = 0.02; // 20ms
	public static final String APP_VERSION = "1.1.0";

	private static MotionProfile m_motionProfile;

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

	public static void loadSettings() {

		try {
			File f = new File(FileIO.PROFILE_DIR + "/default.ini");

			Ini ini = new Ini(f);
			if (m_motionProfile == null) {
				m_motionProfile = new ConstantJerkProfile();
				m_motionProfile.loadFromIni(ini);
			}
			WHEEL_WIDTH_IN = Double.parseDouble(ini.get("Robot", "Wheel Radius"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void saveSettings() {

		try {
			File f = new File(FileIO.PROFILE_DIR + "/default.ini");
			if (!f.exists())
				f.createNewFile();

			Ini ini = new Ini(f);
			if (m_motionProfile != null)
				m_motionProfile.saveToIni(ini);
			ini.put("Robot", "Wheel Radius", WHEEL_WIDTH_IN);
			ini.store();

		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}

	public static MotionProfile getMotionProfile() {
		if (m_motionProfile == null)
			m_motionProfile = new ConstantJerkProfile();

		return m_motionProfile;
	}

}
