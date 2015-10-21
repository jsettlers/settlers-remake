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
package jsettlers.graphics.map;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import go.graphics.event.GOEvent;
import go.graphics.event.GOEventHandler;
import go.graphics.event.GOKeyEvent;
import go.graphics.event.GOModalEventHandler;
import go.graphics.event.command.GOCommandEvent;
import go.graphics.event.mouse.GODrawEvent;
import go.graphics.event.mouse.GOHoverEvent;
import go.graphics.event.mouse.GOPanEvent;
import go.graphics.event.mouse.GOZoomEvent;
import go.graphics.region.RegionContent;
import go.graphics.sound.SoundPlayer;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;
import jsettlers.common.Color;
import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.EDebugColorModes;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.map.shapes.MapShapeFilter;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.FloatRectangle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.common.statistics.IStatisticable;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.ActionHandler;
import jsettlers.graphics.action.ActionThreadBlockingListener;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.PointAction;
import jsettlers.graphics.action.ScreenChangeAction;
import jsettlers.graphics.action.SelectAreaAction;
import jsettlers.graphics.action.ShowConstructionMarksAction;
import jsettlers.graphics.font.FontDrawerFactory;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.IControls;
import jsettlers.graphics.map.controls.original.OriginalControls;
import jsettlers.graphics.map.draw.Background;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.map.draw.MapObjectDrawer;
import jsettlers.graphics.messages.Message;
import jsettlers.graphics.messages.Messenger;
import jsettlers.graphics.sound.BackgroundSound;
import jsettlers.graphics.sound.SoundManager;
import jsettlers.graphics.startscreen.interfaces.IStartedGame;

/**
 * This is the main map content class. It manages the map drawing on the screen region.
 * <p>
 * <h1>The drawing process</h1> The map is drawn in three steps. At first, the background is drawn. After that, it is overlayed with the images for
 * settlers, and other map objects. Then the interface is drawn above everything else.
 * <p>
 * The objects and background are drawn with the map draw context.
 * <p>
 * UI structure:
 * <ul>
 * <li>left minimap decoration</li>
 * <li>right minimap decoration</li>
 * <li>main background
 * <ul>
 * <li>tabs
 * <ol>
 * <li>building tabs</li>
 * <li>goods tabs</li>
 * <li>settlers tabs</li>
 * <li>game tab</li>
 * </ol>
 * </li>
 * </ul>
 * </li>
 * </ul>
 *
 * @author michael
 */
public final class MapContent implements RegionContent, IMapInterfaceListener, ActionFireable, ActionThreadBlockingListener {
	private static final int SCREEN_PADDING = 50;
	private static final float OVERDRAW_BOTTOM_PX = 50;
	private static final int MAX_MESSAGES = 10;
	private static final float MESSAGE_OFFSET_X = 200;
	private static final int MESSAGE_OFFSET_Y = 30;
	private static final int MESSAGE_LINEHIEGHT = 18;
	private static final long GOTO_MARK_TIME = 1500;
	private static final long DOUBLE_CLICK_TIME = 500;

	private final IGraphicsGrid map;

	private final Background background = new Background();

	private final MapDrawContext context;

	private final MapObjectDrawer objectDrawer;

	/**
	 * The current connector that connects the outside world to us.
	 */
	private final MapInterfaceConnector connector;

	private FloatRectangle oldScreen;

	/**
	 * The controls that represent the interface.
	 */
	private final IControls controls;
	private UIPoint mousePosition = new UIPoint(0, 0);

	private int windowWidth = 1;
	private int windowHeight = 1;

	private final Messenger messenger = new Messenger();
	private final SoundManager soundmanager;
	private final BackgroundSound backgroundSound;

	private ShortPoint2D scrollMarker;
	private long scrollMarkerTime;

	private ShortPoint2D moveToMarker;
	private long moveToMarkerTime;

	private final ReplaceableTextDrawer textDrawer;
	private final IStatisticable playerStatistics;

	private String tooltipString = "";

	private EDebugColorModes debugColorMode = EDebugColorModes.NONE;

