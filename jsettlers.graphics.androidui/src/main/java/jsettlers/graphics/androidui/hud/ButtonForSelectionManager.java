package jsettlers.graphics.androidui.hud;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.map.partition.IPartitionData;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.androidui.MapViewChangeObserveable.IMapSelectionListener;
import jsettlers.graphics.androidui.R;
import jsettlers.graphics.androidui.menu.AndroidMenuPutable;
import jsettlers.graphics.androidui.menu.selection.BuildingMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

/**
 * Updates the given button depending on the current selection.
 * 
 * @author Michael Zangl
 */
public class ButtonForSelectionManager implements IMapSelectionListener, OnClickListener {
	private ImageButton button;
	private ISelectionSet selection;
	private AndroidMenuPutable putable;

	public ButtonForSelectionManager(AndroidMenuPutable putable, ImageButton button) {
		this.putable = putable;
		this.button = button;
		mapSelectionChanged(null);
		button.setOnClickListener(this);
	}

	@Override
	public void mapSelectionChanged(ISelectionSet newSelection) {
		if (newSelection == null || newSelection.getSize() == 0) {
			button.setVisibility(View.INVISIBLE);
		} else {
			button.setVisibility(View.VISIBLE);
			int icon;
			switch (newSelection.getSelectionType()) {
			case SOLDIERS:
				icon = R.drawable.icon_sword;
				break;
			case SPECIALISTS:
				icon = R.drawable.icon_selection;
				break;
			default:
				icon = R.drawable.icon_selection_2;
				break;
			}
			button.setImageResource(icon);
		}
		selection = newSelection;
	}

	@Override
	public void onClick(View v) {
		if (selection != null) {
			switch (selection.getSelectionType()) {
			case BUILDING:
				IBuilding building = (IBuilding) selection.get(0);
				ShortPoint2D pos = building.getPos();
				IPartitionData partitionData = putable.getMapContext().getMap().getPartitionData(pos.x, pos.y);
				putable.showMenuFragment(new BuildingMenu(putable, building, partitionData));
				break;
			}
		}
	}
}
