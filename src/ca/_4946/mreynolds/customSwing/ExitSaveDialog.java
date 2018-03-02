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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

/**
 * A custom {@code JDialog} that prompts the user to save their work before
 * exiting.
 * 
 * @author Matthew Reynolds Reynolds
 * @see JDialog
 * 
 */
public class ExitSaveDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private JPanel contentPanel;
	/**
	 * The Dialog's "Save and Exit" button. The user should override this
	 * button's functionality as appropriate, and have the button call
	 * {@link ExitSaveDialog#dispose()} to close the popup
	 */
	public JButton saveAndExitButton;
	/**
	 * The Dialog's "Exit without saving" button. The user should override this
	 * button's functionality as appropriate, and have the button call
	 * {@link ExitSaveDialog#dispose()} to close the popup
	 */
	public JButton exitWithoutSaveButton;
	private JButton cancelButton;

	/**
	 * The default text for the dialog
	 */
	public static String defaultDisplayText = "Are you sure you would like to exit without saving your settings?";

	/**
	 * Create the dialog with the default text ({@link #defaultDisplayText}
	 */
	public ExitSaveDialog() {
		this(defaultDisplayText);
	}

	/**
	 * Create the dialog with the specified text
	 * 
	 * @param displayText
	 *            the text to display
	 */
	public ExitSaveDialog(String displayText) {

		// Setup the window
		setTitle("Exit Without Saving");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 375, 175);
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());

		// Create the main panel to contain the text
		contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(null);
		{
			// Create the text area with the desired text
			JTextArea lblNewLabel = new JTextArea(displayText);
			lblNewLabel.setFocusable(false);
			lblNewLabel.setMargin(new Insets(2, 4, 2, 4));
			lblNewLabel.setBounds(0, 0, 375, 129);
			lblNewLabel.setAutoscrolls(false);
			lblNewLabel.setAlignmentY(Component.TOP_ALIGNMENT);
			lblNewLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			lblNewLabel.setOpaque(false);
			lblNewLabel.setRequestFocusEnabled(false);
			lblNewLabel.setWrapStyleWord(true);
			lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
			lblNewLabel.setLineWrap(true);
			lblNewLabel.setEditable(false);

			// Add the text area to the main panel
			contentPanel.add(lblNewLabel);
		}

		// Create the bottom panel to contain the buttons
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		{
			// Create the three buttons
			saveAndExitButton = new JButton("Save and Exit");
			exitWithoutSaveButton = new JButton("Exit Without Saving");

			cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			// Add the three buttons to the button panel
			buttonPane.add(saveAndExitButton);
			buttonPane.add(exitWithoutSaveButton);
			buttonPane.add(cancelButton);
		}

		// Add the main panel and button panel to the window
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
	}

}
