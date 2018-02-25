package ca._4946.mreynolds.pathplanner.src.data.point;

import java.awt.Graphics;

import ca._4946.mreynolds.pathplanner.src.io.FileIO;
import ca._4946.mreynolds.util.ObservableElement;

public class Point extends ObservableElement {

	protected double x;
	protected double y;
	protected int size = 2;

	public Point() {
		this(0, 0);
	}

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point(Point point) {
		this.x = point.x;
		this.y = point.y;
		this.size = point.size;
	}

	public Point(java.awt.Point point) {
		this.x = point.x;
		this.y = point.y;
	}

	@Override
	public String toString() {
		return "X: " + FileIO.f.format(x) + "\t Y: " + FileIO.f.format(y);
	}

	public double distance(Point p) {
		return Math.sqrt((x - p.x) * (x - p.x) + (y - p.y) * (y - p.y));
	}

	public boolean contains(Point p) {
		return distance(p) < size;
	}

	public void draw(Graphics g) {
		draw(g, size);
	}

	public void draw(Graphics g, int radius) {
		g.fillOval((int) Math.round(x - radius), (int) Math.round(y - radius), 2 * radius, 2 * radius);
	}

	public void setX(double x) {
		this.x = x;
		fireElementChanged();
	}

	public void setY(double y) {
		this.y = y;
		fireElementChanged();
	}
	
	public void setSize(int size) {
		this.size = size;
		fireElementChanged();
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

}
