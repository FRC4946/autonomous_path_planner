package ca._4946.mreynolds.pathplanner.src.data.actions;

public class DelayAction extends Action<DelayAction.Options> {

	// TODO: Add security so that you cannot have a 0s delay!

	public static enum Options implements Action.ActionOptions {
		Wait
	}

	public DelayAction() {
		this(Options.Wait);
	}

	public DelayAction(Options options) {
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
	public Options getDefaultOption() {
		return Options.Wait;
	}

}
