package ca._4946.mreynolds.pathplanner.src.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca._4946.mreynolds.pathplanner.src.data.actions.Action;
import ca._4946.mreynolds.pathplanner.src.data.actions.DriveAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.IntakeAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.OutputAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.TurnAction;

public class ScriptBundle {
	public String name = "";
	public String notes = "";

	public Script LL = new Script();
	public Script LR = new Script();
	public Script RL = new Script();
	public Script RR = new Script();

	public Script[] asArray() {
		return new Script[] { LL, LR, RL, RR };
	}

	public boolean validateParallelActions() {
		for (Script s : asArray()) {

			List<Class<?>> parallelActions = new ArrayList<>();
			for (Action<?> a : s.getActions()) {
				if (a.getBehaviour() == Action.Behaviour.kParallel) {
					parallelActions.add(a.getClass());
					continue;
				}
				parallelActions.add(a.getClass());

				// Equate Drive&Turn, Intake&Output
				for (int i = 0; i < parallelActions.size(); i++) {
					if (parallelActions.get(i) == TurnAction.class)
						parallelActions.set(i, DriveAction.class);
					else if (parallelActions.get(i) == OutputAction.class)
						parallelActions.set(i, IntakeAction.class);
				}

				// Check
				for (Class<?> c : parallelActions) {
					if (Collections.frequency(parallelActions, c) > 1)
						return false;
				}

				parallelActions.clear();
			}
		}

		return true;
	}

}