	private PlacementBuilding placementBuilding;

	/**
	 * Creates a new map content for the given map.
	 *
	 * @param map
	 *            The map.
	 * @param playerStatistics
	 */
	public MapContent(IStartedGame game, SoundPlayer player) {
		this(game, player, null);
	}

	public MapContent(IStartedGame game, SoundPlayer player, IControls controls) {
		this.map = game.getMap();
		this.playerStatistics = game.getPlayerStatistics();
		textDrawer = new ReplaceableTextDrawer();
		this.context = new MapDrawContext(map, textDrawer);
		this.soundmanager = new SoundManager(player);

		objectDrawer = new MapObjectDrawer(context, soundmanager);
		backgroundSound = new BackgroundSound(context, soundmanager);

		if (controls == null) {
			this.controls = new OriginalControls(this, game.getInGamePlayer());
		} else {
			this.controls = controls;
		}
		this.controls.setDrawContext(this, context);
		// controls = new SmallControls();

		this.connector = new MapInterfaceConnector(this);
		this.connector.addListener(this);

		map.setBackgroundListener(background);
	}

	private void resizeTo(int newWindowWidth, int newWindowHeight) {
		windowWidth = newWindowWidth;
		windowHeight = newWindowHeight;
		this.controls.resizeTo(windowWidth, windowHeight);
		reapplyContentSizes();
	}

	private void reapplyContentSizes() {
		this.context.setSize(windowWidth, windowHeight);
	}

	@Override
	public void drawContent(GLDrawContext gl, int newWidth, int newHeight) {
		try {
			// TODO: Do only check once.
			if (textDrawer.getTextDrawer(gl, EFontSize.NORMAL).getWidth("a") == 0) {
				textDrawer.setTextDrawerFactory(new FontDrawerFactory());
			}

			if (newWidth != windowWidth || newHeight != windowHeight) {
				resizeTo(newWidth, newHeight);
			}

			adaptScreenSize();
			this.objectDrawer.increaseAnimationStep();

			this.context.begin(gl);
			long start = System.currentTimeMillis();

			FloatRectangle screen = this.context.getScreen().getPosition()
					.bigger(SCREEN_PADDING);
			drawBackground(screen);
			long bgtime = System.currentTimeMillis() - start;

			start = System.currentTimeMillis();
			drawMain(screen);

			if (scrollMarker != null) {
				drawScrollMarker();
			}
			if (moveToMarker != null) {
				drawMoveToMarker();
			}

			this.context.end();
			long foregroundtime = System.currentTimeMillis() - start;

			start = System.currentTimeMillis();
			gl.glTranslatef(0, 0, .95f);
			drawSelectionHint(gl);
			controls.drawAt(gl);
			drawMessages(gl);

			drawFramerate(gl);
			if (actionThreadIsSlow) {
				drawActionThreadSlow(gl);
			}
			drawTooltip(gl);
			long uitime = System.currentTimeMillis() - start;

			if (CommonConstants.ENABLE_GRAPHICS_TIMES_DEBUG_OUTPUT) {
				System.out.println("Background: " + bgtime + "ms, Foreground: "
						+ foregroundtime + "ms, UI: " + uitime + "ms");
			}
		} catch (Throwable t) {
			System.err.println("Main draw handler cought throwable:");
			t.printStackTrace(System.err);
		}
	}

	private void drawScrollMarker() {
		long timediff = System.currentTimeMillis() - scrollMarkerTime;
		if (timediff > GOTO_MARK_TIME) {
			scrollMarker = null;
		} else {
			context.beginTileContext(scrollMarker.x, scrollMarker.y);
			ImageProvider.getInstance().getSettlerSequence(3, 1)
					.getImageSafe(timediff < GOTO_MARK_TIME / 2 ? 0 : 1)
					.draw(context.getGl(), null, 1);
			context.endTileContext();
		}
	}

	private void drawMoveToMarker() {
		long timediff = System.currentTimeMillis() - moveToMarkerTime;
		if (timediff >= GOTO_MARK_TIME) {
			moveToMarker = null;
		} else {
			objectDrawer.drawMoveToMarker(moveToMarker, timediff
					/ GOTO_MARK_TIME);
		}
	}

