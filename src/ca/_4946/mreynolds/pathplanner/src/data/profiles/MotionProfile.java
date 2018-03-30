package ca._4946.mreynolds.pathplanner.src.data.profiles;

import org.ini4j.Ini;

import ca._4946.mreynolds.pathplanner.src.data.Segment;

public abstract class MotionProfile {

	public abstract String toString();
	
	public abstract void saveToIni(Ini ini);
	public abstract void loadFromIni(Ini ini);

	/**
	 * @return the duration of the path, in seconds
	 */
	public abstract double duration();
	
	public abstract Segment getSeg(double t);

	public abstract void setPathLength(double dist);

}
