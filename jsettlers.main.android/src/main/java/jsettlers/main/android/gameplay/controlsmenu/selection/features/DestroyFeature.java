/*
 * Copyright (c) 2017
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
 */

package jsettlers.main.android.gameplay.controlsmenu.selection.features;

import android.support.design.widget.Snackbar;
import android.view.View;

import jsettlers.common.action.Action;
import jsettlers.common.action.EActionType;
import jsettlers.common.action.IAction;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.images.ImageLink;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.R;
import jsettlers.main.android.core.controls.ActionControls;
import jsettlers.main.android.core.controls.ActionListener;
import jsettlers.main.android.core.resources.OriginalImageProvider;
import jsettlers.main.android.gameplay.customviews.InGameButton;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;

/**
 * Created by tompr on 10/01/2017.
 */
public class DestroyFeature extends SelectionFeature implements ActionListener {
	private static final String imageDestroy = "original_3_GUI_198";

	private final ActionControls actionControls;

	public DestroyFeature(View view, IBuilding building, MenuNavigator menuNavigator, ActionControls actionControls) {
		super(view, building, menuNavigator);
		this.actionControls = actionControls;
	}

	@Override
	public void initialize(BuildingState buildingState) {
		super.initialize(buildingState);
		InGameButton destroyButton = (InGameButton) getView().findViewById(R.id.image_view_destroy);
		destroyButton.setVisibility(View.VISIBLE);

		ImageLink imageLink = ImageLink.fromName(imageDestroy, 0);
		OriginalImageProvider.get(imageLink).setAsImage(destroyButton.getImageView());

		destroyButton.setOnClickListener(view -> actionControls.fireAction(new Action(EActionType.ASK_DESTROY)));
		actionControls.addActionListener(this);
	}

	@Override
	public void finish() {
		super.finish();
		actionControls.removeActionListener(this);
	}

	@Override
	public void actionFired(IAction action) {
		if (action.getActionType() == EActionType.ASK_DESTROY) {
			Snackbar.make(getView(), R.string.ask_destroy, Snackbar.LENGTH_SHORT)
					.setAction("Yes", view -> {
						actionControls.fireAction(new Action(EActionType.DESTROY));
						getMenuNavigator().dismissMenu();
						getMenuNavigator().removeSelectionMenu();
					})
					.show();
		}
	}
}