	private void drawMessages(GLDrawContext gl) {
		TextDrawer drawer = textDrawer.getTextDrawer(gl, EFontSize.NORMAL);
		// TODO: don't let logic wait until we rendered.
		synchronized (messenger) {
			int messageIndex = 0;
			for (Message m : messenger.getMessages()) {
				float x = MESSAGE_OFFSET_X;
				int y = MESSAGE_OFFSET_Y + messageIndex * MESSAGE_LINEHIEGHT;
				if (m.getSender() >= 0) {
					String name = getPlayername(m.getSender()) + ":";
					Color color = context.getPlayerColor(m.getSender());
					float width = (float) drawer.getWidth(name);
					float bright = color.getRed() + color.getGreen() + color.getBlue();
					if (bright < .9f) {
						// black
						gl.color(1, 1, 1, .5f);
						gl.fillQuad(x, y, x + width, y + MESSAGE_LINEHIEGHT);
					} else if (bright < 2f) {
						// bad visibility
						gl.color(0, 0, 0, .5f);
						gl.fillQuad(x, y, x + width, y + MESSAGE_LINEHIEGHT);
					}
					drawer.setColor(color.getRed(), color.getGreen(),
							color.getBlue(), 1);
					drawer.drawString(x, y, name);
					x += width + 10;
				}

				drawer.setColor(1, 1, 1, 1);
				drawer.drawString(x, y, m.getMessage());

				messageIndex++;
				if (messageIndex >= MAX_MESSAGES) {
					break;
				}
			}
		}
	}

	private String getPlayername(byte sender) {
		// TODO: Player names
		return "player " + sender;
	}

	private void adaptScreenSize() {
		FloatRectangle newScreen = context.getScreen().getPosition();
		if (!newScreen.equals(oldScreen)) {
			getInterfaceConnector().fireAction(
					new ScreenChangeAction(context.getScreenArea()));
		}
		oldScreen = newScreen;
	}

	private void drawSelectionHint(GLDrawContext gl) {
		if (this.currentSelectionAreaStart != null
				&& this.currentSelectionAreaEnd != null) {
			float x1 = (float) this.currentSelectionAreaStart.getX();
			float y1 = (float) this.currentSelectionAreaStart.getY();
			float x2 = (float) this.currentSelectionAreaEnd.getX();
			float y2 = (float) this.currentSelectionAreaEnd.getY();

			gl.color(1, 1, 1, 1);
			gl.drawLine(new float[] {
					x1, y1, 0, x2, y1, 0, x2, y2, 0, x1, y2, 0
			}, true);
		}
	}

	private UIPoint currentSelectionAreaStart;
	private final FramerateComputer framerate = new FramerateComputer();

	private void drawFramerate(GLDrawContext gl) {
		framerate.nextFrame();
		String fps = Labels.getString("map-fps", framerate.getRate());
		long gametime = playerStatistics.getGameTime() / 1000;
		String timeString = Labels.getString("map-time", gametime / 60 / 60,
				(gametime / 60) % 60, (gametime) % 60);

		TextDrawer drawer = textDrawer.getTextDrawer(gl, EFontSize.NORMAL);
		double spacing = drawer.getWidth("_");
		float y = windowHeight - 1.5f * (float) drawer.getHeight("X");
		drawer.drawString(windowWidth - 9 * (float) spacing, y, fps);
		drawer.drawString(windowWidth - 23 * (float) spacing, y, timeString);
	}

	private void drawTooltip(GLDrawContext gl) {
		if (!tooltipString.isEmpty()) {
			TextDrawer drawer = textDrawer.getTextDrawer(gl, EFontSize.NORMAL);
			drawer.drawString((int) mousePosition.getX(),
					(int) mousePosition.getY(), tooltipString);
		}
	}

