package jsettlers.graphics;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import go.graphics.event.GOEvent;
import go.graphics.event.GOEventHandler;
import go.graphics.event.command.GOCommandEvent;
import go.graphics.region.Region;
import go.graphics.region.RegionContent;
import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.startscreen.IContentSetable;
import jsettlers.graphics.utils.UIPanel;

/**
 * This is the main jsettlers screen manager. It manages the content
 * {@link Region}. TODO: JOGLPanel had the right timers for redraw.
 * 
 * @author michael
 */
public class JSettlersScreen implements IContentSetable {

	private final Region region = new Region(Region.POSITION_CENTER);
	private UIPanel activePanel;

	private class PanelRegionContent implements RegionContent {
		private final UIPanel root;

		private final GOEventHandler commandHandler = new GOEventHandler() {
			@Override
			public void phaseChanged(GOEvent event) {
			}

			@Override
			public void finished(GOEvent event) {
				GOCommandEvent c = (GOCommandEvent) event;
				UIPoint position = c.getCommandPosition();
				performActionAt(position.getX(), position.getY());
			}

			@Override
			public void aborted(GOEvent event) {
			}
		};

		public PanelRegionContent(UIPanel root) {
			this.root = root;
		}

		protected void performActionAt(double x, double y) {
			float realx = (float) x / root.getPosition().getWidth();
			float realy = (float) y / root.getPosition().getHeight();
			Action action = root.getAction(realx, realy);
			if (action == null) {
				return;
			}
			if (action instanceof ExecutableAction) {
				((ExecutableAction) action).execute();
			}

			region.requestRedraw();
		}

		@Override
		public void handleEvent(GOEvent event) {
			if (event instanceof GOCommandEvent) {
				event.setHandler(commandHandler);
			}
		}

		@Override
		public void drawContent(GLDrawContext gl, int width, int height) {
			root.setPosition(new FloatRectangle(0, 0, width, height));
			root.drawAt(gl);
		}
	}

	@Override
	public void setContent(UIPanel root) {
		if (activePanel != null) {
			activePanel.onDetach();
		}
		this.activePanel = root;
		region.setContent(new PanelRegionContent(root));
		root.onAttach();
		region.requestRedraw();
	}

	public Region getRegion() {
		return region;
	}

	@Override
	public void setContent(RegionContent content) {
		if (activePanel != null) {
			activePanel.onDetach();
		}
		this.activePanel = null;
		region.setContent(content);
		region.requestRedraw();
	}

}
