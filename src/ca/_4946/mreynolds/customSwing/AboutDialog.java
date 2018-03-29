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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import ca._4946.mreynolds.customSwing.FramelessDialog;
import ca._4946.mreynolds.customSwing.ScrollablePanel;

/**
 * The superclass for all 'About' dialogs. This implementation of
 * {@link FramelessDialog} includes a scrollable {@link JTextPane}, which all
 * information should be added to using {@link AboutDialog#setText(String)}
 * 
 * @author Matthew Reynolds
 * 
 */
public class AboutDialog extends FramelessDialog {
	private static final long serialVersionUID = 1L;

	private JTextPane lblInfo = null;

	/**
	 * Default constructor.
	 */
	public AboutDialog() {
		super();
		setupUI();
	}

	/**
	 * Create the dialog
	 */
	private void setupUI() {

		// Create a panel to contain the text pane
		JPanel containingPanel = new ScrollablePanel();
		{
			// Set the layout to a vertical BoxLayout
			containingPanel.setLayout(new BoxLayout(containingPanel,
					BoxLayout.Y_AXIS));

			// Create the textPane to contain all of the text.
			lblInfo = new JTextPane();
			{
				lblInfo.setFont(new Font("Dialog", Font.BOLD, 12));
				lblInfo.setOpaque(true);

				// Make the text pane not editable
				lblInfo.setEditable(false);
				lblInfo.setHighlighter(null);

				// Center the text pane's text
				StyledDocument doc = lblInfo.getStyledDocument();
				SimpleAttributeSet center = new SimpleAttributeSet();
				StyleConstants
						.setAlignment(center, StyleConstants.ALIGN_CENTER);
				doc.setParagraphAttributes(0, doc.getLength(), center, false);

				// Set the default size
				lblInfo.setMaximumSize(new Dimension(450, Integer.MAX_VALUE));
			}

			// Add the text pane to the dialog
			containingPanel.add(Box.createVerticalGlue());
			containingPanel.add(lblInfo);
			containingPanel.add(Box.createVerticalGlue());
		}

		// Put the text pane in a scroller and add it to the dialog
		JScrollPane scrollPane = new JScrollPane(containingPanel);
		add(scrollPane);
	}

	/**
	 * Set the {@link JTextPane}'s text, and apply all of the neccesary sizing
	 * and formatting
	 * 
	 * @param text
	 *            the text to set
	 */
	public void setText(String text) {
		lblInfo.setText(text);
		lblInfo.setCaretPosition(0);

		// Shrink the textPane to size
		lblInfo.setSize(new Dimension(450, Integer.MAX_VALUE));
		StyledDocument doc = lblInfo.getStyledDocument();
		Rectangle r = null;
		try {
			r = lblInfo.modelToView(doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		lblInfo.setSize(new Dimension(450, r.y + r.height + 5));
		lblInfo.setMaximumSize(new Dimension(450, r.y + r.height + 5));
	}

}
