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
import javax.swing.Timer;

import ca._4946.mreynolds.customSwing.ErrorPopup;
import ca._4946.mreynolds.pathplanner.src.PathPlannerSettings;
import ca._4946.mreynolds.pathplanner.src.data.CubicBezier;
import ca._4946.mreynolds.pathplanner.src.data.Script;
import ca._4946.mreynolds.pathplanner.src.data.Segment;
import ca._4946.mreynolds.pathplanner.src.data.actions.Action;
import ca._4946.mreynolds.pathplanner.src.data.actions.Action.Behaviour;
import ca._4946.mreynolds.pathplanner.src.data.actions.DriveAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.TurnAction;
import ca._4946.mreynolds.pathplanner.src.data.point.MagnetPoint;
import ca._4946.mreynolds.pathplanner.src.data.point.Point;
import ca._4946.mreynolds.pathplanner.src.data.point.ControlPoint;
import ca._4946.mreynolds.pathplanner.src.math.PathParser;

public class FieldPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private Script m_script = null;
	private String m_data = "ll";
	private boolean m_isBlue = true;
	private boolean m_isInteractive = true;

	private boolean m_hasFresh = false;

	private Image m_blueField;
	private Image m_redField;
	private Image m_switchRedL;
	private Image m_switchBlueL;
	private Image m_scaleRedL;
	private Image m_scaleBlueL;
	private Image m_blueRobot;
	private Image m_redRobot;

	public static final int IMG_WIDTH = 748;
	public static final int IMG_HEIGHT = 812;
	public static final double PIXELS_PER_INCH = 2.09350808615;
	public static final int ORIGIN_X_PX = IMG_WIDTH / 2;
	public static final int ORIGIN_Y_PX = 752;

	// Refresh at 15fps
	private Timer m_refreshTimer = new Timer(1000 / 15, e -> {
		if (m_hasFresh)
			repaint();
	});

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
	public FieldPanel(boolean isInteractive) {
		m_isInteractive = isInteractive;

		setupUI();

		if (m_isInteractive) {
			addMouseListener(mouse);
			addMouseMotionListener(mouse);
		}

		m_refreshTimer.start();
	}

	/**
	 * Create the panel
	 */
	private void setupUI() {
		try {
			String fieldDir = "/ca/_4946/mreynolds/pathplanner/resources/field/";
			String robotDir = "/ca/_4946/mreynolds/pathplanner/resources/robot/";

			m_blueField = ImageIO.read(this.getClass().getResource(fieldDir + "Blue.png"));
			m_redField = ImageIO.read(this.getClass().getResource(fieldDir + "Red.png"));
			m_switchRedL = ImageIO.read(this.getClass().getResource(fieldDir + "Switch Red L.png"));
			m_switchBlueL = ImageIO.read(this.getClass().getResource(fieldDir + "Switch Blue L.png"));
			m_scaleRedL = ImageIO.read(this.getClass().getResource(fieldDir + "Scale Red L.png"));
			m_scaleBlueL = ImageIO.read(this.getClass().getResource(fieldDir + "Scale Blue L.png"));
			m_redRobot = ImageIO.read(this.getClass().getResource(robotDir + "Red.png"));
			m_blueRobot = ImageIO.read(this.getClass().getResource(robotDir + "Blue.png"));

		} catch (IOException | IllegalArgumentException e) {
			ErrorPopup.createPopup("Error loading resources", e);
			e.printStackTrace();
		}
	}

	public void drawPt(Graphics g, Point pt) {
		int radius = (int) (pt.getSize() * ((double) getWidth() / (double) IMG_WIDTH));

		g.fillOval((int) Math.round(pt.getX() - radius), (int) Math.round(pt.getY() - radius), 2 * radius, 2 * radius);
	}

	public void drawBackground(Graphics g) {
		if (m_isBlue) {
			g.drawImage(m_blueField, 0, 0, getWidth(), getHeight(), this);
			if (m_data.charAt(1) == 'l')
				g.drawImage(m_scaleBlueL, 0, 0, getWidth(), getHeight(), this);
			else
				g.drawImage(m_scaleRedL, 0, 0, getWidth(), getHeight(), this);

			if (m_data.charAt(0) == 'l')
				g.drawImage(m_switchBlueL, 0, 0, getWidth(), getHeight(), this);
			else
				g.drawImage(m_switchRedL, 0, 0, getWidth(), getHeight(), this);
		}

		else {
			g.drawImage(m_redField, 0, 0, getWidth(), getHeight(), this);
			if (m_data.charAt(1) == 'l')
				g.drawImage(m_scaleRedL, 0, 0, getWidth(), getHeight(), this);
			else
				g.drawImage(m_scaleBlueL, 0, 0, getWidth(), getHeight(), this);

			if (m_data.charAt(0) == 'l')
				g.drawImage(m_switchRedL, 0, 0, getWidth(), getHeight(), this);
			else
				g.drawImage(m_switchBlueL, 0, 0, getWidth(), getHeight(), this);
		}
	}

	public void drawMagnets(Graphics g) {
		g.setColor(Color.YELLOW);
		for (MagnetPoint p : PathPlannerSettings.getMagnets())
			drawPt(g, pt2px(p));
	}

	public void drawPaths(Graphics g) {
		for (DriveAction path : m_script.getDriveActions()) {

			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(1));

			// path.generatePath();

			for (int i = 0; i < path.getLeftPath().size(); i++) {
				Point l = pt2px(new Point(path.getLeftPath().get(i).x, path.getLeftPath().get(i).y));
				Point r = pt2px(new Point(path.getRightPath().get(i).x, path.getRightPath().get(i).y));

				g.setColor(Color.GREEN);
				g.drawLine((int) l.getX(), (int) l.getY(), (int) r.getX(), (int) r.getY());

				g.setColor(Color.RED);
				drawPt(g, l);
				drawPt(g, r);
			}

			g.setColor(Color.CYAN);
			for (Segment s : PathParser.fillPath(path.getCurves()))
				drawPt(g, pt2px(new Point(s.x, s.y)));
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
				drawPt(g, p);
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

		ControlPoint prevPt = null;
		for (Action<?> a : list) {

			// Draw the endpoint of each path
			if (a instanceof DriveAction && ((DriveAction) a).getNumPts() > 1) {

				prevPt = new ControlPoint(((DriveAction) a).getPt(((DriveAction) a).getNumPts() - 1));
				drawBot(prevPt, a.getData() == 1, (Graphics2D) g);
			}

			// Draw any of the rotated robots
			if (a instanceof TurnAction && a.getData() != 0 && prevPt != null) {
				prevPt.setHeading(prevPt.getHeading() - a.getData());
				drawBot(prevPt, false, (Graphics2D) g);
			}
		}
	}

	private void drawBot(ControlPoint o, boolean isFlipped, Graphics2D g) {
		Image robot = m_isBlue ? m_blueRobot : m_redRobot;

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

	private void drawControlPoint(ControlPoint p, Graphics g) {
		Point h1 = pt2px(p.getHandle());
		Point h2 = pt2px(p.getFlipHandle());

		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(2));
		g2.setColor(Color.BLUE);
		g.drawLine((int) h1.getX(), (int) h1.getY(), (int) h2.getX(), (int) h2.getY());
		drawPt(g, pt2px(p));

		g2.setColor(Color.MAGENTA);
		drawPt(g2, h1);
		drawPt(g2, h2);
	}

	public void paintComponent(Graphics g) {
		drawBackground(g);

		if (m_script != null) {
			drawPaths(g);
			drawActions(g);
			drawBots(g);

			if (m_isInteractive) {
				drawMagnets(g);
			}
			for (DriveAction path : m_script.getDriveActions())
				for (int i = 0; i < path.getNumPts(); i++)
					drawControlPoint(path.getPt(i), g);
		}
	}

	MouseAdapter mouse = new MouseAdapter() {
		boolean refIsHandle = false;
		boolean handleIsFlip = false;
		DriveAction curAction = null;
		int ref = -1;

		private ControlPoint curPt() {
			if (curAction == null)
				return new ControlPoint();
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
				for (MagnetPoint mag : PathPlannerSettings.getMagnets())
					if (mag.contains(curPt())) {
						foundMagnet = true;

						curPt().setX(mag.getX());
						curPt().setY(mag.getY());
						if (mag.hasHeading() && !refIsHandle) {
							((ControlPoint) curPt()).setHeading(mag.getHeading());
							((ControlPoint) curPt()).setMagnet(true);
						}
					}

				if (!foundMagnet)
					curPt().setMagnet(false);
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			curAction = null;

			if (m_script.getDriveActions().isEmpty())
				m_script.addAction(new DriveAction());

			Point click = pt2in(new Point(e.getPoint()));

			List<DriveAction> actions = m_script.getDriveActions();
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
					for (int i = 0; i < curAction.getCurves().size(); i++) {
						CubicBezier curve = curAction.getCurves().get(i);

						if (curve.ptIsOnCurve(click, 0.15)) {
							curAction.addPt(i + 1, new ControlPoint(click));
							ref = i + 1;
							refIsHandle = false;
							return;
						}
					}
				}

				// If we haven't found a match and this is the last action in the script...
				if (ref == -1 && j == actions.size() - 1) {
					ref = curAction.getNumPts();
					curAction.addPt(new ControlPoint(click));
					refIsHandle = false;

					// Check for magnet points
					for (MagnetPoint mag : PathPlannerSettings.getMagnets())
						if (mag.contains(click))
							mag.latch(curAction.getPt(ref));
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			ref = -1;
			curAction = null;
		}
	};

	/**
	 * @return the color
	 */
	public boolean isBlue() {
		return m_isBlue;
	}

	/**
	 * @param isBlue
	 *            the color to set
	 */
	public void setBlue(boolean isBlue) {
		this.m_isBlue = isBlue;
		repaint();
	}

	public void setScript(Script script, String data) {
		m_script = script;
		m_data = data;

		if (m_script != null)
			m_script.getActions().addListListener(() -> m_hasFresh = true);
		repaint();
	}
}
