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
package jsettlers.main.android.fragments;

import jsettlers.graphics.androidui.menu.AndroidMenuPutable;
import jsettlers.graphics.androidui.menu.HudFragment;
import jsettlers.graphics.androidui.menu.IFragmentHandler;
import jsettlers.main.android.R;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * This is an empty fragment that is used when a game is active. It forwards button presses to the game and contains a context menu for testing.
 * 
 * @author michael
 */
public class GameCommandFragment extends JsettlersFragment implements
		IFragmentHandler {

	private static final String MY_TAG = "android-menu";
	private static final int MY_ID = 237263849;

	private HudFragment hudBase;
	private AndroidMenuPutable putable;

	@Override
	public String getName() {
		return "game-command";
	}

	public GameCommandFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		hudBase = new HudFragment(getPutable(getActivity()));
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		FrameLayout layout = new FrameLayout(inflater.getContext());
		layout.setId(MY_ID);
		return layout;
	}

	@Override
	public void onResume() {
		FragmentManager manager = getActivity().getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(MY_ID, hudBase, MY_TAG);
		transaction.commit();
		super.onResume();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.options_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.f12btn:
			getJsettlersActivity().fireKey("F12");
			return true;
		case R.id.savebtn:
			getJsettlersActivity().fireKey("F2");
			return true;
			// case R.id.loadbtn:
			// glView.fireKey("q");
			// return true;
			// case R.id.pausebtn:
			// glView.fireKey("PAUSE");
			// return true;
		case R.id.speedup:
			getJsettlersActivity().fireKey("+");
			getJsettlersActivity().fireKey("+");
			return true;
		case R.id.slowdown:
			getJsettlersActivity().fireKey("-");
			getJsettlersActivity().fireKey("-");
			return true;
		case R.id.kill:
			getJsettlersActivity().fireKey("DELETE");
			return true;
		case R.id.stop:
			getJsettlersActivity().fireKey("STOP");
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onStop() {
		getJsettlersActivity().fireKey("Q");
		super.onStop();
		getJsettlersActivity().showBgMap();
	}

	/**
	 * Gets an object that allows you to access the android menu.
	 * 
	 * @param context
	 *            An Android context.
	 * @return The menu putable.
	 */
	public AndroidMenuPutable getPutable(Context context) {
		if (putable == null) {
			putable = new AndroidMenuPutable(context, this);
		}
		return putable;
	}

	@Override
	public void showMenuFragment(Fragment fragment) {
		Activity activity = getActivity();
		if (activity != null) {
			FragmentTransaction transaction =
					activity.getFragmentManager().beginTransaction();
			transaction.replace(MY_ID, fragment, MY_TAG);
			transaction.commit();
		}
	}

	@Override
	public void hideMenu() {
		Activity activity = getActivity();
		if (activity != null) {
			FragmentManager manager = activity.getFragmentManager();
			Fragment fragment = manager.findFragmentByTag(MY_TAG);
			if (fragment != null) {
				FragmentTransaction transaction = manager.beginTransaction();
				transaction.replace(MY_ID, hudBase, MY_TAG);
				transaction.commit();
			}
		}
	}

	@Override
	public boolean onBackButtonPressed() {
		getJsettlersActivity().fireKey("BACK");
		return true;
	}

}
