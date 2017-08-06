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
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import java.util.List;

import java8.util.stream.Collectors;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.menu.action.EActionType;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.AskSetTradingWaypointAction;
import jsettlers.graphics.action.ChangeTradingRequestAction;
import jsettlers.graphics.action.SetTradingWaypointAction;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.logic.buildings.trading.TradingBuilding;
import jsettlers.main.android.R;
import jsettlers.main.android.core.controls.ActionControls;
import jsettlers.main.android.core.controls.DrawControls;
import jsettlers.main.android.core.controls.DrawListener;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;
import jsettlers.main.android.gameplay.ui.adapters.TradeMaterialsAdapter;
import jsettlers.main.android.gameplay.ui.customviews.InGameButton;
import jsettlers.main.android.gameplay.viewstates.TradeMaterialState;
import jsettlers.main.android.utils.OriginalImageProvider;

import static java8.util.J8Arrays.stream;

/**
 * Created by Rudolf Polzer
 */
public class TradingFeature extends SelectionFeature implements DrawListener {
    private static final int TRADING_MULTIPLE_STEP_INCREASE = 8;

    private static final String imageWaypointsLand = "original_3_GUI_374";
    private static final String imageWaypointsSea = "original_3_GUI_377";
    private static final String imageInfinite = "original_3_GUI_384";
    private static final String imageNone = "original_3_GUI_387";
    private static final String imageEightMore = "original_3_GUI_219";
    private static final String imageOneMore = "original_3_GUI_225";
    private static final String imageOneLess = "original_3_GUI_228";
    private static final String imageEightLess = "original_3_GUI_222";

    private final Activity activity;
    private final MenuNavigator menuNavigator;
    private final ActionControls actionControls;
    private final DrawControls drawControls;

    private RecyclerView recyclerView;
    private TradeMaterialsAdapter adapter;

    private View placeDockView;
    private View vaypointOneView;
    private View vaypointTwoView;
    private View vaypointThreeView;
    private View vaypointDestinationView;

