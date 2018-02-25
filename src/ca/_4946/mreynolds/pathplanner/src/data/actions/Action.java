package ca._4946.mreynolds.pathplanner.src.data.actions;

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
}
