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
	private boolean m_fieldIsBlue = true;
	private String m_gameData = "ll";
	private PrimaryWindow window;

	private static PathPlanner instance;

	private PathPlanner() {

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
				window.getFieldPanel().setScript(getScript(), getGameData());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		PathPlanner.getInstance();
	}

	public void saveState() {
		scBundle.pushHistory(m_gameData);
	}

	public void undo() {
		scBundle.popHistory(m_gameData);
		window.getFieldPanel().setScript(getScript(), getGameData());
		window.getControlPanel().setupListeners();
	}

	public void open(File file) {
		try {
			scBundle = FileIO.loadScript(file);
		} catch (Exception e) {
			ErrorPopup.createPopup("Error loading file", e);
		}

		m_gameData = "ll";
		window.getFieldPanel().setScript(getScript(), getGameData());
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

	public Script[] getScripts() {
		return scBundle.asArray();
	}

	public Script getScript(String code) {
		return scBundle.getScript(code);
	}

	public Script getScript() {
		return getScript(m_gameData);
	}

	public void setScript(Script newScript, String code) {
		setGameData(code.toLowerCase().substring(0, 2));
		scBundle.setScript(newScript, getGameData());
		window.getFieldPanel().setScript(getScript(), getGameData());
	}

	public void setScript(Script newScript) {
		setScript(newScript, m_gameData);
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

	/**
	 * @return the {@code true} if the field is blue
	 */
	public boolean getFieldColor() {
		return m_fieldIsBlue;
	}

	/**
	 * @param isBlue
	 *            {@code true} if the field is blue
	 */
	public void setFieldColor(boolean isBlue) {
		m_fieldIsBlue = isBlue;
		window.getFieldPanel().setBlue(isBlue);
	}

	/**
	 * @return the gameData
	 */
	public String getGameData() {
		return m_gameData;
	}

	/**
	 * @param gameData
	 *            the gameData to set
	 */
	public void setGameData(String gameData) {
		this.m_gameData = gameData;
		window.getFieldPanel().setScript(getScript(), getGameData());
	}

	/**
	 * @return the main PathPlanner instance
	 */
	public static PathPlanner getInstance() {
		if (instance == null)
			instance = new PathPlanner();
		return instance;
	}

}
