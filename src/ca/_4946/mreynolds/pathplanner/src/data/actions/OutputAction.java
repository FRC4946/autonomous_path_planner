package ca._4946.mreynolds.pathplanner.src.data.actions;

public class OutputAction extends Action<OutputAction.Options> {

	public static enum Options implements Action.ActionOptions {
		Output
	}

	public OutputAction() {
		this(Options.Output);
	}

	public OutputAction(Options options) {
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
	public Options getDefaultOption() {
		return Options.Output;
	}

}
