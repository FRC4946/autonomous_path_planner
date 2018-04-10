package ca._4946.mreynolds.pathplanner.src.data.profiles;

import java.util.Map;

import ca._4946.mreynolds.pathplanner.src.data.Segment;

public abstract class MotionProfile {

	public abstract String toString();
	
	public abstract Map<String, String> exportProfile();
	public abstract void importProfile(Map<String, String> data);

	/**
	 * @return the duration of the path, in seconds
	 */
	public abstract double duration();
	
	public abstract Segment getSeg(double t);

	public abstract void setPathLength(double dist);

}
