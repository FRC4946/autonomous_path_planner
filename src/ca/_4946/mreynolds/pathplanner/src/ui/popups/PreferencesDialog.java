package ca._4946.mreynolds.pathplanner.src.ui.popups;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;

import ca._4946.mreynolds.pathplanner.src.PathPlannerSettings;
import ca._4946.mreynolds.pathplanner.src.data.profiles.ConstantJerkProfile;
import ca._4946.mreynolds.pathplanner.src.io.FileIO;

public class PreferencesDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private JSpinner m_maxVelSpinner;
	private JSpinner m_maxAccelSpinner;
	private JSpinner m_accelTimeSpinner;
	private JSpinner m_maxJerkSpinner;
	private JSpinner m_jerkMultiplierSpinner;
	private ConstantJerkProfile m_profile;

	private boolean quiet = false;
	private JRadioButton m_absTuningBtn;
	private JRadioButton m_relTuningBtn;
	private JSpinner m_wheelRadiusSpinner;

	/**
	 * Create the dialog.
	 */
	public PreferencesDialog() {
		m_profile = (ConstantJerkProfile) PathPlannerSettings.getMotionProfile();

		setSize(900, 500);
		setTitle("Preferences");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(JDialog.DEFAULT_MODALITY_TYPE);
		setLocationRelativeTo(null);
		setupUI();

		updateValues();

		if (m_profile.tuningIsAbs())
			m_absTuningBtn.doClick();
		else
			m_relTuningBtn.doClick();

		// Close on esc
		// TODO: Doesn't work
		getRootPane().registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	/**
	 * Create the panel
	 */
	private void setupUI() {

		JPanel centerPanel = new JPanel();

		GridBagLayout gbl_centerPanel = new GridBagLayout();
		gbl_centerPanel.columnWidths = new int[] { 0, 0, 100, 0, 0, 100, 0, 0 };
		gbl_centerPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_centerPanel.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_centerPanel.rowWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0,
				Double.MIN_VALUE };
		centerPanel.setLayout(gbl_centerPanel);

		JLabel lblMotionProfileGeneration = new JLabel("Motion Profile Generation");
		lblMotionProfileGeneration.setFont(new Font("Tahoma", Font.PLAIN, 18));
		GridBagConstraints gbc_lblMotionProfileGeneration = new GridBagConstraints();
		gbc_lblMotionProfileGeneration.gridwidth = 5;
		gbc_lblMotionProfileGeneration.insets = new Insets(0, 0, 5, 5);
		gbc_lblMotionProfileGeneration.gridx = 1;
		gbc_lblMotionProfileGeneration.gridy = 1;
		centerPanel.add(lblMotionProfileGeneration, gbc_lblMotionProfileGeneration);

		JLabel lblMotionProfile = new JLabel("Motion Profile");
		GridBagConstraints gbc_lblMotionProfile = new GridBagConstraints();
		gbc_lblMotionProfile.insets = new Insets(0, 0, 5, 5);
		gbc_lblMotionProfile.gridx = 1;
		gbc_lblMotionProfile.gridy = 2;
		centerPanel.add(lblMotionProfile, gbc_lblMotionProfile);

		// TODO: Make this work with different types of profiles
		JComboBox<String> motionProfileDropdown = new JComboBox<String>();
		motionProfileDropdown.setModel(new DefaultComboBoxModel<String>(new String[] { "Constant Jerk" }));
		GridBagConstraints gbc_motionProfileDropdown = new GridBagConstraints();
		gbc_motionProfileDropdown.gridwidth = 4;
		gbc_motionProfileDropdown.insets = new Insets(0, 0, 5, 5);
		gbc_motionProfileDropdown.fill = GridBagConstraints.HORIZONTAL;
		gbc_motionProfileDropdown.gridx = 2;
		gbc_motionProfileDropdown.gridy = 2;
		centerPanel.add(motionProfileDropdown, gbc_motionProfileDropdown);

		JLabel lblTuneBy = new JLabel("Tune by");
		GridBagConstraints gbc_lblTuneBy = new GridBagConstraints();
		gbc_lblTuneBy.insets = new Insets(0, 0, 5, 5);
		gbc_lblTuneBy.gridx = 1;
		gbc_lblTuneBy.gridy = 4;
		centerPanel.add(lblTuneBy, gbc_lblTuneBy);

		JPanel radioButtonPanel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 4;
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 2;
		gbc_panel.gridy = 4;
		centerPanel.add(radioButtonPanel, gbc_panel);
		{
			m_absTuningBtn = new JRadioButton("Absolute Values");
			m_absTuningBtn.addActionListener(e -> {
				m_maxAccelSpinner.setEnabled(true);
				m_maxJerkSpinner.setEnabled(true);
				m_accelTimeSpinner.setEnabled(false);
				m_jerkMultiplierSpinner.setEnabled(false);
			});
			m_relTuningBtn = new JRadioButton("Relative Values");
			m_relTuningBtn.addActionListener(e -> {
				m_maxAccelSpinner.setEnabled(false);
				m_maxJerkSpinner.setEnabled(false);
				m_accelTimeSpinner.setEnabled(true);
				m_jerkMultiplierSpinner.setEnabled(true);
			});

			ButtonGroup grp = new ButtonGroup();
			grp.add(m_absTuningBtn);
			grp.add(m_relTuningBtn);

			radioButtonPanel.add(m_absTuningBtn);
			radioButtonPanel.add(m_relTuningBtn);
		}

		JLabel lblMaxAcel = new JLabel("Max Velocity");
		GridBagConstraints gbc_lblMaxAcel = new GridBagConstraints();
		gbc_lblMaxAcel.insets = new Insets(0, 0, 5, 5);
		gbc_lblMaxAcel.gridx = 1;
		gbc_lblMaxAcel.gridy = 5;
		centerPanel.add(lblMaxAcel, gbc_lblMaxAcel);

		m_maxVelSpinner = new JSpinner();
		m_maxVelSpinner.setModel(new SpinnerNumberModel(1.0, 0.1, null, 5.0));
		GridBagConstraints gbc_maxVelSpinner = new GridBagConstraints();
		gbc_maxVelSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_maxVelSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_maxVelSpinner.gridx = 2;
		gbc_maxVelSpinner.gridy = 5;
		centerPanel.add(m_maxVelSpinner, gbc_maxVelSpinner);
		m_maxVelSpinner.addChangeListener(e -> {
			if (quiet)
				return;
			m_profile.setMaxVel((double) m_maxVelSpinner.getValue());
			updateValues();
		});

		JLabel lblMaxAcceleration = new JLabel("Max Acceleration");
		GridBagConstraints gbc_lblMaxAcceleration = new GridBagConstraints();
		gbc_lblMaxAcceleration.insets = new Insets(0, 0, 5, 5);
		gbc_lblMaxAcceleration.gridx = 1;
		gbc_lblMaxAcceleration.gridy = 6;
		centerPanel.add(lblMaxAcceleration, gbc_lblMaxAcceleration);

		m_maxAccelSpinner = new JSpinner();
		m_maxAccelSpinner.setModel(new SpinnerNumberModel(1.0, 0.1, null, 5.0));
		GridBagConstraints gbc_maxAccelSpinner = new GridBagConstraints();
		gbc_maxAccelSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_maxAccelSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_maxAccelSpinner.gridx = 2;
		gbc_maxAccelSpinner.gridy = 6;
		centerPanel.add(m_maxAccelSpinner, gbc_maxAccelSpinner);
		m_maxAccelSpinner.addChangeListener(e -> {
			if (quiet)
				return;
			m_profile.setMaxAccel((double) m_maxAccelSpinner.getValue());
			updateValues();
		});

		JLabel lblApproxAccelTime = new JLabel("Approx Accel Time");
		GridBagConstraints gbc_lblApproxAccelTime = new GridBagConstraints();
		gbc_lblApproxAccelTime.insets = new Insets(0, 0, 5, 5);
		gbc_lblApproxAccelTime.gridx = 4;
		gbc_lblApproxAccelTime.gridy = 6;
		centerPanel.add(lblApproxAccelTime, gbc_lblApproxAccelTime);

		m_accelTimeSpinner = new JSpinner();
		m_accelTimeSpinner.setModel(new SpinnerNumberModel(1.0, 0.1, null, 0.25));
		GridBagConstraints gbc_accelTimeSpinner = new GridBagConstraints();
		gbc_accelTimeSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_accelTimeSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_accelTimeSpinner.gridx = 5;
		gbc_accelTimeSpinner.gridy = 6;
		centerPanel.add(m_accelTimeSpinner, gbc_accelTimeSpinner);
		m_accelTimeSpinner.addChangeListener(e -> {
			if (quiet)
				return;
			m_profile.setAccelTime((double) m_accelTimeSpinner.getValue());
			updateValues();
		});

		JLabel lblMaxJerk = new JLabel("Max Jerk");
		GridBagConstraints gbc_lblMaxJerk = new GridBagConstraints();
		gbc_lblMaxJerk.insets = new Insets(0, 0, 5, 5);
		gbc_lblMaxJerk.gridx = 1;
		gbc_lblMaxJerk.gridy = 7;
		centerPanel.add(lblMaxJerk, gbc_lblMaxJerk);

		m_maxJerkSpinner = new JSpinner();
		m_maxJerkSpinner.setModel(new SpinnerNumberModel(1.0, 0.1, null, 5.0));
		GridBagConstraints gbc_maxJerkSpinner = new GridBagConstraints();
		gbc_maxJerkSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_maxJerkSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_maxJerkSpinner.gridx = 2;
		gbc_maxJerkSpinner.gridy = 7;
		centerPanel.add(m_maxJerkSpinner, gbc_maxJerkSpinner);
		m_maxJerkSpinner.addChangeListener(e -> {
			if (quiet)
				return;
			m_profile.setMaxAccel((double) m_maxJerkSpinner.getValue());
			updateValues();
		});

		JLabel lblJerkMultiplier = new JLabel("Jerk Multiplier");
		GridBagConstraints gbc_lblJerkMultiplier = new GridBagConstraints();
		gbc_lblJerkMultiplier.insets = new Insets(0, 0, 5, 5);
		gbc_lblJerkMultiplier.gridx = 4;
		gbc_lblJerkMultiplier.gridy = 7;
		centerPanel.add(lblJerkMultiplier, gbc_lblJerkMultiplier);

		m_jerkMultiplierSpinner = new JSpinner();
		m_jerkMultiplierSpinner.setModel(new SpinnerNumberModel(1.0, 1.0, null, 0.25));
		GridBagConstraints gbc_jerkMultiplierSpinner = new GridBagConstraints();
		gbc_jerkMultiplierSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_jerkMultiplierSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_jerkMultiplierSpinner.gridx = 5;
		gbc_jerkMultiplierSpinner.gridy = 7;
		centerPanel.add(m_jerkMultiplierSpinner, gbc_jerkMultiplierSpinner);
		m_jerkMultiplierSpinner.addChangeListener(e -> {
			if (quiet)
				return;
			m_profile.setJerkMultplier((double) m_jerkMultiplierSpinner.getValue());
			updateValues();
		});

		JLabel lblRobotSettings = new JLabel("Robot Settings");
		lblRobotSettings.setFont(new Font("Tahoma", Font.PLAIN, 18));
		GridBagConstraints gbc_lblRobotSettings = new GridBagConstraints();
		gbc_lblRobotSettings.gridwidth = 5;
		gbc_lblRobotSettings.insets = new Insets(0, 0, 5, 5);
		gbc_lblRobotSettings.gridx = 1;
		gbc_lblRobotSettings.gridy = 9;
		centerPanel.add(lblRobotSettings, gbc_lblRobotSettings);

		JLabel label = new JLabel("Robot Wheel Radius");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 1;
		gbc_label.gridy = 10;
		centerPanel.add(label, gbc_label);

		m_wheelRadiusSpinner = new JSpinner();
		m_wheelRadiusSpinner.setModel(new SpinnerNumberModel(1.0, 0.1, null, 0.5));
		GridBagConstraints gbc_wheelRadiusSpinner = new GridBagConstraints();
		gbc_wheelRadiusSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_wheelRadiusSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_wheelRadiusSpinner.gridx = 2;
		gbc_wheelRadiusSpinner.gridy = 10;
		centerPanel.add(m_wheelRadiusSpinner, gbc_wheelRadiusSpinner);
		m_wheelRadiusSpinner
				.addChangeListener(e -> PathPlannerSettings.WHEEL_WIDTH_IN = (double) m_wheelRadiusSpinner.getValue());

		JPanel buttonPanel = new JPanel();
		{
			JButton btnSave = new JButton("Save");
			buttonPanel.add(btnSave);
			btnSave.addActionListener(e -> PathPlannerSettings.saveSettings());

			JButton btnSaveAs = new JButton("Save As");
			buttonPanel.add(btnSaveAs);
			btnSaveAs.addActionListener(e -> {
				JFileChooser fc = FileIO.getProfileChooser();
				if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					if (!file.getName().endsWith(".ini"))
						file = new File(file.getAbsolutePath() + ".ini");
					PathPlannerSettings.saveSettings(file);
				}
			});

			JButton btnLoad = new JButton("Load");
			buttonPanel.add(btnLoad);
			btnLoad.addActionListener(e -> {
				JFileChooser fc = FileIO.getProfileChooser();
				if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					if (!file.getName().endsWith(".ini"))
						file = new File(file.getAbsolutePath() + ".ini");
					PathPlannerSettings.loadSettings(file);
				}
				updateValues();
			});
		}

		getContentPane().add(centerPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
	}

	private void updateValues() {

		quiet = true;
		m_maxVelSpinner.setValue(m_profile.getMaxVel());
		m_maxAccelSpinner.setValue(m_profile.getMaxAccel());
		m_maxJerkSpinner.setValue(m_profile.getMaxJerk());

		m_accelTimeSpinner.setValue(m_profile.getAccelTime());
		m_jerkMultiplierSpinner.setValue(m_profile.getJerkMultiplier());

		m_wheelRadiusSpinner.setValue(PathPlannerSettings.WHEEL_WIDTH_IN);

		quiet = false;
	}
}
