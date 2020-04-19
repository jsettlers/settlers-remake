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

import android.view.View;

import jsettlers.common.action.EActionType;
import jsettlers.common.action.IAction;
import jsettlers.common.action.SetBuildingPriorityAction;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.images.ImageLink;
import jsettlers.common.material.EPriority;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.R;
import jsettlers.main.android.core.controls.ActionControls;
import jsettlers.main.android.core.controls.ActionListener;
import jsettlers.main.android.core.controls.DrawControls;
import jsettlers.main.android.core.controls.DrawListener;
import jsettlers.main.android.core.resources.OriginalImageProvider;
import jsettlers.main.android.gameplay.customviews.InGameButton;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;

/**
 * Created by tompr on 10/01/2017.
 */
public class PriorityFeature extends SelectionFeature implements ActionListener, DrawListener {
	private static final String stoppedImage = "original_3_GUI_192";
	private static final String lowImage = "original_3_GUI_195";
	private static final String highImage = "original_3_GUI_378";
	private final ActionControls actionControls;
	private final DrawControls drawControls;

	private InGameButton priorityButton;

	public PriorityFeature(View view, IBuilding building, MenuNavigator menuNavigator, ActionControls actionControls, DrawControls drawControls) {
		super(view, building, menuNavigator);
		this.actionControls = actionControls;
		this.drawControls = drawControls;
	}

	@Override
	public void initialize(BuildingState buildingState) {
		super.initialize(buildingState);

		EPriority[] supportedPriorities = getBuildingState().getSupportedPriorities();
		if (supportedPriorities.length > 1) {
			priorityButton = (InGameButton) getView().findViewById(R.id.image_view_priority);
			priorityButton.setVisibility(View.VISIBLE);

			setImageForPriority(getBuilding().getPriority());

			priorityButton.setOnClickListener(view -> actionControls.fireAction(new SetBuildingPriorityAction(getNextPriority())));
		}

		actionControls.addActionListener(this);
		drawControls.addInfrequentDrawListener(this);
	}

	@Override
	public void finish() {
		super.finish();
		actionControls.removeActionListener(this);
		drawControls.removeInfrequentDrawListener(this);
	}

	@Override
	public void actionFired(IAction action) {
		if (action.getActionType() == EActionType.SET_BUILDING_PRIORITY) {
			SetBuildingPriorityAction priorityAction = (SetBuildingPriorityAction) action;
			setImageForPriority(priorityAction.getNewPriority());
		}
	}

	@Override
	public void draw() {
		if (hasNewState()) {
			if (getBuildingState().getSupportedPriorities().length <= 1) {
				getView().post(() -> priorityButton.setVisibility(View.INVISIBLE));
			}
		}
	}

	private EPriority getNextPriority() {
		EPriority[] supported = getBuildingState().getSupportedPriorities();
		EPriority current = getBuilding().getPriority();

		EPriority next = supported[0];
		for (int i = 0; i < supported.length; i++) {
			if (supported[i] == current) {
				next = supported[(i + 1) % supported.length];
			}
		}
		return next;
	}

	private void setImageForPriority(EPriority priority) {
		ImageLink imageLink = getImageLink(priority);
		getView().post(() -> OriginalImageProvider.get(imageLink).setAsImage(priorityButton.getImageView()));
	}

	private ImageLink getImageLink(EPriority priority) {
		switch (priority) {
		case STOPPED:
			return ImageLink.fromName(stoppedImage, 0);
		case LOW:
			return ImageLink.fromName(lowImage, 0);
		case HIGH:
			return ImageLink.fromName(highImage, 0);
		default:
			throw new RuntimeException("Image not found for priority " + priority.name());
		}
	}
}
