package ca._4946.mreynolds.pathplanner.src;

import java.awt.EventQueue;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import ca._4946.mreynolds.customSwing.ErrorPopup;
import ca._4946.mreynolds.pathplanner.src.data.Script;
import ca._4946.mreynolds.pathplanner.src.data.ScriptBundle;
import ca._4946.mreynolds.pathplanner.src.data.point.MagnetPoint;
import ca._4946.mreynolds.pathplanner.src.io.FileIO;
import ca._4946.mreynolds.pathplanner.src.ui.PrimaryWindow;

public class PathPlanner {

	public static final double ROBOT_LENGTH_IN = 39.375;
	public static final double ROBOT_WIDTH_IN = 34.375;
	public static final double WHEEL_WIDTH_IN = 32;

	private ScriptBundle scBundle = new ScriptBundle();
	private ArrayList<MagnetPoint> magnets = new ArrayList<>();

	public boolean fieldIsBlue = true;

	public String gameData = "ll";

	public static PathPlanner main;
	private PrimaryWindow window;

	public PathPlanner() {
		magnets.add(new MagnetPoint(-116, ROBOT_LENGTH_IN / 2, 90));
		magnets.add(new MagnetPoint(-73, ROBOT_LENGTH_IN / 2, 90));
		magnets.add(new MagnetPoint(2, ROBOT_LENGTH_IN / 2, 90));
		magnets.add(new MagnetPoint(44, ROBOT_LENGTH_IN / 2, 90));
		magnets.add(new MagnetPoint(73, ROBOT_LENGTH_IN / 2, 90));
		magnets.add(new MagnetPoint(116, ROBOT_LENGTH_IN / 2, 90));

		// Set the project's Look And Feel to the default cross-platform (Metal)
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}

		EventQueue.invokeLater(() -> {
			try {
				window = new PrimaryWindow();
				window.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		main = new PathPlanner();
	}

	public List<MagnetPoint> getMagnets() {
		return Collections.unmodifiableList(magnets);
	}

	public Script[] getScripts() {
		return new Script[] { scBundle.LL, scBundle.LR, scBundle.RL, scBundle.RR };
	}

	public Script getScript(String code) {
		code = code.toLowerCase();

		if (code.contains("ll"))
			return scBundle.LL;
		else if (code.contains("lr"))
			return scBundle.LR;
		else if (code.contains("rl"))
			return scBundle.RL;
		else if (code.contains("rr"))
			return scBundle.RR;

		return null;
	}

	public Script getScript() {
		return getScript(gameData);
	}

	public void setScript(Script newScript, String code) {

		code = code.toLowerCase();
		if (code.contains("ll"))
			scBundle.LL = newScript;
		else if (code.contains("lr"))
			scBundle.LR = newScript;
		else if (code.contains("rl"))
			scBundle.RL = newScript;
		else if (code.contains("rr"))
			scBundle.RR = newScript;
	}

	public void setScript(Script newScript) {
		setScript(newScript, gameData);
	}

	public void setScriptName(String name) {
		scBundle.name = name;
	}

	public String getScriptName() {
		return scBundle.name;
	}

	public void setScriptNotes(String notes) {
		scBundle.notes = notes;
	}

	public String getScriptNotes() {
		return scBundle.notes;
	}

	public void load(File file) {
		try {
			scBundle = FileIO.loadScript(file);
		} catch (Exception e) {
			ErrorPopup.createPopup("Error loading file", e);
		}
	}

	public void save(File file) {
		try {
			FileIO.saveScript(scBundle, file);
		} catch (Exception e) {
			ErrorPopup.createPopup("Error saving file", e);
		}
	}

	public void upload(File file) {
		try {
			FileIO.uploadScript(scBundle, file);
		} catch (Exception e) {
			ErrorPopup.createPopup("Error uploading file", e);
		}
	}

}
