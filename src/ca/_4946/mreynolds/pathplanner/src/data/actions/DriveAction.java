package ca._4946.mreynolds.pathplanner.src.data.actions;

import java.util.ArrayList;

import ca._4946.mreynolds.pathplanner.src.PathPlannerSettings;
import ca._4946.mreynolds.pathplanner.src.data.CubicBezier;
import ca._4946.mreynolds.pathplanner.src.data.Segment;
import ca._4946.mreynolds.pathplanner.src.data.point.ControlPoint;
import ca._4946.mreynolds.pathplanner.src.math.PathParser;
import ca._4946.mreynolds.pathplanner.src.math.TrapezoidMotionProfile;
import ca._4946.mreynolds.util.ObservableList;

/**
 * An {@link Action} describing driving and navigation
 * 
 * @author Matthew Reynolds
 *
 */
public class DriveAction extends Action<DriveAction.Option> {

	/**
	 * <li>{@link Option#FollowPath} follows a path generated by a
	 * {@link TrapezoidMotionProfile}
	 *
	 * @author Matthew Reynolds
	 * @see Action.ActionOption
	 */
	public static enum Option implements Action.ActionOption {
		FollowPath, Detached
	}

	private ArrayList<Segment> m_left;
	private ArrayList<Segment> m_right;
	private ObservableList<ControlPoint> m_controlpts;
	private ArrayList<CubicBezier> m_curves;
	public boolean isValid = true;

	/**
	 * Create a {@code DriveAction} with:
	 * <li>Default {@link Option} of {@link Option#FollowPath}
	 * <li>Default {@link Behaviour} of {@link Behaviour#kSequential}
	 */
	public DriveAction() {
		this(Option.FollowPath);
	}

	/**
	 * Create a {@code DriveAction} with:
	 * <li>The specified {@link Option}
	 * <li>Default {@link Behaviour} of {@link Behaviour#kSequential}
	 * 
	 * @param options
	 *            the desired {@code Option}
	 */
	public DriveAction(Option options) {
		super(options);
		m_controlpts = new ObservableList<>();
		m_left = new ArrayList<>();
		m_right = new ArrayList<>();
		m_curves = new ArrayList<>();

		m_controlpts.addListListener(() -> fireElementChanged());
	}

	/**
	 * Get the {@link ControlPoint} at the specified index
	 * 
	 * @param index
	 *            the {@code index} to retrieve
	 * @return the {@code ControlPoint}
	 */
	public ControlPoint getPt(int index) {
		return m_controlpts.get(index);
	}

	/**
	 * @return the number of {@link ControlPoints} on this path
	 */
	public int getNumPts() {
		return m_controlpts.size();
	}

	/**
	 * @return {@code true} if the path contains no {@link ControlPoints}
	 */
	public boolean isEmpty() {
		return m_controlpts.isEmpty();
	}

	/**
	 * @return the time it will take to navigate this path, in seconds
	 */
	public double getDuration() {
		return getLeftPath().size() * PathPlannerSettings.SAMPLE_PERIOD;
	}

	/**
	 * @param index
	 *            the index of the {@link ControlPoint} to remove
	 */
	public void removePt(int index) {
		if (index < 0 || index >= m_controlpts.size())
			return;

		// If this isn't the first or last pt, remove the curve that originates at this
		// point
		if (0 < index && index < m_controlpts.size() - 1) {
			m_curves.remove(index);
			m_curves.get(index - 1).updateEnd(m_controlpts.get(index + 1));
		}

		// If this is the first point, only remove the first curve if it exists
		else if (0 == index) {
			if (m_curves.size() > 0)
				m_curves.remove(index);
		}
		// If this is the last point, remove the last curve
		else if (index == m_controlpts.size() - 1)
			m_curves.remove(index - 1);

		// Remove the point
		m_controlpts.remove(index);
	}

	/**
	 * Add a {@link ControlPoint} to the end of the path
	 * 
	 * @param pt
	 *            the {@code ControlPoint} to add
	 */
	public void addPt(ControlPoint pt) {
		addPt(m_controlpts.size(), pt);
	}

	/**
	 * Add a {@link ControlPoint} to the specified location on the path
	 * 
	 * @param index
	 *            the location to insert the new {@code ControlPoint}
	 * @param pt
	 *            the {@code ControlPoint} to add
	 */
	public void addPt(int index, ControlPoint pt) {
		if (index < 0 || index > m_controlpts.size())
			return;

		// Add the element, but do not fire an element changed until we have also
		// updated the curves
		m_controlpts.quiet();
		m_controlpts.add(index, pt);

		// If this is the first pt, insert a new curve at the beginning of the list
		if(index == 0 && m_controlpts.size() > 1) {
			getCurves().add(index, new CubicBezier(pt, m_controlpts.get(index + 1)));
		}
		
		// If this isn't the first or last pt...
		else if (0 < index && index < m_controlpts.size() - 1) {
			getCurves().add(index, new CubicBezier(pt, m_controlpts.get(index + 1)));
			getCurves().get(index - 1).updateEnd(pt);
		}

		// If this is the last...
		if (index == m_controlpts.size() - 1 && m_controlpts.size() > 1)
			getCurves().add(index - 1, new CubicBezier(m_controlpts.get(index - 1), pt));

		fireElementChanged();
	}

