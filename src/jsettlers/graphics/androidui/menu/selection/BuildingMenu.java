package jsettlers.graphics.androidui.menu.selection;

import java.util.ArrayList;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.material.EMaterialType;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.androidui.Graphics;
import jsettlers.graphics.androidui.R;
import jsettlers.graphics.androidui.menu.AndroidMenuPutable;
import jsettlers.graphics.androidui.menu.AndroidMobileMenu;
import jsettlers.graphics.localization.Labels;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class BuildingMenu extends AndroidMobileMenu {

	private final IBuilding building;

	private final ArrayList<MaterialTab> tabList = new ArrayList<MaterialTab>();

	private LinearLayout tabContent;

	private ImageButton workingButton;

	private class MaterialTab implements OnClickListener {
		private EMaterialType mat;
		private ImageButton button;

		public MaterialTab(ImageButton button, TextView count, EMaterialType mat) {
			this.button = button;
			this.mat = mat;
		}

		@Override
		public void onClick(View arg0) {
			showMaterialContent(mat);
		}

		public void setActiveMaterial(EMaterialType active) {
			button.setEnabled(mat != active);
		}
	}

	public BuildingMenu(AndroidMenuPutable androidMenuPutable,
	        IBuilding building) {
		super(androidMenuPutable, R.layout.building);
		this.building = building;
	}

	public void showMaterialContent(EMaterialType mat) {
		for (MaterialTab t : tabList) {
			t.setActiveMaterial(mat);
		}

		if (tabContent != null) {
			tabContent.removeAllViews();
			// TODO: stats
		}
	}

	@Override
	protected void fillLayout(View menu) {
		ImageView image = (ImageView) menu.findViewById(R.id.building_image);
		image.setImageResource(Graphics.BUILDING_IMAGE_MAP[building
		        .getBuildingType().ordinal()]);

		TextView title = (TextView) menu.findViewById(R.id.building_name);
		String name = Labels.getName(building.getBuildingType());
		if (building.getStateProgress() < 1) {
			title.setText(String.format(Labels.getString("under_construction"),
			        name));
		} else {
			title.setText(name);
		}

		TableLayout tabs = (TableLayout) menu.findViewById(R.id.building_tabs);

		for (EMaterialType mat : new EMaterialType[] {
		        EMaterialType.PICK, EMaterialType.AXE
		}) {
			addMaterialTab(tabs, mat);
		}

		if (building.getBuildingType().getWorkradius() > 0) {
			TableRow row = new TableRow(getContext());
			ImageButton button = new ImageButton(getContext());
			button.setImageResource(R.drawable.building_set_workarea);
			button.setOnClickListener(generateActionListener(new Action(EActionType.SET_WORK_AREA), true));
			row.addView(button);
			tabs.addView(row);
		}

		{
			TableRow row = new TableRow(getContext());
			workingButton = new ImageButton(getContext());
			reloadWorkingButton();
			row.addView(workingButton);
			tabs.addView(row);
		}

		tabContent = (LinearLayout) menu.findViewById(R.id.building_tabcontent);
	}

	private void reloadWorkingButton() {
		workingButton
		        .setImageResource(building.isWorking() ? R.drawable.building_stop_working
		                : R.drawable.building_start_working);
		// TODO: We should not hide here, but we would need to reload on action!
		workingButton.setOnClickListener(generateActionListener(new Action(
		        building.isWorking() ? EActionType.STOP_WORKING
		                : EActionType.START_WORKING), true));
	}

	private void addMaterialTab(TableLayout tabs, EMaterialType mat) {
		TableRow row = new TableRow(getContext());
		ImageButton button = new ImageButton(getContext());
		button.setImageResource(Graphics.MATERIAL_IMAGE_MAP[mat.ordinal()]);
		row.addView(button);

		TextView count = new TextView(getContext());
		count.setText("?");
		row.addView(count);

		tabs.addView(row);
		MaterialTab materialTab = new MaterialTab(button, count, mat);
		button.setOnClickListener(materialTab);
		tabList.add(materialTab);
	}
}
