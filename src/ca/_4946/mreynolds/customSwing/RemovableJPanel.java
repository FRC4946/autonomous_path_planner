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

import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

/**
 * An extension of the {@link JPanel} class, adding functionality for the
 * {@link PanelRemovedListener}
 * 
 * @author Matthew Reynolds
 * @see ca._4946.mreynolds.customSwing.PanelRemovedListener
 * 
 */
public class RemovableJPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * Adds a {@link PanelRemovedListener} to the panel
	 * 
	 * @param listener
	 *            the {@link PanelRemovedListener} to be added
	 */
	public void addPanelRemovedListener(PanelRemovedListener listener) {
		listenerList.add(PanelRemovedListener.class, listener);
	}

	/**
	 * Removes a {@link PanelRemovedListener} to the panel
	 * 
	 * @param listener
	 *            the {@link PanelRemovedListener} to be removed
	 */
	public void removePanelRemovedListener(PanelRemovedListener listener) {
		listenerList.remove(PanelRemovedListener.class, listener);
	}

	/**
	 * Notifies any {@link PanelRemovedListener}s in the component's
	 * {@link #listenerList}
	 * 
	 * @see EventListenerList
	 */
	public void firePanelRemoved() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i = i + 2) {
			if (listeners[i] == PanelRemovedListener.class) {
				((PanelRemovedListener) listeners[i + 1])
						.panelRemovedOccurred();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#removeNotify()
	 */
	@Override
	public void removeNotify() {
		super.removeNotify();
		firePanelRemoved();
	}
}
