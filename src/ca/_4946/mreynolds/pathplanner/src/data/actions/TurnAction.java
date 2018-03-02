package ca._4946.mreynolds.pathplanner.src.data.actions;

/**
 * An {@link Action} describing rotating the robot on the spot, without any
 * overall positional translation
 * 
 * @author Matthew Reynolds
 *
 */
public class TurnAction extends Action<TurnAction.Option> {

	/**
	 * <li>{@link Option#TurnOnSpot} turns the robot on the spot the specified angle
	 *
	 * @author Matthew Reynolds
	 * @see Action.ActionOption
	 */
	public static enum Option implements Action.ActionOption {
		TurnOnSpot
	}

	/**
	 * Create a {@code TurnAction} with:
	 * <li>Default {@link Option} of {@link Option#TurnOnSpot}
	 * <li>Default {@link Behaviour} of {@link Behaviour#kSequential}
	 */
	public TurnAction() {
		this(Option.TurnOnSpot);
	}

	/**
	 * Create a {@code TurnAction} with:
	 * <li>The specified {@link Option}
	 * <li>Default {@link Behaviour} of {@link Behaviour#kSequential}
	 * 
	 * @param options
	 *            the desired {@code Option}
	 */
	public TurnAction(Option options) {
		super(options);
		data = 90;
		timeout = 1;
	}

	@Override
	public String getName() {
		return "Turn";
	}

	@Override
	public String getDataLabel() {
		return "Angle";
	}

	@Override
	public Option getDefaultOption() {
		return Option.TurnOnSpot;
	}

}
