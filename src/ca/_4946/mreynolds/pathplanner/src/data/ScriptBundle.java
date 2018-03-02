package ca._4946.mreynolds.pathplanner.src.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca._4946.mreynolds.pathplanner.src.data.actions.Action;
import ca._4946.mreynolds.pathplanner.src.data.actions.DriveAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.IntakeAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.OutputAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.TurnAction;

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

	public Script LL = new Script();
	public Script LR = new Script();
	public Script RL = new Script();
	public Script RR = new Script();

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
			return LL;
		case "lr":
			return LR;
		case "rl":
			return RL;
		case "rr":
			return RR;
		}

		return null;
	}

	/**
	 * @return a {@code Script} array {LL, LR, RL, RR}
	 */
	public Script[] asArray() {
		return new Script[] { LL, LR, RL, RR };
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
