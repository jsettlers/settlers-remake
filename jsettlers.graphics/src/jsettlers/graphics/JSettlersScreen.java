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
package jsettlers.graphics;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import go.graphics.event.GOEvent;
import go.graphics.event.GOEventHandler;
import go.graphics.event.GOKeyEvent;
import go.graphics.event.command.GOCommandEvent;
import go.graphics.region.Region;
import go.graphics.region.RegionContent;
import go.graphics.sound.SoundPlayer;
import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.startscreen.IContentSetable;
import jsettlers.graphics.startscreen.StartScreen;
import jsettlers.graphics.startscreen.interfaces.IStartScreen;
import jsettlers.graphics.utils.FocusAction;
import jsettlers.graphics.utils.UIInput;
import jsettlers.graphics.utils.UIPanel;

/**
 * This is the main jsettlers screen manager. It manages the content {@link Region}. TODO: JOGLPanel had the right timers for redraw.
 * 
 * @author michael
 */
public class JSettlersScreen implements IContentSetable {

	private final Region region = new Region(Region.POSITION_CENTER);
	private UIPanel activePanel;
	private final SoundPlayer soundPlayer;
	private final IStartScreen baseConnector;
	private final String revision;
	private UIInput focusedInput;

	public JSettlersScreen(IStartScreen baseConnector, SoundPlayer soundPlayer,
			String revision) {
		super();
		this.baseConnector = baseConnector;
		this.soundPlayer = soundPlayer;
		this.revision = revision;
	}

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

			if (action instanceof FocusAction) {
				focusedInput = ((FocusAction) action).getInput();
			} else {
				focusedInput = null;
			}

			region.requestRedraw();
		}

		@Override
		public void handleEvent(GOEvent event) {
			if (event instanceof GOCommandEvent) {
				event.setHandler(commandHandler);
			} else if (event instanceof GOKeyEvent && focusedInput != null) {
				focusedInput.handleEvent((GOKeyEvent) event);
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
		focusedInput = null;
		if (activePanel != null) {
			activePanel.onDetach();
		}
		this.activePanel = root;
		region.setContent(new PanelRegionContent(root));
		root.onAttach();
		region.requestRedraw();
	}

	@Override
	public void goToStartScreen(String message) {
		setContent(new StartScreen(baseConnector, this, revision));
	}

	public Region getRegion() {
		return region;
	}

	@Override
	public void setContent(RegionContent content) {
		focusedInput = null;
		if (activePanel != null) {
			activePanel.onDetach();
		}
		this.activePanel = null;
		region.setContent(content);
		region.requestRedraw();
	}

	@Override
	public SoundPlayer getSoundPlayer() {
		return soundPlayer;
	}

}
