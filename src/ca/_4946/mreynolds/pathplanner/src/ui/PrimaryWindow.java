package ca._4946.mreynolds.pathplanner.src.ui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import ca._4946.mreynolds.pathplanner.src.PathPlanner;
import ca._4946.mreynolds.pathplanner.src.PathPlannerSettings;
import ca._4946.mreynolds.pathplanner.src.data.Script;
import ca._4946.mreynolds.pathplanner.src.ui.aboutDialogs.AboutAppDialog;
import ca._4946.mreynolds.pathplanner.src.ui.popups.PreferencesDialog;

public class PrimaryWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	private int m_width = 1350;
	private int m_height = 675;

	private FieldPanel m_fieldPanel;
	private ControlPanel m_controlPanel;

	/**
	 * Create the application.
	 */
	public PrimaryWindow() {
		setTitle("Team 4946 - Autonomous Path Planner");
		getContentPane().setPreferredSize(new Dimension(m_width, m_height));
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);

		setupMenu();
		setupUI();
	}

	/**
	 * Setup the menu bar
	 */
	private void setupMenu() {
		// Create the menu bar
		JMenuBar menuBar = new JMenuBar();
		{
			// Create the File menu for the menu bar
			JMenu fileMenu = new JMenu("File");
			{

				JMenuItem newButton = new JMenuItem("New");
				newButton.addActionListener(e -> {
					if (shouldProceed()) {
						for (Script s : PathPlanner.getInstance().getScripts())
							s.clear();
						PathPlanner.getInstance().setScriptName("");
						PathPlanner.getInstance().setScriptNotes("");
					}
				});
				newButton.setAccelerator(
						KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

				JMenuItem openButton = new JMenuItem("Open");
				openButton.addActionListener(e -> {
					if (shouldProceed())
						PathPlanner.getInstance().open();
				});
				openButton.setAccelerator(
						KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

				JMenuItem importButton = new JMenuItem("Import");
				importButton.addActionListener(e -> PathPlanner.getInstance().importScript());
				importButton.setAccelerator(
						KeyStroke.getKeyStroke('I', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

				JMenuItem saveButton = new JMenuItem("Save");
				saveButton.addActionListener(e -> PathPlanner.getInstance().save());
				saveButton.setAccelerator(
						KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

				// Add the JLabel to the About menu
				fileMenu.add(newButton);
				fileMenu.add(openButton);
				fileMenu.add(importButton);
				fileMenu.add(saveButton);

			}

			// Create the Edit menu for the menu bar
			JMenu editMenu = new JMenu("Edit");
			{
				JMenuItem undoButton = new JMenuItem("Undo");
				undoButton.addActionListener(e -> PathPlanner.getInstance().undo());
				undoButton.setAccelerator(
						KeyStroke.getKeyStroke('Z', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

				JMenuItem flipButton = new JMenuItem("Flip");
				flipButton.addActionListener(e -> PathPlanner.getInstance().getScript().flip());
				flipButton.setAccelerator(
						KeyStroke.getKeyStroke('F', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

				JMenuItem clearButton = new JMenuItem("Clear");
				clearButton.setToolTipText("Clear the currently selected script");
				clearButton.addActionListener(e -> PathPlanner.getInstance().getScript().clear());
				// clearButton.setAccelerator(
				// KeyStroke.getKeyStroke('C',
				// Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

				JMenuItem preferencesButton = new JMenuItem("Preferences");
				preferencesButton.addActionListener(e -> {
					new PreferencesDialog().setVisible(true);
					 PathPlannerSettings.saveSettings();
					for (Script s : PathPlanner.getInstance().getScripts())
						s.regenerate();
				});
				preferencesButton.setAccelerator(
						KeyStroke.getKeyStroke(',', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

				editMenu.add(undoButton);
				editMenu.add(flipButton);
				editMenu.add(clearButton);
				editMenu.addSeparator();
				editMenu.add(preferencesButton);

			}

			// Create the About menu for the menu bar
			JMenu helpMenu = new JMenu("Help");
			{
				JMenuItem aboutPathPlanner = new JMenuItem("About Path Planner");
				aboutPathPlanner.addActionListener(e -> new AboutAppDialog().setVisible(true));

				// Create a JLabel containing the version and copyright
				JLabel about = new JLabel(
						"<html>© 2018 The Alpha Dogs <br>Version " + PathPlannerSettings.APP_VERSION + "</html>");

				// Add the JLabel to the About menu
				helpMenu.add(about);
				helpMenu.addSeparator();
				helpMenu.add(aboutPathPlanner);

			}

			menuBar.add(fileMenu);
			menuBar.add(editMenu);
			menuBar.add(helpMenu);
		}

		setJMenuBar(menuBar);
	}

	/**
	 * Create the panel
	 */
	private void setupUI() {
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));

		m_fieldPanel = new FieldPanel(true);
		m_fieldPanel.setMinimumSize(new Dimension((int) (m_height / 1.08556149733), m_height));
		m_fieldPanel.setPreferredSize(new Dimension((int) (m_height / 1.08556149733), m_height));
		m_fieldPanel.setMaximumSize(new Dimension((int) (m_height / 1.08556149733), m_height));

		m_controlPanel = new ControlPanel();

		add(m_fieldPanel);
		add(m_controlPanel);
	}

	public ControlPanel getControlPanel() {
		return m_controlPanel;
	}

	public FieldPanel getFieldPanel() {
		return m_fieldPanel;
	}

	private boolean shouldProceed() {
		boolean shouldWarn = false;
		for (Script s : PathPlanner.getInstance().getScripts())
			if (s.getNumActions() > 0)
				shouldWarn = true;

		if (shouldWarn) {
			int n = JOptionPane.showConfirmDialog(null,
					"This will erase all unsaved changes on all 4 scripts. Are you sure?", "Confirm",
					JOptionPane.YES_NO_OPTION);

			if (n != JOptionPane.YES_OPTION)
				return false;
		}
		return true;
	}
}
