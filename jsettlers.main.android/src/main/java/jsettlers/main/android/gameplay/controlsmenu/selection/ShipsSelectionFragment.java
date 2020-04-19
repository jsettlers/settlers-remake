/*
 * Copyright (c) 2017 - 2018
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

package jsettlers.main.android.gameplay.controlsmenu.selection;

import java.util.List;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewsById;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import jsettlers.common.action.EActionType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.movable.EMovableType;
import jsettlers.main.android.R;
import jsettlers.main.android.core.controls.ActionControls;
import jsettlers.main.android.core.controls.ControlsResolver;
import jsettlers.main.android.core.resources.OriginalImageProvider;

/**
 * Created by Rudolf Polzer
 */
@EFragment(R.layout.menu_selection_ships)
public class ShipsSelectionFragment extends SelectionFragment {
	private static final EMovableType[] shipTypes = new EMovableType[] {
			EMovableType.FERRY,
			EMovableType.CARGO_SHIP,
	};

	private static final ImageLink[] shipImageLinks = new ImageLink[] {
			ImageLink.fromName("original_14_GUI_272", 0),
			ImageLink.fromName("original_14_GUI_278", 0)
	};

	@ViewsById({ R.id.layout_ferries, R.id.layout_tradeShips })
	List<LinearLayout> shipLayouts;
	@ViewsById({ R.id.textView_ferriesCount, R.id.textView_tradeShipsCount })
	List<TextView> countTextViews;
	@ViewsById({ R.id.imageView_ferry, R.id.imageView_tradeShip })
	List<ImageView> shipImageViews;

	private ActionControls actionControls;

	public static Fragment newInstance() {
		return ShipsSelectionFragment_.builder().build();
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		actionControls = new ControlsResolver(getActivity()).getActionControls();

		for (int i = 0; i < shipTypes.length; i++) {
			EMovableType shipType = shipTypes[i];
			int count = getSelection().getMovableCount(shipType);

			if (count > 0) {
				OriginalImageProvider.get(shipImageLinks[i]).setAsImage(shipImageViews.get(i));
				countTextViews.get(i).setText(count + "");
			} else {
				shipLayouts.get(i).setVisibility(View.GONE);
			}
		}
	}

	@Click(R.id.button_unload)
	void unloadClicked() {
		actionControls.fireAction(EActionType.UNLOAD_FERRIES);
	}

	@Click(R.id.button_kill)
	void killClicked() {
		Snackbar.make(getView(), R.string.confirm_destory_ships, Snackbar.LENGTH_SHORT)
				.setAction(R.string.yes, view1 -> actionControls.fireAction(EActionType.DESTROY))
				.show();
	}
}
