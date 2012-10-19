package jsettlers.graphics.swing;

import go.graphics.area.Area;
import go.graphics.region.Region;
import go.graphics.sound.SoundPlayer;

import java.util.Timer;
import java.util.TimerTask;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.statistics.IStatisticable;
import jsettlers.graphics.INetworkScreenAdapter;
import jsettlers.graphics.ISettlersGameDisplay;
import jsettlers.graphics.SettlersContent;
import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.progress.ProgressConnector;
import jsettlers.graphics.progress.ProgressContent;
import jsettlers.graphics.startscreen.IStartScreenConnector;
import jsettlers.graphics.startscreen.NetworkScreen;
import jsettlers.graphics.startscreen.StartScreen;

/**
 * This is the jogl panel that displays the game content.
 * <p>
 * Initially, it is empty.
 * 
 * @see JOGLPanel#showHexMap(IHexMap)
 * @author michael
 */
public class JOGLPanel implements ISettlersGameDisplay {

	private Region region;

	private SettlersContent content = null;
	private Area area;

	private final SoundPlayer player;

	private TimerTask redrawTimerTask;

	/**
	 * Creates a new empty panel.
	 */
	public JOGLPanel(SoundPlayer player) {
		this.player = player;
		area = new Area();
		this.region = new Region(Region.POSITION_CENTER);
		area.add(this.region);
	}

	public synchronized ProgressConnector showProgress() {
		ProgressContent content = new ProgressContent();
		changeContent(content);

		return new ProgressConnector(content);
	}

	private void changeContent(SettlersContent content) {
		if (this.content != null) {
			this.content.removeRedrawListener(region);
		}
		this.region.setContent(content);
		content.addRedrawListener(region);
		this.content = content;
		region.requestRedraw();
		if (redrawTimerTask != null) {
			redrawTimerTask.cancel();
			redrawTimerTask = null;
		}
	}

	@Override
	public synchronized MapInterfaceConnector showGameMap(
	        final IGraphicsGrid map, IStatisticable playerStatistics) {
		MapContent content = new MapContent(map, player);
		changeContent(content);

		Timer timer = new Timer(true);
		redrawTimerTask = new TimerTask() {
			@Override
			public void run() {
				// TODO: this is only for testing. Implement a real animator on Jogl.
				JOGLPanel.this.region.requestRedraw();
			}
		};
		timer.schedule(redrawTimerTask, 10, 33);

		return content.getInterfaceConnector();
	}

	public synchronized void showStartScreen(IStartScreenConnector connector) {
		changeContent(new StartScreen(connector));
	}

	public Area getArea() {
		return area;
	}

	@Override
	public void showNetworkScreen(INetworkScreenAdapter networkScreen) {
		changeContent(new NetworkScreen(networkScreen));
	}

	@Override
    public void showErrorMessage(String string) {
	    // TODO Display error message on gui
	    
    }

}
