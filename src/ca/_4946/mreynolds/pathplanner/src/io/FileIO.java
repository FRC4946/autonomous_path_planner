package ca._4946.mreynolds.pathplanner.src.io;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ca._4946.mreynolds.customSwing.ErrorPopup;
import ca._4946.mreynolds.customSwing.StatusPopup;
import ca._4946.mreynolds.pathplanner.src.data.Script;
import ca._4946.mreynolds.pathplanner.src.data.ScriptBundle;
import ca._4946.mreynolds.pathplanner.src.data.Segment;
import ca._4946.mreynolds.pathplanner.src.data.actions.Action;
import ca._4946.mreynolds.pathplanner.src.data.actions.DelayAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.DriveAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.ElevatorAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.IntakeAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.OutputAction;
import ca._4946.mreynolds.pathplanner.src.data.point.Waypoint;
import ca._4946.mreynolds.pathplanner.src.math.PathParser;

public class FileIO {
	public static String DEFAULT_DIR = System.getProperty("user.home") + System.getProperty("file.separator")
			+ "Documents" + System.getProperty("file.separator") + "AutoPathPlanner";

	public static DecimalFormat f = new DecimalFormat("0.0#####");

	// Pulled from
	// https://wpilib.screenstepslive.com/s/currentCS/m/cs_hardware/l/282299-roborio-ftp
	public static String FTP_SERVER = "roboRIO-4946-frc.local";
	public static String FTP_USERNAME = "anonymous";// "lvuser";
	public static String FTP_PASSWORD = "";

	// public static String FTP_SERVER = "ftp.dlptest.com";
	// public static String FTP_USERNAME = "dlpuser@dlptest.com";
	// public static String FTP_PASSWORD = "hZ3Xr8alJPl8TtE";

	public static void createDefaultDir() {
		File dir = new File(DEFAULT_DIR);

		if (!dir.exists() || !dir.isDirectory())
			dir.mkdir();
	}

	public static ScriptBundle loadScript(File file) throws ParserConfigurationException, SAXException, IOException {
		ScriptBundle scripts = new ScriptBundle();

		// Read the contents of the file into the Document object `doc`
		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = dBuilder.parse(file);

		// Optional, but recommended. See here for info:
		// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();

		scripts.name = ((Element) doc.getElementsByTagName("script").item(0)).getAttribute("name");

		// // All of the nodes of the main element (the "script" element)
		// NodeList nodeList = doc.getFirstChild().getChildNodes();

		Element ll = (Element) doc.getElementsByTagName("ll").item(0);
		Element lr = (Element) doc.getElementsByTagName("lr").item(0);
		Element rl = (Element) doc.getElementsByTagName("rl").item(0);
		Element rr = (Element) doc.getElementsByTagName("rr").item(0);

		scripts.LL = loadScript(ll);
		scripts.LR = loadScript(lr);
		scripts.RL = loadScript(rl);
		scripts.RR = loadScript(rr);

		// Return the newly-read data
		return scripts;
	}

