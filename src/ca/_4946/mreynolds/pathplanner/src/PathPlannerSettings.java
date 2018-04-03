package ca._4946.mreynolds.pathplanner.src;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

import org.ini4j.Ini;

import ca._4946.mreynolds.pathplanner.src.data.point.MagnetPoint;
import ca._4946.mreynolds.pathplanner.src.data.profiles.ConstantJerkProfile;
import ca._4946.mreynolds.pathplanner.src.data.profiles.MotionProfile;
import ca._4946.mreynolds.pathplanner.src.io.FileIO;

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

	public static void loadSettings(File newFile) {
		Preferences prefs = Preferences.userNodeForPackage(PathPlanner.class);
		prefs.put("Profile Path", newFile.getAbsolutePath());

		loadSettings();
	}
	
	public static void loadSettings() {

		try {
			File f = getFile();

			// If the default file does not exist, create a default file and save it
			if (!f.exists()) {
				f.createNewFile();
				Ini ini = new Ini(f);
				m_motionProfile = new ConstantJerkProfile();
				m_motionProfile.saveToIni(ini);
				ini.put("Robot", "Wheel Radius", WHEEL_WIDTH_IN);
				ini.store();
			}

			// Assuming it does exist, simply load its values
			else {
				Ini ini = new Ini(f);
				if (m_motionProfile == null)
					m_motionProfile = new ConstantJerkProfile();

				m_motionProfile.loadFromIni(ini);
				WHEEL_WIDTH_IN = Double.parseDouble(ini.get("Robot", "Wheel Radius"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void saveSettings(File newFile) {
		Preferences prefs = Preferences.userNodeForPackage(PathPlanner.class);
		prefs.put("Profile Path", newFile.getAbsolutePath());

		saveSettings();
	}

	public static void saveSettings() {
		try {
			File f = getFile();

			if (!f.exists())
				f.createNewFile();

			Ini ini = new Ini(f);
			m_motionProfile.saveToIni(ini);
			ini.put("Robot", "Wheel Radius", WHEEL_WIDTH_IN);
			ini.store();

		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}

	public static MotionProfile getMotionProfile() {
		return m_motionProfile;
	}

	private static File getFile() throws IOException {
		Preferences prefs = Preferences.userNodeForPackage(PathPlanner.class);
		File f = new File(prefs.get("Profile Path", FileIO.PROFILE_DIR + "/default.ini"));
		return f;
	}

}
