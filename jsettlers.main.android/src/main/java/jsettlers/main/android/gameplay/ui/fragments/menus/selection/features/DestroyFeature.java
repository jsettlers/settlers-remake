package jsettlers.main.android.gameplay.ui.fragments.menus.selection.features;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.images.ImageLink;
import jsettlers.common.menu.action.EActionType;
import jsettlers.common.menu.action.IAction;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.androidui.utils.OriginalImageProvider;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.R;
import jsettlers.main.android.core.controls.ActionControls;
import jsettlers.main.android.core.controls.ActionListener;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;
import jsettlers.main.android.gameplay.ui.customviews.InGameButton;

import android.support.design.widget.Snackbar;
import android.view.View;

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

		destroyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				actionControls.fireAction(new Action(EActionType.ASK_DESTROY));
			}
		});

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
			Snackbar
					.make(getView(), "Destroy this building?", Snackbar.LENGTH_SHORT)
					.setAction("Yes", new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							actionControls.fireAction(new Action(EActionType.DESTROY));
							getMenuNavigator().dismissMenu();
							getMenuNavigator().removeSelectionMenu();
						}
					})
					.show();
		}
	}
}