	@SuppressWarnings("unchecked")
	private static Script loadScript(Element parent) {
		Script s = new Script();

		NodeList list = parent.getElementsByTagName("action");

		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element curEl = (Element) list.item(i);

				@SuppressWarnings("rawtypes")
				Action curAction = null;

				String type = curEl.getAttribute("type");
				switch (type) {
				case "Delay":
					curAction = new DelayAction();
					break;
				case "Elevator":
					curAction = new ElevatorAction();
					break;
				case "Intake":
					curAction = new IntakeAction();
					break;
				case "Output":
					curAction = new OutputAction();
					break;
				case "Drive":
					curAction = new DriveAction();
					break;
				}

				if (curAction == null)
					continue;

				curAction.options = Enum.valueOf(curAction.options.getDeclaringClass(), curEl.getAttribute("options"));
				curAction.behaviour = Enum.valueOf(curAction.behaviour.getDeclaringClass(),
						curEl.getAttribute("behaviour"));
				curAction.data = Double.parseDouble(curEl.getAttribute("data"));
				curAction.timeout = Double.parseDouble(curEl.getAttribute("timeout"));

				if (curAction instanceof DriveAction) {
					NodeList waypoints = curEl.getElementsByTagName("waypoint");
					for (int j = 0; j < waypoints.getLength(); j++) {
						if (waypoints.item(j).getNodeType() == Node.ELEMENT_NODE) {

							Waypoint pt = new Waypoint();
							Element curPtEl = (Element) waypoints.item(j);

							pt.setX(Double.parseDouble(curPtEl.getAttribute("x")));
							pt.setY(Double.parseDouble(curPtEl.getAttribute("y")));
							pt.setR(Double.parseDouble(curPtEl.getAttribute("radius")));
							pt.setHeading(Double.parseDouble(curPtEl.getAttribute("heading")));
							pt.setAutomaticHeading(curPtEl.getAttribute("autoHeading").contains("true"));
							pt.setMagnet(curPtEl.getAttribute("magnet").contains("true"));

							((DriveAction) curAction).addPt(pt);
						}
					}
				}

				s.addAction(curAction);
			}
		}

		return s;
	}

	public static void saveScript(ScriptBundle scBundle, File file)
			throws ParserConfigurationException, TransformerException {

		// Create a DocumentBuilder object, to build the XML file
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		// Create the root element "ll"
		Document doc = docBuilder.newDocument();
		Element root = doc.createElement("script");
		doc.appendChild(root);

		root.setAttribute("name", scBundle.name);

		Element llElement = doc.createElement("ll");
		root.appendChild(saveScript(doc, llElement, scBundle.LL, false));

		PathParser.smoothAccelJerk(((DriveAction) scBundle.LL.getAction(0)).right);
		System.out.println(printPath((DriveAction) scBundle.LL.getAction(0)));

		Element lrElement = doc.createElement("lr");
		root.appendChild(saveScript(doc, lrElement, scBundle.LR, false));

		Element rlElement = doc.createElement("rl");
		root.appendChild(saveScript(doc, rlElement, scBundle.RL, false));

		Element rrElement = doc.createElement("rr");
		root.appendChild(saveScript(doc, rrElement, scBundle.RR, false));

		// Write the content into an XML file
		DOMSource source = new DOMSource(doc);

		// The file location where the file will be written
		StreamResult result = new StreamResult(file);

		// The object used to build the XML document into the file
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(source, result);

	}

	private static Element saveScript(Document doc, Element parentElement, Script sc, boolean isExport)
			throws ParserConfigurationException {

		// For every action in the script list
		for (Action<?> a : sc.getActions()) {

			// Create an element with called "Team"
			Element curElement = doc.createElement("action");
			parentElement.appendChild(curElement);

			// Set the new element's "type" attribute (Drive)
			curElement.setAttribute("type", a.getName());

			// Set the new element's "options" attribute (eg. kIntakeOn, etc)
			curElement.setAttribute("options", a.options.toString());

			// Set the new element's "timeout" attribute
			curElement.setAttribute("timeout", "" + a.timeout);

			// Set the new element's "data" attribute
			curElement.setAttribute("data", "" + a.data);

			// Set the new element's "behaviour" attribute (Sequential or Parallel)
			curElement.setAttribute("behaviour", a.behaviour.toString());

			// Print out the path
			if (a instanceof DriveAction) {

				if (!isExport) {
					for (int i = 0; i < ((DriveAction) a).getNumPts(); i++) {
						Waypoint p = ((DriveAction) a).getPt(i);

						Element curWaypoint = doc.createElement("waypoint");
						curElement.appendChild(curWaypoint);

						curWaypoint.setAttribute("x", f.format(p.getX()));
						curWaypoint.setAttribute("y", f.format(p.getY()));
						// curWaypoint.setAttribute("size", "" + p.get());
						curWaypoint.setAttribute("heading", f.format(p.getHeading()));
						curWaypoint.setAttribute("radius", f.format(p.getR()));
						curWaypoint.setAttribute("magnet", p.isMagnet() ? "true" : "false");
						curWaypoint.setAttribute("autoHeading", p.isAutomaticHeading() ? "true" : "false");
					}
				} else {
					curElement.setTextContent(printPath((DriveAction) a));
				}

			}
		}

		// Return the element that contains all the team subemelents
		return parentElement;
	}

	public static void uploadScript(ScriptBundle scBundle, File file)
			throws ParserConfigurationException, TransformerException, IOException {

		// Create a DocumentBuilder object, to build the XML file
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		// Create the root element "ll"
		Document doc = docBuilder.newDocument();
		Element root = doc.createElement("script");
		doc.appendChild(root);

		root.setAttribute("name", scBundle.name);

		Element llElement = doc.createElement("ll");
		root.appendChild(saveScript(doc, llElement, scBundle.LL, true));

		Element lrElement = doc.createElement("lr");
		root.appendChild(saveScript(doc, lrElement, scBundle.LR, true));

		Element rlElement = doc.createElement("rl");
		root.appendChild(saveScript(doc, rlElement, scBundle.RL, true));

		Element rrElement = doc.createElement("rr");
		root.appendChild(saveScript(doc, rrElement, scBundle.RR, true));

		// Write the content into an XML file
		DOMSource source = new DOMSource(doc);

		StatusPopup popup = new StatusPopup("Uploading file", "");
		EventQueue.invokeLater(() -> popup.setVisible(true));

		Thread ftpThread = new Thread(() -> {
			FTPClient ftp = ftpConnect(popup);
			if (ftp != null) {

				// The OutputStream where the file will be written
				OutputStream ftpOut = ftpOpen(ftp, popup, "script.xml");
				StreamResult result = new StreamResult(ftpOut);

				// The object used to build the XML document into the file
				try {
					Transformer transformer = TransformerFactory.newInstance().newTransformer();
					transformer.transform(source, result);
				} catch (TransformerFactoryConfigurationError | TransformerException e) {
					e.printStackTrace();
				}

				try {
					ftpOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				ftpDisconnect(ftp, popup);
			}
		});
		ftpThread.start();

	}

	private static FTPClient ftpConnect(StatusPopup log) {
		FTPClient ftp = new FTPClient();
		FTPClientConfig config = new FTPClientConfig();

		// config.setXXX(YYY); // change required options
		// for example config.setServerTimeZoneId("Pacific/Pitcairn")

		ftp.configure(config);
		int reply;

		try {

			log.append("Establishing connection...", "Connecting");
			ftp.connect(FTP_SERVER);
			log.append(ftp.getReplyString());

			log.append("Logging in as user " + FTP_USERNAME + "...");
			ftp.login(FTP_USERNAME, FTP_PASSWORD);
			log.append(ftp.getReplyString());

			// After connection attempt, you should check the reply code to verify
			// success.
			reply = ftp.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {
				log.append("Bad reply. Disconnecting...", "Disconnecting");
				ftp.disconnect();
				log.append("Disconnected");
				log.done();
				ErrorPopup.createPopup("FTP server refused connection", new Exception(ftp.getReplyString()));
				return null;
			}

		} catch (IOException e) {
			log.append("Error estalishing connection (Unable to connect or login)");
			log.done();
			ErrorPopup.createPopup("Error establishing connection (Unable to connect or login)", e);
			return null;
		}

		return ftp;
	}

	private static OutputStream ftpOpen(FTPClient ftp, StatusPopup log, String filename) {

		try {

			log.append("Going to dir /home/lvuser", "Uploading file");
			ftp.changeWorkingDirectory("/home/lvuser");
			log.append(ftp.getReplyString());

			// Check if directory exists
			log.append("Checking if directory AutoPathPlanner exists");
			if (ftp.cwd("AutoPathPlanner") == 550) { // 250=exists

				// If it does not exist, create it
				log.append("Dir does not exist. Creating directory AutoPathPlanner");
				if (!ftp.makeDirectory("AutoPathPlanner")) {

					// Throw an error if we could not create the dir
					log.append("Unable to create dir");
					log.append(ftp.getReplyString());
					log.done();
					ErrorPopup.createPopup("Unable to create directory AutoPathPlanner",
							new Exception(ftp.getReplyString()));
					return null;
				}
			} else {
				log.append("Directory AutoPathPlanner exists!");
			}

			// At this point, the directory should exist
			log.append("");

			// Set the directory
			log.append("Changing dir to AutoPathPlanner");
			ftp.changeWorkingDirectory("AutoPathPlanner");
			log.append(ftp.getReplyString());

			// Upload the file
			log.append("Opening Output Stream '" + filename + "'");
			OutputStream os = ftp.appendFileStream(filename);
			log.append(ftp.getReplyString());

			if (os == null)
				throw new IOException("Error opening output stream: " + ftp.getReplyString());

			return os;

		} catch (IOException e) {
			log.append("Status: " + ftp.getReplyString());
			log.done();
			ErrorPopup.createPopup("Error uploading file", e);
		}

		return null;

	}

	private static void ftpDisconnect(FTPClient ftp, StatusPopup log) {
		try {
			log.append("Closing file");
			ftp.completePendingCommand();
			log.append(ftp.getReplyString());

			log.append("Logging out", "Disconnect");
			ftp.logout();
			log.append(ftp.getReplyString());
			if (ftp.isConnected()) {
				log.append("Disconnecting");
				ftp.disconnect();
			}

			log.done();
		} catch (IOException e) {
			log.done();
			ErrorPopup.createPopup("Error uploading file", e);
		}
	}

	private static String printPath(DriveAction a) {

		String path = "";
		path += a.left.size() + "\n";
		for (Segment s : a.left) {
			path += f.format(s.pos) + "\t";
			path += f.format(s.vel) + "\t";
			path += f.format(s.accel) + "\t";
			path += f.format(s.jerk) + "\t";
			path += f.format(s.heading) + "\t";
			path += f.format(s.dt) + "\t";
			path += f.format(s.x) + "\t";
			path += f.format(s.y) + "\n";
		}
		path += a.right.size() + "\n";
		for (Segment s : a.right) {
			path += f.format(s.pos) + "\t";
			path += f.format(s.vel) + "\t";
			path += f.format(s.accel) + "\t";
			path += f.format(s.jerk) + "\t";
			path += f.format(s.heading) + "\t";
			path += f.format(s.dt) + "\t";
			path += f.format(s.x) + "\t";
			path += f.format(s.y) + "\n";
		}

		return path;
	}

	public static JFileChooser getFileChooser() {
		FileIO.createDefaultDir();
		JFileChooser fileChooser = new JFileChooser(FileIO.DEFAULT_DIR);

		FileNameExtensionFilter filter = new FileNameExtensionFilter("XML", "xml");
		fileChooser.setFileFilter(filter);

		return fileChooser;
	}

	public static JFileChooser getFileChooser(String defaultFile) {
		JFileChooser fc = getFileChooser();
		fc.setSelectedFile(new File(defaultFile));
		return fc;
	}
}
