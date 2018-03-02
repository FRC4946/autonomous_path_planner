package ca._4946.mreynolds.pathplanner.src.data.actions;

/**
 * An {@link Action} describing running an intake
 * 
 * @author Matthew Reynolds
 *
 */
public class IntakeAction extends Action<IntakeAction.Option> {

	/**
	 * <li>{@link Option#IntakeOn} turns on the intake until the action times out
	 * <li>{@link Option#IntakeUntil} turns on the intake until the robot recognizes
	 * that it has successfully intaked a game element
	 *
	 * @author Matthew Reynolds
	 * @see Action.ActionOption
	 */
	public static enum Option implements Action.ActionOption {
		IntakeOn, IntakeUntil
	}

	public IntakeAction() {
		this(Option.IntakeOn);
	}

	public IntakeAction(Option options) {
		super(options);
		data = 1;
		timeout = 1;
	}

	@Override
	public String getName() {
		return "Intake";
	}

	@Override
	public String getDataLabel() {
		return "Speed";
	}

	@Override
	public Option getDefaultOption() {
		return Option.IntakeOn;
	}
}
