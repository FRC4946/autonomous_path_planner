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

	/**
	 * Create an {@code IntakeAction} with:
	 * <li>Default {@link Option} of {@link Option#IntakeOn}
	 * <li>Default {@link Behaviour} of {@link Behaviour#kSequential}
	 */
	public IntakeAction() {
		this(Option.IntakeOn);
	}

	/**
	 * Create a {@code IntakeAction} with:
	 * <li>The specified {@link Option}
	 * <li>Default {@link Behaviour} of {@link Behaviour#kSequential}
	 * 
	 * @param options
	 *            the desired {@code Option}
	 */
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
