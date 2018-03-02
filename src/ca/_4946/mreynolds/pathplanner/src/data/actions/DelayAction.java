package ca._4946.mreynolds.pathplanner.src.data.actions;

/**
 * An {@link Action} describing doing nothing and waiting for a certain period
 * 
 * @author Matthew Reynolds
 *
 */
public class DelayAction extends Action<DelayAction.Option> {

	// TODO: Add security so that you cannot have a 0s delay!

	/**
	 * <li>{@link Option#Wait} delays for a specified amount of time
	 *
	 * @author Matthew Reynolds
	 * @see Action.ActionOption
	 */
	public static enum Option implements Action.ActionOption {
		Wait
	}

	public DelayAction() {
		this(Option.Wait);
	}

	public DelayAction(Option options) {
		super(options);
		behaviour = Behaviour.kSequential;
		timeout = 1;
	}

	@Override
	public String getName() {
		return "Delay";
	}

	@Override
	public String getDataLabel() {
		return "";
	}

	@Override
	public Option getDefaultOption() {
		return Option.Wait;
	}

}
