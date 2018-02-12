package ca._4946.mreynolds.pathplanner.src.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import ca._4946.mreynolds.customSwing.ErrorPopup;
import ca._4946.mreynolds.pathplanner.src.PathPlanner;
import ca._4946.mreynolds.pathplanner.src.data.Script;
import ca._4946.mreynolds.pathplanner.src.data.Segment;
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
	private Image blueRobot;
	private Image redRobot;

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
			String fieldDir = "/ca/_4946/mreynolds/pathplanner/resources/field/";
			String robotDir = "/ca/_4946/mreynolds/pathplanner/resources/robot/";

			blueField = ImageIO.read(this.getClass().getResource(fieldDir + "Blue.png"));
			redField = ImageIO.read(this.getClass().getResource(fieldDir + "Red.png"));
			switchRedL = ImageIO.read(this.getClass().getResource(fieldDir + "Switch Red L.png"));
			switchBlueL = ImageIO.read(this.getClass().getResource(fieldDir + "Switch Blue L.png"));
			scaleRedL = ImageIO.read(this.getClass().getResource(fieldDir + "Scale Red L.png"));
			scaleBlueL = ImageIO.read(this.getClass().getResource(fieldDir + "Scale Blue L.png"));
			redRobot = ImageIO.read(this.getClass().getResource(robotDir + "Red.png"));
			blueRobot = ImageIO.read(this.getClass().getResource(robotDir + "Blue.png"));

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
		for (DriveAction path : PathPlanner.main.getScript().getDriveActions()) {

			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(1));

			path.generatePath();

			for (int i = 0; i < path.left.size(); i += 5) {
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
			for (Segment s : PathParser.fillPath(path.curves))
				pt2px(new Point(s.x, s.y)).draw(g);

		}
	}

	private void drawBots(Graphics g) {
		List<DriveAction> list = PathPlanner.main.getScript().getDriveActions();

		if (!list.isEmpty()) {
			DriveAction path = list.get(0);
			if (!path.isEmpty())
				drawBot(path.getPt(0), path.data == 1, (Graphics2D) g);
		}

		for (DriveAction path : list) {
			if (!path.isEmpty())
				drawBot(path.getPt(path.getNumPts() - 1), path.data == 1, (Graphics2D) g);
		}
	}

	private void drawBot(Waypoint o, boolean isFlipped, Graphics2D g) {
		Image robot = PathPlanner.main.fieldIsBlue ? blueRobot : redRobot;

		double x = robot.getWidth(null) / 2;
		double y = robot.getHeight(null) / 2;

		AffineTransform tx = new AffineTransform();
		tx.translate(in2px_x(o.getX()), in2px_y(o.getY()));

		tx.scale(PathPlanner.ROBOT_WIDTH_IN / x, PathPlanner.ROBOT_WIDTH_IN / x);
		tx.translate(-x, -y);

		double angle = -Math.toRadians(o.getHeading() + 90);
		if (isFlipped)
			angle += Math.PI;

		tx.rotate(angle, x, y);

		g.drawImage(robot, tx, null);
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
		drawPaths(g);
		drawBots(g);
		drawMagnets(g);

		for (DriveAction path : PathPlanner.main.getScript().getDriveActions())
			for (int i = 0; i < path.getNumPts(); i++)
				drawWaypoint(path.getPt(i), g);
	}

	MouseAdapter mouse = new MouseAdapter() {
		boolean refIsHandle = false;
		boolean handleIsFlip = false;
		int ref = -1;

		private Waypoint curPt() {
			if (PathPlanner.main.getScript().getSelectedAction() == null)
				return new Waypoint();
			return PathPlanner.main.getScript().getSelectedAction().getPt(ref);
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
					curPt().setMagnet(false);
			}

			PathPlanner.main.getScript().connectPaths();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			Script script = PathPlanner.main.getScript();

			if (script.getDriveActions().isEmpty())
				script.addAction(new DriveAction());

			Point click = pt2in(new Point(e.getPoint()));

			List<DriveAction> actions = script.getDriveActions();
			for (int j = 0; j < actions.size(); j++) {
				DriveAction a = actions.get(j);

				// Right Click
				if (e.getButton() == MouseEvent.BUTTON3) {

					for (int i = 0; i < a.getNumPts(); i++)
						if (a.getPt(i).contains(click)) {
							a.removePt(i);
							return;
						}
				}

				// Left click
				else {

					// Check handles
					for (int i = 0; i < a.getNumPts(); i++) {
						if (a.getPt(i).getHandle().contains(click)) {
							ref = i;
							handleIsFlip = false;
							refIsHandle = true;
							script.setSelectedAction(a);
							return;
						} else if (a.getPt(i).getFlipHandle().contains(click)) {
							ref = i;
							handleIsFlip = true;
							refIsHandle = true;
							script.setSelectedAction(a);
							return;
						}
					}

					// Check each point
					for (int i = 0; i < a.getNumPts(); i++) {
						if (a.getPt(i).contains(click)) {
							ref = i;
							refIsHandle = false;
							script.setSelectedAction(a);
							return;
						}
					}

					// Check each curve
					for (int i = 0; i < a.curves.size(); i++) {
						CubicBezier curve = a.curves.get(i);

						if (curve.ptIsOnCurve(click, 0.15)) {
							a.addPt(i + 1, new Waypoint(click));
							ref = i + 1;
							refIsHandle = false;
							script.setSelectedAction(a);
							return;
						}
					}
				}

				// If we haven't found a match and this is the last action in the script...
				if (ref == -1 && j == actions.size() - 1) {
					ref = a.getNumPts();
					a.addPt(new Waypoint(click));
					refIsHandle = false;
					script.setSelectedAction(a);

					// Check for magnet points
					for (MagnetPoint mag : PathPlanner.main.getMagnets())
						if (mag.contains(click))
							mag.latch(a.getPt(ref));
				}

				PathPlanner.main.getScript().connectPaths();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (PathPlanner.main.getScript().getSelectedAction() != null)
				PathPlanner.main.getScript().connectPaths();

			ref = -1;
		}
	};
}
