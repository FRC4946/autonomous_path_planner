package ca._4946.mreynolds.pathplanner.src.data;

import ca._4946.mreynolds.pathplanner.src.data.actions.Action;
import ca._4946.mreynolds.pathplanner.src.data.actions.DriveAction;
import ca._4946.mreynolds.pathplanner.src.data.point.Waypoint;
import ca._4946.mreynolds.util.ObservableList;

public class Script {

	private ObservableList<Action<?>> script;

	private int selectedAction = -1;

	public static Script newScript() {
		Script sc = new Script();
		sc.addAction(new DriveAction());
		return sc;
	}

	public Script() {
		script = new ObservableList<>();
	}

	public void connectPaths() {
		DriveAction prevDrive = null;

		for (int i = 0; i < script.size(); i++) {

			Action<?> a = script.get(i);
			if (!(a instanceof DriveAction))
				continue;

			if (prevDrive != null && prevDrive.waypoints.size() > 1) {
				Waypoint pt = new Waypoint(prevDrive.waypoints.get(prevDrive.waypoints.size() - 1));

				if ((a.data == 1) ^ (prevDrive.data == 1))
					pt.setHeading(pt.getHeading() - 180);

				pt.setAutomaticHeading(false);
				if (((DriveAction) a).waypoints.isEmpty())
					((DriveAction) a).waypoints.add(pt);
				else
					((DriveAction) a).waypoints.set(0, pt);
				((DriveAction) a).generatePath();
			}
			prevDrive = (DriveAction) a;
		}
	}

	public ObservableList<Action<?>> getActions() {
		return script;
	}

	public void moveActionUp(Action<?> a) {
		int index = script.indexOf(a);
		if (index == 0)
			return;

		if (index == selectedAction)
			selectedAction = index - 1;
		else if (index - 1 == selectedAction)
			selectedAction = index;

		script.quiet();
		script.remove(a);
		script.add(index - 1, a);

	}

	public void moveActionDown(Action<?> a) {
		int index = script.indexOf(a);
		if (index == script.size() - 1)
			return;

		if (index == selectedAction)
			selectedAction = index + 1;
		else if (index + 1 == selectedAction)
			selectedAction = index;

		script.quiet();
		script.remove(a);
		script.add(index + 1, a);
	}

	public void removeAction(Action<?> a) {

		if (script.indexOf(a) < selectedAction)
			selectedAction -= 1;

		if (a == getSelectedAction())
			selectedAction = -1;

		script.remove(a);
	}

	public void addAction(Action<?> a) {

		if (a instanceof DriveAction)
			selectedAction = script.size();

		script.add(a);
	}

	public void setSelectedAction(Action<?> a) {
		selectedAction = script.indexOf(a);
	}

	public DriveAction getSelectedAction() {
		if (selectedAction == -1)
			return null;
		return (DriveAction) script.get(selectedAction);
	}

	public Action<?> getAction(int index) {
		return script.get(index);
	}

	public int indexOf(Action<?> a) {
		return script.indexOf(a);
	}

	public void clear() {
		selectedAction = -1;
		script.clear();
	}

}
