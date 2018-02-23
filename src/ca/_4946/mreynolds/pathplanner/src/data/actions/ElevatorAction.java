package ca._4946.mreynolds.pathplanner.src.data.actions;

public class ElevatorAction extends Action<ElevatorAction.Options> {

	public static enum Options implements Action.ActionOptions {
		kMoveToBottom, kMoveToSwitch, kMoveToScaleLow, kMoveToScaleHigh, kMoveToCustom
	}

	public ElevatorAction() {
		this(Options.kMoveToBottom);
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
		if (options == Options.kMoveToCustom)
			return "Height:";

		return "";
	}
}
