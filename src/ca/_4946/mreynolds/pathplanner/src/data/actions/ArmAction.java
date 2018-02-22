package ca._4946.mreynolds.pathplanner.src.data.actions;

public class ArmAction extends Action<ArmAction.Options> {

	public static enum Options implements Action.ActionOptions {
		kArmDown, kArmUp
	}

	public ArmAction() {
		this(Options.kArmDown);
	}

	public ArmAction(Options options) {
		super(options);
		timeout = 0.5;
	}

	@Override
	public String getName() {
		return "Arm";
	}

	@Override
	public String getDataLabel() {
		return "";
	}

}