    public TradingFeature(Activity activity, View view, IBuilding building, MenuNavigator menuNavigator, DrawControls drawControls, ActionControls actionControls) {
        super(view, building, menuNavigator);
        this.activity = activity;
        this.menuNavigator = menuNavigator;
        this.actionControls = actionControls;
        this.drawControls = drawControls;

        placeDockView = getView().findViewById(R.id.view_placeDock);
        placeDockView.setOnClickListener(this::placeDock);

        vaypointOneView = getView().findViewById(R.id.view_waypointOne);
        vaypointTwoView = getView().findViewById(R.id.view_waypointTwo);
        vaypointThreeView = getView().findViewById(R.id.view_waypointThree);
        vaypointDestinationView = getView().findViewById(R.id.view_waypointDestination);
        vaypointOneView.setOnClickListener(v -> setWaypoint(SetTradingWaypointAction.EWaypointType.WAYPOINT_1));
        vaypointTwoView.setOnClickListener(v -> setWaypoint(SetTradingWaypointAction.EWaypointType.WAYPOINT_2));
        vaypointThreeView.setOnClickListener(v -> setWaypoint(SetTradingWaypointAction.EWaypointType.WAYPOINT_3));
        vaypointDestinationView.setOnClickListener(v -> setWaypoint(SetTradingWaypointAction.EWaypointType.DESTINATION));

        adapter = new TradeMaterialsAdapter(activity);
        adapter.setItemClickListener(this::materialSelected);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void initialize(BuildingState buildingState) {
        super.initialize(buildingState);
        drawControls.addInfrequentDrawListener(this);

        ImageView waypointsButton = (ImageView) getView().findViewById(R.id.imageView_waypoints);
        if (((TradingBuilding) (this.getBuilding())).isSeaTrading()) {
            OriginalImageProvider.get(imageWaypointsSea).setAsImage(waypointsButton);
            placeDockView.setVisibility(View.VISIBLE);
        } else {
            OriginalImageProvider.get(imageWaypointsLand).setAsImage(waypointsButton);
            placeDockView.setVisibility(View.GONE);
        }


        adapter.setMaterialStates(materialStates());
        recyclerView.setAdapter(adapter);

        update();
    }

    @Override
    public void finish() {
        super.finish();
        drawControls.removeInfrequentDrawListener(this);
    }

    @Override
    public void draw() {
        if (hasNewState()) {
            getView().post(() -> update());
        }
    }

    private void placeDock(View view) {
        actionControls.fireAction(new Action(EActionType.ASK_SET_DOCK));
        menuNavigator.dismissMenu();
    }

    private void setWaypoint(SetTradingWaypointAction.EWaypointType waypointType) {
        actionControls.fireAction(new AskSetTradingWaypointAction(waypointType));
        menuNavigator.dismissMenu();
    }


    private void changeTradeMaterialAmount(EMaterialType materialType, int amount, boolean relative) {
        actionControls.fireAction(new ChangeTradingRequestAction(materialType, amount, relative));
    }

    private void update() {
        if (getBuildingState().isTrading() || getBuildingState().isSeaTrading()) {
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setMaterialStates(materialStates());
        }
    }

    private void materialSelected(View sender, TradeMaterialState materialState) {
        View popupView = activity.getLayoutInflater().inflate(R.layout.popup_trade_material, null);

        ImageView infiniteButton = (ImageView) popupView.findViewById(R.id.imageView_tradeButton_infinite);
        ImageView noneButton = (ImageView) popupView.findViewById(R.id.imageView_tradeButton_none);
        ImageView eightMoreButton = (ImageView) popupView.findViewById(R.id.imageView_tradeButton_eightMore);
        ImageView oneMoreButton = (ImageView) popupView.findViewById(R.id.imageView_tradeButton_oneMore);
        ImageView oneLessButton = (ImageView) popupView.findViewById(R.id.imageView_tradeButton_oneLess);
        ImageView eightLessButton = (ImageView) popupView.findViewById(R.id.imageView_tradeButton_eightLess);

        OriginalImageProvider.get(imageInfinite).setAsImage(infiniteButton);
        OriginalImageProvider.get(imageNone).setAsImage(noneButton);
        OriginalImageProvider.get(imageEightMore).setAsImage(eightMoreButton);
        OriginalImageProvider.get(imageOneMore).setAsImage(oneMoreButton);
        OriginalImageProvider.get(imageOneLess).setAsImage(oneLessButton);
        OriginalImageProvider.get(imageEightLess).setAsImage(eightLessButton);

        infiniteButton.setOnClickListener(v -> changeTradeMaterialAmount(materialState.getMaterialType(), Integer.MAX_VALUE, false));
        noneButton.setOnClickListener(v -> changeTradeMaterialAmount(materialState.getMaterialType(), 0, false));
        eightMoreButton.setOnClickListener(v -> changeTradeMaterialAmount(materialState.getMaterialType(), TRADING_MULTIPLE_STEP_INCREASE, true));
        oneMoreButton.setOnClickListener(v -> changeTradeMaterialAmount(materialState.getMaterialType(), 1, true));
        oneLessButton.setOnClickListener(v -> changeTradeMaterialAmount(materialState.getMaterialType(), -1, true));
        eightLessButton.setOnClickListener(v -> changeTradeMaterialAmount(materialState.getMaterialType(), -TRADING_MULTIPLE_STEP_INCREASE, true));

        popupView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int xOffset = -((popupView.getMeasuredWidth() - sender.getWidth()) / 2);
        int yOffset = -(popupView.getMeasuredHeight() + sender.getHeight());

        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(sender, xOffset, yOffset);
    }

    private List<TradeMaterialState> materialStates() {
        BuildingState buildingState = getBuildingState();

        return stream(EMaterialType.STOCK_MATERIALS)
                .map(eMaterialType -> new TradeMaterialState(eMaterialType, buildingState.getTradingCount(eMaterialType)))
                .collect(Collectors.toList());
    }
}
