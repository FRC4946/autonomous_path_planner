package ca._4946.mreynolds.pathplanner.src.data.point;

import java.text.DecimalFormat;

import ca._4946.mreynolds.util.ObservableElement;

/**
 * A simple 2D point representation (x, y) with double precision
 * 
 * @author Matthew
 *
 */
public class Point extends ObservableElement {

	protected double x;
	protected double y;
	protected int size = 3;

	/**
	 * Create a {@code Point} with coordinates (0,0)
	 */
	public Point() {
		this(0, 0);
	}

	/**
	 * Create a {@code Point} with the specified coordinates
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 */
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Create a new {@code Point} with parameters cloned from the specified
	 * {@code Point}
	 * 
	 * @param point
	 *            the {@code Point} to clone
	 */
	public Point(Point point) {
		this.x = point.x;
		this.y = point.y;
		this.size = point.size;
	}

	/**
	 * Create a new {@code Point} with parameters cloned from the specified
	 * {@link java.awt.Point}
	 * 
	 * @param point
	 *            the {@code java.awt.Point} to clone
	 */
	public Point(java.awt.Point point) {
		this.x = point.x;
		this.y = point.y;
	}

	/**
	 * Calculate the distance between this {@code Point} and the specified other
	 * 
	 * @param p
	 *            the {@code Point} to measure to
	 * @return the distance
	 */
	public double distance(Point p) {
		return Math.sqrt((x - p.x) * (x - p.x) + (y - p.y) * (y - p.y));
	}

	/**
	 * Determine if the center of the specified {@code Point} is within this
	 * {@code Point}
	 * 
	 * @param p
	 *            the {@code Point} to check
	 * @return {@code true} if the parameter is within this {@code Point}
	 */
	public boolean contains(Point p) {
		return distance(p) < size;
	}

	/**
	 * @param x
	 *            the new x position to set
	 */
	public void setX(double x) {
		this.x = x;
		fireElementChanged();
	}

	/**
	 * @param y
	 *            the new y position to set
	 */
	public void setY(double y) {
		this.y = y;
		fireElementChanged();
	}

	/**
	 * @param size
	 *            the new size to set
	 */
	public void setSize(int size) {
		this.size = size;
		fireElementChanged();
	}

	/**
	 * @return the {@code Point}'s x position
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return the {@code Point}'s y position
	 */
	public double getY() {
		return y;
	}

	/**
	 * @return the {@code Point}'s size
	 */
	public double getSize() {
		return size;
	}

	@Override
	public String toString() {
		DecimalFormat f = new DecimalFormat("0.00");
		return "X: " + f.format(x) + "\t Y: " + f.format(y) + "\t Size: " + size;
	}
}
