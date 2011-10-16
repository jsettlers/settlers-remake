package jsettlers.graphics;

import go.graphics.area.Area;
import go.graphics.region.Region;
import go.graphics.swing.AreaContainer;

import java.awt.Dimension;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.statistics.IStatisticable;
import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.progress.ProgressConnector;
import jsettlers.graphics.progress.ProgressContent;

/**
 * This is the jogl panel that displays the game content.
 * <p>
 * Initially, it is empty.
 * 
 * @see JOGLPanel#showHexMap(IHexMap)
 * @author michael
 */
public class JOGLPanel {

	private AreaContainer panel;
	private Region region;

	private SettlersContent content = null;

	/**
	 * Creates a new empty panel.
	 */
	public JOGLPanel() {
		Area area = new Area();
		this.region = new Region(Region.POSITION_CENTER);
		area.add(this.region);
		this.panel = new AreaContainer(area);
		this.panel.setPreferredSize(new Dimension(640, 480));
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
	}

	/**
	 * Sets the content of the panel to be amap.
	 * <p>
	 * This method also sets up the draw context of the map and returns a
	 * {@link MapInterfaceConnector} that can be accessed to change the view.
	 * 
	 * @param map
	 *            The map to display.
	 * @param playerStatistics
	 *            the statistics to be displayed. (can be null) <br>
	 *            TODO @Michael use player statistics
	 * @return The connector to access the view and add event listenrs
	 * @see MapInterfaceConnector
	 */
	public synchronized MapInterfaceConnector showHexMap(
	        final IGraphicsGrid map, IStatisticable playerStatistics) {
		MapContent content = new MapContent(map);
		changeContent(content);

		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO: this is only for testing
				JOGLPanel.this.region.requestRedraw();
			}
		}, 10, 50);
		// this.panel.setAutoAnimate(true);

		return content.getInterfaceConnector();
	}

	public JPanel getJOGLJPanel() {
		return this.panel;
	}

}