	private void drawActionThreadSlow(GLDrawContext gl) {
		TextDrawer drawer = textDrawer.getTextDrawer(gl, EFontSize.NORMAL);
		String string = Labels.getString("action_firerer_slow");
		float x = windowWidth - (float) drawer.getWidth(string) - 5;
		float y = windowHeight - 3 * (float) drawer.getHeight(string);
		drawer.drawString(x, y, string);
	}

	/**
	 * Draws the main content (buildings, settlers, ...), assuming the context is set up.
	 */
	private void drawMain(FloatRectangle screen) {
		short height = map.getHeight();
		short width = map.getWidth();
		MapRectangle area = this.context.getConverter().getMapForScreen(screen);

		double bottomdrawy = screen.getMinY() - OVERDRAW_BOTTOM_PX;

		boolean linePartuallyVisible = true;
		for (int line = 0; line < area.getLines() + 50 && linePartuallyVisible; line++) {
			int y = area.getLineY(line);
			if (y < 0) {
				continue;
			}
			if (y >= height) {
				break;
			}
			linePartuallyVisible = false;

			int endX = Math.min(area.getLineEndX(line), width - 1);
			int startX = Math.max(area.getLineStartX(line), 0);
			for (int x = startX; x <= endX; x = map.nextDrawableX(x, y, endX)) {
				drawTile(x, y);
				if (!linePartuallyVisible) {
					double drawspacey = this.context.getConverter().getViewY(x, y,
							this.context.getHeight(x, y));
					if (drawspacey > bottomdrawy) {
						linePartuallyVisible = true;
					}
				}
			}
		}

		if (placementBuilding != null) {
			ShortPoint2D underMouse = this.context.getPositionOnScreen((float) mousePosition.getX(), (float) mousePosition.getY());
			IMapObject mapObject = context.getMap().getMapObjectsAt(underMouse.x, underMouse.y);

			if (mapObject != null && mapObject.getMapObject(EMapObjectType.CONSTRUCTION_MARK) != null) { // if there is a construction mark
				this.objectDrawer.drawMapObject(map, underMouse.x, underMouse.y, placementBuilding);
			}
		}

		if (debugColorMode != EDebugColorModes.NONE) {
			drawDebugColors();
		}

		context.getDrawBuffer().flush();
	}

	private void drawTile(int x, int y) {
		IMapObject object = map.getMapObjectsAt(x, y);
		if (object != null) {
			this.objectDrawer.drawMapObject(this.map, x, y, object);
		}

		IMovable movable = map.getMovableAt(x, y);
		if (movable != null) {
			this.objectDrawer.draw(movable);
		}

		if (map.isBorder(x, y)) {
			byte player = map.getPlayerIdAt(x, y);
			objectDrawer.drawPlayerBorderObject(x, y, player);
		}
	}

	private void drawDebugColors() {
		IMapArea tiles = new MapShapeFilter(context.getScreenArea(), map.getWidth(),
				map.getHeight());
		GLDrawContext gl = this.context.getGl();

		float[] shape = new float[] {
				0,
				4,
				.5f,
				0,
				0,
				-3,
				2,
				.5f,
				0,
				0,
				-3,
				-2,
				.5f,
				0,
				0,
				0,
				-4,
				.5f,
				0,
				0,
				0,
				-4,
				.5f,
				0,
				0,
				3,
				-2,
				.5f,
				0,
				0,
				3,
				2,
				.5f,
				0,
				0,
				0,
				4,
				.5f,
				0,
				0,
		};

		for (ShortPoint2D pos : tiles) {
			short x = pos.x;
			short y = pos.y;
			int argb = map.getDebugColorAt(x, y, debugColorMode);
			if (argb != 0) {
				this.context.beginTileContext(x, y);
				gl.color(((argb >> 16) & 0xff) / 255f,
						((argb >> 8) & 0xff) / 255f,
						((argb >> 0) & 0xff) / 255f,
						((argb >> 24) & 0xff) / 255f);
				gl.drawQuadWithTexture(0, shape);
				context.endTileContext();
			}
		}
	}

	/**
	 * Draws the background.
	 *
	 * @param gl
	 * @param screen2
	 */
	private void drawBackground(FloatRectangle screen) {
		this.background.drawMapContent(this.context, screen);
	}

