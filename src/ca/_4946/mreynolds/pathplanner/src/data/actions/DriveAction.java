package ca._4946.mreynolds.pathplanner.src.data.actions;

import java.util.ArrayList;

import ca._4946.mreynolds.pathplanner.src.PathPlannerSettings;
import ca._4946.mreynolds.pathplanner.src.data.Script;
import ca._4946.mreynolds.pathplanner.src.data.Segment;
import ca._4946.mreynolds.pathplanner.src.data.point.ControlPoint;
import ca._4946.mreynolds.pathplanner.src.math.CubicBezier;
import ca._4946.mreynolds.pathplanner.src.math.PathParser;
import ca._4946.mreynolds.util.ObservableList;

public class DriveAction extends Action<DriveAction.Options> {

	public static enum Options implements Action.ActionOptions {
		FollowPath
	}

	private ArrayList<Segment> left;
	private ArrayList<Segment> right;
	private ObservableList<ControlPoint> controlpts;
	private ArrayList<CubicBezier> curves;

	public DriveAction() {
		this(Options.FollowPath);
	}

	public DriveAction(Options options) {
		super(options);
		controlpts = new ObservableList<>();
		left = new ArrayList<>();
		right = new ArrayList<>();
		curves = new ArrayList<>();

		controlpts.addListListener(() -> fireElementChanged());
	}

	public ControlPoint getPt(int index) {
		return controlpts.get(index);
	}

	public int getNumPts() {
		return controlpts.size();
	}

	public boolean isEmpty() {
		return controlpts.isEmpty();
	}

	public double getDuration() {
		return getLeftPath().size() * PathPlannerSettings.SAMPLE_PERIOD;
	}

	public void removePt(int index) {
		if (index < 0 || index >= controlpts.size())
			return;

		// If this isn't the first or last pt...
		if (0 < index && index < controlpts.size() - 1) {
			getCurves().remove(index);
			getCurves().get(index - 1).updateEnd(controlpts.get(index + 1));
		}

		// If this is the first...
		else if (0 == index) {
			if (getCurves().size() > 0)
				getCurves().remove(index);
		}
		// If this is the last...
		else if (index == controlpts.size() - 1)
			getCurves().remove(index - 1);

		controlpts.remove(index);
	}

	public void addPt(ControlPoint pt) {
		addPt(controlpts.size(), pt);
	}

	public void addPt(int index, ControlPoint pt) {
		if (index < 0 || index > controlpts.size())
			return;

		controlpts.quiet();
		controlpts.add(index, pt);

		// If this isn't the first or last pt...
		if (0 <= index && index < controlpts.size() - 1) {
			getCurves().add(index, new CubicBezier(pt, controlpts.get(index + 1)));
			getCurves().get(index - 1).updateEnd(pt);
		}

		// If this is the last...
		if (index == controlpts.size() - 1 && controlpts.size() > 1)
			getCurves().add(index - 1, new CubicBezier(controlpts.get(index - 1), pt));

		fireElementChanged();
	}

	public void setPt(int index, ControlPoint pt) {
		if (index < 0 || index > controlpts.size())
			return;

		controlpts.quiet();
		if (index == controlpts.size())
			controlpts.add(pt);
		else
			controlpts.set(index, pt);

		if (index > 0)
			getCurves().get(index - 1).updateEnd(pt);

		if (index < controlpts.size() - 1)
			getCurves().get(index).updateStart(pt);

		fireElementChanged();
	}

	public void connectToPrev(Script sc) {
		int index = sc.indexOf(this);
		if (index < 0)
			return;

		for (int i = index - 1; i >= 0; i--) {
			if (sc.getAction(i) instanceof DriveAction) {
				ObservableList<ControlPoint> pts = ((DriveAction) sc.getAction(i)).controlpts;
				if (pts.size() < 1)
					continue;

				ControlPoint origin = pts.get(pts.size() - 1);
				if (controlpts.get(0) != origin) {
					controlpts.add(0, origin);
					controlpts.get(0).setAutomaticHeading(false);
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
		if (controlpts.size() < 2) {
			left = new ArrayList<>();
			right = new ArrayList<>();
			return;
		}

		controlpts.get(0).updateAutoHeading(controlpts.get(0), controlpts.get(1));
		controlpts.get(controlpts.size() - 1).updateAutoHeading(controlpts.get(controlpts.size() - 2),
				controlpts.get(controlpts.size() - 1));

		for (int i = 1; i < controlpts.size() - 1; i++)
			controlpts.get(i).updateAutoHeading(controlpts.get(i - 1), controlpts.get(i + 1));

		updateCurves();
		DriveAction newPath = PathParser.generatePath(PathParser.smoothPath(this));
		left = newPath.getLeftPath();
		right = newPath.getRightPath();
	}

	public void clear() {
		getLeftPath().clear();
		getRightPath().clear();
		controlpts.clear();
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

		for (ControlPoint pt : controlpts)
			a.controlpts.add(pt.clone());

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

		getCurves().get(0).updateStart(controlpts.get(0));

		for (int i = 1; i < controlpts.size() - 1; i++) {
			getCurves().get(i - 1).updateEnd(controlpts.get(i));
			getCurves().get(i).updateStart(controlpts.get(i));
		}

		getCurves().get(controlpts.size() - 2).updateEnd(controlpts.get(controlpts.size() - 1));

	}
}
