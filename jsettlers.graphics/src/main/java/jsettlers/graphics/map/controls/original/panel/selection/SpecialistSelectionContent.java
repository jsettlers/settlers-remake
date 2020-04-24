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
package jsettlers.graphics.map.controls.original.panel.selection;

import jsettlers.common.action.EActionType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.player.IInGamePlayer;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.common.action.Action;
import jsettlers.common.action.ConvertAction;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.ui.LabeledButton;
import jsettlers.graphics.ui.UIPanel;

/**
 * Displays a selection of specialists.
 * 
 * @author michael
 */
public class SpecialistSelectionContent extends AbstractSelectionContent {

	private static final EMovableType[] specialists = new EMovableType[] {
			EMovableType.PIONEER, EMovableType.THIEF, EMovableType.GEOLOGIST,
	};

	private final UIPanel panel;

	public SpecialistSelectionContent(ISelectionSet selection) {
		panel = new UIPanel();

		SoldierSelectionContent.addRowsToPanel(panel, selection, specialists);

		UIPanel stop = new LabeledButton(Labels.getString("stop"),
				new Action(EActionType.STOP_WORKING));
		UIPanel work = new LabeledButton(Labels.getString("work"), new Action(
				EActionType.START_WORKING));

		panel.addChild(stop, .1f, .1f, .5f, .2f);
		panel.addChild(work, .5f, .1f, .9f, .2f);

		if (selection.getMovableCount(EMovableType.PIONEER, null) > 0) {
			UIPanel convert = new LabeledButton(Labels.getString("convert_all_to_BEARER"),
					new ConvertAction(EMovableType.BEARER,
							Short.MAX_VALUE));
			panel.addChild(convert, .1f, .2f, .9f, .3f);
		}
	}

	@Override
	public UIPanel getPanel() {
		return panel;
	}

}
