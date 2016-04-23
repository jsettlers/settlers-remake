/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.graphics.map.controls.original;

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.original.panel.MainPanel;
import jsettlers.graphics.map.minimap.MinimapMode;
import jsettlers.graphics.ui.Button;

/**
 * A button that controls the display of the buildings on the minimap.
 *
 * @author Michael Zangl
 */
public class MinimapBuildingButton extends Button {
	/**
	 * This action switches to the next display mode.
	 * 
	 * @author Michael Zangl
	 */
	private static final class NextDisplayMode extends ExecutableAction {
		private final MinimapMode minimapSettings;

		private NextDisplayMode(MinimapMode minimapSettings) {
			this.minimapSettings = minimapSettings;
		}

		@Override
		public void execute() {
			minimapSettings.setDisplayBuildings(!minimapSettings.getDisplayBuildings());
		}
	}

	/**
	 * The inactive icon.
	 * <p>
	 * TODO this should be referencing MiniMapLayoutProperties
	 */
	private static final OriginalImageLink INACTIVE = new OriginalImageLink(EImageLinkType.GUI, MainPanel.BUTTONS_FILE, 363, 0);
	/**
	 * The active icon.
	 */
	private static final OriginalImageLink ACTIVE = new OriginalImageLink(EImageLinkType.GUI, MainPanel.BUTTONS_FILE, 360, 0);
	private MinimapMode minimapSettings;

	/**
	 * Creates a new {@link MinimapBuildingButton}.
	 * 
	 * @param minimapSettings
	 *            The settings to influence.
	 */
	public MinimapBuildingButton(final MinimapMode minimapSettings) {
		super(new NextDisplayMode(minimapSettings), null, null, Labels.getString("minimap-buildings"));
		this.minimapSettings = minimapSettings;
	}

	@Override
	protected OriginalImageLink getBackgroundImage() {
		if (minimapSettings.getDisplayBuildings()) {
			return ACTIVE;
		} else {
			return INACTIVE;
		}
	}
}
