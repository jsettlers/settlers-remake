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
import jsettlers.graphics.action.BuildAction;
import jsettlers.graphics.androidui.Graphics;
import jsettlers.graphics.androidui.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

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
		TableLayout list =
				(TableLayout) ((LinearLayout) view)
						.findViewById(R.id.build_list);

		TableRow currentRow = null;
		for (EBuildingType type : EBuildingType.values) {
			if (currentRow == null
					|| currentRow.getChildCount() >= ITEMS_PER_ROW) {
				currentRow = new TableRow(view.getContext());
				list.addView(currentRow);
			}

			int resourceId = Graphics.BUILDING_IMAGE_MAP[type.ordinal()];
			if (resourceId != -1) {
				ImageButton b = new ImageButton(view.getContext());
				b.setImageResource(resourceId);
				b.setOnClickListener(generateActionListener(new BuildAction(
						type), true));
				currentRow.addView(b);
			}
		}

	}
}
