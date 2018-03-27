package ca._4946.mreynolds.pathplanner.src.data;

import java.util.List;
import java.util.ArrayList;

import ca._4946.mreynolds.pathplanner.src.data.actions.Action;
import ca._4946.mreynolds.pathplanner.src.data.actions.ArmAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.DriveAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.TurnAction;
import ca._4946.mreynolds.pathplanner.src.data.point.ControlPoint;
import ca._4946.mreynolds.util.ObservableElement;
import ca._4946.mreynolds.util.ObservableList;

/**
 * An ordered collection of {@link Actions} representing an autonomous behavior
 * 
 * @author Matthew Reynolds
 *
 */
public class Script extends ObservableElement {
	private ObservableList<Action<?>> m_script;

	/**
	 * Create an empty {@code Script}
	 */
	public Script() {
		m_script = new ObservableList<>();
		m_script.addListListener(() -> {
			connectPaths();
			fireElementChanged();
		});
	}

	/**
	 * Create a {@code Script} with all the same {@code Action}s as s
	 * 
	 * @paam s the {@link Script} to clone
	 */
	public Script(Script s) {
		this();
		for (Action<?> a : s.m_script)
			m_script.add(a.clone());
	}

	/**
	 * Ensure that consecutive paths start/end at the same point, taking into
	 * account alternative directions and any intermediate {@link TurnAction}
	 */
	private void connectPaths() {
		double offset = 0;
		DriveAction prevPath = null;
		for (int i = 0; i < m_script.size(); i++) {
			if (prevPath == null) {
				if (m_script.get(i) instanceof DriveAction) {
					prevPath = (DriveAction) m_script.get(i);
					prevPath.generatePath();
				}
				continue;
			}

			if (m_script.get(i) instanceof TurnAction)
				offset += m_script.get(i).getData();
			else if (m_script.get(i) instanceof DriveAction) {
				if (m_script.get(i).getOption() == DriveAction.Option.Detached) {
					prevPath = (DriveAction) m_script.get(i);
					prevPath.generatePath();
					continue;
				}

				ControlPoint pt = new ControlPoint(prevPath.getPt(prevPath.getNumPts() - 1));

				// If the isReversed flag differs on the prev and cur action, flip the heading
				if ((m_script.get(i).getData() == 1) ^ (prevPath.getData() == 1))
					pt.setHeading(pt.getHeading() - 180);

				pt.setHeading(pt.getHeading() - offset);
				pt.setAutomaticHeading(false);

				if (!((DriveAction) m_script.get(i)).isEmpty())
					pt.setR(((DriveAction) m_script.get(i)).getPt(0).getR());

				m_script.quiet();
				((DriveAction) m_script.get(i)).setPt(0, pt);

				prevPath.generatePath();
				((DriveAction) m_script.get(i)).generatePath();

				prevPath = (DriveAction) m_script.get(i);
				offset = 0;
			}

		}
	}

	/**
	 * @return the {@link ObservableList} of generic {@link Action}
	 */
	public ObservableList<Action<?>> getActions() {
		return m_script;
	}

	/**
	 * Move the specified action up one position in the script. If the script is
	 * already at the top, nothing is done.
	 * 
	 * @param a
	 *            the {@link Action} to move
	 */
	public void moveActionUp(Action<?> a) {
		int index = m_script.indexOf(a);
		if (index == 0)
			return;

		m_script.quiet();
		m_script.remove(a);
		m_script.add(index - 1, a);
	}

	/**
	 * Move the specified action down one position in the script. If the script is
	 * already at the bottom, nothing is done.
	 * 
	 * @param a
	 *            the {@link Action} to move
	 */
	public void moveActionDown(Action<?> a) {
		int index = m_script.indexOf(a);
		if (index == m_script.size() - 1)
			return;

		m_script.quiet();
		m_script.remove(a);
		m_script.add(index + 1, a);
	}

	/**
	 * Remove an Action from the script, and reconnect the paths with
	 * {@link Script#connectPaths()}
	 * 
	 * @param a
	 *            the {@link Action} to remove
	 */
	public void removeAction(Action<?> a) {
		m_script.remove(a);
		connectPaths();
	}

