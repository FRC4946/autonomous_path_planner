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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.BevelBorder;

/**
 * A {@link JPanel} that can be folded/collapsed vertically.
 * 
 * @author Matthew Reynolds
 * 
 */
public class CollapsablePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	// Declare a series of Swing components to use throughout the panel
	private JComponent m_contentComponent;
	private JSeparator m_separator;
	private JButton m_showHideButton;

	// Declare a boolean to store the state of the collapsed panel
	private boolean m_isCollapsed = true;

	/**
	 * Default constructor. Create an empty {@link CollapsablePanel}. The panel is
	 * collapsed by default. Content can be added using
	 * {@link #setContents(JComponent)}
	 */
	public CollapsablePanel() {
		setupUI();

		// Collapse the pane by default
		collapsePanel();
	}

	/**
	 * Create the panel.
	 */
	private void setupUI() {
		setBorder(null);
		setLayout(new BorderLayout(0, 0));

		// Create the header panel
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout(0, 0));
		{

			// Create a panel to contain the separator bar
			JPanel separatorPanel = new JPanel();
			separatorPanel.setLayout(new BorderLayout(0, 0));
			{

				// Create a separator bar
				m_separator = new JSeparator();

				// Add the separator bar to its subpanel
				separatorPanel.add(m_separator, BorderLayout.CENTER);
				separatorPanel.add(Box.createVerticalStrut(9), BorderLayout.NORTH);
				separatorPanel.add(Box.createVerticalStrut(9), BorderLayout.SOUTH);
			}

			// Create a button to toggle the collapsed state of the panel
			m_showHideButton = new JButton();
			m_showHideButton.addActionListener(new ActionListener() {

				// When the button is selected...
				@Override
				public void actionPerformed(ActionEvent arg0) {

					// If the panel is collapsed, expand it
					if (m_isCollapsed)
						expandPanel();

					// Otherwise, collapse it
					else
						collapsePanel();
				}
			});

			// Add the button and the separator bar to the header panel
			topPanel.add(separatorPanel, BorderLayout.CENTER);
			topPanel.add(m_showHideButton, BorderLayout.EAST);
		}

		add(topPanel, BorderLayout.NORTH);
	}

	/**
	 * Expand the panel's contents. If the panel is already expanded, do nothing
	 */
	public void expandPanel() {
		m_isCollapsed = false;

		// Hide the bar and update the button's text
		m_separator.setVisible(false);
		m_showHideButton.setText("Collapse");

		// Make the contents visible
		m_contentComponent.setVisible(true);

		// Add a indented border to the panel
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		// Update the panel
		validate();
		repaint();
		firePanelExpandedOccurred();
	}

	/**
	 * Collapse the panel's contents. If the panel is already collapsed, do nothing
	 */
	public void collapsePanel() {
		m_isCollapsed = true;

		// Show the bar and update the button's text
		m_separator.setVisible(true);
		m_showHideButton.setText("Expand");

		// Make the contents not visible
		if (m_contentComponent != null)
			m_contentComponent.setVisible(false);

		// Remove the indented border to the panel
		setBorder(null);

		// Update the panel
		validate();
		repaint();
		firePanelCollapsedOccurred();
	}

	/**
	 * Set the contents of the {@link CollapsablePanel} (The components that will be
	 * hidden/shown)
	 * 
	 * @param newContents
	 *            the component to set
	 */
	public void setContents(JComponent newContents) {

		// If there used to be a component in the collapsible panel, remove it
		if (m_contentComponent != null)
			remove(m_contentComponent);

		// Save and add the new contents
		m_contentComponent = newContents;
		if (m_isCollapsed)
			collapsePanel();
		add(newContents, BorderLayout.CENTER);
	}

	/**
	 * Add a {@link PanelCollapsedListener} to this panel
	 * 
	 * @param listener
	 *            the {@code PanelCollapsedListener} to add
	 */
	public void addPanelCollapsedListener(PanelCollapsedListener listener) {
		listenerList.add(PanelCollapsedListener.class, listener);
	}

	/**
	 * Remove a {@link PanelCollapsedListener} from this panel
	 * 
	 * @param listener
	 *            the {@code PanelCollapsedListener} to remove
	 */
	public void removePanelCollapsedListener(PanelCollapsedListener listener) {
		listenerList.remove(PanelCollapsedListener.class, listener);
	}

	/**
	 * Notify all {@link PanelCollapsedListener} that the panel has collapsed
	 */
	void firePanelCollapsedOccurred() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i = i + 2) {
			if (listeners[i] == PanelCollapsedListener.class) {
				((PanelCollapsedListener) listeners[i + 1]).panelCollapsedOccurred();
			}
		}
	}

	/**
	 * Add a {@link PanelExpandedListener} to this panel
	 * 
	 * @param listener
	 *            the {@code PanelExpandedListener} to add
	 */
	public void addPanelExpandedListener(PanelExpandedListener listener) {
		listenerList.add(PanelExpandedListener.class, listener);
	}

	/**
	 * Remove a {@link PanelExpandedListener} from this panel
	 * 
	 * @param listener
	 *            the {@code PanelExpandedListener} to remove
	 */
	public void removePanelExpandedListener(PanelExpandedListener listener) {
		listenerList.remove(PanelExpandedListener.class, listener);
	}

	/**
	 * Notify all {@link PanelExpandedListener} that the panel has expanded
	 */
	void firePanelExpandedOccurred() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i = i + 2) {
			if (listeners[i] == PanelCollapsedListener.class) {
				((PanelExpandedListener) listeners[i + 1]).panelExpandedOccurred();
			}
		}
	}
}
