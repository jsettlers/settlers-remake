package jsettlers.main.android.fragments;

import jsettlers.main.android.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * This is an empty fragment that is used when a game is active. It forwards
 * button presses to the game and contains a context menu for testing.
 * 
 * @author michael
 */
public class GameCommandFragment extends JsettlersFragment {

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
		return new View(inflater.getContext());
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

}
