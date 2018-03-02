package ca._4946.mreynolds.pathplanner.src.data.actions;

/**
 * An {@link Action} describing the actuation of an arm
 * 
 * @author Matthew Reynolds
 *
 */
public class ArmAction extends Action<ArmAction.Option> {

	/**
	 * <li>{@link Option#ArmDown} puts the arm in the down position
	 * <li>{@link Option#ArmUp} puts the arm in the upright position
	 *
	 * @author Matthew Reynolds
	 * @see Action.ActionOption
	 */
	public static enum Option implements Action.ActionOption {
		ArmDown, ArmUp
	}

	/**
	 * Create an {@code ArmAction} with:
	 * <li>Default {@link Option} of {@link Option#ArmDown}
	 * <li>Default {@link Behaviour} of {@link Behaviour#kSequential}
	 */
	public ArmAction() {
		this(Option.ArmDown);
	}

	/**
	 * Create an {@code ArmAction} with:
	 * <li>The specified {@link Option}
	 * <li>Default {@link Behaviour} of {@link Behaviour#kSequential}
	 * 
	 * @param options
	 *            the desired {@code Option}
	 */
	public ArmAction(Option options) {
		super(options);
		timeout = 0.5;
	}

	@Override
	public String getName() {
		return "Arm";
	}

	@Override
	public String getDataLabel() {
		return "";
	}

	@Override
	public Option getDefaultOption() {
		return Option.ArmDown;
	}

}
