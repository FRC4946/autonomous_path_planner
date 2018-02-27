package ca._4946.mreynolds.pathplanner.src.data.actions;

import java.awt.Color;

import ca._4946.mreynolds.pathplanner.src.data.actions.Action.ActionOptions;
import ca._4946.mreynolds.util.ObservableElement;

public abstract class Action<T extends Enum<T> & ActionOptions> extends ObservableElement {

	public interface ActionOptions {
	}

	public static enum Behaviour {
		kSequential, kParallel
	}

	protected Action(T options) {
		this.options = options;
	}

	protected Enum<T> options;
	protected Behaviour behaviour = Behaviour.kSequential;
	protected double delay = 0.0;
	protected double timeout = -1.0;
	protected double data = 0.0;

	public abstract String getName();

	public abstract T getDefaultOption();

	public abstract String getDataLabel();

	/**
	 * @return the options
	 */
	public Enum<T> getOptions() {
		return options;
	}

	/**
	 * @param options the options to set
	 */
	public void setOptions(Enum<T> options) {
		this.options = options;
		fireElementChanged();
	}

	/**
	 * @return the behaviour
	 */
	public Behaviour getBehaviour() {
		return behaviour;
	}

	/**
	 * @param behaviour the behaviour to set
	 */
	public void setBehaviour(Behaviour behaviour) {
		this.behaviour = behaviour;
		fireElementChanged();
	}

	/**
	 * @return the delay
	 */
	public double getDelay() {
		return delay;
	}

	/**
	 * @param delay the delay to set
	 */
	public void setDelay(double delay) {
		this.delay = delay;
		fireElementChanged();
	}

	/**
	 * @return the timeout
	 */
	public double getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(double timeout) {
		this.timeout = timeout;
		fireElementChanged();
	}

	/**
	 * @return the data
	 */
	public double getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(double data) {
		this.data = data;
		fireElementChanged();
	}
	
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
