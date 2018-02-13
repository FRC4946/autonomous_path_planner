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
import ca._4946.mreynolds.pathplanner.src.data.actions.DelayAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.DriveAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.ElevatorAction;

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
	public ActionEditorPanel(Action<?> a, ButtonGroup grp) {
		setBorder(new LineBorder(Color.LIGHT_GRAY));
		// setPreferredSize(new Dimension(1000, 25));
		setMaximumSize(new Dimension(1000, 28));

		action = a;

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 60, 50, 150, 30, 60, 50, 30, 0, 30, 75, 60, 30, 45, 45, 45, 45, 0 };
		gridBagLayout.rowHeights = new int[] { 21, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0,
				0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel actionTypeLbl = new JLabel(action.getName());
		actionTypeLbl.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_actionTypeLbl = new GridBagConstraints();
		gbc_actionTypeLbl.fill = GridBagConstraints.VERTICAL;
		gbc_actionTypeLbl.insets = new Insets(0, 0, 0, 5);
		gbc_actionTypeLbl.gridx = 0;

		JLabel actionLbl = new JLabel("Action:");
		GridBagConstraints gbc_lblAction = new GridBagConstraints();
		gbc_lblAction.fill = GridBagConstraints.VERTICAL;
		gbc_lblAction.insets = new Insets(0, 0, 0, 5);
		gbc_lblAction.anchor = GridBagConstraints.EAST;
		gbc_lblAction.gridx = 1;

		JComboBox<String> actionSelector = new JComboBox<String>();
		for (Object o : a.options.getDeclaringClass().getEnumConstants())
			actionSelector.addItem(o.toString());
		GridBagConstraints gbc_actionSelector = new GridBagConstraints();
		gbc_actionSelector.insets = new Insets(0, 0, 0, 5);
		gbc_actionSelector.fill = GridBagConstraints.BOTH;
		gbc_actionSelector.gridx = 2;
		actionSelector.setSelectedItem(action.options.toString());

		detailsLbl = new JLabel(action.getDataLabel());
		GridBagConstraints gbc_detailsLbl = new GridBagConstraints();
		gbc_detailsLbl.fill = GridBagConstraints.VERTICAL;
		gbc_detailsLbl.anchor = GridBagConstraints.EAST;
		gbc_detailsLbl.insets = new Insets(0, 0, 0, 5);
		gbc_detailsLbl.gridx = 4;

		GridBagConstraints gbc_data = new GridBagConstraints();
		gbc_data.anchor = GridBagConstraints.WEST;
		gbc_data.gridx = 6;

		if (action instanceof DriveAction) {
			JCheckBox reverseBox = new JCheckBox("");
			reverseBox.setSelected(action.data == 1.0);
			reverseBox.addActionListener(e -> {
				action.data = reverseBox.isSelected() ? 1 : 0;
				PathPlanner.main.getScript().connectPaths();
			});
			data = reverseBox;
		} else {
			JSpinner heightSpinner = new JSpinner();
			heightSpinner.setModel(new SpinnerNumberModel(12, 12.0, 60.0, 1));
			heightSpinner.addChangeListener(e -> action.data = (double) heightSpinner.getValue());
			data = heightSpinner;
			data.setVisible(false);
			if (action instanceof ElevatorAction && action.options == ElevatorAction.Options.kMoveToCustom)
				data.setVisible(true);
		}

		JLabel timeoutLbl = new JLabel("Timeout (s):");
		GridBagConstraints gbc_timeoutLbl = new GridBagConstraints();
		gbc_timeoutLbl.fill = GridBagConstraints.VERTICAL;
		gbc_timeoutLbl.anchor = GridBagConstraints.EAST;
		gbc_timeoutLbl.insets = new Insets(0, 0, 0, 5);
		gbc_timeoutLbl.gridx = 7;

		JSpinner timeoutSpinner = new JSpinner();
		timeoutSpinner.setModel(new SpinnerNumberModel(action.timeout, 0.0, 15.0, 0.5));
		GridBagConstraints gbc_timeoutSpinner = new GridBagConstraints();
		gbc_timeoutSpinner.fill = GridBagConstraints.VERTICAL;
		gbc_timeoutSpinner.insets = new Insets(0, 0, 0, 5);
		gbc_timeoutSpinner.anchor = GridBagConstraints.WEST;
		gbc_timeoutSpinner.gridx = 8;

		JRadioButton rdbtnSequential = new JRadioButton("Sequential");
		rdbtnSequential.setSelected(action.behaviour == Behaviour.kSequential);
		behaviourBtns.add(rdbtnSequential);
		GridBagConstraints gbc_rdbtnSequential = new GridBagConstraints();
		gbc_rdbtnSequential.anchor = GridBagConstraints.NORTHEAST;
		gbc_rdbtnSequential.gridx = 10;

		JRadioButton rdbtnParallel = new JRadioButton("Parallel");
		rdbtnParallel.setSelected(action.behaviour == Behaviour.kParallel);
		behaviourBtns.add(rdbtnParallel);
		GridBagConstraints gbc_rdbtnParallel = new GridBagConstraints();
		gbc_rdbtnParallel.anchor = GridBagConstraints.NORTHWEST;
		gbc_rdbtnParallel.gridx = 11;

		JButton btnUp = new JButton("Up");
		btnUp.setBorder(new EmptyBorder(0, 0, 0, 0));
		GridBagConstraints gbc_btnUp = new GridBagConstraints();
		gbc_btnUp.insets = new Insets(0, 0, 0, 5);
		gbc_btnUp.fill = GridBagConstraints.BOTH;
		gbc_btnUp.gridx = 12;

		JButton btnDown = new JButton("Down");
		btnDown.setBorder(new EmptyBorder(0, 0, 0, 0));
		GridBagConstraints gbc_btnDown = new GridBagConstraints();
		gbc_btnDown.insets = new Insets(0, 0, 0, 5);
		gbc_btnDown.fill = GridBagConstraints.BOTH;
		gbc_btnDown.gridx = 13;

		JButton btnClear = new JButton("Clear");
		btnClear.setBorder(new EmptyBorder(0, 0, 0, 0));
		GridBagConstraints gbc_btnReset = new GridBagConstraints();
		gbc_btnReset.insets = new Insets(0, 0, 0, 5);
		gbc_btnReset.fill = GridBagConstraints.BOTH;
		gbc_btnReset.gridx = 14;

		JButton btnDelete = new JButton("Delete");
		btnDelete.setBorder(new EmptyBorder(0, 0, 0, 0));
		GridBagConstraints gbc_btnDelete = new GridBagConstraints();
		gbc_btnDelete.fill = GridBagConstraints.BOTH;
		gbc_btnDelete.gridx = 15;

		if (!(action instanceof DriveAction))
			btnClear.setEnabled(false);

		// Setup all the listeners
		actionSelector.addActionListener(actionSelectListener);
		timeoutSpinner.addChangeListener(e -> action.timeout = (double) timeoutSpinner.getValue());
		rdbtnSequential.addActionListener(e -> action.behaviour = Behaviour.kSequential);
		rdbtnParallel.addActionListener(e -> action.behaviour = Behaviour.kParallel);
		btnUp.addActionListener(e -> PathPlanner.main.getScript().moveActionUp(action));
		btnDown.addActionListener(e -> PathPlanner.main.getScript().moveActionDown(action));
		btnClear.addActionListener(e -> ((DriveAction) action).clear());
		btnDelete.addActionListener(e -> PathPlanner.main.getScript().removeAction(action));

		// Add the action label
		add(actionTypeLbl, gbc_actionTypeLbl);

		// Add the action options
		add(actionLbl, gbc_lblAction);
		add(actionSelector, gbc_actionSelector);
		add(detailsLbl, gbc_detailsLbl);
		add(data, gbc_data);
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
		add(btnDelete, gbc_btnDelete);
	}

	ActionListener actionSelectListener = new ActionListener() {

		@SuppressWarnings("unchecked")
		@Override
		public void actionPerformed(ActionEvent e) {
			action.options = Enum.valueOf(action.options.getDeclaringClass(),
					((JComboBox<?>) e.getSource()).getSelectedItem().toString());

			detailsLbl.setText(action.getDataLabel());
			data.setVisible(action.options == ElevatorAction.Options.kMoveToCustom);
		}
	};
}
