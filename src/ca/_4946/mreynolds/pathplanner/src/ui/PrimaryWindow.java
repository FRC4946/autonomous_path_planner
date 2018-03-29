package ca._4946.mreynolds.pathplanner.src.ui;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import ca._4946.mreynolds.pathplanner.src.PathPlannerSettings;
import ca._4946.mreynolds.pathplanner.src.ui.aboutDialogs.AboutAppDialog;

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

			// Add the Help menu to the menu bar
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
}
