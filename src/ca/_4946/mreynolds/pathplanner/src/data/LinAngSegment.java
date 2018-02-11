package ca._4946.mreynolds.pathplanner.src.data;

import ca._4946.mreynolds.pathplanner.src.data.point.Point;

public class LinAngSegment {
	public Segment lin; // Linear component
	public Segment ang; // Angular component

	public LinAngSegment() {
		lin = new Segment();
		ang = new Segment();
	}

	public LinAngSegment(Segment linear) {
		this.lin = linear;
		ang = new Segment();
	}

	public LinAngSegment(Segment linear, Segment angular) {
		this.lin = linear;
		this.ang = angular;
	}
	
	public Point toPt() {
		return lin.toPt();
	}
}
