/*******************************************************************************
 * Reynolds FMS
 * Copyright (c) 2015 Matthew Reynolds
 * 
 * This product was developed at The Alpha Dogs (www.4946.ca).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package ca._4946.mreynolds.customSwing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

/**
 * A frameless, draggable dialog. This implementation of {@link JDialog} removes
 * the window's frame and adds a mandatory 'Dismiss' button to the south of the
 * frame
 * 
 * @author Matthew Reynolds Reynolds
 * 
 */
public class FramelessDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	// Create JPanels for use throughout the class and subclasses
	private JPanel m_mainPanel = null;
	private JPanel m_editableContentPanel = null;

	/**
	 * The default size of the FramelessDialog
	 */
	public static final Dimension defaultDimensions = new Dimension(450, 300);

	// Create a Point to use when keeping track of the movement of the window
	private Point initialClick;

	/**
	 * Default constructor
	 */
	public FramelessDialog() {
		setupUI();
		setupWindowDragListeners(this);
		setupWindowDragListeners(m_mainPanel);
		setupWindowDragListeners(m_editableContentPanel);
	}

	/**
	 * Create the dialog.
	 */
	private void setupUI() {
		setSize(defaultDimensions);
		setUndecorated(true); // Remove the frame's border
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);

		m_mainPanel = new JPanel();
		setContentPane(m_mainPanel);

		if (m_editableContentPanel == null)
			m_editableContentPanel = new JPanel();
		m_editableContentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		m_editableContentPanel.setLayout(new BorderLayout());

		m_mainPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null,
				null, null));
		m_mainPanel.setLayout(new BorderLayout());

		JButton btnDismiss = new JButton("Dismiss");
		btnDismiss.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		m_mainPanel.add(m_editableContentPanel, BorderLayout.CENTER);
		m_mainPanel.add(btnDismiss, BorderLayout.SOUTH);
	}

	/**
	 * Setup the listeners required to allow the dialog to be dragged. Every
	 * component in the dialog must have these listeners
	 * 
	 * @param comp
	 *            the {@link Component} to apply the listeners to
	 */
	private void setupWindowDragListeners(Component comp) {

		// Get point of initial mouse click
		comp.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				initialClick = e.getPoint();
			}
		});

		// Move window when mouse is dragged
		comp.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {

				// Get location of Window
				int thisX = getLocation().x;
				int thisY = getLocation().y;

				// Determine how much the mouse moved since the initial click
				int xMoved = (thisX + e.getX()) - (thisX + initialClick.x);
				int yMoved = (thisY + e.getY()) - (thisY + initialClick.y);

				// Move window to this position
				int X = thisX + xMoved;
				int Y = thisY + yMoved;
				setLocation(X, Y);
			}
		});
	}

	@Override
	public Container getContentPane() {
		return m_editableContentPanel;
	}

	@Override
	public Component add(Component comp) {
		setupWindowDragListeners(comp);

		List<Component> compList = new ArrayList<Component>();
		compList.addAll(getChildren(comp));

		for (Component curComp : compList) {
			setupWindowDragListeners(curComp);
		}

		return m_editableContentPanel.add(comp);
	}

	/**
	 * Get all of the passed {@link Component}'s children, and children's
	 * children, etc.
	 * 
	 * @param comp
	 *            the {@code Component} to analyze
	 * @return a {@link List} of all of the component's children
	 */
	private List<Component> getChildren(Component comp) {
		List<Component> compList = new ArrayList<Component>();

		if (comp instanceof Container) {
			for (Component curComp : ((Container) comp).getComponents()) {
				if (!(curComp instanceof JScrollBar))
					compList.add(curComp);
				if (curComp instanceof Container)
					compList.addAll(getChildren(curComp));
			}
		}

		return compList;
	}

	@Override
	public void setLayout(LayoutManager manager) {
		if (m_editableContentPanel == null)
			m_editableContentPanel = new JPanel();

		m_editableContentPanel.setLayout(manager);

		if (!(getLayout() instanceof BorderLayout)) {
			super.setRootPaneCheckingEnabled(false);
			super.setLayout(new BorderLayout());
			super.setRootPane(super.getRootPane());
			super.setRootPaneCheckingEnabled(true);
		}
	}
}
