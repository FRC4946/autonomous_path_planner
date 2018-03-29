package ca._4946.mreynolds.pathplanner.src.io;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Properties;

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
import ca._4946.mreynolds.pathplanner.src.data.actions.Action.Behaviour;
import ca._4946.mreynolds.pathplanner.src.data.actions.ArmAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.DelayAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.DriveAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.ElevatorAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.IntakeAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.OutputAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.TurnAction;
import ca._4946.mreynolds.pathplanner.src.data.point.ControlPoint;
import ca._4946.mreynolds.pathplanner.src.math.PathParser;

/**
 * Contains the utilities for reading and writing {@code XML} data to the host
 * computer and uploading files to the robot using
 * {@link org.apache.commons.net}
 * 
 * @author Matthew Reynolds
 * 
 */
public class FileIO {
	private static DecimalFormat f = new DecimalFormat("0.0#####");

	/**
	 * The default directory for the application's files on the host machine. This
	 * is usually {@code <Username>/Documents/AutoPathPlanner/}
	 */
	public static String WORKING_DIR = System.getProperty("user.home") + System.getProperty("file.separator")
			+ "Documents" + System.getProperty("file.separator") + "AutoPathPlanner";

	/**
	 * The default directory for the scripts. Equivalent to
	 * {@link #WORKING_DIR}{@code + "/Scripts"}
	 */
	public static String SCRIPT_DIR = WORKING_DIR + System.getProperty("file.separator") + "Scripts";

	/**
	 * The default directory for the motion profiles. Equivalent to
	 * {@link #WORKING_DIR}{@code + "/Profiles"}
	 */
	public static String PROFILE_DIR = WORKING_DIR + System.getProperty("file.separator") + "Profiles";

	// Pulled from
	// wpilib.screenstepslive.com/s/currentCS/m/cs_hardware/l/282299-roborio-ftp
	public static String FTP_SERVER = "roboRIO-4946-frc.local";
	public static String FTP_USERNAME = "anonymous";// "lvuser";
	public static String FTP_PASSWORD = "";

	/**
	 * Ensure the {@link #WORKING_DIR}, {@link #SCRIPT_DIR}, and
	 * {@link #PROFILE_DIR} exist
	 */
	public static void createDefaultDir() {
		File dir = new File(WORKING_DIR);
		if (!dir.exists() || !dir.isDirectory())
			dir.mkdir();

		dir = new File(SCRIPT_DIR);
		if (!dir.exists() || !dir.isDirectory())
			dir.mkdir();

		dir = new File(PROFILE_DIR);
		if (!dir.exists() || !dir.isDirectory())
			dir.mkdir();
	}

	// /**
	// * Get the application data of the program. This directory is different based
	// on
	// * the OS. On Windows, it is normally {@code \Users\<name>\AppData\Roaming}
	// (AKA
	// * {@code %AppData%}), while on OS X it is
	// * {@code ~/Library/Application Support/PathPlanner}
	// *
	// * @return a string containing the url of the data directory
	// */
	// public static String getDataDir() {
	//
	// String workingDirectory = "";
	//
	// // Assign the name of the OS, according to Java, to a variable to
	// // determine what the workingDirectory is.
	// String OS = (System.getProperty("os.name")).toUpperCase();
	//
	// // If the operating system is Windows, the workingDirectory is the
	// // location of the "AppData" folder
	// if (OS.contains("WIN"))
	// workingDirectory = System.getenv("AppData");
	//
	// // Otherwise, we assume Linux or Mac
	// else {
	// // In either case, we would start in the user's home directory
	// workingDirectory = System.getProperty("user.home");
	//
	// // If we are on a Mac, we look for "Application Support"
	// if (OS.contains("OS X"))
	// workingDirectory += "/Library/Application Support";
	// }
	//
	// workingDirectory += "/ReynoldsFMS";
	// return workingDirectory;
	// }

