package ca._4946.mreynolds.pathplanner.src.data.actions;

import java.awt.Color;

import ca._4946.mreynolds.pathplanner.src.data.actions.Action.ActionOptions;

public abstract class Action<T extends Enum<T> & ActionOptions> {

	public interface ActionOptions {
	}

	public static enum Behaviour {
		kSequential, kParallel
	}

	protected Action(T options) {
		this.options = options;
	}

	public Enum<T> options;
	public Behaviour behaviour = Behaviour.kSequential;
	public double delay = 0.0;
	public double timeout = -1.0;
	public double data = 0.0;

	public abstract String getName();

	public abstract T getDefaultOption();

	public abstract String getDataLabel();

	@SuppressWarnings("unchecked")
	public Action<T> clone() {
		Action<T> a = null;

		try {
			a = this.getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		a.options = options;
		a.behaviour = behaviour;
		a.delay = delay;
		a.timeout = timeout;
		a.data = data;

		return a;
	}

	public static Color getBkgColor(Action<?> a) {
		if (a instanceof DriveAction)
			return new Color(255, 200, 220);
		else if (a instanceof ArmAction)
			return new Color(204, 210, 255);
		else if (a instanceof DelayAction)
			return new Color(204, 239, 255);
		else if (a instanceof ElevatorAction)
			return new Color(204, 255, 227);
		else if (a instanceof IntakeAction)
			return new Color(240, 255, 204);
		else if (a instanceof OutputAction)
			return new Color(255, 225, 204);

		return new Color(255, 0, 255);
	}
}
