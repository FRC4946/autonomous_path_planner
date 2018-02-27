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
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import ca._4946.mreynolds.pathplanner.src.PathPlanner;
import ca._4946.mreynolds.pathplanner.src.data.Script;
import ca._4946.mreynolds.pathplanner.src.data.actions.Action;
import ca._4946.mreynolds.pathplanner.src.data.actions.ArmAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.DelayAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.DriveAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.ElevatorAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.IntakeAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.OutputAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.TurnAction;
import ca._4946.mreynolds.pathplanner.src.data.point.Waypoint;
import ca._4946.mreynolds.pathplanner.src.io.FileIO;
import ca._4946.mreynolds.util.ObservableList;
import javax.swing.border.EmptyBorder;

public class ControlPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JPanel m_actionListPanel;
	private JComboBox<String> m_fieldConfigDropdown;
	private JTextPane m_notesTxtPane;
	private JTextField m_scriptNameField;
	private JLabel m_statusLbl;
	private JButton m_copyLLBtn;
	private JButton m_copyLRBtn;
	private JButton m_copyRLBtn;
	private JButton m_copyRRBtn;

	private Thread pingThread = new Thread(() -> {

		InetAddress address;
		boolean isConnected = false;
		boolean running = true;

		while (running) {
			try {
				address = InetAddress.getByName(FileIO.FTP_SERVER);
				isConnected = address.isReachable(1000);
			} catch (IOException e) {
				// NOT CONNECTED!
				isConnected = false;
			}

			if (isConnected) {
				m_statusLbl.setText("Connected");
				m_statusLbl.setBackground(Color.GREEN);

				// Sleep 2sec
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				m_statusLbl.setText("Not Connected");
				m_statusLbl.setBackground(Color.RED);
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
		setLayout(new BorderLayout(0, 0));

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		{

			JPanel statusPanel = new JPanel();
			topPanel.add(statusPanel);
			statusPanel.setMaximumSize(new Dimension(32767, 30));
			GridBagLayout gbl_statusPanel = new GridBagLayout();
			gbl_statusPanel.columnWidths = new int[] { 0, 60, 150, 0, 0, 0, 0, 0, 75, 0 };
			gbl_statusPanel.rowHeights = new int[] { 30, 0 };
			gbl_statusPanel.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0,
					Double.MIN_VALUE };
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

				m_scriptNameField = new JTextField();
				GridBagConstraints gbc_scriptNameField = new GridBagConstraints();
				gbc_scriptNameField.fill = GridBagConstraints.BOTH;
				gbc_scriptNameField.insets = new Insets(0, 0, 0, 5);
				gbc_scriptNameField.gridx = 2;
				gbc_scriptNameField.gridy = 0;
				statusPanel.add(m_scriptNameField, gbc_scriptNameField);
				m_scriptNameField.setColumns(20);
				m_scriptNameField.addActionListener(e -> PathPlanner.main.setScriptName(m_scriptNameField.getText()));

				m_fieldConfigDropdown = new JComboBox<String>();
				GridBagConstraints gbc_fieldConfigDropdown = new GridBagConstraints();
				gbc_fieldConfigDropdown.insets = new Insets(0, 0, 0, 5);
				gbc_fieldConfigDropdown.gridx = 4;
				gbc_fieldConfigDropdown.gridy = 0;
				statusPanel.add(m_fieldConfigDropdown, gbc_fieldConfigDropdown);
				m_fieldConfigDropdown.setModel(
						new DefaultComboBoxModel<String>(new String[] { "LL (F1)", "LR (F2)", "RL (F3)", "RR (F4)" }));
				m_fieldConfigDropdown.setSelectedIndex(0);
				m_fieldConfigDropdown.setMaximumRowCount(4);
				m_fieldConfigDropdown
						.addActionListener(e -> setConfig((String) ((JComboBox<?>) e.getSource()).getSelectedItem()));

				JButton toggleColor = new JButton("Red");
				toggleColor.addActionListener(e -> {
					PathPlanner.main.fieldIsBlue = !PathPlanner.main.fieldIsBlue;
					if (PathPlanner.main.fieldIsBlue)
						toggleColor.setText("Red");
					else
						toggleColor.setText("Blue");
				});
				GridBagConstraints gbc_toggleColor = new GridBagConstraints();
				gbc_toggleColor.insets = new Insets(0, 0, 0, 5);
				gbc_toggleColor.gridx = 6;
				gbc_toggleColor.gridy = 0;
				statusPanel.add(toggleColor, gbc_toggleColor);

				m_statusLbl = new JLabel("Not Connected");
				m_statusLbl.setHorizontalAlignment(SwingConstants.CENTER);
				m_statusLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
				m_statusLbl.setOpaque(true);
				m_statusLbl.setBackground(Color.RED);
				GridBagConstraints gbc_statusLbl = new GridBagConstraints();
				gbc_statusLbl.fill = GridBagConstraints.BOTH;
				gbc_statusLbl.gridx = 8;
				gbc_statusLbl.gridy = 0;
				statusPanel.add(m_statusLbl, gbc_statusLbl);
			}

			JPanel notesPanel = new JPanel();
			topPanel.add(notesPanel);
			GridBagLayout gbl_notesPanel = new GridBagLayout();
			gbl_notesPanel.columnWidths = new int[] { 101, 37, 6, 100, 0 };
			gbl_notesPanel.rowHeights = new int[] { 22, 0 };
			gbl_notesPanel.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
			gbl_notesPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
			notesPanel.setLayout(gbl_notesPanel);
			{
				JLabel lblNotes = new JLabel("Notes:");
				GridBagConstraints gbc_lblNotes = new GridBagConstraints();
				gbc_lblNotes.anchor = GridBagConstraints.WEST;
				gbc_lblNotes.insets = new Insets(0, 0, 0, 5);
				gbc_lblNotes.gridx = 1;
				gbc_lblNotes.gridy = 0;
				notesPanel.add(lblNotes, gbc_lblNotes);

				m_notesTxtPane = new JTextPane();
				GridBagConstraints gbc_notesTxtPane = new GridBagConstraints();
				gbc_notesTxtPane.fill = GridBagConstraints.BOTH;
				gbc_notesTxtPane.insets = new Insets(0, 0, 0, 5);
				gbc_notesTxtPane.gridx = 2;
				gbc_notesTxtPane.gridy = 0;
				notesPanel.add(m_notesTxtPane, gbc_notesTxtPane);
			}

			JPanel configPanel = new JPanel();
			topPanel.add(configPanel);
			configPanel.setMaximumSize(new Dimension(32767, 100));
			GridBagLayout gbl_configPanel = new GridBagLayout();
			gbl_configPanel.columnWidths = new int[] { 0, 10, 0, 0, 0 };
			gbl_configPanel.rowHeights = new int[] { 0, 0 };
			gbl_configPanel.rowWeights = new double[] { 1.0, 1.0 };
			gbl_configPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 };
			configPanel.setLayout(gbl_configPanel);
			{
				JLabel copyLbl = new JLabel("Copy To:");
				GridBagConstraints gbc_copyLbl = new GridBagConstraints();
				gbc_copyLbl.insets = new Insets(0, 0, 5, 5);
				gbc_copyLbl.gridx = 0;
				gbc_copyLbl.gridy = 0;
				configPanel.add(copyLbl, gbc_copyLbl);

				m_copyLLBtn = new JButton("LL");
				GridBagConstraints gbc_copyLLBtn = new GridBagConstraints();
				gbc_copyLLBtn.insets = new Insets(0, 0, 5, 5);
				gbc_copyLLBtn.gridx = 1;
				gbc_copyLLBtn.gridy = 0;
				configPanel.add(m_copyLLBtn, gbc_copyLLBtn);

				m_copyLRBtn = new JButton("LR");
				GridBagConstraints gbc_copyLRBtn = new GridBagConstraints();
				gbc_copyLRBtn.insets = new Insets(0, 0, 5, 5);
				gbc_copyLRBtn.gridx = 2;
				gbc_copyLRBtn.gridy = 0;
				configPanel.add(m_copyLRBtn, gbc_copyLRBtn);

				m_copyRLBtn = new JButton("RL");
				GridBagConstraints gbc_copyRLBtn = new GridBagConstraints();
				gbc_copyRLBtn.insets = new Insets(0, 0, 5, 5);
				gbc_copyRLBtn.gridx = 3;
				gbc_copyRLBtn.gridy = 0;
				configPanel.add(m_copyRLBtn, gbc_copyRLBtn);

				m_copyRRBtn = new JButton("RR");
				GridBagConstraints gbc_copyRRBtn = new GridBagConstraints();
				gbc_copyRRBtn.insets = new Insets(0, 0, 5, 0);
				gbc_copyRRBtn.gridx = 4;
				gbc_copyRRBtn.gridy = 0;
				configPanel.add(m_copyRRBtn, gbc_copyRRBtn);

				m_copyLLBtn.setEnabled(false);

				m_copyLLBtn.addActionListener(copyScript);
				m_copyLRBtn.addActionListener(copyScript);
				m_copyRLBtn.addActionListener(copyScript);
				m_copyRRBtn.addActionListener(copyScript);
			}
		}

		JPanel actionPanel = new JPanel();
		actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
		{
			JLabel actionsLbl = new JLabel("Script Actions");
			actionsLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
			actionsLbl.setFont(new Font("Tahoma", Font.BOLD, 18));

			JScrollPane actionScroller = new JScrollPane();
			actionScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			{
				m_actionListPanel = new JPanel();
				m_actionListPanel.setLayout(new BoxLayout(m_actionListPanel, BoxLayout.Y_AXIS));
				m_actionListPanel.add(Box.createVerticalGlue());
				actionScroller.setViewportView(m_actionListPanel);
			}

			JPanel actionBtnPanel = new JPanel();
			actionBtnPanel.setLayout(new BoxLayout(actionBtnPanel, BoxLayout.X_AXIS));
			{
				JButton addPathBtn = new JButton("Add Drive (1)");
				JButton addElevatorBtn = new JButton("Add Elevator (2)");
				JButton addArmBtn = new JButton("Add Arm (3)");
				JButton addIntakeBtn = new JButton("Add Intake (4)");
				JButton addOutputBtn = new JButton("Add Output (5)");
				JButton addTurnBtn = new JButton("Add Turn (6)");
				JButton addDelayBtn = new JButton("Add Delay (7)");

				addPathBtn.addActionListener(addNewAction);
				addElevatorBtn.addActionListener(addNewAction);
				addArmBtn.addActionListener(addNewAction);
				addIntakeBtn.addActionListener(addNewAction);
				addOutputBtn.addActionListener(addNewAction);
				addTurnBtn.addActionListener(addNewAction);
				addDelayBtn.addActionListener(addNewAction);

				EmptyBorder border = new EmptyBorder(2, 4, 2, 4);
				addPathBtn.setBorder(border);
				addElevatorBtn.setBorder(border);
				addArmBtn.setBorder(border);
				addIntakeBtn.setBorder(border);
				addOutputBtn.setBorder(border);
				addTurnBtn.setBorder(border);
				addDelayBtn.setBorder(border);

				actionBtnPanel.add(addPathBtn);
				actionBtnPanel.add(addElevatorBtn);
				actionBtnPanel.add(addArmBtn);
				actionBtnPanel.add(addIntakeBtn);
				actionBtnPanel.add(addOutputBtn);
				actionBtnPanel.add(addTurnBtn);
				actionBtnPanel.add(addDelayBtn);

			}

			actionPanel.add(actionsLbl);
			actionPanel.add(actionScroller);
			actionPanel.add(actionBtnPanel);

		}

		JPanel btnPanel = new JPanel();
		{
			JButton loadBtn = new JButton("Open (O)");
			loadBtn.addActionListener(e -> load());

			btnPanel.add(loadBtn);

			JButton saveBtn = new JButton("Save (S)");
			saveBtn.addActionListener(e -> save());
			btnPanel.add(saveBtn);

			JButton uploadBtn = new JButton("Upload (Space)");
			uploadBtn.addActionListener(e -> upload());
			btnPanel.add(uploadBtn);

			JButton flipBtn = new JButton("Flip (F)");
			flipBtn.addActionListener(e -> flip());
			btnPanel.add(flipBtn);

			JButton clearBtn = new JButton("Clear");
			btnPanel.add(clearBtn);
			clearBtn.addActionListener(e -> PathPlanner.main.getScript().clear());
		}

		add(topPanel, BorderLayout.NORTH);
		add(actionPanel, BorderLayout.CENTER);
		add(btnPanel, BorderLayout.SOUTH);
	}

	private void setConfig(String msg) {
		PathPlanner.main.gameData = msg.toLowerCase().substring(0, 2);
		updateActionList(PathPlanner.main.getScript().getActions());

		m_copyLLBtn.setEnabled(true);
		m_copyLRBtn.setEnabled(true);
		m_copyRLBtn.setEnabled(true);
		m_copyRRBtn.setEnabled(true);

		switch (PathPlanner.main.gameData.toLowerCase()) {
		case "ll":
			m_copyLLBtn.setEnabled(false);
			break;
		case "lr":
			m_copyLRBtn.setEnabled(false);
			break;
		case "rl":
			m_copyRLBtn.setEnabled(false);
			break;
		case "rr":
			m_copyRRBtn.setEnabled(false);
			break;
		}
	}

	public void updateActionList(ObservableList<Action<?>> actions) {
		m_actionListPanel.removeAll();
		for (Action<?> a : actions)
			m_actionListPanel.add(new ActionEditorPanel(a));
		m_actionListPanel.revalidate();
	}

	private void setupKeyListeners() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {

			// If ctrl is down...
			if (e.isControlDown() && e.getID() == KeyEvent.KEY_PRESSED) {
				if (e.getKeyCode() == KeyEvent.VK_1) {
					PathPlanner.main.getScript().addAction(new DriveAction());
					// PathPlanner.main.getScript().connectPaths();
				} else if (e.getKeyCode() == KeyEvent.VK_2)
					PathPlanner.main.getScript().addAction(new ElevatorAction());
				else if (e.getKeyCode() == KeyEvent.VK_3)
					PathPlanner.main.getScript().addAction(new ArmAction());
				else if (e.getKeyCode() == KeyEvent.VK_4)
					PathPlanner.main.getScript().addAction(new IntakeAction());
				else if (e.getKeyCode() == KeyEvent.VK_5)
					PathPlanner.main.getScript().addAction(new OutputAction());
				else if (e.getKeyCode() == KeyEvent.VK_6)
					PathPlanner.main.getScript().addAction(new TurnAction());
				else if (e.getKeyCode() == KeyEvent.VK_7)
					PathPlanner.main.getScript().addAction(new DelayAction());

				else if (e.getKeyCode() == KeyEvent.VK_O)
					load();
				else if (e.getKeyCode() == KeyEvent.VK_S)
					save();
				else if (e.getKeyCode() == KeyEvent.VK_F)
					flip();

				else if (e.getKeyCode() == KeyEvent.VK_SPACE)
					upload();
			}

			else if (e.getKeyCode() == KeyEvent.VK_F1)
				m_fieldConfigDropdown.setSelectedIndex(0);
			else if (e.getKeyCode() == KeyEvent.VK_F2)
				m_fieldConfigDropdown.setSelectedIndex(1);
			else if (e.getKeyCode() == KeyEvent.VK_F3)
				m_fieldConfigDropdown.setSelectedIndex(2);
			else if (e.getKeyCode() == KeyEvent.VK_F4)
				m_fieldConfigDropdown.setSelectedIndex(3);
			else
				return false;

			e.consume();
			return true;
		});
	}

	private void setupListeners() {

		for (Script curScript : PathPlanner.main.getScripts()) {
			curScript.getActions()
					.removeListListener(() -> updateActionList(PathPlanner.main.getScript().getActions()));
			curScript.getActions().addListListener(() -> updateActionList(PathPlanner.main.getScript().getActions()));
		}

		updateActionList(PathPlanner.main.getScript().getActions());
	}

	private void load() {
		JFileChooser fc = FileIO.getFileChooser();
		if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			PathPlanner.main.load(file);
			m_scriptNameField.setText(PathPlanner.main.getScriptName());
			m_notesTxtPane.setText(PathPlanner.main.getScriptNotes());
			setupListeners();
		}
	}

	private void save() {
		PathPlanner.main.setScriptName(m_scriptNameField.getText());
		PathPlanner.main.setScriptNotes(m_notesTxtPane.getText());

		JFileChooser fc = FileIO.getFileChooser(m_scriptNameField.getText() + ".xml");
		if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			if (!file.getName().endsWith(".xml"))
				file = new File(file.getAbsolutePath() + ".xml");

			PathPlanner.main.save(file);
		}
	}

	private void upload() {
		PathPlanner.main.setScriptName(m_scriptNameField.getText());
		PathPlanner.main.setScriptNotes(m_notesTxtPane.getText());
		PathPlanner.main.upload(new File(m_scriptNameField.getText() + ".xml"));
	}

	private void flip() {
		for (DriveAction a : PathPlanner.main.getScript().getDriveActions())
			for (int i = 0; i < a.getNumPts(); i++) {
				Waypoint p = a.getPt(i);
				p.setHeading(180 - p.getHeading());
				p.setX(-p.getX());
				a.setPt(i, p);
			}
	}

	ActionListener copyScript = e -> {
		String data = ((JButton) e.getSource()).getText().toLowerCase();
		PathPlanner.main.setScript(new Script(PathPlanner.main.getScript()), data);

		switch (data) {
		case "ll":
			m_fieldConfigDropdown.setSelectedIndex(0);
			break;
		case "lr":
			m_fieldConfigDropdown.setSelectedIndex(1);
			break;
		case "rl":
			m_fieldConfigDropdown.setSelectedIndex(2);
			break;
		case "rr":
			m_fieldConfigDropdown.setSelectedIndex(3);
			break;
		}
		setupListeners();
	};

	ActionListener addNewAction = e -> {
		String lbl = ((JButton) e.getSource()).getText();

		if (lbl.contains("Drive")) {
			PathPlanner.main.getScript().addAction(new DriveAction());
			// PathPlanner.main.getScript().connectPaths();
		} else if (lbl.contains("Turn")) {
			PathPlanner.main.getScript().addAction(new TurnAction());
			// PathPlanner.main.getScript().connectPaths();
		} else if (lbl.contains("Arm"))
			PathPlanner.main.getScript().addAction(new ArmAction());
		else if (lbl.contains("Intake"))
			PathPlanner.main.getScript().addAction(new IntakeAction());
		else if (lbl.contains("Output"))
			PathPlanner.main.getScript().addAction(new OutputAction());
		else if (lbl.contains("Elevator"))
			PathPlanner.main.getScript().addAction(new ElevatorAction());
		else if (lbl.contains("Delay"))
			PathPlanner.main.getScript().addAction(new DelayAction());
	};
}
