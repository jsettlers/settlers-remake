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

import go.graphics.UIPoint;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.androidui.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class PauseMenu extends AndroidMenu {

	public PauseMenu(AndroidMenuPutable androidMenuPutable) {
		super(androidMenuPutable);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.pause, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Button quit = (Button) view.findViewById(R.id.pause_quit);
		quit.setOnClickListener(generateActionListener(new Action(
				EActionType.EXIT), true));

		Button resume = (Button) view.findViewById(R.id.pause_resume);
		resume.setOnClickListener(generateActionListener(new Action(
				EActionType.SPEED_UNSET_PAUSE), false));
	}

	@Override
	public Action getActionFor(UIPoint position) {
		return new Action(EActionType.SPEED_UNSET_PAUSE);
	}

	@Override
	public boolean onBackButtonPressed() {
		getActionFireable().fireAction(new Action(EActionType.EXIT));
		return true;
	}
}
