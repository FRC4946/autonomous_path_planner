package ca._4946.mreynolds.pathplanner.src;

import java.awt.EventQueue;
import java.io.File;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import ca._4946.mreynolds.customSwing.ErrorPopup;
import ca._4946.mreynolds.pathplanner.src.data.Script;
import ca._4946.mreynolds.pathplanner.src.data.ScriptBundle;
import ca._4946.mreynolds.pathplanner.src.io.FileIO;
import ca._4946.mreynolds.pathplanner.src.ui.PrimaryWindow;

public class PathPlanner {

	private ScriptBundle scBundle = new ScriptBundle();

	public boolean fieldIsBlue = true;

	public String gameData = "ll";

	public static PathPlanner main;
	private PrimaryWindow window;

	public PathPlanner() {

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

	public Script[] getScripts() {
		return scBundle.asArray();
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

		if (!scBundle.validateParallelActions()) {
			ErrorPopup.createPopup("Invalid Script", "Script contains illegal parallel actions!");
			return;
		}

		try {
			FileIO.saveScript(scBundle, file);
		} catch (Exception e) {
			ErrorPopup.createPopup("Error saving file", e);
		}
	}

	public void upload(File file) {

		if (!scBundle.validateParallelActions()) {
			ErrorPopup.createPopup("Invalid Script", "Script contains illegal parallel actions!");
			return;
		}

		try {
			FileIO.uploadScript(scBundle, file);
		} catch (Exception e) {
			ErrorPopup.createPopup("Error uploading file", e);
		}
	}

}
