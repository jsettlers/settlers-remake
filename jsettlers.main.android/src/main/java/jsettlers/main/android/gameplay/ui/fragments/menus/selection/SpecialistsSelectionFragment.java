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

import jsettlers.common.menu.action.EActionType;
import jsettlers.common.movable.EMovableType;
import jsettlers.graphics.action.ConvertAction;
import jsettlers.main.android.R;
import jsettlers.main.android.core.controls.ActionClickListener;
import jsettlers.main.android.core.controls.ActionControls;
import jsettlers.main.android.core.controls.ControlsResolver;
import jsettlers.main.android.gameplay.ImageLinkFactory;
import jsettlers.main.android.utils.OriginalImageProvider;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by tompr on 13/01/2017.
 */
public class SpecialistsSelectionFragment extends SelectionFragment {
	private static final EMovableType[] specialistTypes = new EMovableType[] {
			EMovableType.PIONEER,
			EMovableType.THIEF,
			EMovableType.GEOLOGIST,
	};

	private ActionControls actionControls;

	public static Fragment newInstance() {
		return new SpecialistsSelectionFragment();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.menu_selection_specialists, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		actionControls = new ControlsResolver(getActivity()).getActionControls();

		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
		LinearLayout specialistsLayout = (LinearLayout) getView().findViewById(R.id.layout_specialists);

		View convertCarriersButton = getView().findViewById(R.id.button_convert_carriers);
		View workHereButton = getView().findViewById(R.id.button_work_here);
		View haltButton = getView().findViewById(R.id.button_halt);

		convertCarriersButton.setOnClickListener(new ActionClickListener(actionControls, new ConvertAction(EMovableType.BEARER, Short.MAX_VALUE)));
		workHereButton.setOnClickListener(new ActionClickListener(actionControls, EActionType.START_WORKING));
		haltButton.setOnClickListener(new ActionClickListener(actionControls, EActionType.STOP_WORKING));

		for (EMovableType movableType : specialistTypes) {
			int count = getSelection().getMovableCount(movableType);

			if (count > 0) {
				View view = layoutInflater.inflate(R.layout.view_specialist, specialistsLayout, false);
				ImageView imageView = (ImageView) view.findViewById(R.id.image_view_specialist);
				TextView textView = (TextView) view.findViewById(R.id.text_view_specialist_count);

				OriginalImageProvider.get(ImageLinkFactory.get(movableType)).setAsImage(imageView);
				textView.setText(count + "");

				specialistsLayout.addView(view);
			}
		}
	}
}
