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

import java.util.EventListener;

import javax.swing.JComponent;

/**
 * To be used in conjunction with the {@link RemovableJPanel} class. This
 * listener listens for a panel losing its parent component.
 * 
 * @author Matthew Reynolds
 * 
 */
public interface PanelRemovedListener extends EventListener {

	/**
	 * Invoked when the panel no longer has a parent component.
	 * 
	 * @see JComponent#removeNotify()
	 */
	public void panelRemovedOccurred();
}
