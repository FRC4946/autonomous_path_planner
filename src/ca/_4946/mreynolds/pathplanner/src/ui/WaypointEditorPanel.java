package ca._4946.mreynolds.pathplanner.src.ui;

import java.text.DecimalFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ca._4946.mreynolds.pathplanner.src.data.point.Waypoint;
import ca._4946.mreynolds.pathplanner.src.io.FileIO;

public class WaypointEditorPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final DecimalFormat f = FileIO.f;

	private Waypoint waypoint;

	/**
	 * Create the panel.
	 */
	public WaypointEditorPanel(Waypoint pt) {
		waypoint = pt;
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		JLabel xLbl = new JLabel("X val:");
		JLabel xVal = new JLabel("" + f.format(waypoint.getX()));
		JLabel yLbl = new JLabel("Y val:");
		JLabel yVal = new JLabel("" + f.format(waypoint.getY()));

		JLabel headingLbl = new JLabel("Heading:");
		JLabel headingVal = new JLabel("" + f.format(waypoint.getHeading()));

		add(xLbl);
		add(xVal);
		add(Box.createHorizontalGlue());
		add(yLbl);
		add(yVal);
		add(Box.createHorizontalGlue());
		add(headingLbl);
		add(headingVal);
	}
}
