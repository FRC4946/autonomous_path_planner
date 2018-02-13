package ca._4946.mreynolds.pathplanner.src.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import ca._4946.mreynolds.pathplanner.src.PathPlanner;
import ca._4946.mreynolds.pathplanner.src.data.Script;
import ca._4946.mreynolds.pathplanner.src.data.actions.Action;
import ca._4946.mreynolds.pathplanner.src.data.actions.DelayAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.DriveAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.ElevatorAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.IntakeAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.OutputAction;
import ca._4946.mreynolds.pathplanner.src.data.point.Waypoint;
import ca._4946.mreynolds.pathplanner.src.io.FileIO;
import ca._4946.mreynolds.util.ObservableList;

public class ControlPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	JPanel actionListPanel;
	JComboBox<String> fieldConfigDropdown;
	JTextField scriptNameField;
	JLabel statusLbl;
	ButtonGroup actionSelectButtonGroup;

	private Thread pingThread = new Thread(() -> {

		InetAddress address;
		boolean isConnected = false;
		boolean running = true; // TODO: This is bad

		while (running) {
			try {
				address = InetAddress.getByName(FileIO.FTP_SERVER);
				isConnected = address.isReachable(1000);
			} catch (IOException e) {
				// NOT CONNECTED!
				isConnected = false;
			}

			if (isConnected) {
				statusLbl.setText("Connected");
				statusLbl.setBackground(Color.GREEN);
			} else {

				statusLbl.setText("Not Connected");
				statusLbl.setBackground(Color.RED);
			}

			// Sleep 2sec
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	});

	/**
	 * Create the panel.
	 */
	public ControlPanel() {
		initialize();
		setupKeyListeners();
		setupListeners();

		pingThread.start();
	}

	private void initialize() {

		JPanel btnPanel = new JPanel();
		{
			JButton loadBtn = new JButton("Load Script");
			loadBtn.addActionListener(e -> {
				JFileChooser fc = FileIO.getFileChooser();
				if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					PathPlanner.main.load(file);
					scriptNameField.setText(PathPlanner.main.getScriptName());
					setupListeners();
				}
			});

			btnPanel.add(loadBtn);

			JButton saveBtn = new JButton("Save Script");
			saveBtn.addActionListener(e -> {
				PathPlanner.main.setScriptName(scriptNameField.getText());

				JFileChooser fc = FileIO.getFileChooser(scriptNameField.getText() + ".xml");
				if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					if (!file.getName().endsWith(".xml"))
						file = new File(file.getAbsolutePath() + ".xml");

					PathPlanner.main.save(file);
				}
			});
			btnPanel.add(saveBtn);

			JButton uploadBtn = new JButton("Upload Script");
			uploadBtn.addActionListener(e -> {
				PathPlanner.main.setScriptName(scriptNameField.getText());
				PathPlanner.main.upload(new File(scriptNameField.getText() + ".xml"));
			});
			btnPanel.add(uploadBtn);

			JButton clearBtn = new JButton("Clear Script!");
			btnPanel.add(clearBtn);
			clearBtn.addActionListener(e -> PathPlanner.main.getScript().clear());
		}
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JPanel statusPanel = new JPanel();
		panel.add(statusPanel);
		statusPanel.setMaximumSize(new Dimension(32767, 30));
		GridBagLayout gbl_statusPanel = new GridBagLayout();
		gbl_statusPanel.columnWidths = new int[] { 0, 60, 150, 0, 75, 0 };
		gbl_statusPanel.rowHeights = new int[] { 30, 0 };
		gbl_statusPanel.columnWeights = new double[] { 1.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_statusPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		statusPanel.setLayout(gbl_statusPanel);

		{

			JLabel lblScriptName = new JLabel("Script Name:");
			lblScriptName.setFont(new Font("Tahoma", Font.BOLD, 18));
			GridBagConstraints gbc_lblScriptName = new GridBagConstraints();
			gbc_lblScriptName.anchor = GridBagConstraints.WEST;
			gbc_lblScriptName.insets = new Insets(0, 0, 0, 5);
			gbc_lblScriptName.gridx = 1;
			gbc_lblScriptName.gridy = 0;
			statusPanel.add(lblScriptName, gbc_lblScriptName);

			scriptNameField = new JTextField();
			GridBagConstraints gbc_scriptNameField = new GridBagConstraints();
			gbc_scriptNameField.fill = GridBagConstraints.BOTH;
			gbc_scriptNameField.insets = new Insets(0, 0, 0, 5);
			gbc_scriptNameField.gridx = 2;
			gbc_scriptNameField.gridy = 0;
			statusPanel.add(scriptNameField, gbc_scriptNameField);
			scriptNameField.setColumns(20);
			scriptNameField.addActionListener(e -> PathPlanner.main.setScriptName(scriptNameField.getText()));

			statusLbl = new JLabel("Not Connected");
			statusLbl.setHorizontalAlignment(SwingConstants.CENTER);
			statusLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
			statusLbl.setOpaque(true);
			statusLbl.setBackground(Color.RED);
			GridBagConstraints gbc_statusLbl = new GridBagConstraints();
			gbc_statusLbl.fill = GridBagConstraints.BOTH;
			gbc_statusLbl.gridx = 4;
			gbc_statusLbl.gridy = 0;
			statusPanel.add(statusLbl, gbc_statusLbl);
		}

		JPanel configPanel = new JPanel();
		panel.add(configPanel);
		configPanel.setMaximumSize(new Dimension(32767, 100));
		GridBagLayout gbl_configPanel = new GridBagLayout();
		gbl_configPanel.rowWeights = new double[] { 1.0, 1.0 };
		gbl_configPanel.columnWeights = new double[] { 0.0, 0.0, 0.0 };
		configPanel.setLayout(gbl_configPanel);
		JLabel alliancePanelLabel = new JLabel("Alliance:");
		GridBagConstraints gbc_allianceLbl = new GridBagConstraints();
		gbc_allianceLbl.anchor = GridBagConstraints.EAST;
		gbc_allianceLbl.insets = new Insets(0, 0, 5, 5);
		gbc_allianceLbl.gridx = 0;
		gbc_allianceLbl.gridy = 0;

		JPanel allianceSelectPanel = new JPanel();
		GridBagConstraints gbc_allianceSelectPanel = new GridBagConstraints();
		gbc_allianceSelectPanel.fill = GridBagConstraints.BOTH;
		gbc_allianceSelectPanel.insets = new Insets(0, 0, 5, 0);
		gbc_allianceSelectPanel.gridx = 2;
		gbc_allianceSelectPanel.gridy = 0;

		actionSelectButtonGroup = new ButtonGroup();
		{

			ButtonGroup allianceBtns = new ButtonGroup();

			JRadioButton redAllianceBtn = new JRadioButton("Red");
			redAllianceBtn.addActionListener(e -> PathPlanner.main.fieldIsBlue = false);

			JRadioButton blueAllianceBtn = new JRadioButton("Blue");
			blueAllianceBtn.addActionListener(e -> PathPlanner.main.fieldIsBlue = true);
			allianceBtns.add(blueAllianceBtn);
			allianceBtns.add(redAllianceBtn);
			allianceBtns.setSelected(blueAllianceBtn.getModel(), true);
			allianceSelectPanel.add(blueAllianceBtn);
			allianceSelectPanel.add(redAllianceBtn);
		}

		JLabel lblConfig = new JLabel("Field Configuration:");
		GridBagConstraints gbc_lblConfig = new GridBagConstraints();
		gbc_lblConfig.anchor = GridBagConstraints.EAST;
		gbc_lblConfig.insets = new Insets(0, 0, 0, 5);
		gbc_lblConfig.gridx = 0;
		gbc_lblConfig.gridy = 1;

		fieldConfigDropdown = new JComboBox<String>();
		fieldConfigDropdown.setModel(new DefaultComboBoxModel<String>(new String[] { "LL", "LR", "RL", "RR" }));
		fieldConfigDropdown.setSelectedIndex(0);
		fieldConfigDropdown.setMaximumRowCount(4);
		fieldConfigDropdown.addActionListener(e -> {

			String msg = (String) ((JComboBox<?>) e.getSource()).getSelectedItem();
			PathPlanner.main.switchIsL = (msg.charAt(0) == 'L');
			PathPlanner.main.scaleIsL = (msg.charAt(1) == 'L');

			updateActionList(PathPlanner.main.getScript().getActions());
			// updateWaypointList(PathPlanner.main.getScript().getSelectedAction().waypoints);

		});

		GridBagConstraints gbc_configDropdown = new GridBagConstraints();
		gbc_configDropdown.fill = GridBagConstraints.BOTH;
		gbc_configDropdown.gridx = 2;
		gbc_configDropdown.gridy = 1;

		configPanel.add(alliancePanelLabel, gbc_allianceLbl);
		configPanel.add(allianceSelectPanel, gbc_allianceSelectPanel);
		configPanel.add(lblConfig, gbc_lblConfig);
		configPanel.add(fieldConfigDropdown, gbc_configDropdown);

		JPanel actionPanel = new JPanel();
		add(actionPanel);
		actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
		{
			JLabel actionsLbl = new JLabel("Script Actions");
			actionsLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
			actionsLbl.setFont(new Font("Tahoma", Font.BOLD, 18));

			JScrollPane actionScroller = new JScrollPane();
			actionScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			{
				actionListPanel = new JPanel();
				actionListPanel.setLayout(new BoxLayout(actionListPanel, BoxLayout.Y_AXIS));
				actionScroller.setViewportView(actionListPanel);
			}

			JPanel actionBtnPanel = new JPanel();
			actionBtnPanel.setLayout(new BoxLayout(actionBtnPanel, BoxLayout.X_AXIS));
			{
				JButton addPathBtn = new JButton("Add Drive Action");
				JButton addElevatorBtn = new JButton("Add Elevator Action");
				JButton addIntakeBtn = new JButton("Add Intake Action");
				JButton addOutputBtn = new JButton("Add Output Action");
				JButton addDelayBtn = new JButton("Add Delay");

				addPathBtn.addActionListener(addNewAction);
				addElevatorBtn.addActionListener(addNewAction);
				addIntakeBtn.addActionListener(addNewAction);
				addOutputBtn.addActionListener(addNewAction);
				addDelayBtn.addActionListener(addNewAction);

				actionBtnPanel.add(addPathBtn);
				actionBtnPanel.add(addElevatorBtn);
				actionBtnPanel.add(addIntakeBtn);
				actionBtnPanel.add(addOutputBtn);
				actionBtnPanel.add(addDelayBtn);

			}

			actionPanel.add(actionsLbl);
			actionPanel.add(actionScroller);
			actionPanel.add(actionBtnPanel);

		}

		actionListPanel.add(Box.createVerticalGlue());
		add(btnPanel, BorderLayout.SOUTH);
	}

	public void updateWaypointList(ObservableList<Waypoint> points) {
		// waypointListPanel.removeAll();
		//
		// for (Waypoint p : points)
		// waypointListPanel.add(new WaypointEditorPanel(p));
		//
		// waypointListPanel.revalidate();
	}

	public void updateActionList(ObservableList<Action<?>> actions) {
		actionListPanel.removeAll();
		while (actionSelectButtonGroup.getElements().hasMoreElements())
			actionSelectButtonGroup.remove(actionSelectButtonGroup.getElements().nextElement());

		for (Action<?> a : actions)
			actionListPanel.add(new ActionEditorPanel(a, actionSelectButtonGroup));
		actionListPanel.revalidate();
	}

	private void setupKeyListeners() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
			if (e.getKeyCode() == KeyEvent.VK_F1)
				fieldConfigDropdown.setSelectedIndex(0);
			else if (e.getKeyCode() == KeyEvent.VK_F2)
				fieldConfigDropdown.setSelectedIndex(1);
			else if (e.getKeyCode() == KeyEvent.VK_F3)
				fieldConfigDropdown.setSelectedIndex(2);
			else if (e.getKeyCode() == KeyEvent.VK_F4)
				fieldConfigDropdown.setSelectedIndex(3);
			return false;
		});
	}

	private void setupListeners() {

		for (Script curScript : PathPlanner.main.getScripts()) {
			curScript.getActions().addListListener(() -> updateActionList(PathPlanner.main.getScript().getActions()));
		}

		updateActionList(PathPlanner.main.getScript().getActions());

	}

	ActionListener addNewAction = e -> {
		String lbl = ((JButton) e.getSource()).getText();

		if (lbl.contains("Drive")) {
			PathPlanner.main.getScript().addAction(new DriveAction());
			PathPlanner.main.getScript().connectPaths();
		} else if (lbl.contains("Intake"))
			PathPlanner.main.getScript().addAction(new IntakeAction(IntakeAction.Options.kIntakeUntil));
		else if (lbl.contains("Output"))
			PathPlanner.main.getScript().addAction(new OutputAction());
		else if (lbl.contains("Elevator"))
			PathPlanner.main.getScript().addAction(new ElevatorAction(ElevatorAction.Options.kMoveToSwitch));
		else if (lbl.contains("Delay"))
			PathPlanner.main.getScript().addAction(new DelayAction());
	};
}
