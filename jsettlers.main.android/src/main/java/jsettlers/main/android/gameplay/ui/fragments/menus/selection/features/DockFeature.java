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

package jsettlers.main.android.gameplay.ui.fragments.menus.selection.features;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.images.ImageLink;
import jsettlers.common.menu.action.EActionType;
import jsettlers.common.menu.action.IAction;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.R;
import jsettlers.main.android.core.controls.ActionControls;
import jsettlers.main.android.core.controls.ActionListener;
import jsettlers.main.android.core.controls.DrawControls;
import jsettlers.main.android.core.controls.DrawListener;
import jsettlers.main.android.core.controls.TaskControls;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;
import jsettlers.main.android.gameplay.ui.customviews.InGameButton;
import jsettlers.main.android.utils.OriginalImageProvider;

import static jsettlers.common.menu.action.EActionType.ABORT;

/**
 * Created by Tom Pratt on 10/01/2017.
 */
public class DockFeature extends SelectionFeature implements DrawListener, ActionListener {
	private final DrawControls drawControls;
	private final ActionControls actionControls;
	private final TaskControls taskControls;

	private final InGameButton placeDockButton;
	private final ImageView ferryImageShip;
	private final ImageView tradeShipImageView;

	private Snackbar snackbar;

	public DockFeature(Activity activity, View view, IBuilding building, MenuNavigator menuNavigator, DrawControls drawControls, ActionControls actionControls, TaskControls taskControls) {
		super(view, building, menuNavigator);
		this.drawControls = drawControls;
		this.actionControls = actionControls;
		this.taskControls = taskControls;

		placeDockButton = (InGameButton) getView().findViewById(R.id.imageView_placeDock);
		placeDockButton.setVisibility(View.VISIBLE);
		placeDockButton.setOnClickListener(v -> actionControls.fireAction(new Action(EActionType.ASK_SET_DOCK)));
		OriginalImageProvider.get(ImageLink.fromName("original_3_GUI_390", 0)).setAsImage(placeDockButton.getImageView());

		ferryImageShip = (ImageView) getView().findViewById(R.id.imageView_ferry);
		ferryImageShip.setOnClickListener(v -> actionControls.fireAction(new Action(EActionType.MAKE_FERRY)));
		OriginalImageProvider.get(ImageLink.fromName("original_14_GUI_272", 0)).setAsImage(ferryImageShip);

		tradeShipImageView = (ImageView) getView().findViewById(R.id.imageView_tradeShip);
		tradeShipImageView.setOnClickListener(v -> actionControls.fireAction(new Action(EActionType.MAKE_CARGO_BOAT)));
		OriginalImageProvider.get(ImageLink.fromName("original_14_GUI_278", 0)).setAsImage(tradeShipImageView);
	}

	@Override
	public void initialize(BuildingState buildingState) {
		super.initialize(buildingState);
		drawControls.addInfrequentDrawListener(this);
		actionControls.addActionListener(this);

		update();
	}

	@Override
	public void finish() {
		super.finish();
		drawControls.removeInfrequentDrawListener(this);
		actionControls.removeActionListener(this);
		dismissSnackbar();
	}

	@Override
	public void actionFired(IAction action) {
		switch (action.getActionType()) {
			case ASK_SET_DOCK:
				snackbar = Snackbar
						.make(getView(), "Choose dock position", Snackbar.LENGTH_INDEFINITE)
						.setAction("Cancel", view -> taskControls.endTask());
				snackbar.show();
				break;
			case SET_DOCK:
			case ABORT:
				dismissSnackbar();
		}

	}

	@Override
	public void draw() {
		if (hasNewState()) {
			getView().post(this::update);
		}
	}

	private void update() {
	}

	private void dismissSnackbar() {
		if (snackbar != null) {
			snackbar.dismiss();
			snackbar = null;
		}
	}
}
