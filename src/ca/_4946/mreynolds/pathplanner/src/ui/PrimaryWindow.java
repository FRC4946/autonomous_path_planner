package ca._4946.mreynolds.pathplanner.src.ui;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

public class PrimaryWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	int width = 1350;
	int height = 675;

	public FieldPanel pathPanel;
	private ControlPanel controlPanel;

	/**
	 * Create the application.
	 */
	public PrimaryWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setTitle("Team 4946 - Autonomous Path Planner");
		getContentPane().setPreferredSize(new Dimension(width, height));
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);

		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));

		pathPanel = new FieldPanel(true);
		pathPanel.setMinimumSize(new Dimension((int) (height / 1.08556149733), height));
		pathPanel.setPreferredSize(new Dimension((int) (height / 1.08556149733), height));
		pathPanel.setMaximumSize(new Dimension((int) (height / 1.08556149733), height));

		controlPanel = new ControlPanel();

		add(pathPanel);
		add(controlPanel);
	}
}
