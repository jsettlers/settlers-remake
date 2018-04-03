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

package jsettlers.main.android.gameplay.ui.fragments.menus.selection;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import jsettlers.common.movable.EMovableType;
import jsettlers.graphics.action.ConvertAction;
import jsettlers.main.android.R;
import jsettlers.main.android.core.controls.ActionControls;
import jsettlers.main.android.core.controls.ControlsResolver;
import jsettlers.main.android.gameplay.ImageLinkFactory;
import jsettlers.main.android.utils.OriginalImageProvider;

import android.widget.ImageView;

/**
 * Created by tompr on 13/01/2017.
 */
@EFragment(R.layout.menu_selection_carriers)
public class CarriersSelectionFragment extends SelectionFragment {

	public static CarriersSelectionFragment newInstance() {
		return new CarriersSelectionFragment_();
	}

	@ViewById(R.id.image_view_pioneer)
	ImageView pioneerImageView;
	@ViewById(R.id.image_view_geologist)
	ImageView geologistImageView;
	@ViewById(R.id.image_view_thief)
	ImageView thiefImageView;

	private ActionControls actionControls;

	@AfterViews
	void setupImageProvider() {
		OriginalImageProvider.get(ImageLinkFactory.get(EMovableType.PIONEER)).setAsImage(pioneerImageView);
		OriginalImageProvider.get(ImageLinkFactory.get(EMovableType.GEOLOGIST)).setAsImage(geologistImageView);
		OriginalImageProvider.get(ImageLinkFactory.get(EMovableType.THIEF)).setAsImage(thiefImageView);

		actionControls = new ControlsResolver(getActivity()).getActionControls();
	}

	@Click(R.id.button_convert_one_pioneer)
	void convertOnePioneerClicked() {
		fireConvertAction(EMovableType.PIONEER, false);
	}

	@Click(R.id.button_convert_all_pioneer)
	void convertAllPioneerClicked() {
		fireConvertAction(EMovableType.PIONEER, true);
	}

	@Click(R.id.button_convert_one_geologist)
	void convertOneGeologistClicked() {
		fireConvertAction(EMovableType.GEOLOGIST, false);
	}

	@Click(R.id.button_convert_all_geologist)
	void convertAllGeologistClicked() {
		fireConvertAction(EMovableType.GEOLOGIST, true);
	}

	@Click(R.id.button_convert_one_thief)
	void convertOneThiefClicked() {
		fireConvertAction(EMovableType.THIEF, false);
	}

	@Click(R.id.button_convert_all_thief)
	void convertAllThiefClicked() {
		fireConvertAction(EMovableType.THIEF, true);
	}

	private void fireConvertAction(EMovableType convertToType, boolean convertAll) {
		actionControls.fireAction(new ConvertAction(convertToType, convertAll ? Short.MAX_VALUE : (short) 1));
	}
}
