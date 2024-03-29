package ca._4946.mreynolds.pathplanner.src;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import org.ini4j.Ini;

import ca._4946.mreynolds.pathplanner.src.data.point.MagnetPoint;
import ca._4946.mreynolds.pathplanner.src.data.profiles.ConstantJerkProfile;
import ca._4946.mreynolds.pathplanner.src.data.profiles.MotionProfile;

public class PathPlannerSettings {

	public static final double SAMPLE_PERIOD = 0.02; // 20ms
	public static final String APP_VERSION = "1.1.0";
	public static final double ROBOT_LENGTH_IN = 39.375;
	public static final double ROBOT_WIDTH_IN = 34.375;
	public static double WHEEL_WIDTH_IN = 28;

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
		Preferences prefs = Preferences.userNodeForPackage(PathPlanner.class);
		WHEEL_WIDTH_IN = prefs.getDouble("Wheel Radius", WHEEL_WIDTH_IN);

		if (m_motionProfile == null)
			m_motionProfile = new ConstantJerkProfile();

		Map<String, String> profile = m_motionProfile.exportProfile();
		for (String key : profile.keySet())
			profile.put(key, prefs.get(key, profile.get(key)));
		m_motionProfile.importProfile(profile);
	}

	public static void saveSettings() {
		Preferences prefs = Preferences.userNodeForPackage(PathPlanner.class);
		prefs.put("Wheel Radius", "" + WHEEL_WIDTH_IN);

		Map<String, String> profile = m_motionProfile.exportProfile();
		for (String key : profile.keySet())
			prefs.put(key, profile.get(key));
	}

	public static void importSettings(File f) {
		try {
			Ini ini = new Ini(f);

			if (m_motionProfile == null)
				m_motionProfile = new ConstantJerkProfile();

			Map<String, String> profile = m_motionProfile.exportProfile();
			for (String key : profile.keySet())
				profile.put(key, ini.get("Motion Profile", key));
			m_motionProfile.importProfile(profile);

			WHEEL_WIDTH_IN = Double.parseDouble(ini.get("Robot", "Wheel Radius"));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void exportSettings(File f) {
		try {
			if (f.exists())
				f.delete();
			f.createNewFile();

			Ini ini = new Ini(f);
			Map<String, String> profile = m_motionProfile.exportProfile();
			for (String key : profile.keySet())
				ini.put("Motion Profile", key, profile.get(key));
			ini.put("Robot", "Wheel Radius", WHEEL_WIDTH_IN);
			ini.store();

		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}

	public static MotionProfile getMotionProfile() {
		return m_motionProfile;
	}
}