	/**
	 * Load a {@link ScriptBundle} from the specified {@code XML} {@link File}
	 * 
	 * @param file
	 *            the {@code XML File} to read
	 * @return the read {@code ScriptBundle}
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static ScriptBundle loadScript(File file) throws ParserConfigurationException, SAXException, IOException {
		ScriptBundle scripts = new ScriptBundle();

		// Read the contents of the file into the Document object `doc`
		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = dBuilder.parse(file);

		// Optional, but recommended. See here for info:
		// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();

		if (doc.getElementsByTagName("script").getLength() != 1)
			throw new IOException("Invalid script file!");

		// Load all of the data from the file
		scripts.name = ((Element) doc.getElementsByTagName("script").item(0)).getAttribute("name");
		scripts.notes = ((Element) doc.getElementsByTagName("script").item(0)).getAttribute("notes");

		Element ll = (Element) doc.getElementsByTagName("ll").item(0);
		Element lr = (Element) doc.getElementsByTagName("lr").item(0);
		Element rl = (Element) doc.getElementsByTagName("rl").item(0);
		Element rr = (Element) doc.getElementsByTagName("rr").item(0);

		scripts.setScript((ll == null) ? new Script() : loadScript(ll), "ll");
		scripts.setScript((lr == null) ? new Script() : loadScript(lr), "lr");
		scripts.setScript((rl == null) ? new Script() : loadScript(rl), "rl");
		scripts.setScript((rr == null) ? new Script() : loadScript(rr), "rr");

		// Return the newly-read data
		return scripts;
	}

	/**
	 * Load a {@link Script} from the specified {@link Element}, assuming the
	 * {@code Element} has a tag name equal to one of {@code ll}, {@code lr},
	 * {@code rl}, or {@code rr}
	 * 
	 * @param parent
	 *            the {@code Element} to read
	 * 
	 * @return the read {@code Script}
	 */
	@SuppressWarnings("unchecked")
	private static Script loadScript(Element parent) {
		Script s = new Script();

		// Iterate through every action element
		NodeList list = parent.getElementsByTagName("action");
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element curEl = (Element) list.item(i);

			@SuppressWarnings("rawtypes")
			Action curAction = null;

			// Create the Action with the specified type
			String type = curEl.getAttribute("type");
			switch (type) {
			case "Delay":
				curAction = new DelayAction();
				break;
			case "Elevator":
				curAction = new ElevatorAction();
				break;
			case "Arm":
				curAction = new ArmAction();
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
			case "Turn":
				curAction = new TurnAction();
				break;
			default:
				curAction = null;
			}

			// If the type was invalid, skip this action and proceed to the next action in
			// the script
			if (curAction == null)
				continue;

			// Load the options
			try {
				curAction.setOptions(
						Enum.valueOf(curAction.getOption().getDeclaringClass(), curEl.getAttribute("options")));
			} catch (IllegalArgumentException e) {
				curAction.setOptions(curAction.getDefaultOption());
			}

			// Load the behaviour
			try {
				curAction.setBehaviour(
						Enum.valueOf(curAction.getBehaviour().getDeclaringClass(), curEl.getAttribute("behaviour")));
			} catch (IllegalArgumentException e) {
				curAction.setBehaviour(Behaviour.kSequential);
			}

			// Load the remaining action parameters
			curAction.setDelay(Double.parseDouble(curEl.getAttribute("delay") + "0"));
			curAction.setData(Double.parseDouble(curEl.getAttribute("data") + "0"));
			curAction.setTimeout(Double.parseDouble(curEl.getAttribute("timeout") + "0"));

			// If the action is a DriveAction, we need to load more data
			if (curAction instanceof DriveAction) {

				// Iterate through every waypoint element
				NodeList controlpts = curEl.getElementsByTagName("waypoint"); // TODO: Change to "controlpoint"
				for (int j = 0; j < controlpts.getLength(); j++) {
					if (controlpts.item(j).getNodeType() != Node.ELEMENT_NODE)
						continue;

					// Load the point
					ControlPoint pt = new ControlPoint();
					Element curPtEl = (Element) controlpts.item(j);

					pt.setX(Double.parseDouble(curPtEl.getAttribute("x") + "0"));
					pt.setY(Double.parseDouble(curPtEl.getAttribute("y") + "0"));
					pt.setR(Double.parseDouble(curPtEl.getAttribute("radius") + "0"));
					pt.setHeading(Double.parseDouble(curPtEl.getAttribute("heading") + "0"));
					pt.setAutomaticHeading(curPtEl.getAttribute("autoHeading").contains("true"));
					pt.setMagnet(curPtEl.getAttribute("magnet").contains("true"));

					((DriveAction) curAction).addPt(pt);
				}

			}

			s.addAction(curAction);
		}

