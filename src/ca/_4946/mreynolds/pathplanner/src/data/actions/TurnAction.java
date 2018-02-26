package ca._4946.mreynolds.pathplanner.src.data.actions;

public class TurnAction extends Action<TurnAction.Options> {

	public static enum Options implements Action.ActionOptions {
		TurnOnSpot
	}

	public TurnAction() {
		this(Options.TurnOnSpot);
	}

	public TurnAction(Options options) {
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
	public Options getDefaultOption() {
		return Options.TurnOnSpot;
	}

}
