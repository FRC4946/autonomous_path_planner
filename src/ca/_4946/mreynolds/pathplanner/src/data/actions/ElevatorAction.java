package ca._4946.mreynolds.pathplanner.src.data.actions;

public class ElevatorAction extends Action<ElevatorAction.Options> {

	public static enum Options implements Action.ActionOptions {
		ToBottom, ToSwitch, ToScaleLow, ToScaleHigh, ToCustom
	}

	public ElevatorAction() {
		this(Options.ToBottom);
	}

	public ElevatorAction(Options options) {
		super(options);
		data = 12;
	}

	@Override
	public String getName() {
		return "Elevator";
	}

	@Override
	public String getDataLabel() {
		if (options == Options.ToCustom)
			return "Height:";

		return "";
	}
	
	@Override
	public Options getDefaultOption() {
		return Options.ToBottom;
	}
}
