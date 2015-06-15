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
package jsettlers.graphics.androidui.menu.selection;

import java.util.ArrayList;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.IBuildingMaterial;
import jsettlers.common.map.partition.IPartitionSettings;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.action.SetBuildingPriorityAction;
import jsettlers.graphics.action.SetMaterialDistributionSettingsAction;
import jsettlers.graphics.androidui.Graphics;
import jsettlers.graphics.androidui.R;
import jsettlers.graphics.androidui.actions.SelectWorkareaAction;
import jsettlers.graphics.androidui.menu.AndroidMenu;
import jsettlers.graphics.androidui.menu.AndroidMenuPutable;
import jsettlers.graphics.androidui.menu.selection.MaterialAdapter.DistributionListener;
import jsettlers.graphics.localization.Labels;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class BuildingMenu extends AndroidMenu {

	private final IBuilding building;

	private final ArrayList<MaterialTab> tabList = new ArrayList<MaterialTab>();

	private LinearLayout tabContent;

	private ImageButton workButton;

	private ImageButton pauseButton;

	private IPartitionSettings settings;

	private float lastState;

	private EPriority lastPriority;

	private class MaterialTab implements OnClickListener {
		private IBuildingMaterial mat;
		private ImageButton button;
		private TextView count;

		public MaterialTab(ImageButton button, TextView count,
				IBuildingMaterial mat) {
			this.button = button;
			this.count = count;
			this.mat = mat;
		}

		@Override
		public void onClick(View arg0) {
			showMaterialContent(mat.getMaterialType());
		}

		public void setActiveMaterial(EMaterialType active) {
			button.setEnabled(mat.getMaterialType() != active);
		}
	}

	public BuildingMenu(AndroidMenuPutable androidMenuPutable,
			IBuilding building, IPartitionSettings settings) {
		super(androidMenuPutable);
		this.building = building;
		this.settings = settings;
	}

	public void showMaterialContent(EMaterialType mat) {
		for (MaterialTab t : tabList) {
			t.setActiveMaterial(mat);
		}

		if (tabContent != null) {
			tabContent.removeAllViews();

			ListView list = new ListView(getContext());
			DistributionListener listener = new DistributionListener() {
				@Override
				public void distributionChanged(EMaterialType material,
						float[] distribution) {
					getActionFireable().fireAction(
							new SetMaterialDistributionSettingsAction(building
									.getPos(), material, distribution));
				}
			};
			MaterialAdapter matAdapter =
					new MaterialAdapter(getContext(),
							settings.getDistributionSettings(mat), listener);
			list.setAdapter(matAdapter);
			tabContent.addView(list);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.building, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		ImageView image = (ImageView) view.findViewById(R.id.building_image);
		image.setImageResource(Graphics.BUILDING_IMAGE_MAP[building
				.getBuildingType().ordinal()]);

		TextView title = (TextView) view.findViewById(R.id.building_name);
		String name = Labels.getName(building.getBuildingType());
		if (building.getStateProgress() < 1) {
			title.setText(String.format(Labels.getString("under_construction"),
					name));
		} else {
			title.setText(name);
		}

		TableLayout tabs = (TableLayout) view.findViewById(R.id.building_tabs);

		for (IBuildingMaterial mat : building.getMaterials()) {
			addMaterialTab(tabs, mat);
		}

		addSpace(tabs);

		if (building.getBuildingType().getWorkradius() > 0) {
			ImageButton button =
					generateImageButtonTab(tabs,
							R.drawable.building_set_workarea);
			button.setOnClickListener(generateActionListener(
					new ExecutableAction() {
						@Override
						public void execute() {
							setActiveAction(new SelectWorkareaAction());
						}
					}, true));
		}

		{
			workButton =
					generateImageButtonTab(tabs,
							R.drawable.building_start_working);
			workButton.setOnClickListener(generateActionListener(
					new SetBuildingPriorityAction(EPriority.LOW), false));
			pauseButton =
					generateImageButtonTab(tabs,
							R.drawable.building_stop_working);
			pauseButton.setOnClickListener(generateActionListener(
					new SetBuildingPriorityAction(EPriority.STOPPED), false));
			// highPriorityButton = generateImageButtonTab(tabs,
			// R.drawable.building_);
			reloadWorkingButton(building.getPriority());

			ImageButton destroyButton =
					generateImageButtonTab(tabs, R.drawable.building_destroy);
			destroyButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					getPutable().showMenuFragment(
							new DestroyBuildingDialog(getPutable()));
				}
			});
		}

		tabContent = (LinearLayout) view.findViewById(R.id.building_tabcontent);
	}

	private void addSpace(TableLayout tabs) {
		View space = new TableRow(getContext());
		TableLayout.LayoutParams params = new TableLayout.LayoutParams();
		params.weight = 1;
		space.setLayoutParams(params);
		tabs.addView(space);
	}

	private ImageButton generateImageButtonTab(TableLayout tabs, int resource) {
		TableRow row = new TableRow(getContext());
		ImageButton button = new ImageButton(getContext());
		button.setImageResource(resource);
		row.addView(button);
		tabs.addView(row);
		return button;
	}

	private void reloadWorkingButton(EPriority priority) {
		if (workButton != null) {
			workButton.setVisibility(priority != EPriority.LOW ? View.VISIBLE
					: View.INVISIBLE);
		}
		if (pauseButton != null) {
			pauseButton
					.setVisibility(priority != EPriority.STOPPED ? View.VISIBLE
							: View.INVISIBLE);
		}
		lastPriority = priority;
	}

	private void addMaterialTab(TableLayout tabs, IBuildingMaterial mat) {
		ImageButton button =
				generateImageButtonTab(tabs, Graphics.MATERIAL_IMAGE_MAP[mat
						.getMaterialType().ordinal()]);

		TextView count = new TextView(getContext());
		count.setText(mat.getMaterialCount() + "");
		((TableRow) button.getParent()).addView(count);

		MaterialTab materialTab = new MaterialTab(button, count, mat);
		button.setOnClickListener(materialTab);
		tabList.add(materialTab);
	}

	@Override
	public void poll() {
		super.poll();

		EPriority priority = building.getPriority();
		if (priority != lastPriority) {
			reloadWorkingButton(priority);
		}
	}
}
