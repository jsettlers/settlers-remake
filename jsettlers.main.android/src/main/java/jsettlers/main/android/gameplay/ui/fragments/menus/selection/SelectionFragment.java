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

import jsettlers.common.selectable.ISelectionSet;
import jsettlers.input.SelectionSet;
import jsettlers.main.android.core.controls.ControlsResolver;
import jsettlers.main.android.core.controls.SelectionControls;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by tompr on 10/01/2017.
 */

public abstract class SelectionFragment extends Fragment {
	private SelectionControls selectionControls;

	private ISelectionSet selection;

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		selectionControls = new ControlsResolver(getActivity()).getSelectionControls();

		selection = selectionControls.getCurrentSelection();

		// It shouldnt be possible for the selection to be null because the selection menu is only launched due to selection. Noticed it happen when doing loads of minute skips though.
		if (selection == null) {
			MenuNavigator menuNavigator = (MenuNavigator) getParentFragment();
			menuNavigator.removeSelectionMenu();
			menuNavigator.dismissMenu();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// If the selection hasn't changed when the selection menu is dismissed by the user then the user has started using some other menu and we should deselect by calling endTask(). Check
		// isRemoving to confirm its not just a rotation.
		// If the selection has changed then we don't want to overwrite it.
		if (selection == selectionControls.getCurrentSelection() && isRemoving()) {
			selectionControls.deselect();
		}
	}

	public ISelectionSet getSelection() {
		if (selection == null)
			return new SelectionSet(); // if its null return a dummy set to stop the subclasses from null reference crashing, menu dismiss has already been triggered.
		else
			return selection;
	}
}
