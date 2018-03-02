package ca._4946.mreynolds.pathplanner.src.ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import ca._4946.mreynolds.customSwing.ErrorPopup;
import ca._4946.mreynolds.pathplanner.src.PathPlannerSettings;
import ca._4946.mreynolds.pathplanner.src.data.Script;
import ca._4946.mreynolds.pathplanner.src.data.Segment;
import ca._4946.mreynolds.pathplanner.src.data.actions.Action;
import ca._4946.mreynolds.pathplanner.src.data.actions.Action.Behaviour;
import ca._4946.mreynolds.pathplanner.src.data.actions.DriveAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.TurnAction;
import ca._4946.mreynolds.pathplanner.src.data.point.Point;
import ca._4946.mreynolds.pathplanner.src.data.point.Waypoint;
import ca._4946.mreynolds.pathplanner.src.math.PathParser;

public class FieldPanel2 extends JPanel {
	private static final long serialVersionUID = 1L;

	public Script m_script;
	public String m_data;

	private Image blueField;
	private Image switchRedL;
	private Image switchBlueL;
	private Image scaleRedL;
	private Image scaleBlueL;
	private Image blueRobot;

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

	@SuppressWarnings("unused")
	private Point pt2in(Point p) {
		Point scaled = new Point(p);
		scaled.setX(px2in_x(p.getX()));
		scaled.setY(px2in_y(p.getY()));
		return scaled;
	}

	/**
	 * Create the panel.
	 */
	public FieldPanel2(Script sc, String data) {
		m_script = sc;
		m_data = data.toLowerCase();

		try {
			String fieldDir = "/ca/_4946/mreynolds/pathplanner/resources/field/";
			String robotDir = "/ca/_4946/mreynolds/pathplanner/resources/robot/";

			blueField = ImageIO.read(this.getClass().getResource(fieldDir + "Blue.png"));
			// redField = ImageIO.read(this.getClass().getResource(fieldDir + "Red.png"));
			switchRedL = ImageIO.read(this.getClass().getResource(fieldDir + "Switch Red L.png"));
			switchBlueL = ImageIO.read(this.getClass().getResource(fieldDir + "Switch Blue L.png"));
			scaleRedL = ImageIO.read(this.getClass().getResource(fieldDir + "Scale Red L.png"));
			scaleBlueL = ImageIO.read(this.getClass().getResource(fieldDir + "Scale Blue L.png"));
			// redRobot = ImageIO.read(this.getClass().getResource(robotDir + "Red.png"));
			blueRobot = ImageIO.read(this.getClass().getResource(robotDir + "Blue.png"));

		} catch (IOException | IllegalArgumentException e) {
			ErrorPopup.createPopup("Error loading resources", e);
			e.printStackTrace();
		}

	}

	public void drawBackground(Graphics g) {

		// Assume blue field
		g.drawImage(blueField, 0, 0, getWidth(), getHeight(), this);
		if (m_data.charAt(1) == 'l')
			g.drawImage(scaleBlueL, 0, 0, getWidth(), getHeight(), this);
		else
			g.drawImage(scaleRedL, 0, 0, getWidth(), getHeight(), this);

		if (m_data.charAt(0) == 'l')
			g.drawImage(switchBlueL, 0, 0, getWidth(), getHeight(), this);
		else
			g.drawImage(switchRedL, 0, 0, getWidth(), getHeight(), this);
	}

	public void drawPaths(Graphics g) {
		for (DriveAction path : m_script.getDriveActions()) {

			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(1));

			path.generatePath();

			for (int i = 0; i < path.getLeftPath().size(); i++) {
				Point l = pt2px(new Point(path.getLeftPath().get(i).x, path.getLeftPath().get(i).y));
				Point r = pt2px(new Point(path.getRightPath().get(i).x, path.getRightPath().get(i).y));

				g.setColor(Color.GREEN);
				g.drawLine((int) l.getX(), (int) l.getY(), (int) r.getX(), (int) r.getY());

				g.setColor(Color.RED);
				l.draw(g);
				r.draw(g);
			}

			g.setColor(Color.CYAN);
			for (Segment s : PathParser.fillPath(path.getCurves()))
				pt2px(new Point(s.x, s.y)).draw(g);
		}
	}

	public void drawActions(Graphics g) {

		for (int i = m_script.getActions().size() - 1; i >= 0; i--) {

			Action<?> d = m_script.getAction(i);
			if (!(d instanceof DriveAction))
				continue;

			for (int j = i - 1; j >= 0; j--) {
				Action<?> a = m_script.getAction(j);
				if (a.getBehaviour() == Behaviour.kSequential || a instanceof DriveAction)
					break;
				if (a.getDelay() == 0)
					continue;

				int position = (int) ((a.getDelay() - d.getDelay()) / PathPlannerSettings.SAMPLE_PERIOD);
				position = Math.min(((DriveAction) d).getLeftPath().size() - 1, position);

				Point l = ((DriveAction) d).getLeftPath().get(position).toPt();
				Point r = ((DriveAction) d).getRightPath().get(position).toPt();
				Point p = pt2px(new Point((l.getX() + r.getX()) / 2, (l.getY() + r.getY()) / 2));

				g.setColor(Action.getBkgColor(a));
				p.setSize(10);
				p.draw(g);
			}

		}

	}

	private void drawBots(Graphics g) {

		List<DriveAction> driveActions = m_script.getActionOfType(DriveAction.class);
		if (driveActions.isEmpty())
			return;

		// Draw the 1st bot
		DriveAction origin = driveActions.get(0);
		if (origin != null && !origin.isEmpty())
			drawBot(origin.getPt(0), origin.getData() == 1, (Graphics2D) g);

		List<Action<?>> list = m_script.getActionOfType(DriveAction.class, TurnAction.class);

		Waypoint prevPt = null;
		for (Action<?> a : list) {

			// Draw the endpoint of each path
			if (a instanceof DriveAction && ((DriveAction) a).getNumPts() > 1) {

				prevPt = new Waypoint(((DriveAction) a).getPt(((DriveAction) a).getNumPts() - 1));
				drawBot(prevPt, a.getData() == 1, (Graphics2D) g);
			}

			// Draw any of the rotated robots
			if (a instanceof TurnAction && a.getData() != 0 && prevPt != null) {
				prevPt.setHeading(prevPt.getHeading() - a.getData());
				drawBot(prevPt, false, (Graphics2D) g);
			}
		}
	}

	private void drawBot(Waypoint o, boolean isFlipped, Graphics2D g) {
		Image robot = blueRobot;

		// Move the image to the point
		AffineTransform tx = new AffineTransform();
		tx.translate(in2px_x(o.getX()), in2px_y(o.getY()));

		// Scale the image
		double pxPerIn = (double) (getWidth()) / IMG_WIDTH * PIXELS_PER_INCH;
		double widthPx = pxPerIn * PathPlannerSettings.ROBOT_WIDTH_IN;
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
		double dpos = (robot.getWidth(null) / PathPlannerSettings.ROBOT_WIDTH_IN * PathPlannerSettings.ROBOT_LENGTH_IN
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

		if (m_script != null) {
			drawPaths(g);
			drawActions(g);
			drawBots(g);

			for (DriveAction path : m_script.getDriveActions())
				for (int i = 0; i < path.getNumPts(); i++)
					drawWaypoint(path.getPt(i), g);
		}
	}
}
