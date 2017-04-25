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

import java.util.List;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.menu.action.EActionType;
import jsettlers.common.movable.ESoldierClass;
import jsettlers.common.movable.ESoldierType;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.SoldierAction;
import jsettlers.graphics.androidui.utils.OriginalImageProvider;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.R;
import jsettlers.main.android.core.controls.ActionClickListener;
import jsettlers.main.android.core.controls.ActionControls;
import jsettlers.main.android.core.controls.DrawControls;
import jsettlers.main.android.core.controls.DrawListener;
import jsettlers.main.android.gameplay.ImageLinkFactory;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;
import jsettlers.main.android.gameplay.ui.customviews.InGameButton;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;

/**
 * Created by tompr on 12/01/2017.
 */

public class OccupiedFeature extends SelectionFeature implements DrawListener {
	private static final OriginalImageLink SOILDER_MISSING = new OriginalImageLink(EImageLinkType.GUI, 3, 45, 0);
	private static final OriginalImageLink SOILDER_COMING = new OriginalImageLink(EImageLinkType.GUI, 3, 48, 0);

	private static final String imageMaximumSoldiers = "original_3_GUI_39";
	private static final String imageAddSwordsman = "original_3_GUI_27";
	private static final String imageAddBowman = "original_3_GUI_21";
	private static final String imageAddPikeman = "original_3_GUI_24";
	private static final String imageMinimumSoldiers = "original_3_GUI_42";
	private static final String imageRemoveSwordsman = "original_3_GUI_36";
	private static final String imageRemoveBowman = "original_3_GUI_30";
	private static final String imageRemovePikeman = "original_3_GUI_33";

	private final ActionControls actionControls;
	private final DrawControls drawControls;

	private TableLayout controlsLayout;
	private LinearLayout infantryLayout;
	private LinearLayout bowmenLayout;
	private LinearLayout.LayoutParams occupiedLayoutParams;
	private LinearLayout.LayoutParams waitingLayoutParams;

	public OccupiedFeature(View view, IBuilding building, MenuNavigator menuNavigator, ActionControls actionControls, DrawControls drawControls) {
		super(view, building, menuNavigator);
		this.actionControls = actionControls;
		this.drawControls = drawControls;
	}

