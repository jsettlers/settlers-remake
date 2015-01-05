package jsettlers.main.android.fragments;

import jsettlers.graphics.androidui.menu.AndroidMenuPutable;
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

	private static final int MY_ID = 237263849;

	@Override
	public String getName() {
		return "game-command";
	}

	public GameCommandFragment() {
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		FrameLayout layout = new FrameLayout(inflater.getContext());
		layout.setId(MY_ID);
		return layout;
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

	public AndroidMenuPutable getPutable(Context context) {
		return new AndroidMenuPutable(context, this);
	}

	@Override
	public void showMenuFragment(Fragment fragment) {
		Activity activity = getActivity();
		if (activity != null) {
			FragmentTransaction transaction =
					activity.getFragmentManager().beginTransaction();
			transaction.replace(MY_ID, fragment, "android-menu");
			transaction.commit();
		}
	}

	@Override
	public void hideMenu() {
		Activity activity = getActivity();
		if (activity != null) {
			FragmentManager getManager = activity.getFragmentManager();
			Fragment fragment = getManager.findFragmentByTag("android-menu");
			if (fragment != null) {
				FragmentTransaction transaction = getManager.beginTransaction();
				transaction.remove(fragment);
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
