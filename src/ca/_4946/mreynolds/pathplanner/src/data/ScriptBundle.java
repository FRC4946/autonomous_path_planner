package ca._4946.mreynolds.pathplanner.src.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca._4946.mreynolds.pathplanner.src.data.actions.Action;
import ca._4946.mreynolds.pathplanner.src.data.actions.DriveAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.IntakeAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.OutputAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.TurnAction;
import ca._4946.mreynolds.util.FixedStack;

/**
 * A bundle of 4 {@link Script}s, one representing each of the 4 possible field
 * configurations ("LLL", "LRL", "RLR", "RRR")
 * 
 * @author Matthew Reynolds
 *
 */
public class ScriptBundle {
	public String name = "";
	public String notes = "";

	private int HISTORY_LENGTH = 10;
	FixedStack<Script> m_llStack = new FixedStack<>(HISTORY_LENGTH);
	FixedStack<Script> m_lrStack = new FixedStack<>(HISTORY_LENGTH);
	FixedStack<Script> m_rlStack = new FixedStack<>(HISTORY_LENGTH);
	FixedStack<Script> m_rrStack = new FixedStack<>(HISTORY_LENGTH);

	private Script m_llScript = new Script();
	private Script m_lrScript = new Script();
	private Script m_rlScript = new Script();
	private Script m_rrScript = new Script();

	public void pushHistory(String code) {
		switch (code.toLowerCase()) {
		case "ll":
			m_llStack.push(new Script(m_llScript));
			break;
		case "lr":
			m_lrStack.push(new Script(m_lrScript));
			break;
		case "rl":
			m_rlStack.push(new Script(m_rlScript));
			break;
		case "rr":
			m_rrStack.push(new Script(m_rrScript));
			break;
		default:
			return;
		}
	}

	public void popHistory(String code) {
		switch (code.toLowerCase()) {
		case "ll":
			if (!m_llStack.isEmpty())
				m_llScript = m_llStack.pop();
		case "lr":
			if (!m_lrStack.isEmpty())
				m_lrScript = m_lrStack.pop();
			break;
		case "rl":
			if (!m_rlStack.isEmpty())
				m_rlScript = m_rlStack.pop();
			break;
		case "rr":
			if (!m_rrStack.isEmpty())
				m_rrScript = m_rrStack.pop();
			break;
		}
	}

	/**
	 * Set the specified script
	 * 
	 * @param newScript
	 *            the new {@link Script}
	 * @param code
	 *            the code of the {@code Script} to set
	 */
	public void setScript(Script newScript, String code) {
		switch (code.toLowerCase()) {
		case "ll":
			m_llScript = newScript;
		case "lr":
			m_lrScript = newScript;
		case "rl":
			m_rlScript = newScript;
		case "rr":
			m_rrScript = newScript;
		}
	}

	/**
	 * Get the {@link Script} for the specified field configuration
	 * 
	 * @param code
	 *            the field config. Must be one of "ll", "lr", "rl", "rr"
	 * @return the specified {@code Script}, or {@code null} if the specified
	 *         {@code code} was invalid
	 */
	public Script getScript(String code) {
		switch (code.toLowerCase()) {
		case "ll":
			return m_llScript;
		case "lr":
			return m_lrScript;
		case "rl":
			return m_rlScript;
		case "rr":
			return m_rrScript;
		}

		return null;
	}

	/**
	 * @return a {@code Script} array {LL, LR, RL, RR}
	 */
	public Script[] asArray() {
		return new Script[] { m_llScript, m_lrScript, m_rlScript, m_rrScript };
	}

	/**
	 * Determine if there are any parallel actions operating on the same subsystem
	 * that would conflict in any of the 4 {@link Script}s in the bundle
	 * 
	 * @return {@code true} if there are no conflicts
	 */
	public boolean validateParallelActions() {
		for (Script s : asArray()) {

			// Add up all of the parallel actions preceding each sequential action
			List<Class<?>> parallelActions = new ArrayList<>();
			for (Action<?> a : s.getActions()) {
				if (a.getBehaviour() == Action.Behaviour.kParallel) {
					parallelActions.add(a.getClass());
					continue;
				}
				parallelActions.add(a.getClass());

				// Equate Drive&Turn, Intake&Output actions since they operate on the same
				// subsystem, and this allows for easy checking of subsystem conflicts
				for (int i = 0; i < parallelActions.size(); i++) {
					if (parallelActions.get(i) == TurnAction.class)
						parallelActions.set(i, DriveAction.class);
					else if (parallelActions.get(i) == OutputAction.class)
						parallelActions.set(i, IntakeAction.class);
				}

				// Check if the actions conflict
				// TODO: Allow parallel actions if they are delayed and not overlapping
				for (Class<?> c : parallelActions) {
					if (Collections.frequency(parallelActions, c) > 1)
						return false;
				}

				// Clear the current list of parallel actions
				parallelActions.clear();
			}
		}

		// If we haven't run into any conflicts, return true
		return true;
	}
}
