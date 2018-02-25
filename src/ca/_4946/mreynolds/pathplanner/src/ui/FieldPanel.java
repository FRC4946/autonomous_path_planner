package ca._4946.mreynolds.pathplanner.src.ui;

import java.awt.AlphaComposite;
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
import ca._4946.mreynolds.pathplanner.src.data.actions.Action;
import ca._4946.mreynolds.pathplanner.src.data.actions.Action.Behaviour;
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
			if (PathPlanner.main.gameData.charAt(1) == 'l')
				g.drawImage(scaleBlueL, 0, 0, getWidth(), getHeight(), this);
			else
				g.drawImage(scaleRedL, 0, 0, getWidth(), getHeight(), this);

			if (PathPlanner.main.gameData.charAt(0) == 'l')
				g.drawImage(switchBlueL, 0, 0, getWidth(), getHeight(), this);
			else
				g.drawImage(switchRedL, 0, 0, getWidth(), getHeight(), this);
		}

		else {
			g.drawImage(redField, 0, 0, getWidth(), getHeight(), this);
			if (PathPlanner.main.gameData.charAt(1) == 'l')
				g.drawImage(scaleRedL, 0, 0, getWidth(), getHeight(), this);
			else
				g.drawImage(scaleBlueL, 0, 0, getWidth(), getHeight(), this);

			if (PathPlanner.main.gameData.charAt(0) == 'l')
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
		for (DriveAction path : PathPlanner.main.getScript().getPathActions()) {

			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(1));

			path.generatePath();

			for (int i = 0; i < path.left.size(); i++) {
				Point l = pt2px(new Point(path.left.get(i).x, path.left.get(i).y));
				Point r = pt2px(new Point(path.right.get(i).x, path.right.get(i).y));

				g.setColor(Color.GREEN);
				g.drawLine((int) l.getX(), (int) l.getY(), (int) r.getX(), (int) r.getY());

				g.setColor(Color.RED);
				l.draw(g);
				r.draw(g);
			}

			g.setColor(Color.CYAN);
			for (Segment s : PathParser.fillPath(path.curves))
				pt2px(new Point(s.x, s.y)).draw(g);
		}
	}

	public void drawActions(Graphics g) {

		for (int i = PathPlanner.main.getScript().getActions().size() - 1; i >= 0; i--) {

			Action<?> d = PathPlanner.main.getScript().getAction(i);
			if (!(d instanceof DriveAction))
				continue;

			for (int j = i - 1; j >= 0; j--) {
				Action<?> a = PathPlanner.main.getScript().getAction(j);
				if (a.behaviour == Behaviour.kSequential || a instanceof DriveAction)
					break;
				if (a.delay == 0)
					continue;

				int position = (int) (a.delay / PathParser.SAMPLE_PERIOD);
				position = Math.min(((DriveAction) d).left.size() - 1, position);

				Point l = ((DriveAction) d).left.get(position).toPt();
				Point r = ((DriveAction) d).right.get(position).toPt();
				Point p = pt2px(new Point((l.getX() + r.getX()) / 2, (l.getY() + r.getY()) / 2));

				g.setColor(Action.getBkgColor(a));
				p.setSize(10);
				p.draw(g);
			}

		}

	}

	private void drawBots(Graphics g) {
		List<DriveAction> list = PathPlanner.main.getScript().getPathActions();

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

		// Move the image to the point
		AffineTransform tx = new AffineTransform();
		tx.translate(in2px_x(o.getX()), in2px_y(o.getY()));

		// Scale the image
		double pxPerIn = (double) (getWidth()) / IMG_WIDTH * PIXELS_PER_INCH;
		double widthPx = pxPerIn * PathPlanner.ROBOT_WIDTH_IN;
		double factor = widthPx / robot.getWidth(null);
		tx.scale(factor, factor);

		// Center the image
		double x = robot.getWidth(null) / 2;
		double y = robot.getHeight(null) / 2;
		tx.translate(-x, -y);

		// Rotate the image
		double angle = -Math.toRadians(o.getHeading() + 90);
		if (isFlipped)
			angle += Math.PI;
		tx.rotate(angle, x, y);

		// Align the back of the robot
		double dpos = (robot.getWidth(null) / PathPlanner.ROBOT_WIDTH_IN * PathPlanner.ROBOT_LENGTH_IN
				- robot.getHeight(null)) / 2;
		tx.translate(0, -dpos);

		// Draw the image
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.66f));
		g.drawImage(robot, tx, null);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

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
		drawActions(g);
		drawBots(g);
		drawMagnets(g);

		for (DriveAction path : PathPlanner.main.getScript().getPathActions())
			for (int i = 0; i < path.getNumPts(); i++)
				drawWaypoint(path.getPt(i), g);
	}

	MouseAdapter mouse = new MouseAdapter() {
		boolean refIsHandle = false;
		boolean handleIsFlip = false;
		DriveAction curAction = null;
		int ref = -1;

		private Waypoint curPt() {
			if (curAction == null)
				return new Waypoint();
			return curAction.getPt(ref);
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
			curAction = null;
			Script script = PathPlanner.main.getScript();

			if (script.getPathActions().isEmpty())
				script.addAction(new DriveAction());

			Point click = pt2in(new Point(e.getPoint()));

			List<DriveAction> actions = script.getPathActions();
			for (int j = 0; j < actions.size(); j++) {
				curAction = actions.get(j);

				// Right Click
				if (e.getButton() == MouseEvent.BUTTON3) {

					for (int i = 0; i < curAction.getNumPts(); i++)
						if (curAction.getPt(i).contains(click)) {
							curAction.removePt(i);
							return;
						}
				}

				// Left click
				else {

					// Check handles
					for (int i = 0; i < curAction.getNumPts(); i++) {
						if (curAction.getPt(i).getHandle().contains(click)) {
							ref = i;
							handleIsFlip = false;
							refIsHandle = true;
							return;
						} else if (curAction.getPt(i).getFlipHandle().contains(click)) {
							ref = i;
							handleIsFlip = true;
							refIsHandle = true;
							return;
						}
					}

					// Check each point
					for (int i = 0; i < curAction.getNumPts(); i++) {
						if (curAction.getPt(i).contains(click)) {
							ref = i;
							refIsHandle = false;
							return;
						}
					}

					// Check each curve
					for (int i = 0; i < curAction.curves.size(); i++) {
						CubicBezier curve = curAction.curves.get(i);

						if (curve.ptIsOnCurve(click, 0.15)) {
							curAction.addPt(i + 1, new Waypoint(click));
							ref = i + 1;
							refIsHandle = false;
							return;
						}
					}
				}

				// If we haven't found a match and this is the last action in the script...
				if (ref == -1 && j == actions.size() - 1) {
					ref = curAction.getNumPts();
					curAction.addPt(new Waypoint(click));
					refIsHandle = false;

					// Check for magnet points
					for (MagnetPoint mag : PathPlanner.main.getMagnets())
						if (mag.contains(click))
							mag.latch(curAction.getPt(ref));
				}

				PathPlanner.main.getScript().connectPaths();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			PathPlanner.main.getScript().connectPaths();
			ref = -1;
			curAction = null;
		}
	};
}
