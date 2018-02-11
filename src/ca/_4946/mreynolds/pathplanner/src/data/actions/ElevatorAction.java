package ca._4946.mreynolds.pathplanner.src.data.actions;

public class ElevatorAction extends Action<ElevatorAction.Options> {

	public static enum Options implements Action.ActionOptions {
		kMoveToBottom, kMoveToSwitch, kMoveToScale, kMoveToCustom
	}

	public ElevatorAction() {
		this(Options.kMoveToCustom);
	}

	public ElevatorAction(Options options) {
		this(options, 0);
	}

	public ElevatorAction(Options options, int timeout) {
		super(options);
		this.timeout = timeout;
	}

	@Override
	public String getName() {
		return "Elevator";
	}

	@Override
	public String getDataLabel() {
		if (options == Options.kMoveToCustom)
			return "Height (in):";

		return "";
	}
}
