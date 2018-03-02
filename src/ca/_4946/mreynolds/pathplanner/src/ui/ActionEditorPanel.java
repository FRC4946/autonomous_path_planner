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
import ca._4946.mreynolds.util.MathUtil;
import ca._4946.mreynolds.pathplanner.src.data.actions.DelayAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.DriveAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.ElevatorAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.IntakeAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.OutputAction;
import ca._4946.mreynolds.pathplanner.src.data.actions.TurnAction;

public class ActionEditorPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("rawtypes")
	private Action m_action;

	private JLabel m_dataLbl;
	private JComponent m_data = null;

	/**
	 * Create the panel.
	 */
	public ActionEditorPanel(Action<?> a) {
		m_action = a;
		setupUI();
		setBackground(Action.getBkgColor(m_action));
	}

	/**
	 * Create the panel
	 */
	private void setupUI() {
		setBorder(new LineBorder(Color.LIGHT_GRAY));
		setMaximumSize(new Dimension(1000, 28));

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 55, 90, 45, 50, 0, 30, 55, 30, 0, 0, 30, 45, 45, 45, 45, 0 };
		gridBagLayout.rowHeights = new int[] { 21, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0,
				0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel actionTypeLbl = new JLabel(m_action.getName());
		actionTypeLbl.setFont(new Font("Tahoma", Font.BOLD, 10));
		GridBagConstraints gbc_actionTypeLbl = new GridBagConstraints();
		gbc_actionTypeLbl.gridy = 0;
		gbc_actionTypeLbl.fill = GridBagConstraints.VERTICAL;
		gbc_actionTypeLbl.insets = new Insets(0, 0, 0, 5);
		gbc_actionTypeLbl.gridx = 0;

		JComboBox<String> actionSelector = new JComboBox<String>();
		actionSelector.setFont(new Font("Tahoma", Font.PLAIN, 11));
		for (Object o : m_action.getOption().getDeclaringClass().getEnumConstants())
			actionSelector.addItem(o.toString());
		GridBagConstraints gbc_actionSelector = new GridBagConstraints();
		gbc_actionSelector.gridy = 0;
		gbc_actionSelector.insets = new Insets(0, 0, 0, 5);
		gbc_actionSelector.fill = GridBagConstraints.BOTH;
		gbc_actionSelector.gridx = 1;
		actionSelector.setSelectedItem(m_action.getOption().toString());

		m_dataLbl = new JLabel(m_action.getDataLabel());
		m_dataLbl.setFont(new Font("Tahoma", Font.PLAIN, 10));
		GridBagConstraints gbc_detailsLbl = new GridBagConstraints();
		gbc_detailsLbl.gridy = 0;
		gbc_detailsLbl.anchor = GridBagConstraints.WEST;
		gbc_detailsLbl.fill = GridBagConstraints.VERTICAL;
		gbc_detailsLbl.insets = new Insets(0, 0, 0, 5);
		gbc_detailsLbl.gridx = 2;

		GridBagConstraints gbc_data = new GridBagConstraints();
		gbc_data.anchor = GridBagConstraints.WEST;
		gbc_data.gridx = 3;

		if (m_action instanceof DriveAction) {
			JCheckBox reverseBox = new JCheckBox("");
			reverseBox.setOpaque(false);
			reverseBox.setSelected(m_action.getData() == 1.0);
			reverseBox.addActionListener(e -> {
				m_action.setData(reverseBox.isSelected() ? 1 : 0);
				// PathPlanner.main.getScript().connectPaths();
			});
			m_data = reverseBox;
		} else {
			JSpinner heightSpinner = new JSpinner();
			m_data = heightSpinner;
			gbc_data.fill = 1;

			if (m_action instanceof IntakeAction || m_action instanceof OutputAction) {
				heightSpinner.setModel(new SpinnerNumberModel(MathUtil.limit(0, m_action.getData(), 1), 0, 1, 0.1));
				m_data.setVisible(true);
			} else if (m_action instanceof ElevatorAction && m_action.getOption() == ElevatorAction.Option.ToCustom) {
				heightSpinner
						.setModel(new SpinnerNumberModel(MathUtil.limit(6, m_action.getData(), 90), 6.0, 90.0, 6.0));
				m_data.setVisible(true);
			} else if (m_action instanceof TurnAction) {
				heightSpinner.setModel(
						new SpinnerNumberModel((int) MathUtil.limit(-180, m_action.getData(), 180), -180, 180, 5));
				m_data.setVisible(true);
			} else
				m_data.setVisible(false);

			heightSpinner
					.addChangeListener(e -> m_action.setData(Double.parseDouble(heightSpinner.getValue().toString())));
			((JSpinner.DefaultEditor) heightSpinner.getEditor()).getTextField().setColumns(2);
		}

		JLabel delayLbl = new JLabel("Delay");
		delayLbl.setFont(new Font("Tahoma", Font.PLAIN, 10));
		GridBagConstraints gbc_delayLbl = new GridBagConstraints();
		gbc_delayLbl.insets = new Insets(0, 0, 0, 5);
		gbc_delayLbl.gridx = 4;
		gbc_delayLbl.gridy = 0;

		JSpinner delaySpinner = new JSpinner();
		delaySpinner.setModel(new SpinnerNumberModel(m_action.getDelay(), 0, 15.0, 0.5));
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
		timeoutSpinner.setModel(new SpinnerNumberModel(m_action.getTimeout(), -1, 15.0, 0.5));
		GridBagConstraints gbc_timeoutSpinner = new GridBagConstraints();
		gbc_timeoutSpinner.gridy = 0;
		gbc_timeoutSpinner.fill = GridBagConstraints.VERTICAL;
		gbc_timeoutSpinner.insets = new Insets(0, 0, 0, 5);
		gbc_timeoutSpinner.anchor = GridBagConstraints.WEST;
		gbc_timeoutSpinner.gridx = 7;

		ButtonGroup behaviourBtns = new ButtonGroup();

		JRadioButton rdbtnSequential = new JRadioButton("Seq");
		rdbtnSequential.setSelected(m_action.getBehaviour() == Behaviour.kSequential);
		rdbtnSequential.setOpaque(false);
		behaviourBtns.add(rdbtnSequential);
		GridBagConstraints gbc_rdbtnSequential = new GridBagConstraints();
		gbc_rdbtnSequential.anchor = GridBagConstraints.NORTHEAST;
		gbc_rdbtnSequential.gridx = 9;

		JRadioButton rdbtnParallel = new JRadioButton("Par");
		rdbtnParallel.setSelected(m_action.getBehaviour() == Behaviour.kParallel);
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

		if (!(m_action instanceof DriveAction))
			btnClear.setEnabled(false);

		// Setup all the listeners
		actionSelector.addActionListener(actionSelectListener);
		delaySpinner.addChangeListener(e -> m_action.setDelay((double) delaySpinner.getValue()));
		timeoutSpinner.addChangeListener(e -> m_action.setTimeout((double) timeoutSpinner.getValue()));
		rdbtnSequential.addActionListener(e -> m_action.setBehaviour(Behaviour.kSequential));
		rdbtnParallel.addActionListener(e -> m_action.setBehaviour(Behaviour.kParallel));
		btnUp.addActionListener(e -> PathPlanner.main.getScript().moveActionUp(m_action));
		btnDown.addActionListener(e -> PathPlanner.main.getScript().moveActionDown(m_action));
		btnClear.addActionListener(e -> ((DriveAction) m_action).clear());

		// Add the action label
		add(actionTypeLbl, gbc_actionTypeLbl);
		add(actionSelector, gbc_actionSelector);
		add(m_dataLbl, gbc_detailsLbl);
		add(m_data, gbc_data);
		add(delayLbl, gbc_delayLbl);
		add(delaySpinner, gbc_delaySpinner);
		add(timeoutLbl, gbc_timeoutLbl);
		add(timeoutSpinner, gbc_timeoutSpinner);

		// Add the action behaviour
		if (!(m_action instanceof DelayAction)) {
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
		btnDelete.addActionListener(e -> PathPlanner.main.getScript().removeAction(m_action));
		add(btnDelete, gbc_btnDelete);
	}

	ActionListener actionSelectListener = new ActionListener() {

		@SuppressWarnings("unchecked")
		@Override
		public void actionPerformed(ActionEvent e) {
			m_action.setOptions(Enum.valueOf(m_action.getOption().getDeclaringClass(),
					((JComboBox<?>) e.getSource()).getSelectedItem().toString()));

			if (m_action instanceof ElevatorAction) {
				if (m_action.getOption() == ElevatorAction.Option.ToCustom) {
					m_dataLbl.setText(m_action.getDataLabel());
					((JSpinner) m_data).setModel(
							new SpinnerNumberModel(MathUtil.limit(6, m_action.getData(), 90), 6.0, 90.0, 6.0));
					m_data.setVisible(true);
				} else {
					m_data.setVisible(false);
				}
			}
		}
	};

}
