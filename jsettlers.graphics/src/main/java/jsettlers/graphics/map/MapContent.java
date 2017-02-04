/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
import go.graphics.IllegalBufferException;
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
import jsettlers.common.CommitInfo;
import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.images.AnimationSequence;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.map.EDebugColorModes;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.menu.IMapInterfaceListener;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.UIState;
import jsettlers.common.menu.action.EActionType;
import jsettlers.common.menu.action.IAction;
import jsettlers.common.menu.messages.IMessage;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.FloatRectangle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.common.statistics.IGameTimeProvider;
import jsettlers.graphics.action.*;
import jsettlers.graphics.font.FontDrawerFactory;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.IControls;
import jsettlers.graphics.map.controls.original.OriginalControls;
import jsettlers.graphics.map.draw.Background;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.map.draw.MapObjectDrawer;
import jsettlers.graphics.messages.Messenger;
import jsettlers.graphics.sound.BackgroundSound;
import jsettlers.graphics.sound.SoundManager;

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
	private static final AnimationSequence GOTO_ANIMATION = new AnimationSequence(new OriginalImageLink(EImageLinkType.SETTLER, 3, 1).getName(), 0,
			2);
	private static final float UI_OVERLAY_Z = .95f;

	private final class ZoomEventHandler implements GOModalEventHandler {
		float startzoom = context.getScreen().getZoom();

		@Override
		public void phaseChanged(GOEvent event) {
		}

		@Override
		public void finished(GOEvent event) {
			eventDataChanged(((GOZoomEvent) event).getZoomFactor());
		}

		@Override
		public void aborted(GOEvent event) {
			eventDataChanged(1);
		}

		@Override
		public void eventDataChanged(GOEvent event) {
			eventDataChanged(((GOZoomEvent) event).getZoomFactor());
		}

		private void eventDataChanged(float zoomFactor) {
			float newZoom = startzoom * zoomFactor;
			setZoom(newZoom);
		}
	}

	private static final int SCREEN_PADDING = 50;
	private static final float OVERDRAW_BOTTOM_PX = 50;
	private static final float MESSAGE_OFFSET_X = 300;
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

	private final Messenger messenger;
	private final SoundManager soundmanager;
	private final BackgroundSound backgroundSound;

	private ShortPoint2D scrollMarker;
	private long scrollMarkerTime;

	private ShortPoint2D moveToMarker;
	private long moveToMarkerTime;

	private final ReplaceableTextDrawer textDrawer;
	private final IGameTimeProvider gameTimeProvider;

	private String tooltipString = "";

	private EDebugColorModes debugColorMode = EDebugColorModes.NONE;

	private PlacementBuilding placementBuilding;

	private UIPoint currentSelectionAreaEnd;
	private boolean actionThreadIsSlow;
	private long lastSelectPointTime = 0;
	private ShortPoint2D lastSelectPointPos = null;

	/**
	 * Creates a new map content for the given map.
	 *
	 * @param game
	 *            The map.
	 * @param player
	 *            The player
	 */
	public MapContent(IStartedGame game, SoundPlayer player) {
		this(game, player, null);
	}

	/**
	 * 
	 * Creates a new map content for the given map.
	 *
	 * @param game
	 *            The map.
	 * @param player
	 *            The player
	 * @param controls
	 *            The controls object to use as user interface. If it is <code>null</code> the original controls are overlayed.
	 */
	public MapContent(IStartedGame game, SoundPlayer player, IControls controls) {
		this.map = game.getMap();
		this.gameTimeProvider = game.getGameTimeProvider();
		this.messenger = new Messenger(this.gameTimeProvider);
		this.textDrawer = new ReplaceableTextDrawer();
		this.context = new MapDrawContext(map);
		this.soundmanager = new SoundManager(player);

		objectDrawer = new MapObjectDrawer(context, soundmanager);
		backgroundSound = new BackgroundSound(context, soundmanager);
		backgroundSound.start();

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
				drawGotoMarker();
			}
			if (moveToMarker != null) {
				drawMoveToMarker();
			}

			this.context.end();
			long foregroundtime = System.currentTimeMillis() - start;

			start = System.currentTimeMillis();
			gl.glTranslatef(0, 0, UI_OVERLAY_Z);
			drawSelectionHint(gl);
			controls.drawAt(gl);
			drawMessages(gl);

			drawFramerate(gl);
			drawCommitHash(gl);
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

	private void drawGotoMarker() {
		long timediff = System.currentTimeMillis() - scrollMarkerTime;
		if (timediff > GOTO_MARK_TIME) {
			scrollMarker = null;
		} else {
			ImageLink image = GOTO_ANIMATION.getImage(timediff < GOTO_MARK_TIME / 2 ? 0 : 1);
			objectDrawer.drawGotoMarker(scrollMarker, ImageProvider.getInstance().getImage(image));
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

	private float messageAlpha(IMessage m) {
		int age = (int) m.getAge();
		return age < 1500
				? Math.min(1, age / 1000f)
				: Math.max(0,
						1f - (float) age / IMessage.MESSAGE_TTL);
	}

	private void drawMessages(GLDrawContext gl) {
		TextDrawer drawer = textDrawer.getTextDrawer(gl, EFontSize.HEADLINE);
		int messageIndex = 0;
		messenger.doTick();
		for (IMessage m : messenger.getMessages()) {
			float x = MESSAGE_OFFSET_X;
			int y = MESSAGE_OFFSET_Y + messageIndex * MESSAGE_LINEHIEGHT;
			float a = messageAlpha(m);
			if (m.getSender() >= 0) {
				String name = getPlayername(m.getSender()) + ":";
				Color color = context.getPlayerColor(m.getSender());
				float width = (float) drawer.getWidth(name);
				float bright = color.getRed() + color.getGreen() + color.getBlue();
				if (bright < .9f) {
					// black
					drawer.setColor(1, 1, 1, a / 2);
				} else if (bright < 2f) {
					// bad visibility
					drawer.setColor(0, 0, 0, a / 2);
				}
				for (int i = -1; i < 3; i++) {
					drawer.drawString(x + i, y - 1, name);
				}
				drawer.setColor(color.getRed(), color.getGreen(), color.getBlue(), a);
				drawer.drawString(x, y, name);
				x += width + 10;
			}

			drawer.setColor(1, 1, 1, a);
			drawer.drawString(x, y, m.getMessage());

			messageIndex++;
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
		long gametime = gameTimeProvider.getGameTime() / 1000;
		String timeString = Labels.getString("map-time", gametime / 60 / 60,
				(gametime / 60) % 60, (gametime) % 60);

		TextDrawer drawer = textDrawer.getTextDrawer(gl, EFontSize.NORMAL);
		double spacing = drawer.getWidth("_");
		float y = windowHeight - 1.5f * (float) drawer.getHeight("X");
		drawer.drawString(windowWidth - 9 * (float) spacing, y, fps);
		drawer.drawString(windowWidth - 23 * (float) spacing, y, timeString);
	}

	private void drawCommitHash(GLDrawContext gl) {
		String commitHash = CommitInfo.COMMIT_HASH_SHORT;

		TextDrawer drawer = textDrawer.getTextDrawer(gl, EFontSize.NORMAL);
		double spacing = drawer.getWidth("_");
		float y = 1.5f * (float) drawer.getHeight("X");
		drawer.drawString(windowWidth - 8 * (float) spacing, y, commitHash);
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
					double drawspacey = this.context.getConverter().getViewY(x, y, this.context.getHeight(x, y));
					if (drawspacey > bottomdrawy) {
						linePartuallyVisible = true;
					}
				}
			}
		}

		if (placementBuilding != null) {
			ShortPoint2D underMouse = this.context.getPositionOnScreen((float) mousePosition.getX(), (float) mousePosition.getY());
			if (0 <= underMouse.x && underMouse.x < width && 0 <= underMouse.y && underMouse.y < height) {
				IMapObject mapObject = map.getMapObjectsAt(underMouse.x, underMouse.y);

				if (mapObject != null && mapObject.getMapObject(EMapObjectType.CONSTRUCTION_MARK) != null) { // if there is a construction mark
					this.objectDrawer.drawMapObject(underMouse.x, underMouse.y, placementBuilding);
				}
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
			this.objectDrawer.drawMapObject(x, y, object);
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
		GLDrawContext gl = this.context.getGl();

		// @formatter:off
		float[] shape = new float[] {
									  0,  4, .5f,  0,  0,
									 -3,  2, .5f,  0,  0,
									 -3, -2, .5f,  0,  0,
									  0, -4, .5f,  0,  0,
									  0, -4, .5f,  0,  0,
									  3, -2, .5f,  0,  0,
									  3,  2, .5f,  0,  0,
									  0,  4, .5f,  0,  0
									};
		// @formatter:on

		context.getScreenArea().stream().filterBounds(map.getWidth(), map.getHeight()).forEach((x, y) -> {
			try {
				int argb = map.getDebugColorAt(x, y, debugColorMode);
				if (argb != 0) {
					this.context.beginTileContext(x, y);
					gl.color(((argb >> 16) & 0xff) / 255f,
							((argb >> 8) & 0xff) / 255f,
							((argb >> 0) & 0xff) / 255f,
							((argb >> 24) & 0xff) / 255f);
					gl.drawQuadWithTexture(null, shape);
					context.endTileContext();
				}
			} catch (IllegalBufferException e) {
				// TODO: Create a crash report
				// This should never happen since we only use texture 0 (no texture)
			}
		});
	}

	/**
	 * Draws the background.
	 *
	 * @param screen
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
		zoomEvent.setHandler(new ZoomEventHandler());
	}

	/**
	 * Zoom in
	 */
	public void zoomIn() {
		if (context != null) {
			float zoom = context.getScreen().getZoom();
			setZoom(zoom * 1.3f);
		}
	}

	/**
	 * Zoom out
	 */
	public void zoomOut() {
		if (context != null) {
			float zoom = context.getScreen().getZoom();
			setZoom(zoom / 1.3f);
		}
	}

	/**
	 * Zoom to default value
	 */
	public void zoom100() {
		if (context != null) {
			setZoom(1.0f);
		}
	}

	private void fireActionEvent(GOEvent event, Action action) {
		event.setHandler(new ActionHandler(action, this));
	}

	/**
	 * Gets a action for a keyboard key.
	 *
	 * @param keyCode
	 *            The key name as String.
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
			return new Action(EActionType.SHOW_MESSAGE);
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
		} else if ("z".equalsIgnoreCase(keyCode)) {
			return new Action(EActionType.SHOW_SELECTION);
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

	/**
	 * This is called whenever the mouse pointer position changed. Used for tooltips.
	 * 
	 * @param position
	 *            The new mouse position.
	 */
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
			updateSelectionArea(((GODrawEvent) event).getDrawPosition(), true);
		}

		@Override
		public void aborted(GOEvent event) {
			abortSelectionArea();
		}

		@Override
		public void eventDataChanged(GOEvent event) {
			updateSelectionArea(((GODrawEvent) event).getDrawPosition(), false);
		}

	};

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
		if (point != null) {
			this.context.scrollTo(point);
			if (mark) {
				scrollMarker = point;
				scrollMarkerTime = System.currentTimeMillis();
			}
		}
	}

	@Override
	public void action(IAction action) {
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
		case SHOW_MESSAGE:
			scrollTo(messenger.getPosition(), true);
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

	public void addMessage(IMessage message) {
		boolean printMsg;
		synchronized (messenger) {
			printMsg = messenger.addMessage(message);
		}
		if (printMsg)
			switch (message.getType()) {
			case ATTACKED:
				soundmanager.playSound(SoundManager.NOTIFY_ATTACKED, 1, 1);
				break;

			default:
				break;
			}
	}

	@Override
	public void fireAction(IAction action) {
		IAction fire = controls.replaceAction(action);
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
		if (state == null) {
			return;
		}

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

	/**
	 * Gets the color for a given player.
	 * 
	 * @param player
	 *            The player to get the color for.
	 * @return The color.
	 */
	public Color getPlayerColor(byte player) {
		return context.getPlayerColor(player);
	}

}