	@Override
	public void initialize(BuildingState buildingState) {
		super.initialize(buildingState);

		InGameButton maximumSoldiersButton = (InGameButton) getView().findViewById(R.id.image_view_maximum_soldiers);
		InGameButton addSwordsmanButton = (InGameButton) getView().findViewById(R.id.image_view_add_swordsman);
		InGameButton addBowmanButton = (InGameButton) getView().findViewById(R.id.image_view_add_bowman);
		InGameButton addPikemanButton = (InGameButton) getView().findViewById(R.id.image_view_add_pikeman);
		InGameButton minimumSolidersButton = (InGameButton) getView().findViewById(R.id.image_view_minimum_soldiers);
		InGameButton removeSwordsmanButton = (InGameButton) getView().findViewById(R.id.image_view_remove_swordsman);
		InGameButton removeBowmanButton = (InGameButton) getView().findViewById(R.id.image_view_remove_bowman);
		InGameButton removePikemanButton = (InGameButton) getView().findViewById(R.id.image_view_remove_pikeman);

		controlsLayout = (TableLayout) getView().findViewById(R.id.layout_occupier_controls);
		infantryLayout = (LinearLayout) getView().findViewById(R.id.layout_infantry);
		bowmenLayout = (LinearLayout) getView().findViewById(R.id.layout_bowmen);

		OriginalImageProvider.get(imageMaximumSoldiers).setAsImage(maximumSoldiersButton.getImageView());
		OriginalImageProvider.get(imageAddSwordsman).setAsImage(addSwordsmanButton.getImageView());
		OriginalImageProvider.get(imageAddBowman).setAsImage(addBowmanButton.getImageView());
		OriginalImageProvider.get(imageAddPikeman).setAsImage(addPikemanButton.getImageView());
		OriginalImageProvider.get(imageMinimumSoldiers).setAsImage(minimumSolidersButton.getImageView());
		OriginalImageProvider.get(imageRemoveSwordsman).setAsImage(removeSwordsmanButton.getImageView());
		OriginalImageProvider.get(imageRemoveBowman).setAsImage(removeBowmanButton.getImageView());
		OriginalImageProvider.get(imageRemovePikeman).setAsImage(removePikemanButton.getImageView());

		occupiedLayoutParams = new LinearLayout.LayoutParams(0, 0);
		occupiedLayoutParams.weight = 1;
		occupiedLayoutParams.height = getContext().getResources().getDimensionPixelSize(R.dimen.menu_tile_occupied_height);

		waitingLayoutParams = new LinearLayout.LayoutParams(0, 0);
		waitingLayoutParams.weight = 1;
		waitingLayoutParams.height = getContext().getResources().getDimensionPixelSize(R.dimen.menu_tile_waiting_height);

		maximumSoldiersButton.setOnClickListener(new ActionClickListener(actionControls, EActionType.SOLDIERS_ALL));
		addSwordsmanButton.setOnClickListener(new SingleSoldierClickListener(actionControls, EActionType.SOLDIERS_MORE, ESoldierType.SWORDSMAN));
		addBowmanButton.setOnClickListener(new SingleSoldierClickListener(actionControls, EActionType.SOLDIERS_MORE, ESoldierType.BOWMAN));
		addPikemanButton.setOnClickListener(new SingleSoldierClickListener(actionControls, EActionType.SOLDIERS_MORE, ESoldierType.PIKEMAN));

		minimumSolidersButton.setOnClickListener(new ActionClickListener(actionControls, EActionType.SOLDIERS_ONE));
		removeSwordsmanButton.setOnClickListener(new SingleSoldierClickListener(actionControls, EActionType.SOLDIERS_LESS, ESoldierType.SWORDSMAN));
		removeBowmanButton.setOnClickListener(new SingleSoldierClickListener(actionControls, EActionType.SOLDIERS_LESS, ESoldierType.BOWMAN));
		removePikemanButton.setOnClickListener(new SingleSoldierClickListener(actionControls, EActionType.SOLDIERS_LESS, ESoldierType.PIKEMAN));

		update();
		drawControls.addDrawListener(this);
	}

	@Override
	public void finish() {
		super.finish();
		drawControls.removeDrawListener(this);
	}

	@Override
	public void draw() {
		if (hasNewState()) {
			getView().post(new Runnable() {
				@Override
				public void run() {
					update();
				}
			});
		}
	}

	private void update() {
		if (getBuildingState().isOccupied()) {
			controlsLayout.setVisibility(View.VISIBLE);
			infantryLayout.removeAllViews();
			bowmenLayout.removeAllViews();
			addOccupiers(infantryLayout, getBuildingState().getOccupiers(ESoldierClass.INFANTRY));
			addOccupiers(bowmenLayout, getBuildingState().getOccupiers(ESoldierClass.BOWMAN));
		}
	}

	private void addOccupiers(LinearLayout container, List<BuildingState.OccupierState> occupierStates) {
		for (BuildingState.OccupierState occupierState : occupierStates) {
			ImageView occupierImageView = new ImageView(getContext());

			if (occupierState.isMissing()) {
				OriginalImageProvider.get(SOILDER_MISSING).setAsImage(occupierImageView);
				occupierImageView.setLayoutParams(waitingLayoutParams);
			} else if (occupierState.isComming()) {
				OriginalImageProvider.get(SOILDER_COMING).setAsImage(occupierImageView);
				occupierImageView.setLayoutParams(waitingLayoutParams);
			} else {
				OriginalImageProvider.get(ImageLinkFactory.get(occupierState.getMovable().getMovableType())).setAsImage(occupierImageView);
				occupierImageView.setLayoutParams(occupiedLayoutParams);
			}

			container.addView(occupierImageView);
		}
	}

	private class SingleSoldierClickListener extends ActionClickListener {
		private SingleSoldierClickListener(ActionFireable actionFireable, EActionType actionType, ESoldierType soldierType) {
			super(actionFireable, new SoldierAction(actionType, soldierType));
		}
	}
}