	/**
	 * Set the {@link ControlPoint} at index {@code index} to the specified
	 * {@code ControlPoint}
	 * 
	 * @param index
	 *            the index of the {@code ControlPoint} to set
	 * @param pt
	 *            the {@code ControlPoint} to set
	 */
	public void setPt(int index, ControlPoint pt) {
		if (index < 0 || index > m_controlpts.size())
			return;

		m_controlpts.quiet();
		if (index == m_controlpts.size())
			m_controlpts.add(pt);
		else
			m_controlpts.set(index, pt);

		if (index > 0)
			getCurves().get(index - 1).updateEnd(pt);

		if (index < m_controlpts.size() - 1)
			getCurves().get(index).updateStart(pt);

		fireElementChanged();
	}

	/**
	 * Add a {@link Segment} to the generated path to be uploaded
	 * 
	 * @param isL
	 *            {@code true} if this {@code Segment} should be appended to the
	 *            left wheel path
	 * @param seg
	 *            the {@code Segment} to append
	 */
	public void addSegment(boolean isL, Segment seg) {
		if (isL)
			getLeftPath().add(seg);
		else
			getRightPath().add(seg);
	}

	/**
	 * Generate two parallel paths specifying the movement of the left and right
	 * wheels of a differential drive robot. This is the path that will be uploaded
	 * to the robot for it to follow
	 */
	public void generatePath() {

		// If we have less than 2 control points, we can't generate a path. Set both
		// left and right paths empty.
		if (m_controlpts.size() < 2) {
			m_left = new ArrayList<>();
			m_right = new ArrayList<>();
			return;
		}

		// Automatically determine the optimal heading for every control point that has
		// not been manually set or overridden by a magnet
		m_controlpts.get(0).updateAutoHeading(m_controlpts.get(0), m_controlpts.get(1));
		for (int i = 1; i < m_controlpts.size() - 1; i++)
			m_controlpts.get(i).updateAutoHeading(m_controlpts.get(i - 1), m_controlpts.get(i + 1));
		m_controlpts.get(m_controlpts.size() - 1).updateAutoHeading(m_controlpts.get(m_controlpts.size() - 2),
				m_controlpts.get(m_controlpts.size() - 1));

		// Update the bezier curves based on these new headings, and generate the math
		// using a TrapezoidMotionProfile
		updateCurves();
		isValid = PathParser.generatePath(this, PathParser.smoothPath(this));
	}

	/**
	 * Remove the left and right paths
	 */
	public void clearGenerated() {
		getLeftPath().clear();
		getRightPath().clear();
	}

	/**
	 * Remove all {@link ControlPoint}s
	 */
	public void clear() {
		getLeftPath().clear();
		getRightPath().clear();
		m_controlpts.clear();
		getCurves().clear();
	}

	@Override
	public void setOptions(Enum<Option> option) {
		Enum<Option> oldOpt = this.option;

		if (oldOpt != option && option == Option.FollowPath)
			addPt(0, new ControlPoint());

		super.setOptions(option);

		// If we're switching from FollowPath to DisconnectedPath, remove the first ctrl
		// point
		if (oldOpt != option && option == Option.Detached)
			removePt(0);

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
	public Option getDefaultOption() {
		return Option.FollowPath;
	}

	@Override
	public DriveAction clone() {
		DriveAction a = new DriveAction();
		a.option = option;
		a.behaviour = behaviour;
		a.delay = delay;
		a.timeout = timeout;
		a.data = data;

		for (ControlPoint pt : m_controlpts)
			a.m_controlpts.add(pt.clone());

		for (CubicBezier c : getCurves())
			a.getCurves().add(c.clone());

		a.generatePath();
		return a;
	}

	/**
	 * @return the left path
	 */
	public ArrayList<Segment> getLeftPath() {
		return m_left;
	}

	/**
	 * @return the right path
	 */
	public ArrayList<Segment> getRightPath() {
		return m_right;
	}

	/**
	 * @return the bezier curves
	 */
	public ArrayList<CubicBezier> getCurves() {
		return m_curves;
	}

	/**
	 * Update the list of {@link CubicBezier}s specified by the
	 * {@link ControlPoint}s
	 */
	private void updateCurves() {
		getCurves().get(0).updateStart(m_controlpts.get(0));

		for (int i = 1; i < m_controlpts.size() - 1; i++) {
			getCurves().get(i - 1).updateEnd(m_controlpts.get(i));
			getCurves().get(i).updateStart(m_controlpts.get(i));
		}

		getCurves().get(m_controlpts.size() - 2).updateEnd(m_controlpts.get(m_controlpts.size() - 1));
	}
}
