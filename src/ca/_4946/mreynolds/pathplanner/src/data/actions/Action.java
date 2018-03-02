package ca._4946.mreynolds.pathplanner.src.data.actions;

import java.awt.Color;

import ca._4946.mreynolds.pathplanner.src.data.actions.Action.ActionOption;
import ca._4946.mreynolds.util.ObservableElement;

/**
 * Abstract class describing an action the robot can perform in autonomous.
 * These roughly correspond to WPILib Commands. Each {@code Action} allows the
 * selection of one of several possible action to perform
 * 
 * @author Matthew Reynolds
 *
 * @param <T>
 *            The {@link ActionOption} that the overriding subclass implements
 * @see Action.ActionOption
 * @see Action.Behaviour
 */
public abstract class Action<T extends Enum<T> & ActionOption> extends ObservableElement {

	/**
	 * {@code ActionOption} is implemented in each class that inherits from
	 * {@code Action}. It describes the specific action that this {@code Action}
	 * will perform. For example, a {@code ClawAction} might have options to close
	 * and open the claw
	 * 
	 * @author Matthew Reynolds
	 *
	 */
	public interface ActionOption {
	}

	/**
	 * {@code Behaviour} describes the way the actions are run when they are
	 * converted to WPILib commands on the robot.
	 * <li>{@code kSequential} is used for running commands sequentially, one after
	 * anther
	 * <li>{@code kParallel} is used for running commands simultaneously
	 * 
	 * @author Matthew Reynolds
	 *
	 */
	public static enum Behaviour {
		kSequential, kParallel
	}

	/**
	 * Create an {@code Action} with the specified {@link ActionOption}
	 * 
	 * @param option
	 *            the option to set
	 */
	protected Action(T option) {
		this.option = option;
	}

	protected Enum<T> option;
	protected Behaviour behaviour = Behaviour.kSequential;
	protected double delay = 0.0;
	protected double timeout = -1.0;
	protected double data = 0.0;

	/**
	 * @return the human-readable name of the {@code Action}. For example, "Claw",
	 *         "Arm", "Elevator", etc
	 */
	public abstract String getName();

	/**
	 * @return the default {@code ActionOption} if no other option is specified
	 */
	public abstract T getDefaultOption();

	/**
	 * @return the human-readable label for the usage of the {@code data} field. For
	 *         example, "Height", "Speed", etc
	 */
	public abstract String getDataLabel();

	/**
	 * @return the option
	 */
	public Enum<T> getOption() {
		return option;
	}

	/**
	 * @param option
	 *            the option to set
	 */
	public void setOptions(Enum<T> option) {
		this.option = option;
		fireElementChanged();
	}

	/**
	 * @return the behaviour
	 */
	public Behaviour getBehaviour() {
		return behaviour;
	}

	/**
	 * @param behaviour
	 *            the behaviour to set
	 */
	public void setBehaviour(Behaviour behaviour) {
		this.behaviour = behaviour;
		fireElementChanged();
	}

	/**
	 * @return the starting delay
	 */
	public double getDelay() {
		return delay;
	}

	/**
	 * @param delay
	 *            the delay to set
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
	 * @param timeout
	 *            the timeout to set
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
	 * @param data
	 *            the data to set
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

		a.option = option;
		a.behaviour = behaviour;
		a.delay = delay;
		a.timeout = timeout;
		a.data = data;

		return a;
	}

	/**
	 * Get a unique color for each of the known {@code Action}s, or gross purple if
	 * the specified {@code Action} is of unknown type
	 * 
	 * @param a
	 *            the {@code Action} to check
	 * @return the {@link Color}
	 */
	public static Color getBkgColor(Action<?> a) {
		if (a instanceof DriveAction)
			return new Color(255, 0, 0);
		else if (a instanceof ElevatorAction)
			return new Color(255, 127, 0);
		else if (a instanceof ArmAction)
			return new Color(255, 255, 0);
		else if (a instanceof IntakeAction)
			return new Color(0, 255, 0);
		else if (a instanceof OutputAction)
			return new Color(0, 255, 255);
		else if (a instanceof TurnAction)
			return new Color(0, 127, 255);
		else if (a instanceof DelayAction)
			return new Color(255, 0, 255);

		return new Color(127, 127, 127);
	}
}
