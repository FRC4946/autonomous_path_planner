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

	public ArmAction() {
		this(Option.ArmDown);
	}

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
