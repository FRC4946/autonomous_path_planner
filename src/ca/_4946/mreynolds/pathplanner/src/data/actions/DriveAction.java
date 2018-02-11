package ca._4946.mreynolds.pathplanner.src.data.actions;

import java.util.ArrayList;

import ca._4946.mreynolds.pathplanner.src.data.Script;
import ca._4946.mreynolds.pathplanner.src.data.Segment;
import ca._4946.mreynolds.pathplanner.src.data.point.Waypoint;
import ca._4946.mreynolds.pathplanner.src.math.PathParser;
import ca._4946.mreynolds.util.ObservableList;

public class DriveAction extends Action<DriveAction.Options> {

	public static enum Options implements Action.ActionOptions {
		kDrive
	}

	public ArrayList<Segment> left;
	public ArrayList<Segment> right;
	public ObservableList<Waypoint> waypoints = new ObservableList<>();
	// public ArrayList<CubicBezier> curves = new ArrayList<>();

	public DriveAction() {
		this(Options.kDrive);
	}

	public DriveAction(Options options) {
		super(options);
		left = new ArrayList<>();
		right = new ArrayList<>();
		// curves = new ArrayList<>();
	}

	public void connectToPrev(Script sc) {
		int index = sc.indexOf(this);
		if (index < 0)
			return;

		for (int i = index - 1; i >= 0; i--) {
			if (sc.getAction(i) instanceof DriveAction) {
				ObservableList<Waypoint> pts = ((DriveAction) sc.getAction(i)).waypoints;
				if (pts.size() < 1)
					continue;

				Waypoint origin = pts.get(pts.size() - 1);
				if (waypoints.get(0) != origin) {
					waypoints.add(0, origin);
					waypoints.get(0).setAutomaticHeading(false);
				}
			}
		}
	}

	public void addSegment(boolean isL, Segment seg) {
		if (isL)
			left.add(seg);
		else
			right.add(seg);
	}

	public void generatePath() {
		if (waypoints.size() < 2) {
			left = new ArrayList<>();
			right = new ArrayList<>();
			return;
		}

		waypoints.get(0).updateAutoHeading(waypoints.get(0), waypoints.get(1));
		waypoints.get(waypoints.size() - 1).updateAutoHeading(waypoints.get(waypoints.size() - 2),
				waypoints.get(waypoints.size() - 1));

		for (int i = 1; i < waypoints.size() - 1; i++)
			waypoints.get(i).updateAutoHeading(waypoints.get(i - 1), waypoints.get(i + 1));

		DriveAction newPath = PathParser.generatePath(PathParser.smoothPath(waypoints));
		left = newPath.left;
		right = newPath.right;
	}

	public void clear() {
		left.clear();
		right.clear();
		waypoints.clear();
	}

	@Override
	public String getName() {
		return "Drive";
	}

	@Override
	public String getDataLabel() {
		return "Reverse?";
	}
}
