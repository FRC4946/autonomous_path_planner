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

	private ScriptBundle m_scBundle = new ScriptBundle();
	private boolean m_fieldIsBlue = true;
	private String m_gameData = "ll";
	private PrimaryWindow m_window;

	private static PathPlanner m_instance;

	private PathPlanner() {

		FileIO.createDefaultDir();
		
		// Set the project's Look And Feel to the default cross-platform (Metal)
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}

		EventQueue.invokeLater(() -> {
			try {
				m_window = new PrimaryWindow();
				m_window.setVisible(true);
				m_window.getFieldPanel().setScript(getScript(), getGameData());
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
		m_scBundle.pushHistory(m_gameData);
	}

	public void undo() {
		m_scBundle.popHistory(m_gameData);
		m_window.getFieldPanel().setScript(getScript(), getGameData());
		m_window.getControlPanel().setupListeners();
	}

	public void open(File file) {
		try {
			m_scBundle = FileIO.loadScript(file);
		} catch (Exception e) {
			ErrorPopup.createPopup("Error loading file", e);
		}

		m_gameData = "ll";
		m_window.getFieldPanel().setScript(getScript(), getGameData());
	}

	public void save(File file) {

		if (!m_scBundle.validateParallelActions()) {
			ErrorPopup.createPopup("Invalid Script", "Script contains illegal parallel actions!");
			return;
		}

		try {
			FileIO.saveScript(m_scBundle, file);
		} catch (Exception e) {
			ErrorPopup.createPopup("Error saving file", e);
		}
	}

	public void upload(File file) {

		if (!m_scBundle.validateParallelActions()) {
			ErrorPopup.createPopup("Invalid Script", "Script contains illegal parallel actions!");
			return;
		}

		try {
			FileIO.uploadScript(m_scBundle, file);
		} catch (Exception e) {
			ErrorPopup.createPopup("Error uploading file", e);
		}
	}

	public Script[] getScripts() {
		return m_scBundle.asArray();
	}

	public Script getScript(String code) {
		return m_scBundle.getScript(code);
	}

	public Script getScript() {
		return getScript(m_gameData);
	}

	public void setScript(Script newScript, String code) {
		setGameData(code.toLowerCase().substring(0, 2));
		m_scBundle.setScript(newScript, getGameData());
		m_window.getFieldPanel().setScript(getScript(), getGameData());
	}

	public void setScript(Script newScript) {
		setScript(newScript, m_gameData);
	}

	public void setScriptName(String name) {
		m_scBundle.name = name;
	}

	public String getScriptName() {
		return m_scBundle.name;
	}

	public void setScriptNotes(String notes) {
		m_scBundle.notes = notes;
	}

	public String getScriptNotes() {
		return m_scBundle.notes;
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
		m_window.getFieldPanel().setBlue(isBlue);
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
		m_window.getFieldPanel().setScript(getScript(), getGameData());
	}

	/**
	 * @return the main PathPlanner instance
	 */
	public static PathPlanner getInstance() {
		if (m_instance == null)
			m_instance = new PathPlanner();
		return m_instance;
	}

}
