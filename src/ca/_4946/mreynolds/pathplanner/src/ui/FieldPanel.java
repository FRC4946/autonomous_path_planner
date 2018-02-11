package ca._4946.mreynolds.pathplanner.src.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import ca._4946.mreynolds.customSwing.ErrorPopup;
import ca._4946.mreynolds.pathplanner.src.PathPlanner;
import ca._4946.mreynolds.pathplanner.src.data.LinAngSegment;
import ca._4946.mreynolds.pathplanner.src.data.actions.Action;
import ca._4946.mreynolds.pathplanner.src.data.actions.DriveAction;
import ca._4946.mreynolds.pathplanner.src.data.point.MagnetPoint;
import ca._4946.mreynolds.pathplanner.src.data.point.Point;
import ca._4946.mreynolds.pathplanner.src.data.point.Waypoint;
import ca._4946.mreynolds.pathplanner.src.math.PathParser;
import ca._4946.mreynolds.pathplanner.src.math.bezier.CubicBezier;

public class FieldPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private Image blueField;
	private Image redField;
	private Image switchRedL;
	private Image switchBlueL;
	private Image scaleRedL;
	private Image scaleBlueL;

	public static final int IMG_WIDTH = 748;
	public static final int IMG_HEIGHT = 812;
	public static final double PIXELS_PER_INCH = 2.09350808615;
	public static final int ORIGIN_X_PX = IMG_WIDTH / 2;
	public static final int ORIGIN_Y_PX = 752;

	private double px2in_x(double a) {
		double scaling = (double) (getWidth()) / IMG_WIDTH * PIXELS_PER_INCH;
		double origin = (double) (ORIGIN_X_PX) / IMG_WIDTH * getWidth();
		return (a - origin) / scaling;
	}

	private double px2in_y(double a) {
		double scaling = -(double) (getHeight()) / IMG_HEIGHT * PIXELS_PER_INCH;
		double origin = (double) (ORIGIN_Y_PX) / IMG_HEIGHT * getHeight();
		return (a - origin) / scaling;
	}

	private double in2px_x(double a) {
		double scaling = (double) (getWidth()) / IMG_WIDTH * PIXELS_PER_INCH;
		double origin = (double) (ORIGIN_X_PX) / IMG_WIDTH * getWidth();
		return (a * scaling) + origin;
	}

	private double in2px_y(double a) {
		double scaling = -(double) (getHeight()) / IMG_HEIGHT * PIXELS_PER_INCH;
		double origin = (double) (ORIGIN_Y_PX) / IMG_HEIGHT * getHeight();
		return (a * scaling) + origin;
	}

	private Point pt2px(Point p) {
		Point scaled = new Point(p);
		scaled.setX(in2px_x(p.getX()));
		scaled.setY(in2px_y(p.getY()));
		return scaled;
	}

	private Point pt2in(Point p) {
		Point scaled = new Point(p);
		scaled.setX(px2in_x(p.getX()));
		scaled.setY(px2in_y(p.getY()));
		return scaled;
	}

	/**
	 * Create the panel.
	 */
	public FieldPanel() {
		try {
			String dir = "/ca/_4946/mreynolds/pathplanner/resources/field/";

			blueField = ImageIO.read(this.getClass().getResource(dir + "Blue.png"));
			redField = ImageIO.read(this.getClass().getResource(dir + "Red.png"));
			switchRedL = ImageIO.read(this.getClass().getResource(dir + "Switch Red L.png"));
			switchBlueL = ImageIO.read(this.getClass().getResource(dir + "Switch Blue L.png"));
			scaleRedL = ImageIO.read(this.getClass().getResource(dir + "Scale Red L.png"));
			scaleBlueL = ImageIO.read(this.getClass().getResource(dir + "Scale Blue L.png"));

		} catch (IOException | IllegalArgumentException e) {
			ErrorPopup.createPopup("Error loading resources", e);
			e.printStackTrace();
		}

		addMouseListener(mouse);
		addMouseMotionListener(mouse);
	}

	public void drawBackground(Graphics g) {
		if (PathPlanner.main.fieldIsBlue) {
			g.drawImage(blueField, 0, 0, getWidth(), getHeight(), this);
			if (PathPlanner.main.scaleIsL)
				g.drawImage(scaleBlueL, 0, 0, getWidth(), getHeight(), this);
			else
				g.drawImage(scaleRedL, 0, 0, getWidth(), getHeight(), this);

			if (PathPlanner.main.switchIsL)
				g.drawImage(switchBlueL, 0, 0, getWidth(), getHeight(), this);
			else
				g.drawImage(switchRedL, 0, 0, getWidth(), getHeight(), this);
		}

		else {
			g.drawImage(redField, 0, 0, getWidth(), getHeight(), this);
			if (PathPlanner.main.scaleIsL)
				g.drawImage(scaleRedL, 0, 0, getWidth(), getHeight(), this);
			else
				g.drawImage(scaleBlueL, 0, 0, getWidth(), getHeight(), this);

			if (PathPlanner.main.switchIsL)
				g.drawImage(switchRedL, 0, 0, getWidth(), getHeight(), this);
			else
				g.drawImage(switchBlueL, 0, 0, getWidth(), getHeight(), this);
		}
	}

	public void drawMagnets(Graphics g) {
		g.setColor(Color.YELLOW);
		for (MagnetPoint p : PathPlanner.main.getMagnets())
			pt2px(p).draw(g);
	}

	public void drawPaths(Graphics g) {
		for (Action<?> curAction : PathPlanner.main.getScript().getActions()) {
			if (!(curAction instanceof DriveAction))
				continue;

			DriveAction path = (DriveAction) curAction;
			List<Waypoint> points = path.waypoints;
			path.generatePath();

			for (int i = 0; i < path.left.size(); i++) {
				Point l = pt2px(new Point(path.left.get(i).x, path.left.get(i).y));
				Point r = pt2px(new Point(path.right.get(i).x, path.right.get(i).y));
				// Point c = new Point((l.getX() + r.getX()) / 2, (l.getY() + r.getY()) / 2);

				g.setColor(Color.GREEN);
				g.drawLine((int) l.getX(), (int) l.getY(), (int) r.getX(), (int) r.getY());
				// c.draw(g);
				g.setColor(Color.RED);
				l.draw(g);
				r.draw(g);
			}

			g.setColor(Color.CYAN);
			for (LinAngSegment s : PathParser.fillPath(path.waypoints))
				pt2px(new Point(s.lin.x, s.lin.y)).draw(g);

			for (int i = 0; i < points.size() - 1; i++)
				drawWaypoint(points.get(i), g);

			if (points.size() > 0)
				drawWaypoint(points.get(points.size() - 1), g);

		}
	}

	private void drawWaypoint(Waypoint p, Graphics g) {
		Point h1 = pt2px(p.getHandle());
		Point h2 = pt2px(p.getFlipHandle());

		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(2));
		g2.setColor(Color.BLUE);
		g.drawLine((int) h1.getX(), (int) h1.getY(), (int) h2.getX(), (int) h2.getY());
		pt2px(p).draw(g);

		g2.setColor(Color.MAGENTA);
		h1.draw(g2, 4);
		h2.draw(g2, 4);
	}

	public void paintComponent(Graphics g) {
		drawBackground(g);
		drawMagnets(g);
		drawPaths(g);
	}

	MouseAdapter mouse = new MouseAdapter() {
		boolean refIsHandle = false;
		boolean handleIsFlip = false;
		int ref = -1;

		private Waypoint curPt() {
			DriveAction a = PathPlanner.main.getScript().getSelectedAction();
			if (a == null)
				return new Waypoint();
			return a.waypoints.get(ref);
		}

		@Override
		public void mouseDragged(MouseEvent e) {

			if (refIsHandle) {
				curPt().setR((curPt().distance(pt2in(new Point(e.getPoint())))));
				if (!curPt().isMagnet()) {
					curPt().setAutomaticHeading(false);
					double heading = Math.toDegrees(
							Math.atan2(curPt().getY() - px2in_y(e.getY()), curPt().getX() - px2in_x(e.getX())));
					if (!handleIsFlip)
						heading += 180;
					curPt().setHeading(heading);
				}
			} else {
				curPt().setX(px2in_x(e.getX()));
				curPt().setY(px2in_y(e.getY()));

				// Check for magnets
				boolean foundMagnet = false;
				for (MagnetPoint mag : PathPlanner.main.getMagnets())
					if (mag.contains(curPt())) {
						foundMagnet = true;

						curPt().setX(mag.getX());
						curPt().setY(mag.getY());
						if (mag.hasHeading() && !refIsHandle) {
							((Waypoint) curPt()).setHeading(mag.getHeading());
							((Waypoint) curPt()).setMagnet(true);
						}
					}

				if (!foundMagnet)
					((Waypoint) curPt()).setMagnet(false);
			}

			DriveAction a = (DriveAction) PathPlanner.main.getScript().getSelectedAction();
			if (a != null) {
				PathPlanner.main.getScript().connectPaths();
				// a.generatePath();
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			DriveAction a = (DriveAction) PathPlanner.main.getScript().getSelectedAction();
			if (a == null)
				return;

			List<Waypoint> points = a.waypoints;
			Point click = pt2in(new Point(e.getPoint()));

			// Right Click
			if (e.getButton() == MouseEvent.BUTTON3) {

				for (int i = 0; i < points.size(); i++)
					if (points.get(i).contains(click)) {
						points.remove(i);
						break;
					}
			}

			// Left click
			else {

				// If we already have points, check if the click is on an existing point
				if (points.size() > 0) {

					// Check handles
					for (int i = 0; i < points.size(); i++)
						if (points.get(i).getHandle().contains(click)) {
							ref = i;
							handleIsFlip = false;
							refIsHandle = true;
						} else if (points.get(i).getFlipHandle().contains(click)) {
							ref = i;
							handleIsFlip = true;
							refIsHandle = true;
						}

					// Check pt 0
					if (points.get(0).contains(click)) {
						refIsHandle = false;
						ref = 0;
					}

					// Check every other point and every line
					for (int i = 0; i < points.size() - 1 && ref == -1; i++) {
						CubicBezier curve = new CubicBezier(points.get(i), points.get(i + 1));
						
						if (points.get(i + 1).contains(click)) {
							ref = i + 1;
							refIsHandle = false;
						}
						else if (curve.ptIsOnCurve(click, 0.1)) {
							points.add(i + 1, new Waypoint(click));
							ref = i + 1;
							refIsHandle = false;
						}
					}
				}

				if (ref == -1) {
					ref = points.size();
					points.add(new Waypoint(click));
					refIsHandle = false;
				}

				// Check for magnet points
				if (!refIsHandle)
					for (MagnetPoint mag : PathPlanner.main.getMagnets())
						if (mag.contains(click)) {
							points.get(ref).setX(mag.getX());
							points.get(ref).setY(mag.getY());
							if (mag.hasHeading() && !refIsHandle) {
								((Waypoint) curPt()).setHeading(mag.getHeading());
								((Waypoint) curPt()).setMagnet(true);
							}
						}
			}

			PathPlanner.main.getScript().connectPaths();
			// a.generatePath();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			ref = -1;

			DriveAction a = (DriveAction) PathPlanner.main.getScript().getSelectedAction();
			if (a != null) {
				PathPlanner.main.getScript().connectPaths();
				// a.generatePath();
			}
		}
	};
}
