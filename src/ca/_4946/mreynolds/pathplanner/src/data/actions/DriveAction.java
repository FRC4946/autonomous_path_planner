package ca._4946.mreynolds.pathplanner.src.data.actions;

import java.util.ArrayList;

import ca._4946.mreynolds.pathplanner.src.PathPlannerSettings;
import ca._4946.mreynolds.pathplanner.src.data.Script;
import ca._4946.mreynolds.pathplanner.src.data.Segment;
import ca._4946.mreynolds.pathplanner.src.data.point.Waypoint;
import ca._4946.mreynolds.pathplanner.src.math.CubicBezier;
import ca._4946.mreynolds.pathplanner.src.math.PathParser;
import ca._4946.mreynolds.util.ObservableList;

public class DriveAction extends Action<DriveAction.Options> {

	public static enum Options implements Action.ActionOptions {
		FollowPath
	}

	private ArrayList<Segment> left;
	private ArrayList<Segment> right;
	private ObservableList<Waypoint> waypoints;
	private ArrayList<CubicBezier> curves;

	public DriveAction() {
		this(Options.FollowPath);
	}

	public DriveAction(Options options) {
		super(options);
		waypoints = new ObservableList<>();
		left = new ArrayList<>();
		right = new ArrayList<>();
		curves = new ArrayList<>();

		waypoints.addListListener(() -> fireElementChanged());
	}

	public Waypoint getPt(int index) {
		return waypoints.get(index);
	}

	public int getNumPts() {
		return waypoints.size();
	}

	public boolean isEmpty() {
		return waypoints.isEmpty();
	}

	public double getDuration() {
		return getLeftPath().size() * PathPlannerSettings.SAMPLE_PERIOD;
	}

	public void removePt(int index) {
		if (index < 0 || index >= waypoints.size())
			return;

		// If this isn't the first or last pt...
		if (0 < index && index < waypoints.size() - 1) {
			getCurves().remove(index);
			getCurves().get(index - 1).updateEnd(waypoints.get(index + 1));
		}

		// If this is the first...
		else if (0 == index) {
			if (getCurves().size() > 0)
				getCurves().remove(index);
		}
		// If this is the last...
		else if (index == waypoints.size() - 1)
			getCurves().remove(index - 1);

		waypoints.remove(index);
	}

	public void addPt(Waypoint pt) {
		addPt(waypoints.size(), pt);
	}

	public void addPt(int index, Waypoint pt) {
		if (index < 0 || index > waypoints.size())
			return;

		waypoints.quiet();
		waypoints.add(index, pt);

		// If this isn't the first or last pt...
		if (0 <= index && index < waypoints.size() - 1) {
			getCurves().add(index, new CubicBezier(pt, waypoints.get(index + 1)));
			getCurves().get(index - 1).updateEnd(pt);
		}

		// If this is the last...
		if (index == waypoints.size() - 1 && waypoints.size() > 1)
			getCurves().add(index - 1, new CubicBezier(waypoints.get(index - 1), pt));

		fireElementChanged();
	}

	public void setPt(int index, Waypoint pt) {
		if (index < 0 || index > waypoints.size())
			return;

		waypoints.quiet();
		if (index == waypoints.size())
			waypoints.add(pt);
		else
			waypoints.set(index, pt);

		if (index > 0)
			getCurves().get(index - 1).updateEnd(pt);

		if (index < waypoints.size() - 1)
			getCurves().get(index).updateStart(pt);

		fireElementChanged();
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
			getLeftPath().add(seg);
		else
			getRightPath().add(seg);
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
		left = newPath.getLeftPath();
		right = newPath.getRightPath();
	}

	public void clear() {
		getLeftPath().clear();
		getRightPath().clear();
		waypoints.clear();
		getCurves().clear();
	}

	@Override
	public String getName() {
		return "Drive";
	}

	@Override
	public String getDataLabel() {
		return "Reverse";
	}

	@Override
	public Options getDefaultOption() {
		return Options.FollowPath;
	}

	@Override
	public DriveAction clone() {
		DriveAction a = new DriveAction();
		a.options = options;
		a.behaviour = behaviour;
		a.delay = delay;
		a.timeout = timeout;
		a.data = data;

		for (Waypoint pt : waypoints)
			a.waypoints.add(pt.clone());

		for (CubicBezier c : getCurves())
			a.getCurves().add(c.clone());

		a.generatePath();
		return a;
	}

	/**
	 * @return the left path
	 */
	public ArrayList<Segment> getLeftPath() {
		return left;
	}

	/**
	 * @return the right path
	 */
	public ArrayList<Segment> getRightPath() {
		return right;
	}

	/**
	 * @return the bezier curves
	 */
	public ArrayList<CubicBezier> getCurves() {
		return curves;
	}

	private void updateCurves() {

		getCurves().get(0).updateStart(waypoints.get(0));

		for (int i = 1; i < waypoints.size() - 1; i++) {
			getCurves().get(i - 1).updateEnd(waypoints.get(i));
			getCurves().get(i).updateStart(waypoints.get(i));
		}

		getCurves().get(waypoints.size() - 2).updateEnd(waypoints.get(waypoints.size() - 1));

	}
}
