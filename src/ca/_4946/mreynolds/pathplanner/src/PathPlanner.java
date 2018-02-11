package ca._4946.mreynolds.pathplanner.src;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import ca._4946.mreynolds.customSwing.ErrorPopup;
import ca._4946.mreynolds.pathplanner.src.data.Script;
import ca._4946.mreynolds.pathplanner.src.data.ScriptBundle;
import ca._4946.mreynolds.pathplanner.src.data.point.MagnetPoint;
import ca._4946.mreynolds.pathplanner.src.io.FileIO;
import ca._4946.mreynolds.pathplanner.src.ui.PrimaryWindow;

public class PathPlanner {

	public static final double ROBOT_LENGTH_IN = 39.375;
	public static final double ROBOT_WIDTH_IN = 34.375;
	public static final double WHEEL_WIDTH_IN = 25.375;

	private ScriptBundle scBundle = new ScriptBundle();
	private ArrayList<MagnetPoint> magnets = new ArrayList<>();

	public boolean fieldIsBlue = true;
	public boolean scaleIsL = true;
	public boolean switchIsL = true;

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

	public void setScriptName(String name) {
		scBundle.name = name;
	}

	public Script[] getScripts() {
		return new Script[] { scBundle.LL, scBundle.LR, scBundle.RL, scBundle.RR };
	}

	public Script getScript() {
		if (switchIsL) {
			if (scaleIsL)
				return scBundle.LL;
			else
				return scBundle.LR;
		} else {
			if (scaleIsL)
				return scBundle.RL;
			else
				return scBundle.RR;
		}
	}

	public String getScriptName() {
		return scBundle.name;
	}

	public void load(File file) {
		try {
			scBundle = FileIO.loadScript(file);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			ErrorPopup.createPopup("Error loading file", e);
		}
	}

	public void save(File file) {
		try {
			FileIO.saveScript(scBundle, file);
		} catch (ParserConfigurationException | TransformerException e) {
			ErrorPopup.createPopup("Error saving file", e);
		}
	}

	public void upload(File file) {
		try {
			FileIO.uploadScript(scBundle, file);
		} catch (ParserConfigurationException | TransformerException | IOException e) {
			ErrorPopup.createPopup("Error uploading file", e);
		}
	}

}