	@Override
	public void handleEvent(GOEvent event) {
		if (event instanceof GOPanEvent) {
			UIPoint center = ((GOPanEvent) event).getPanCenter();
			if (center == null || !controls.containsPoint(center)) {
				event.setHandler(new PanHandler(this.context.getScreen()));
			}
		} else if (event instanceof GOCommandEvent) {
			GOCommandEvent commandEvent = (GOCommandEvent) event;
			Action action = getActionForCommand(commandEvent);
			// also set when action was null, to abort drawing.
			fireActionEvent(event, action);
		} else if (event instanceof GOKeyEvent) {
			Action actionForKeyboard = getActionForKeyboard(((GOKeyEvent) event).getKeyCode());
			if (actionForKeyboard != null) {
				fireActionEvent(event, actionForKeyboard);
			}
		} else if (event instanceof GODrawEvent) {
			GODrawEvent drawEvent = (GODrawEvent) event;
			if (!controls.handleDrawEvent(drawEvent)) {
				handleDraw(drawEvent);
			}
		} else if (event instanceof GOHoverEvent) {
			GOHoverEvent hoverEvent = (GOHoverEvent) event;
			handleHover(hoverEvent);
		} else if (event instanceof GOZoomEvent) {
			GOZoomEvent zoomEvent = (GOZoomEvent) event;
			handleZoom(zoomEvent);
		}
	}

	private void handleZoom(GOZoomEvent zoomEvent) {
		zoomEvent.setHandler(new GOModalEventHandler() {
			float startzoom = context.getScreen().getZoom();

			@Override
			public void phaseChanged(GOEvent event) {
			}

			@Override
			public void finished(GOEvent event) {
				eventDataChanged(((GOZoomEvent) event).getZoomFactor());
			}

			private void eventDataChanged(float zoomFactor) {
				float newZoom = startzoom * zoomFactor;
				setZoom(newZoom);
			}

			@Override
			public void aborted(GOEvent event) {
				eventDataChanged(1);
			}

			@Override
			public void eventDataChanged(GOEvent event) {
				eventDataChanged(((GOZoomEvent) event).getZoomFactor());
			}
		});
	}

	private void fireActionEvent(GOEvent event, Action action) {
		event.setHandler(new ActionHandler(action, this));
	}

