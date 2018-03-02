package ca._4946.mreynolds.pathplanner.src.data.actions;

/**
 * An {@link Action} describing running an output
 * 
 * @author Matthew Reynolds
 *
 */
public class OutputAction extends Action<OutputAction.Option> {

	/**
	 * <li>{@link Option#Output} turns on the intake until the action times out
	 *
	 * @author Matthew Reynolds
	 * @see Action.ActionOption
	 */
	public static enum Option implements Action.ActionOption {
		Output
	}

	public OutputAction() {
		this(Option.Output);
	}

	public OutputAction(Option options) {
		super(options);
		data = 1;
		timeout = 1;
	}

	@Override
	public String getName() {
		return "Output";
	}

	@Override
	public String getDataLabel() {
		return "Speed";
	}

	@Override
	public Option getDefaultOption() {
		return Option.Output;
	}

}