	/**
	 * Add an Action to the script, and reconnect the paths with
	 * {@link Script#connectPaths()} <br>
	 * If the new Action is a {@link DriveAction}, one of the two cases will occur:
	 * <li>If there exists any {@link TurnAction}s between the new and previous
	 * {@code DriveAction}s, use the same direction
	 * <li>If there are no {@code TurnAction}s, reverse the new action <br>
	 * If the new Action is a {@link ArmAction}, the default
	 * {@link ArmAction.Option} will be the opposite of the previous
	 * {@code ArmAction}
	 * 
	 * @param a
	 *            the {@link Action} to add
	 */
	public void addAction(Action<?> a) {

		// If the new action is a DriveAction and there exist previous DriveActions, we
		// need to determine which direction to point the new action.
		if (a instanceof DriveAction && !getDriveActions().isEmpty()) {
			List<Action<?>> actions = getActionOfType(DriveAction.class, TurnAction.class);

			// Determine if there were any TurnActions between then end of the script and
			// the last DriveAction
			boolean didTurn = false;
			for (int i = actions.size() - 1; i >= 0; i--) {
				if (actions.get(i) instanceof TurnAction && actions.get(i).getData() != 0)
					didTurn = true;
				else if (actions.get(i) instanceof DriveAction)
					break;
			}

			// If there were any turns, set the direction to forwards. Otherwise, flip it
			// from the prev.
			int prevDir = (int) getDriveActions().get(getDriveActions().size() - 1).getData();
			a.setData(didTurn ? 0 : prevDir ^ 1);
			connectPaths();

		}

		// If the new action is an ArmAction and there exist previous ArmActions, we
		// need to flip the state of the arm
		else if (a instanceof ArmAction && !getActionOfType(ArmAction.class).isEmpty()) {
			ArmAction.Option opt = ArmAction.Option.valueOf(ArmAction.Option.class, getActionOfType(ArmAction.class)
					.get(getActionOfType(ArmAction.class).size() - 1).getOption().toString());
			if (opt == ArmAction.Option.ArmDown)
				((ArmAction) a).setOptions(ArmAction.Option.ArmUp);
			else
				((ArmAction) a).setOptions(ArmAction.Option.ArmDown);
		}

		// Add the new action to the list
		m_script.add(a);
	}

	/**
	 * Get all of the {@code Action}s in the script of the specified types
	 * 
	 * @param types
	 *            the {@link Class} of every {@link Action} we want for the search
	 *            for
	 * @return an {@link ArrayList} of the {@code Action}s
	 */
	public ArrayList<Action<?>> getActionOfType(Class<?>... types) {

		ArrayList<Action<?>> list = new ArrayList<>();
		for (Action<?> a : m_script)
			for (Class<?> type : types)
				if (type.isAssignableFrom(a.getClass()))
					list.add(a);

		return list;
	}

	/**
	 * Get all of the {@code Action}s in the script of the specified type
	 * 
	 * @param types
	 *            the {@link Class} of the {@link Action} type we want to the search
	 *            for
	 * @return an {@link ArrayList} of the {@code Action}s
	 */
	@SuppressWarnings("unchecked")
	public <T extends Action<?>> ArrayList<T> getActionOfType(Class<T> type) {

		ArrayList<T> list = new ArrayList<>();
		for (Action<?> a : m_script)
			if (type.isAssignableFrom(a.getClass()))
				list.add((T) a);

		return list;
	}

	/**
	 * @return all of the {@link DriveAction}s in the script
	 */
	public ArrayList<DriveAction> getDriveActions() {
		return getActionOfType(DriveAction.class);
	}

	/**
	 * @param index
	 *            the index of the desired {@link Action}
	 * @return the {@code Action} at index {@code index}
	 */
	public Action<?> getAction(int index) {
		return m_script.get(index);
	}

	/**
	 * @param a
	 *            the {@link Action} to search for
	 * @return the index of the specified {@code Action} in the script
	 */
	public int indexOf(Action<?> a) {
		return m_script.indexOf(a);
	}

	/**
	 * Remove all {@code Action}s from the script
	 */
	public void clear() {
		m_script.clear();
	}

}