		return s;

	}

	/**
	 * Save the specified {@link ScriptBundle} to the specified {@code XML}
	 * {@link File}
	 * 
	 * @param bundle
	 *            the {@code ScriptBundle} to write
	 * @param file
	 *            the {@code File} to write
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	public static void saveScript(ScriptBundle bundle, File file)
			throws ParserConfigurationException, TransformerException {

		// Create a DocumentBuilder object, to build the XML file
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		// Create the root element "script"
		Document doc = docBuilder.newDocument();
		Element root = doc.createElement("script");
		doc.appendChild(root);

		// Load all the data into the script element
		root.setAttribute("name", bundle.name);
		root.setAttribute("notes", bundle.notes);
		root.appendChild(saveScript(doc, "ll", bundle.getScript("ll"), false));
		root.appendChild(saveScript(doc, "lr", bundle.getScript("lr"), false));
		root.appendChild(saveScript(doc, "rl", bundle.getScript("rl"), false));
		root.appendChild(saveScript(doc, "rr", bundle.getScript("rr"), false));

		// for (DriveAction a : scBundle.LL.getDriveActions()) {
		// PathParser.smoothAccelJerk(a.getLeftPath());
		// PathParser.smoothAccelJerk(a.getRightPath());
		// System.out.println(printPath(a));
		// }

		// Write the content into an XML file
		DOMSource source = new DOMSource(doc);

		// The file location where the file will be written
		StreamResult result = new StreamResult(file);

		// The object used to build the XML document into the file
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(source, result);

	}

	/**
	 * Create an {@link Element} describing the specified {@link Script}
	 * 
	 * @param doc
	 *            the resulting {@code Element}'s source {@link Document}
	 * @param config
	 *            the configuration this {@code Script} specifies. Must be one of
	 *            {@code "ll"}, {@code "lr"}, {@code "rl"}, or {@code "rr"}
	 * @param script
	 *            the {@code Script} to read
	 * @param isExport
	 *            {@code true} if the resulting element will be uploaded to the
	 *            robot rather than saved on the host PC
	 * @return the generated {@code Element}
	 * @throws ParserConfigurationException
	 */
	private static Element saveScript(Document doc, String config, Script script, boolean isExport)
			throws ParserConfigurationException {

		Element parentElement = doc.createElement(config);

		// For every action in the script list
		for (Action<?> a : script.getActions()) {

			// Create an element with called "Team"
			Element curElement = doc.createElement("action");
			parentElement.appendChild(curElement);

			// Set the new element's "type" attribute (Drive)
			curElement.setAttribute("type", a.getName());

			// Set the new element's "options" attribute (eg. kIntakeOn, etc)
			curElement.setAttribute("options", a.getOption().toString());

			// Set the new element's "timeout" attribute
			curElement.setAttribute("timeout", "" + a.getTimeout());

			// Set the new element's "delay" attribute
			curElement.setAttribute("delay", "" + a.getDelay());

			// Set the new element's "data" attribute
			curElement.setAttribute("data", "" + a.getData());

			// Set the new element's "behaviour" attribute (Sequential or Parallel)
			curElement.setAttribute("behaviour", a.getBehaviour().toString());

			// Print out the path
			if (a instanceof DriveAction) {

				if (!isExport) {
					for (int i = 0; i < ((DriveAction) a).getNumPts(); i++) {
						ControlPoint p = ((DriveAction) a).getPt(i);

						// TODO: Change "waypoint" to "controlpoint"
						Element curPt = doc.createElement("waypoint");
						curElement.appendChild(curPt);

						curPt.setAttribute("x", f.format(p.getX()));
						curPt.setAttribute("y", f.format(p.getY()));
						curPt.setAttribute("heading", f.format(p.getHeading()));
						curPt.setAttribute("radius", f.format(p.getR()));
						curPt.setAttribute("magnet", p.isMagnet() ? "true" : "false");
						curPt.setAttribute("autoHeading", p.isAutomaticHeading() ? "true" : "false");
					}
				} else {

					PathParser.smoothAccelJerk(((DriveAction) a).getLeftPath());
					PathParser.smoothAccelJerk(((DriveAction) a).getRightPath());
					curElement.setTextContent(printPath((DriveAction) a));
				}

			}
		}

		// Return the element that contains all the team subemelents
		return parentElement;
	}

	/**
	 * Upload the specified {@link ScriptBundle} to the robot with the specified
	 * {@link File} name
	 * 
	 * @param scBundle
	 *            the {@code ScriptBundle} to upload
	 * @param file
	 *            the {@code File} name
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 * @throws IOException
	 */
	public static void uploadScript(ScriptBundle scBundle, File file)
			throws ParserConfigurationException, TransformerException, IOException {

		// Create a DocumentBuilder object, to build the XML file
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		// Create the root element "script"
		Document doc = docBuilder.newDocument();
		Element root = doc.createElement("script");
		doc.appendChild(root);

		// Load all the data into the script element
		root.setAttribute("name", scBundle.name);
		root.setAttribute("notes", scBundle.notes);
		root.appendChild(saveScript(doc, "ll", scBundle.getScript("ll"), true));
		root.appendChild(saveScript(doc, "lr", scBundle.getScript("lr"), true));
		root.appendChild(saveScript(doc, "rl", scBundle.getScript("rl"), true));
		root.appendChild(saveScript(doc, "rr", scBundle.getScript("rr"), true));

		// Write the content into an XML file
		DOMSource source = new DOMSource(doc);

		// Display a status popup while we upload
		StatusPopup popup = new StatusPopup("Uploading file", "");
		EventQueue.invokeLater(() -> popup.setVisible(true));

		// Spawn a thread to write to the robot over FTP. We use concurrency so as to
		// not stall Swing
		Thread ftpThread = new Thread(() -> {

			// Open an FTP connection to the robot
			FTPClient ftp = ftpConnect(popup);
			if (ftp == null)
				return;

			// The OutputStream where the file will be written
			OutputStream ftpOut = ftpOpen(ftp, popup, "script.xml"); // TODO: Use the file parameter
			StreamResult result = new StreamResult(ftpOut);

			// The object used to build the XML document into the file
			try {
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.transform(source, result);
			} catch (TransformerFactoryConfigurationError | TransformerException e) {
				e.printStackTrace();
			}

			// Close the FTP connection
			try {
				ftpOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			ftpDisconnect(ftp, popup);
		});
		ftpThread.start();
	}

	/**
	 * Open an FTP connection with the robot
	 * 
	 * @param log
	 *            the {@link StatusPopup} to output the log to
	 * @return the connected {@link FTPClient}
	 */
	private static FTPClient ftpConnect(StatusPopup log) {
		FTPClient ftp = new FTPClient();
		FTPClientConfig config = new FTPClientConfig();

		ftp.configure(config);
		int reply;
		try {

			// Set the timeout
			// TODO: This doesn't work
			Properties env = System.getProperties();
			env.put("com.example.jndi.dns.recursion", "false");
			env.put("com.example.jndi.dns.timeout.initial", "500");
			env.put("com.example.jndi.dns.timeout.retries", "1");

			// Connect to the server
			log.append("Establishing connection...", "Connecting");
			ftp.setDefaultTimeout(1000);
			ftp.connect(FTP_SERVER);
			log.append(ftp.getReplyString());

			// Log in
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

	/**
	 * Open an {@link OutputStream} on the FTP server where we can write files
	 * 
	 * @param ftp
	 *            the {@link FTPClient}
	 * @param log
	 *            the {@link StatusPopup} to output the log to
	 * @param filename
	 *            the name of the file to write
	 * @return the {@code OutputStream} to the new file, or null if an error
	 *         occurred
	 */
	private static OutputStream ftpOpen(FTPClient ftp, StatusPopup log, String filename) {
		try {

			// Go to the user home
			log.append("Going to dir /home/lvuser", "Uploading file");
			ftp.changeWorkingDirectory("/home/lvuser");
			log.append(ftp.getReplyString());

			// Check if our directory exists
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

			// Delete the old version of the file
			ftp.deleteFile(filename);

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

	/**
	 * Close the FTP stream and connection
	 * 
	 * @param ftp
	 *            the {@link FTPClient} to close
	 * @param log
	 *            the {@link StatusPopup} to output the log to
	 */
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

	/**
	 * Get every segment of the path pair in a formatted string
	 * 
	 * @param a
	 *            the {@link DriveAction} to parse
	 * @return the formatted string
	 */
	private static String printPath(DriveAction a) {

		String path = "";
		path += a.getLeftPath().size() + "\t";
		for (Segment s : a.getLeftPath()) {
			path += f.format(s.pos) + "\t";
			path += f.format(s.vel) + "\t";
			path += f.format(s.accel) + "\t";
			path += f.format(s.jerk) + "\t";
			path += f.format(s.heading) + "\t";
			path += f.format(s.dt) + "\t";
			path += f.format(s.x) + "\t";
			path += f.format(s.y) + "\n";
		}
		path += a.getRightPath().size() + "\t";
		for (Segment s : a.getRightPath()) {
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

	/**
	 * @return a {@link JFileChooser} filtering for {@code XML} files in the
	 *         {@link #SCRIPT_DIR}
	 */
	public static JFileChooser getFileChooser() {

		JFileChooser fileChooser = new JFileChooser(SCRIPT_DIR);

		FileNameExtensionFilter filter = new FileNameExtensionFilter("XML", "xml");
		fileChooser.setFileFilter(filter);

		return fileChooser;
	}

	/**
	 * @param defaultFile
	 *            the default file to select
	 * @return a {@link JFileChooser} filtering for {@code XML} files in the
	 *         {@link #SCRIPT_DIR}
	 */
	public static JFileChooser getFileChooser(String defaultFile) {
		JFileChooser fc = getFileChooser();
		fc.setSelectedFile(new File(defaultFile));
		return fc;
	}
}
