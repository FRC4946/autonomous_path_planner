package ca._4946.mreynolds.pathplanner.src.data.actions;

import java.util.ArrayList;

import ca._4946.mreynolds.pathplanner.src.data.Script;
import ca._4946.mreynolds.pathplanner.src.data.Segment;
import ca._4946.mreynolds.pathplanner.src.data.point.Waypoint;
import ca._4946.mreynolds.pathplanner.src.math.PathParser;
import ca._4946.mreynolds.pathplanner.src.math.bezier.CubicBezier;
import ca._4946.mreynolds.util.ObservableList;

public class DriveAction extends Action<DriveAction.Options> {

	public static enum Options implements Action.ActionOptions {
		kDrive
	}

	public ArrayList<Segment> left;
	public ArrayList<Segment> right;
	private ObservableList<Waypoint> waypoints = new ObservableList<>();
	public ArrayList<CubicBezier> curves = new ArrayList<>();

	public DriveAction() {
		this(Options.kDrive);
	}

	public DriveAction(Options options) {
		super(options);
		left = new ArrayList<>();
		right = new ArrayList<>();
		curves = new ArrayList<>();
	}

	public Waypoint getPt(int index) {
		return waypoints.get(index);
	}

	public int getNumPts() {
		return waypoints.size();
	}

	public void removePt(int index) {
		if (index < 0 || index >= waypoints.size())
			return;

		// If this isn't the first or last pt...
		if (0 < index && index < waypoints.size() - 1) {
			curves.remove(index);
			curves.get(index - 1).updateEnd(waypoints.get(index + 1));
		}

		// If this is the first...
		else if (0 == index) {
			if (curves.size() > 0)
				curves.remove(index);
		}
		// If this is the last...
		else if (index == waypoints.size() - 1)
			curves.remove(index - 1);

		waypoints.remove(index);
	}

	public void addPt(Waypoint pt) {
		addPt(waypoints.size(), pt);
	}

	public void addPt(int index, Waypoint pt) {
		if (index < 0 || index > waypoints.size())
			return;

		waypoints.add(index, pt);

		// If this isn't the first or last pt...
		if (0 <= index && index < waypoints.size() - 1) {
			curves.add(index, new CubicBezier(pt, waypoints.get(index + 1)));
			curves.get(index - 1).updateEnd(pt);
		}

		// If this is the last...
		if (index == waypoints.size() - 1 && waypoints.size() > 1)
			curves.add(index - 1, new CubicBezier(waypoints.get(index - 1), pt));
	}

	public void setPt(int index, Waypoint pt) {
		if (index < 0 || index >= waypoints.size())
			return;

		waypoints.set(index, pt);

		if (index > 0)
			curves.get(index - 1).updateEnd(pt);

		if (index < waypoints.size() - 1)
			curves.get(index).updateStart(pt);
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

	private void updateCurves() {

		curves.get(0).updateStart(waypoints.get(0));

		for (int i = 1; i < waypoints.size() - 1; i++) {
			curves.get(i - 1).updateEnd(waypoints.get(i));
			curves.get(i).updateStart(waypoints.get(i));
		}

		curves.get(waypoints.size() - 2).updateEnd(waypoints.get(waypoints.size() - 1));

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

		updateCurves();
		DriveAction newPath = PathParser.generatePath(PathParser.smoothPath(this));
		left = newPath.left;
		right = newPath.right;
	}

	public void clear() {
		left.clear();
		right.clear();
		waypoints.clear();
		curves.clear();
	}

	@Override
	public String getName() {
		return "Drive";
	}

	@Override
	public String getDataLabel() {
		return "Reverse";
	}

	public boolean isEmpty() {
		return waypoints.isEmpty();
	}
}
