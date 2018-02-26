package ca._4946.mreynolds.pathplanner.src.data;

import java.util.ArrayList;

import ca._4946.mreynolds.pathplanner.src.data.actions.Action;
import ca._4946.mreynolds.pathplanner.src.data.actions.ArmAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.DriveAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.TurnAction;
import ca._4946.mreynolds.pathplanner.src.data.point.Waypoint;
import ca._4946.mreynolds.util.ObservableList;

public class Script {

	private ObservableList<Action<?>> script;

	public Script() {
		script = new ObservableList<>();
	}

	public Script(Script s) {
		script = new ObservableList<>();
		for (Action<?> a : s.script)
			script.add(a.clone());
	}

	public void connectPaths() {
		// DriveAction prevDrive = null;
		//
		// // Iterate through each drive action
		// for (DriveAction a : getDriveActions()) {
		//
		// if (prevDrive != null && prevDrive.getNumPts() > 1) {
		// Waypoint pt = new Waypoint(prevDrive.getPt(prevDrive.getNumPts() - 1));
		//
		// // If the isReversed flag differs on the prev and cur action, flip the
		// heading
		// if ((a.data == 1) ^ (prevDrive.data == 1))
		// pt.setHeading(pt.getHeading() - 180);
		//
		// pt.setAutomaticHeading(false);
		// if (a.isEmpty())
		// a.addPt(pt);
		// else
		// a.setPt(0, pt);
		// a.generatePath();
		// }
		// prevDrive = a;
		// }

		double offset = 0;
		DriveAction prevPath = null;
		for (int i = 0; i < script.size(); i++) {
			if (prevPath == null) {
				if (script.get(i) instanceof DriveAction)
					prevPath = (DriveAction) script.get(i);
				continue;
			}

			if (script.get(i) instanceof TurnAction)
				offset += script.get(i).data;
			else if (script.get(i) instanceof DriveAction) {
				Waypoint pt = new Waypoint(prevPath.getPt(prevPath.getNumPts() - 1));

				// If the isReversed flag differs on the prev and cur action, flip the heading
				if ((script.get(i).data == 1) ^ (prevPath.data == 1))
					pt.setHeading(pt.getHeading() - 180);

				pt.setHeading(pt.getHeading() - offset);
				pt.setAutomaticHeading(false);
				((DriveAction) script.get(i)).setPt(0, pt);
				
				prevPath.generatePath();
				((DriveAction) script.get(i)).generatePath();

				prevPath = (DriveAction) script.get(i);
				offset = 0;
			}

		}
	}

	public ObservableList<Action<?>> getActions() {
		return script;
	}

	public void moveActionUp(Action<?> a) {
		int index = script.indexOf(a);
		if (index == 0)
			return;

		script.quiet();
		script.remove(a);
		script.add(index - 1, a);
	}

	public void moveActionDown(Action<?> a) {
		int index = script.indexOf(a);
		if (index == script.size() - 1)
			return;

		script.quiet();
		script.remove(a);
		script.add(index + 1, a);
	}

	public void removeAction(Action<?> a) {
		script.remove(a);
	}

	public void addAction(Action<?> a) {
		if (a instanceof DriveAction && !getDriveActions().isEmpty())
			a.data = (int) getDriveActions().get(getDriveActions().size() - 1).data ^ 1;

		if (a instanceof ArmAction && !getActionOfType(ArmAction.class).isEmpty()) {
			ArmAction.Options opt = ArmAction.Options.valueOf(ArmAction.Options.class,
					getActionOfType(ArmAction.class).get(getActionOfType(ArmAction.class).size() - 1).options
							.toString());
			if (opt == ArmAction.Options.ArmDown)
				((ArmAction) a).options = ArmAction.Options.ArmUp;
			else
				((ArmAction) a).options = ArmAction.Options.ArmDown;
		}
		script.add(a);
	}

	public ArrayList<Action<?>> getActionOfType(Class<?>... types) {

		ArrayList<Action<?>> list = new ArrayList<>();
		for (Action<?> a : script)
			for (Class<?> type : types)
				if (type.isAssignableFrom(a.getClass()))
					list.add(a);

		return list;
	}

	@SuppressWarnings("unchecked")
	public <T extends Action<?>> ArrayList<T> getActionOfType(Class<T> type) {

		ArrayList<T> list = new ArrayList<>();
		for (Action<?> a : script)
			if (type.isAssignableFrom(a.getClass()))
				list.add((T) a);

		return list;
	}

	public ArrayList<DriveAction> getDriveActions() {
		// ArrayList<DriveAction> list = new ArrayList<>();
		// for (DriveAction a : getActionOfType(DriveAction.class))
		// if (a.options == DriveAction.Options.FollowPath)
		// list.add((DriveAction) a);
		// return list;
		return getActionOfType(DriveAction.class);
	}

	public Action<?> getAction(int index) {
		return script.get(index);
	}

	public int indexOf(Action<?> a) {
		return script.indexOf(a);
	}

	public void clear() {
		script.clear();
	}

}
