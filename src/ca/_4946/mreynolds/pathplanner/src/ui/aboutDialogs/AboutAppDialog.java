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
package ca._4946.mreynolds.pathplanner.src.ui.aboutDialogs;

import ca._4946.mreynolds.customSwing.AboutDialog;
import ca._4946.mreynolds.pathplanner.src.PathPlannerSettings;

/**
 * An implementation of {@link AboutDialog} to contain information pertaining to
 * the Reynolds FMS application, such as copyright and authors. Basically, the
 * app's Notice file
 * 
 * @author Matthew Reynolds
 * 
 */
public class AboutAppDialog extends AboutDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * Create an {@link AboutDialog} with the text pertaining to
	 * the Path Planner application itself
	 */
	public AboutAppDialog() {
		super();
		setText("Autonomous Path Planner © 2018 Matthew Reynolds, The Alpha Dogs\n"
				+ "Developed by Matthew Reynolds\n"
				+ "\n"
				+ "Version " + PathPlannerSettings.APP_VERSION + "\n"
				+ "\n"
				+ "\n"
				+ "\n"
				+ "Autonomous Path Planner is licensed under the Apache License, Version 2.0 "
				+ "(the \"License\"); you may not use this application except in "
				+ "compliance with the license. You may obtain a copy of the License "
				+ "at http://www.apache.org/licenses/LICENSE-2.0\n"
				+ "\n"
				+ "Unless required by applicable law or agreed to in writing, "
				+ "software distributed under the License is distributed on an \"AS IS\" "
				+ "BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express "
				+ "or implied. See the License for the specific language governing "
				+ "permissions and limitations under the License.)\n"
				+ "\n"
				+ "\n"
				+ "\n"
				+ "This product contains Apache Commons Net, available under the Apache License, "
				+ "Version 2.0. For details, see http://www.apache.org/licenses");
	}
}
