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
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/**
 * A {@link JPanel} implementation of the {@link Scrollable} interface
 * 
 * @author Matthew Reynolds
 * @see JPanel
 * @see Scrollable
 * 
 */
public class ScrollablePanel extends JPanel implements Scrollable {
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Scrollable#getPreferredScrollableViewportSize()
	 */
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.Scrollable#getScrollableUnitIncrement(java.awt.Rectangle,
	 * int, int)
	 */
	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 10;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.Scrollable#getScrollableBlockIncrement(java.awt.Rectangle,
	 * int, int)
	 */
	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return ((orientation == SwingConstants.VERTICAL) ? visibleRect.height
				: visibleRect.width) - 10;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
	 */
	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Scrollable#getScrollableTracksViewportHeight()
	 */
	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
}