	/**
	 * Gets a action for a keyboard key
	 *
	 * @param keyCode
	 *            The key
	 * @return The action that corresponds to the key
	 */
	private static Action getActionForKeyboard(String keyCode) {
		System.out.println(keyCode);
		if ("F12".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.FAST_FORWARD);
		} else if ("P".equalsIgnoreCase(keyCode)
				|| "PAUSE".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.SPEED_TOGGLE_PAUSE);
		} else if ("BACK".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.BACK);
		} else if ("+".equals(keyCode) || "]".equals(keyCode)) {
			return new Action(EActionType.SPEED_FASTER);
		} else if ("-".equals(keyCode) || "/".equals(keyCode)) {
			return new Action(EActionType.SPEED_SLOWER);
		} else if (" ".equals(keyCode) || "space".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.SHOW_SELECTION);
		} else if ("d".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.DEBUG_ACTION);
		} else if ("s".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.STOP_WORKING);
		} else if ("e".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.TOGGLE_DEBUG);
		} else if ("o".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.TOGGLE_ORIGINAL_GRAPHICS);
		} else if ("q".equalsIgnoreCase(keyCode)) {
			// TODO: Only show the exit menu.
			return new Action(EActionType.EXIT);
		} else if ("w".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.TOGGLE_FOG_OF_WAR);
		} else if ("n".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.NEXT_OF_TYPE);
		} else if ("F5".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.ZOOM_IN);
		} else if ("F6".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.ZOOM_OUT);
		} else if ("F2".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.SAVE);
		} else if ("DELETE".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.DESTROY);
		} else if ("ESCAPE".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.ABORT);
		} else {
			return null;
		}
	}

	private final GOEventHandler hoverHandler = new GOModalEventHandler() {
		@Override
		public void phaseChanged(GOEvent event) {
		}

		@Override
		public void finished(GOEvent event) {
			changeMousePosition(((GOHoverEvent) event).getHoverPosition());
		}

		@Override
		public void aborted(GOEvent event) {
		}

		@Override
		public void eventDataChanged(GOEvent event) {
			changeMousePosition(((GOHoverEvent) event).getHoverPosition());
		}
	};

	private void handleHover(GOHoverEvent hoverEvent) {
		hoverEvent.setHandler(hoverHandler);
	}

	protected void changeMousePosition(UIPoint position) {
		mousePosition = position;

		if (controls.containsPoint(position)) {
			tooltipString = controls.getDescriptionFor(position);
		} else {
			float x = (float) position.getX();
			float y = (float) position.getY();
			ShortPoint2D onMap = this.context.getPositionOnScreen(x, y);
			tooltipString = controls.getMapTooltip(onMap);
		}
		if (tooltipString == null) {
			tooltipString = "";
		}
	}

	private Action getActionForCommand(GOCommandEvent commandEvent) {
		UIPoint position = commandEvent.getCommandPosition();
		if (controls.containsPoint(position)) {
			return controls.getActionFor(position, commandEvent.isSelecting());
		} else {
			// handle map click
			return handleCommandOnMap(commandEvent, position);
		}
	}

	private void handleDraw(GODrawEvent drawEvent) {
		handleDrawOnMap(drawEvent);
	}

	private void handleDrawOnMap(GODrawEvent drawEvent) {
		this.currentSelectionAreaStart = drawEvent.getDrawPosition();
		drawEvent.setHandler(this.drawSelectionHandler);
	}

	private final GOEventHandler drawSelectionHandler = new GOModalEventHandler() {

		@Override
		public void phaseChanged(GOEvent event) {
		}

		@Override
		public void finished(GOEvent event) {
			updateSelectionArea(
					((GODrawEvent) event).getDrawPosition(), true);
		}

		@Override
		public void aborted(GOEvent event) {
			abortSelectionArea();
		}

		@Override
		public void eventDataChanged(GOEvent event) {
			updateSelectionArea(
					((GODrawEvent) event).getDrawPosition(), false);
		}

	};

	private UIPoint currentSelectionAreaEnd;
	private boolean actionThreadIsSlow;
	private long lastSelectPointTime = 0;
	private ShortPoint2D lastSelectPointPos = null;

	private Action handleCommandOnMap(GOCommandEvent commandEvent,
			UIPoint position) {

		float x = (float) position.getX();
		float y = (float) position.getY();
		ShortPoint2D onMap = this.context.getPositionOnScreen(x, y);
		if (this.context.checkMapCoordinates(onMap.x, onMap.y)) {
			Action action;
			if (commandEvent.isSelecting()) {
				action = handleSelectCommand(onMap);
			} else {
				action = new PointAction(EActionType.MOVE_TO, onMap);
			}
			return action;
		}
		return null;
	}

	private Action handleSelectCommand(ShortPoint2D onMap) {
		Action action;
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastSelectPointTime < DOUBLE_CLICK_TIME
				&& onMap.equals(lastSelectPointPos)) {
			lastSelectPointTime = 0;
			action = new PointAction(EActionType.SELECT_POINT_TYPE, onMap);
		} else {
			lastSelectPointTime = currentTime;
			lastSelectPointPos = onMap;
			action = new PointAction(EActionType.SELECT_POINT, onMap);
		}
		return action;
	}

	protected void abortSelectionArea() {
		this.currentSelectionAreaStart = null;
		this.currentSelectionAreaEnd = null;
	}

	private void updateSelectionArea(UIPoint mousePosition, boolean finished) {
		if (finished && currentSelectionAreaStart != null) {
			int x1 = (int) mousePosition.getX();
			int x2 = (int) this.currentSelectionAreaStart.getX();
			int y1 = (int) mousePosition.getY();
			int y2 = (int) this.currentSelectionAreaStart.getY();

			if (x1 > x2) {
				int temp = x1;
				x1 = x2;
				x2 = temp;
			}
			if (y1 > y2) {
				int temp = y1;
				y1 = y2;
				y2 = temp;
			}
			IMapArea area = this.context.getRectangleOnScreen(x1, y1, x2, y2);
			getInterfaceConnector().fireAction(new SelectAreaAction(area));

			this.currentSelectionAreaStart = null;
			this.currentSelectionAreaEnd = null;
		} else {
			this.currentSelectionAreaEnd = mousePosition;
		}
	}

	/**
	 * Gets the interface connector for the ui.
	 *
	 * @return The connector to access the interface.
	 */
	public MapInterfaceConnector getInterfaceConnector() {
		return this.connector;
	}

	public void setSelection(ISelectionSet selection) {
		controls.displaySelection(selection);
	}

	public void scrollTo(ShortPoint2D point, boolean mark) {
		this.context.scrollTo(point);
		if (mark) {
			scrollMarker = point;
			scrollMarkerTime = System.currentTimeMillis();
		}
	}

	@Override
	public void action(Action action) {
		controls.action(action);
		switch (action.getActionType()) {
		case TOGGLE_DEBUG:
			debugColorMode = EDebugColorModes.getNextMode(debugColorMode);
			System.out.println("Current debugColorMode: " + debugColorMode);
			break;
		case TOGGLE_ORIGINAL_GRAPHICS:
			context.ENABLE_ORIGINAL = !context.ENABLE_ORIGINAL;
			break;
		case PAN_TO:
			PointAction panAction = (PointAction) action;
			scrollTo(panAction.getPosition(), false);
			break;
		case SCREEN_CHANGE:
			ScreenChangeAction screenAction = (ScreenChangeAction) action;
			controls.setMapViewport(screenAction.getScreenArea());
			break;
		case ZOOM_IN:
			if (context.getScreen().getZoom() < 1.1) {
				setZoom(context.getScreen().getZoom() * 2);
			}
			break;
		case ZOOM_OUT:
			if (context.getScreen().getZoom() > 0.6) {
				setZoom(context.getScreen().getZoom() / 2);
			}
			break;
		case MOVE_TO:
			moveToMarker = ((PointAction) action).getPosition();
			moveToMarkerTime = System.currentTimeMillis();
			break;
		case SHOW_CONSTRUCTION_MARK:
			EBuildingType buildingType = ((ShowConstructionMarksAction) action).getBuildingType();
			placementBuilding = buildingType == null ? null : new PlacementBuilding(buildingType);
			break;
		default:
			break;
		}
	}

	private void setZoom(float f) {
		context.getScreen().setZoom(f);
		reapplyContentSizes();
	}

	public void addMessage(Message message) {
		synchronized (messenger) {
			messenger.addMessage(message);
		}
		switch (message.getType()) {
		case ATTACKED:
			soundmanager.playSound(SoundManager.NOTIFY_ATTACKED, 1, 1);
			break;

		default:
			break;
		}
	}

	@Override
	public void fireAction(Action action) {
		Action fire = controls.replaceAction(action);
		if (fire != null) {
			getInterfaceConnector().fireAction(fire);
		}
	}

	@Override
	public void actionThreadSlow(boolean isBlocking) {
		actionThreadIsSlow = isBlocking;
	}

	@Override
	public void actionThreadCoughtException(Throwable e) {
		// This is currently ignroed. TODO: Where to catch exceptions?
	}

	public void stop() {
		backgroundSound.stop();
		controls.stop();
	}

	protected void loadUIState(UIState state) {
		if (state.getStartPoint() != null) {
			scrollTo(state.getStartPoint(), false);
		} else {
			setZoom(state.getZoom());
			context.getScreen().setScreenCenter(state.getScreenCenterX(),
					state.getScreenCenterY());
		}
	}

	protected UIState getUIState() {
		ScreenPosition screen = context.getScreen();
		return new UIState(screen.getScreenCenterX(),
				screen.getScreenCenterY(), screen.getZoom());
	}
}
