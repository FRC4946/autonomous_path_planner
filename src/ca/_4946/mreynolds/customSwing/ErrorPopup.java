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
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

/**
 * A {@link JDialog} used to display an error message.
 * 
 * @author Matthew Reynolds Reynolds
 * 
 */
public class ErrorPopup extends JDialog {
	private static final long serialVersionUID = 1L;

	// Declare a JTextArea to display the error messages
	private JTextArea m_messageTextArea;
	private JTextArea m_detailsTextArea;

	public static void createPopup(String message, String details) {

		// If there was an error, display a popup
		ErrorPopup popup = new ErrorPopup(message, details);
		popup.setVisible(true);
	}

	public static void createPopup(String desc, Exception e) {

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String stackTrace = sw.toString();

		// If there was an error, display a popup and pipe the error to
		// the console
		ErrorPopup popup = new ErrorPopup(desc, stackTrace);
		popup.setVisible(true);
		System.err.println(desc);
		e.printStackTrace();
	}

	/**
	 * Create a {@link JDialog} to display an error.
	 * 
	 * @param message
	 *            the error text to display
	 * @param details
	 *            a detailed description of the error
	 * 
	 */
	public ErrorPopup(String message, String details) {
		setModalityType(ModalityType.APPLICATION_MODAL);

		// Setup the panel
		setTitle("Error");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setupUI();

		setLocationRelativeTo(null); // Center the window on the screen

		// Set the error message
		m_messageTextArea.setText(message);
		m_detailsTextArea.setText(details);
	}

	/**
	 * Create the dialog.
	 */
	private void setupUI() {

		// Setup the window
		setSize(800, 500);
		getContentPane().setLayout(new BorderLayout());

		// Create the main panel to contain the text
		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		{
			// Create the text area with the desired text
			m_messageTextArea = new JTextArea();
			m_messageTextArea.setFocusable(false);
			m_messageTextArea.setMargin(new Insets(2, 4, 2, 4));
			m_messageTextArea.setAutoscrolls(false);
			m_messageTextArea.setAlignmentY(Component.TOP_ALIGNMENT);
			m_messageTextArea.setOpaque(false);
			m_messageTextArea.setRequestFocusEnabled(false);
			m_messageTextArea.setWrapStyleWord(true);
			m_messageTextArea.setFont(new Font("Tahoma", Font.PLAIN, 20));
			m_messageTextArea.setLineWrap(true);
			m_messageTextArea.setEditable(false);

			JLabel detailslbl = new JLabel("Details:");
			detailslbl.setAlignmentX(Component.CENTER_ALIGNMENT);
			detailslbl.setFont(new Font("Tahoma", Font.BOLD, 18));

			// Create a collapsable panel
			CollapsablePanel collapsablePanel = new CollapsablePanel();
			{
				// Create a ScrollPane for the TextArea
				JScrollPane scroller = new JScrollPane();
				{
					// Create the 'details' TextArea
					m_detailsTextArea = new JTextArea();
					m_detailsTextArea.setEditable(false);
					m_detailsTextArea.setLineWrap(true);
					m_detailsTextArea.setWrapStyleWord(true);
					m_detailsTextArea.setForeground(Color.RED);

					// Add the TextArea to the ScrollPane
					scroller.setViewportView(m_detailsTextArea);
				}

				// Add the ScrollPane to the collapsible panel
				collapsablePanel.setContents(scroller);
				collapsablePanel.expandPanel();
			}

			// Add the text areas to the main panel
			contentPanel.add(m_messageTextArea);
			contentPanel.add(Box.createVerticalGlue());
			contentPanel.add(detailslbl);
			contentPanel.add(collapsablePanel);

		}

		// Create the bottom panel to contain the buttons
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
		{

			// Create the 'Dismiss' button to close the popup
			JButton dismissButton = new JButton("Dismiss");
			dismissButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			getRootPane().setDefaultButton(dismissButton);

			// Add the button to the button panel
			buttonPane.add(Box.createHorizontalGlue());
			buttonPane.add(dismissButton);
			buttonPane.add(Box.createHorizontalGlue());
		}

		// Add the main panel and button panel to the window
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
	}

}
