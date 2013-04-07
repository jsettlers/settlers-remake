package jsettlers.main.android;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.statistics.IStatisticable;
import jsettlers.graphics.INetworkScreenAdapter;
import jsettlers.graphics.ISettlersGameDisplay;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.progress.ProgressConnector;
import jsettlers.graphics.startscreen.IStartScreenConnector;
import jsettlers.main.android.fragments.ProgressFragment;
import android.widget.Toast;

public class JsettlersActivityDisplay implements ISettlersGameDisplay {

	private final JsettlersActivity jsettlersActivity;

	public JsettlersActivityDisplay(JsettlersActivity jsettlersActivity) {
		this.jsettlersActivity = jsettlersActivity;
	}

	@Override
	public ProgressConnector showProgress() {
		ProgressFragment progress = new ProgressFragment();
		jsettlersActivity.showFragment(progress);
		return progress.getConnector();
	}

	@Override
	public void showStartScreen(IStartScreenConnector connector) {
		jsettlersActivity.showStartScreen(connector);
	}

	@Override
	public MapInterfaceConnector showGameMap(IGraphicsGrid map,
	        IStatisticable playerStatistics) {
		return jsettlersActivity.showGameMap(map, playerStatistics);
	}

	@Override
	public void showNetworkScreen(INetworkScreenAdapter networkScreen) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public void showErrorMessage(String string) {
		Toast.makeText(jsettlersActivity, string, Toast.LENGTH_LONG).show();
	}

}
