package ca._4946.mreynolds.pathplanner.src.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import ca._4946.mreynolds.pathplanner.src.PathPlanner;
import ca._4946.mreynolds.pathplanner.src.data.actions.Action;
import ca._4946.mreynolds.pathplanner.src.data.actions.Action.Behaviour;
import ca._4946.mreynolds.pathplanner.src.data.actions.ArmAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.DelayAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.DriveAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.ElevatorAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.IntakeAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.OutputAction;

public class ActionEditorPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("rawtypes")
	private Action action;

	private JLabel detailsLbl;
	private JComponent data = null;

	private ButtonGroup behaviourBtns = new ButtonGroup();

	/**
	 * Create the panel.
	 */
	@SuppressWarnings("unchecked")
	public ActionEditorPanel(Action<?> a) {
		setBorder(new LineBorder(Color.LIGHT_GRAY));
		// setPreferredSize(new Dimension(1000, 25));
		setMaximumSize(new Dimension(1000, 28));

		action = a;

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 55, 120, 45, 45, 0, 30, 55, 30, 0, 0, 30, 45, 45, 45, 45, 0 };
		gridBagLayout.rowHeights = new int[] { 21, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0,
				0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel actionTypeLbl = new JLabel(action.getName());
		actionTypeLbl.setFont(new Font("Tahoma", Font.BOLD, 10));
		GridBagConstraints gbc_actionTypeLbl = new GridBagConstraints();
		gbc_actionTypeLbl.gridy = 0;
		gbc_actionTypeLbl.fill = GridBagConstraints.VERTICAL;
		gbc_actionTypeLbl.insets = new Insets(0, 0, 0, 5);
		gbc_actionTypeLbl.gridx = 0;

		JComboBox<String> actionSelector = new JComboBox<String>();
		actionSelector.setFont(new Font("Tahoma", Font.PLAIN, 11));
		for (Object o : a.options.getDeclaringClass().getEnumConstants())
			actionSelector.addItem(o.toString());
		GridBagConstraints gbc_actionSelector = new GridBagConstraints();
		gbc_actionSelector.gridy = 0;
		gbc_actionSelector.insets = new Insets(0, 0, 0, 5);
		gbc_actionSelector.fill = GridBagConstraints.BOTH;
		gbc_actionSelector.gridx = 1;
		actionSelector.setSelectedItem(action.options.toString());

		detailsLbl = new JLabel(action.getDataLabel());
		detailsLbl.setFont(new Font("Tahoma", Font.PLAIN, 10));
		GridBagConstraints gbc_detailsLbl = new GridBagConstraints();
		gbc_detailsLbl.gridy = 0;
		gbc_detailsLbl.anchor = GridBagConstraints.WEST;
		gbc_detailsLbl.fill = GridBagConstraints.VERTICAL;
		gbc_detailsLbl.insets = new Insets(0, 0, 0, 5);
		gbc_detailsLbl.gridx = 2;

		GridBagConstraints gbc_data = new GridBagConstraints();
		gbc_data.anchor = GridBagConstraints.WEST;
		gbc_data.gridx = 3;

		if (action instanceof DriveAction) {
			JCheckBox reverseBox = new JCheckBox("");
			reverseBox.setOpaque(false);
			reverseBox.setSelected(action.data == 1.0);
			reverseBox.addActionListener(e -> {
				action.data = reverseBox.isSelected() ? 1 : 0;
				PathPlanner.main.getScript().connectPaths();
			});
			data = reverseBox;
		} else {
			JSpinner heightSpinner = new JSpinner();
			data = heightSpinner;
			gbc_data.fill = 1;

			if (action instanceof IntakeAction || action instanceof OutputAction) {
				heightSpinner.setModel(new SpinnerNumberModel(action.data, 0, 1, 0.1));
				data.setVisible(true);
			} else if (action instanceof ElevatorAction && action.options == ElevatorAction.Options.kMoveToCustom) {
				heightSpinner.setModel(new SpinnerNumberModel(action.data, 6.0, 90.0, 6.0));
				data.setVisible(true);
			} else
				data.setVisible(false);

			heightSpinner.addChangeListener(e -> action.data = Double.parseDouble(heightSpinner.getValue().toString()));

		}

		JLabel delayLbl = new JLabel("Delay");
		delayLbl.setFont(new Font("Tahoma", Font.PLAIN, 10));
		GridBagConstraints gbc_delayLbl = new GridBagConstraints();
		gbc_delayLbl.insets = new Insets(0, 0, 0, 5);
		gbc_delayLbl.gridx = 4;
		gbc_delayLbl.gridy = 0;

		JSpinner delaySpinner = new JSpinner();
		delaySpinner.setModel(new SpinnerNumberModel(action.delay, 0, 15.0, 0.5));
		GridBagConstraints gbc_delaySpinner = new GridBagConstraints();
		gbc_delaySpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_delaySpinner.insets = new Insets(0, 0, 0, 5);
		gbc_delaySpinner.gridx = 5;
		gbc_delaySpinner.gridy = 0;

		JLabel timeoutLbl = new JLabel("Timeout");
		timeoutLbl.setFont(new Font("Tahoma", Font.PLAIN, 10));
		GridBagConstraints gbc_timeoutLbl = new GridBagConstraints();
		gbc_timeoutLbl.gridy = 0;
		gbc_timeoutLbl.fill = GridBagConstraints.VERTICAL;
		gbc_timeoutLbl.anchor = GridBagConstraints.EAST;
		gbc_timeoutLbl.insets = new Insets(0, 0, 0, 5);
		gbc_timeoutLbl.gridx = 6;

		JSpinner timeoutSpinner = new JSpinner();
		timeoutSpinner.setModel(new SpinnerNumberModel(action.timeout, -1, 15.0, 0.5));
		GridBagConstraints gbc_timeoutSpinner = new GridBagConstraints();
		gbc_timeoutSpinner.gridy = 0;
		gbc_timeoutSpinner.fill = GridBagConstraints.VERTICAL;
		gbc_timeoutSpinner.insets = new Insets(0, 0, 0, 5);
		gbc_timeoutSpinner.anchor = GridBagConstraints.WEST;
		gbc_timeoutSpinner.gridx = 7;

		JRadioButton rdbtnSequential = new JRadioButton("Seq");
		rdbtnSequential.setSelected(action.behaviour == Behaviour.kSequential);
		rdbtnSequential.setOpaque(false);
		behaviourBtns.add(rdbtnSequential);
		GridBagConstraints gbc_rdbtnSequential = new GridBagConstraints();
		gbc_rdbtnSequential.anchor = GridBagConstraints.NORTHEAST;
		gbc_rdbtnSequential.gridx = 9;

		JRadioButton rdbtnParallel = new JRadioButton("Par");
		rdbtnParallel.setSelected(action.behaviour == Behaviour.kParallel);
		rdbtnParallel.setOpaque(false);
		behaviourBtns.add(rdbtnParallel);
		GridBagConstraints gbc_rdbtnParallel = new GridBagConstraints();
		gbc_rdbtnParallel.anchor = GridBagConstraints.NORTHWEST;
		gbc_rdbtnParallel.gridx = 10;

		JButton btnUp = new JButton("Up");
		btnUp.setBorder(new EmptyBorder(0, 0, 0, 0));
		GridBagConstraints gbc_btnUp = new GridBagConstraints();
		gbc_btnUp.gridy = 0;
		gbc_btnUp.insets = new Insets(0, 0, 0, 5);
		gbc_btnUp.fill = GridBagConstraints.BOTH;
		gbc_btnUp.gridx = 11;

		JButton btnDown = new JButton("Down");
		btnDown.setBorder(new EmptyBorder(0, 0, 0, 0));
		GridBagConstraints gbc_btnDown = new GridBagConstraints();
		gbc_btnDown.gridy = 0;
		gbc_btnDown.insets = new Insets(0, 0, 0, 5);
		gbc_btnDown.fill = GridBagConstraints.BOTH;
		gbc_btnDown.gridx = 12;

		JButton btnClear = new JButton("Clear");
		btnClear.setBorder(new EmptyBorder(0, 0, 0, 0));
		GridBagConstraints gbc_btnReset = new GridBagConstraints();
		gbc_btnReset.gridy = 0;
		gbc_btnReset.insets = new Insets(0, 0, 0, 5);
		gbc_btnReset.fill = GridBagConstraints.BOTH;
		gbc_btnReset.gridx = 13;

		if (!(action instanceof DriveAction))
			btnClear.setEnabled(false);

		// Setup all the listeners
		actionSelector.addActionListener(actionSelectListener);
		delaySpinner.addChangeListener(e -> action.delay = (double) delaySpinner.getValue());
		timeoutSpinner.addChangeListener(e -> action.timeout = (double) timeoutSpinner.getValue());
		rdbtnSequential.addActionListener(e -> action.behaviour = Behaviour.kSequential);
		rdbtnParallel.addActionListener(e -> action.behaviour = Behaviour.kParallel);
		btnUp.addActionListener(e -> PathPlanner.main.getScript().moveActionUp(action));
		btnDown.addActionListener(e -> PathPlanner.main.getScript().moveActionDown(action));
		btnClear.addActionListener(e -> ((DriveAction) action).clear());

		// Add the action label
		add(actionTypeLbl, gbc_actionTypeLbl);
		add(actionSelector, gbc_actionSelector);
		add(detailsLbl, gbc_detailsLbl);
		add(data, gbc_data);
		add(delayLbl, gbc_delayLbl);
		add(delaySpinner, gbc_delaySpinner);
		add(timeoutLbl, gbc_timeoutLbl);
		add(timeoutSpinner, gbc_timeoutSpinner);

		// Add the action behaviour
		if (!(action instanceof DelayAction)) {
			add(rdbtnSequential, gbc_rdbtnSequential);
			add(rdbtnParallel, gbc_rdbtnParallel);
		}

		// Add the script control button
		add(btnUp, gbc_btnUp);
		add(btnDown, gbc_btnDown);
		add(btnClear, gbc_btnReset);

		JButton btnDelete = new JButton("Delete");
		btnDelete.setBorder(new EmptyBorder(0, 0, 0, 0));
		GridBagConstraints gbc_btnDelete = new GridBagConstraints();
		gbc_btnDelete.gridy = 0;
		gbc_btnDelete.fill = GridBagConstraints.BOTH;
		gbc_btnDelete.gridx = 14;
		btnDelete.addActionListener(e -> PathPlanner.main.getScript().removeAction(action));
		add(btnDelete, gbc_btnDelete);

		setBkgColor();
	}

	ActionListener actionSelectListener = new ActionListener() {

		@SuppressWarnings("unchecked")
		@Override
		public void actionPerformed(ActionEvent e) {
			action.options = Enum.valueOf(action.options.getDeclaringClass(),
					((JComboBox<?>) e.getSource()).getSelectedItem().toString());

			if (action instanceof ElevatorAction) {
				detailsLbl.setText(action.getDataLabel());
				((JSpinner) data).setModel(new SpinnerNumberModel(action.data, 6.0, 90.0, 6.0));
				data.setVisible(action.options == ElevatorAction.Options.kMoveToCustom);
			}
		}
	};

	private void setBkgColor() {
		if (action instanceof DriveAction)
			this.setBackground(new Color(255, 200, 220));
		else if (action instanceof ArmAction)
			this.setBackground(new Color(204, 210, 255));
		else if (action instanceof DelayAction)
			this.setBackground(new Color(204, 239, 255));
		else if (action instanceof ElevatorAction)
			this.setBackground(new Color(204, 255, 227));
		else if (action instanceof IntakeAction)
			this.setBackground(new Color(240, 255, 204));
		else if (action instanceof OutputAction)
			this.setBackground(new Color(255, 225, 204));
	}
}
