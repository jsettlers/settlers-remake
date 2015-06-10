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

import jsettlers.common.buildings.EBuildingType;
import jsettlers.graphics.action.ShowConstructionMarksAction;
import jsettlers.graphics.androidui.Graphics;
import jsettlers.graphics.androidui.R;
import jsettlers.graphics.map.controls.original.panel.content.BuildingBuildContent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class BuildMenu extends AndroidMenu {

	private static final int ITEMS_PER_ROW = 5;

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

	private View createButton(TableRow currentRow, EBuildingType type) {
		int resourceId = Graphics.BUILDING_IMAGE_MAP[type.ordinal()];
		if (resourceId == -1) {
			return null;
		}
		LayoutInflater layoutInflater = LayoutInflater.from(currentRow.getContext());
		View buttonRoot = layoutInflater.inflate(R.layout.buildingbutton, currentRow, false);
		TextView count = (TextView) buttonRoot.findViewById(R.id.buildingbutton_count);
		ImageView image = (ImageView) buttonRoot.findViewById(R.id.buildingbutton_image);
		Button button = (Button) buttonRoot.findViewById(R.id.buildingbutton_button);

		image.setImageResource(resourceId);
		button.setOnClickListener(generateActionListener(new ShowConstructionMarksAction(
				type), true));
		count.setText("?\n+?");

		currentRow.addView(buttonRoot);
		return buttonRoot;
	}
}
