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
package jsettlers.graphics.androidui.menu;

import java.util.HashMap;
import java.util.Map.Entry;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.graphics.action.ShowConstructionMarksAction;
import jsettlers.graphics.androidui.R;
import jsettlers.graphics.androidui.utils.OriginalImageProvider;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.map.ScreenPosition;
import jsettlers.graphics.map.controls.original.panel.content.BuildingBuildContent;
import jsettlers.graphics.map.controls.original.panel.content.BuildingBuildContent.BuildingCountState;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class BuildMenu extends AndroidMenu {

	private static final int ITEMS_PER_ROW = 5;

	private HashMap<EBuildingType, TextView> buildingCounts = new HashMap<EBuildingType, TextView>();

	public BuildMenu(AndroidMenuPutable androidMenuPutable) {
		super(androidMenuPutable);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.build, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		LinearLayout outer =
				(LinearLayout) view
						.findViewById(R.id.build_list);

		addBuildings(outer, BuildingBuildContent.normalBuildings, "");
		addBuildings(outer, BuildingBuildContent.foodBuildings, "");
		addBuildings(outer, BuildingBuildContent.militaryBuildings, "");
		addBuildings(outer, BuildingBuildContent.socialBuildings, "");
	}

	private void addBuildings(LinearLayout outer, EBuildingType[] types, String headline) {
		// TODO: add a headline text.
		TableLayout table = new TableLayout(outer.getContext());
		fillTableLayout(table, types);
		outer.addView(table);
	}

	private void updateCounts() {
		BuildingCountState state = getBuildingCounts();
		for (Entry<EBuildingType, TextView> e : buildingCounts.entrySet()) {
			int construct = state.getCount(e.getKey(), true);
			String str = state.getCount(e.getKey(), false) + "\n";
			if (construct > 0) {
				str += "+" + construct;
			}
			e.getValue().setText(str);
		}
	}

	private BuildingCountState getBuildingCounts() {
		MapDrawContext context = getPutable().getMapContext();
		ScreenPosition screen = context.getScreen();
		return new BuildingCountState(context.getPositionUnder(screen.getScreenCenterX(),
				screen.getScreenCenterY()), context.getMap());
	}

	private void fillTableLayout(TableLayout table, EBuildingType[] types) {
		TableRow currentRow = null;
		for (EBuildingType type : types) {
			if (currentRow == null
					|| currentRow.getChildCount() >= ITEMS_PER_ROW) {
				currentRow = new TableRow(table.getContext());
				table.addView(currentRow);
			}

			createButton(currentRow, type);
		}

	}

	private void createButton(TableRow currentRow, EBuildingType type) {
		LayoutInflater layoutInflater = LayoutInflater.from(currentRow.getContext());
		View buttonRoot = layoutInflater.inflate(R.layout.buildingbutton, currentRow, false);
		TextView count = (TextView) buttonRoot.findViewById(R.id.buildingbutton_count);
		buildingCounts.put(type, count);
		count.setText(" \n ");
		ImageButton button = (ImageButton) buttonRoot.findViewById(R.id.buildingbutton_button);

		button.setOnClickListener(generateActionListener(new ShowConstructionMarksAction(
				type), true));
		OriginalImageProvider.get(type).setAsImage(button);
		currentRow.addView(buttonRoot);
	}

	@Override
	public void poll() {
		updateCounts();
		super.poll();
	}
}
