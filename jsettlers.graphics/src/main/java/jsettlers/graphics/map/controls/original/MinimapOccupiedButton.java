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
import jsettlers.graphics.map.minimap.MinimapMode.OccupiedAreaMode;
import jsettlers.graphics.ui.Button;

/**
 * A button that selects the display mode of the occupied areas on the minimap.
 * 
 * @author Michael Zangl
 */
public class MinimapOccupiedButton extends Button {
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
			OccupiedAreaMode mode = minimapSettings.getDisplayOccupied();
			OccupiedAreaMode[] values = OccupiedAreaMode.values();
			OccupiedAreaMode next = values[(mode.ordinal() + 1) % values.length];
			minimapSettings.setDisplayOccupied(next);
		}
	}

	private static final OriginalImageLink NONE = new OriginalImageLink(EImageLinkType.GUI, MainPanel.BUTTONS_FILE, 342, 0);
	private static final OriginalImageLink BORDERS = new OriginalImageLink(EImageLinkType.GUI, MainPanel.BUTTONS_FILE, 348, 0);
	private static final OriginalImageLink AREA = new OriginalImageLink(EImageLinkType.GUI, MainPanel.BUTTONS_FILE, 345, 0);

	private MinimapMode minimapSettings;

	/**
	 * Creates a new {@link MinimapOccupiedButton}.
	 * 
	 * @param minimapSettings
	 *            The settings to influence.
	 */
	public MinimapOccupiedButton(final MinimapMode minimapSettings) {
		super(new NextDisplayMode(minimapSettings), null, null, Labels.getString("minimap-occupied"));
		this.minimapSettings = minimapSettings;
	}

	@Override
	protected OriginalImageLink getBackgroundImage() {
		switch (minimapSettings.getDisplayOccupied()) {
		case AREA:
			return AREA;
		case BORDERS:
			return BORDERS;
		case NONE:
		default:
			return NONE;
		}
	}
}
