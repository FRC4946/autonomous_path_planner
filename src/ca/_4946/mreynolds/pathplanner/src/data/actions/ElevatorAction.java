package ca._4946.mreynolds.pathplanner.src.data.actions;

/**
 * An {@link Action} describing elevator movement to specified heights
 * 
 * @author Matthew Reynolds
 *
 */
public class ElevatorAction extends Action<ElevatorAction.Option> {

	/**
	 * <li>{@link Option#ToBottom} moves the elevator to its minimum height
	 * <li>{@link Option#ToSwitch} moves the elevator to the height needed to score
	 * on the switch
	 * <li>{@link Option#ToScaleLow} moves the elevator to the height needed to
	 * score on the scale when it is tipped down
	 * <li>{@link Option#ToScaleHigh} moves the elevator to the height needed to
	 * score on the scale when it is tipped up
	 * <li>{@link Option#ToCustom} moves the elevator to a custom height
	 * 
	 * @author Matthew Reynolds
	 * @see Action.ActionOption
	 */
	public static enum Option implements Action.ActionOption {
		ToBottom, ToSwitch, ToScaleLow, ToScaleHigh, ToCustom
	}

	/**
	 * Create an {@code ElevatorAction} with:
	 * <li>Default {@link Option} of {@link Option#ToBottom}
	 * <li>Default {@link Behaviour} of {@link Behaviour#kSequential}
	 */
	public ElevatorAction() {
		this(Option.ToBottom);
	}

	/**
	 * Create a {@code ElevatorAction} with:
	 * <li>The specified {@link Option}
	 * <li>Default {@link Behaviour} of {@link Behaviour#kSequential}
	 * 
	 * @param options
	 *            the desired {@code Option}
	 */
	public ElevatorAction(Option options) {
		super(options);
		data = 12;
	}

	@Override
	public String getName() {
		return "Elevator";
	}

	@Override
	public String getDataLabel() {
		if (option == Option.ToCustom)
			return "Height:";

		return "";
	}

	@Override
	public Option getDefaultOption() {
		return Option.ToBottom;
	}
}
