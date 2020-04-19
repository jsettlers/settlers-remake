/*******************************************************************************
 * Copyright (c) 2015 - 2018
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
package jsettlers.graphics.map.draw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;

import go.graphics.GLDrawContext;
import jsettlers.common.Color;
import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.IBuilding.IOccupied;
import jsettlers.common.buildings.IBuildingOccupier;
import jsettlers.common.buildings.OccupierPlace;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IArrowMapObject;
import jsettlers.common.mapobject.IAttackableTowerMapObject;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.mapobject.IStackMapObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.ESoldierClass;
import jsettlers.common.movable.IMovable;
import jsettlers.common.movable.IShipInConstruction;
import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.sound.ISoundable;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.SettlerImage;
import jsettlers.graphics.image.SingleImage;
import jsettlers.graphics.image.sequence.Sequence;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.map.draw.settlerimages.SettlerImageMap;
import jsettlers.graphics.map.geometry.MapCoordinateConverter;
import jsettlers.graphics.sound.SoundManager;

/**
 * This class handles drawing of objects on the map.
 *
 * @author michael
 */
public class MapObjectDrawer {

	private static final int[] PASSENGER_POSITION_TO_FRONT = {
		2,
		-2,
		-2,
		1,
		1,
		-1,
		-1};
	private static final int[] PASSENGER_POSITION_TO_RIGHT = {
		0,
		1,
		-1,
		-1,
		1,
		1,
		-1};
	private static final int   maxNumberOfPassengers       = PASSENGER_POSITION_TO_FRONT.length;
	private static final int   PASSENGER_DECK_HEIGHT       = 10;
	private static final int[] CARGO_POSITION_TO_FRONT     = {
		0,
		3,
		-2};
	private static final int[] CARGO_POSITION_TO_RIGHT     = {
		0,
		0,
		0};
	private static final int   maxNumberOfStacks           = CARGO_POSITION_TO_FRONT.length;
	private static final int   CARGO_DECK_HEIGHT           = 18;

	private static final int SOUND_MILL               = 42;
	private static final int SOUND_SLAUGHTERHOUSE     = 14;
	private static final int SOUND_BUILDING_DESTROYED = 93;
	private static final int SOUND_SETTLER_KILLED     = 35;
	private static final int SOUND_FALLING_TREE       = 36;

	private static final OriginalImageLink INSIDE_BUILDING_RIGHT = new OriginalImageLink(EImageLinkType.SETTLER, 12, 28, 1);
	private static final OriginalImageLink INSIDE_BUILDING_LEFT  = new OriginalImageLink(EImageLinkType.SETTLER, 12, 28, 0);

	private static final int OBJECTS_FILE   = 1;
	private static final int BUILDINGS_FILE = 13;

	private static final int   TREE_TYPES              = 7;
	private static final int[] TREE_SEQUENCES          = new int[]{
		1,
		2,
		4,
		7,
		8,
		16,
		17};
	private static final int[] TREE_CHANGING_SEQUENCES = new int[]{
		3,
		3,
		6,
		9,
		9,
		18,
		18};
	private static final float TREE_CUT_1              = 0.03F;
	private static final float TREE_CUT_2              = 0.06F;
	private static final float TREE_CUT_3              = 0.09F;
	private static final float TREE_TAKEN              = 0.1F;

	/**
	 * First images in tree cutting sequence.
	 */
	private static final int TREE_FALL_IMAGES = 4;

	/**
	 * Tree falling speed. bigger => faster.
	 */
	private static final float TREE_FALLING_SPEED = 1 / 0.001f;
	private static final int   TREE_ROT_IMAGES    = 4;
	private static final int   TREE_SMALL         = 12;
	private static final int   TREE_MEDIUM        = 11;
	private static final int   SMALL_GROWING_TREE = 22;

	private static final int CORN            = 23;
	private static final int CORN_GROW_STEPS = 7;
	private static final int CORN_DEAD_STEP  = 8;

	private static final int WINE            = 25;
	private static final int WINE_GROW_STEPS = 3;
	private static final int WINE_DEAD_STEP  = 0;

	private static final int WINE_BOWL_SEQUENCE = 46;
	private static final int WINE_BOWL_IMAGES   = 9;

	private static final int WAVES = 26;

	private static final int FILE_BORDER_POST = 13;

	private static final int STONE = 31;

	private static final int SELECT_MARK_SEQUENCE = 11;
	private static final int SELECT_MARK_FILE     = 4;

	private static final int MILL_FILE = 13;
	private static final int MILL_SEQ  = 15;

	private static final int PIG_SEQ      = 0;
	private static final int ANIMALS_FILE = 6;
	private static final int FISH_SEQ     = 7;

	private static final int MOVE_TO_MARKER_SEQUENCE = 0;
	private static final int MARKER_FILE             = 3;

	private static final float CONSTRUCTION_MARK_Z         = 0.92f;
	private static final float PLACEMENT_BUILDING_Z        = 0.91f;
	private static final float MOVABLE_SELECTION_MARKER_Z  = 0.9f;
	private static final float BUILDING_SELECTION_MARKER_Z = 0.9f;
	private static final float FLAG_ROOF_Z                 = 0.89f;
	private static final float SMOKE_Z                     = 0.9f;
	private static final float WAVES_Z                     = -0.1f;
	private static final float BORDER_STONE_Z              = -0.1f;

	private static final int SHIP_IMAGE_FILE          = 36;
	private static final int FERRY_BASE_SEQUENCE      = 4;
	private static final int CARGO_SHIP_BASE_SEQUENCE = 0;

	private static final int SMOKE_HEIGHT = 30;

	private static final int FLAG_FILE = 13;
	private final SoundManager   sound;
	private final MapDrawContext context;
	private byte[][] visibleGrid = null;

	/**
	 * An animation counter, used for trees and other waving/animated things.
	 */
	private int             animationStep = 0;
	/**
	 * The image provider that supplies us with the images we need.
	 */
	private ImageProvider   imageProvider;
	private SettlerImageMap imageMap;
	private float           betweenTilesY;

	/**
	 * Creates a new {@link MapObjectDrawer}.
	 *  @param context
	 * 		The context to use for computing the positions.
	 * @param sound
	 *      The handle to play sound
	 */
	public MapObjectDrawer(MapDrawContext context, SoundManager sound) {
		this.context = context;
		this.sound = sound;
	}

	public void setVisibleGrid(byte[][] visibleGrid) {
		this.visibleGrid = visibleGrid;
	}

	/**
	 * Draws a map object at a given position.
	 *
	 * @param x
	 * 		THe position to draw the object.
	 * @param y
	 * 		THe position to draw the object.
	 * @param object
	 * 		The object (tree, ...) to draw.
	 */
	public void drawMapObject(int x, int y, IMapObject object) {
		forceSetup();

		byte fogStatus = visibleGrid != null ? visibleGrid[x][y] : CommonConstants.FOG_OF_WAR_VISIBLE;
		if (fogStatus == 0) {
			return; // break
		}
		float color = getColor(fogStatus);

		drawObject(x, y, object, color);

		if (object.getNextObject() != null) {
			drawMapObject(x, y, object.getNextObject());
		}
	}

	public void drawDock(int x, int y, IMapObject object) {
		forceSetup();
		byte fogStatus = context.getVisibleStatus(x, y);
		if (fogStatus == 0) {
			return;
		}
		float color = getColor(fogStatus);
		Image image = imageProvider.getImage(new OriginalImageLink(EImageLinkType.SETTLER, 1, 112, 0));
		draw(image, x, y, 0, getColor(object), color);
	}

	public void drawStockBack(int x, int y, IBuilding stock) {
		forceSetup();
		byte fogStatus = visibleGrid != null ? visibleGrid[x][y] : CommonConstants.FOG_OF_WAR_VISIBLE;
		if (fogStatus == 0) {
			return;
		}
		float color = getColor(fogStatus);
		float state = stock.getStateProgress();
		if (state >= 0.99) {
			ImageLink[] images = EBuildingType.STOCK.getImages();
			draw(imageProvider.getImage(images[0]), x, y, 0, color);
			draw(imageProvider.getImage(images[1]), x, y, 0, color);
			draw(imageProvider.getImage(images[5]), x, y, 0, color);
		}
	}

	public void drawStockFront(int x, int y, IBuilding stock) {
		forceSetup();
		byte fogStatus = visibleGrid != null ? visibleGrid[x][y] : CommonConstants.FOG_OF_WAR_VISIBLE;
		if (fogStatus == 0) {
			return;
		}
		float color = getColor(fogStatus);
		float state = stock.getStateProgress();
		if (state >= 0.99) {
			ImageLink[] images = EBuildingType.STOCK.getImages();
			for (int i = 2; i < 5; i++) {
				draw(imageProvider.getImage(images[i]), x, y, 0, color);
			}
		}
	}

	private void drawShipInConstruction(int x, int y, IShipInConstruction ship) {
		byte fogOfWarVisibleStatus = visibleGrid != null ? visibleGrid[x][y] : CommonConstants.FOG_OF_WAR_VISIBLE;
		EDirection direction = ship.getDirection();
		EDirection shipImageDirection = direction.rotateRight(3); // ship images have a different direction numbering
		EMapObjectType shipType = ship.getObjectType();
		float shade = getColor(fogOfWarVisibleStatus);
		float state = ship.getStateProgress();
		int baseSequence = (shipType == EMapObjectType.FERRY) ? FERRY_BASE_SEQUENCE : CARGO_SHIP_BASE_SEQUENCE;
		ImageLink shipLink = new OriginalImageLink(EImageLinkType.SETTLER, SHIP_IMAGE_FILE, baseSequence + 3, shipImageDirection.ordinal);
		Image image = imageProvider.getImage(shipLink);
		drawWithConstructionMask(x, y, state, image, shade);
	}

	private void drawShip(IMovable ship, int x, int y) {
		forceSetup();

		byte fogOfWarVisibleStatus = visibleGrid != null ? visibleGrid[x][y] : CommonConstants.FOG_OF_WAR_VISIBLE;
		if (fogOfWarVisibleStatus == 0) {
			return;
		}
		float height = context.getMap().getHeightAt(x, y);
		EDirection direction = ship.getDirection();
		EDirection shipImageDirection = direction.rotateRight(3); // ship images have a different direction numbering
		EMovableType shipType = ship.getMovableType();
		float shade = getColor(fogOfWarVisibleStatus);
		int baseSequence = (shipType == EMovableType.FERRY) ? FERRY_BASE_SEQUENCE : CARGO_SHIP_BASE_SEQUENCE;

		GLDrawContext glDrawContext = context.getGl();
		MapCoordinateConverter mapCoordinateConverter = context.getConverter();
		int sailSequence = (shipType == EMovableType.FERRY) ? 29 : 28;

		// get drawing position
		Color color = context.getPlayerColor(ship.getPlayer().getPlayerId());
		float viewX = context.getOffsetX();
		float viewY = context.getOffsetY();
		if (ship.getAction() == EMovableAction.WALKING) {
			int originX = x - direction.getGridDeltaX();
			int originY = y - direction.getGridDeltaY();
			viewX += betweenTilesX(originX, originY, x, y, ship.getMoveProgress());
			viewY += betweenTilesY;
		} else {
			viewX += mapCoordinateConverter.getViewX(x, y, height);
			viewY += mapCoordinateConverter.getViewY(x, y, height);
		}
		// draw ship body
		drawShipLink(SHIP_IMAGE_FILE, baseSequence, shipImageDirection, glDrawContext, viewX, viewY, color, shade);
		// prepare freight drawing
		List<? extends IMovable> passengerList = ship.getPassengers();

		float baseViewX = mapCoordinateConverter.getViewX(x, y, height);
		float baseViewY = mapCoordinateConverter.getViewY(x, y, height);
		float xShiftForward = mapCoordinateConverter.getViewX(x + direction.gridDeltaX, y + direction.gridDeltaY, height) - baseViewX;
		float yShiftForward = mapCoordinateConverter.getViewY(x + direction.gridDeltaX, y + direction.gridDeltaY, height) - baseViewY;
		int xRight = x + direction.rotateRight(1).gridDeltaX + direction.rotateRight(2).gridDeltaX;
		int yRight = y + direction.rotateRight(1).gridDeltaY + direction.rotateRight(2).gridDeltaY;

		float xShiftRight = (mapCoordinateConverter.getViewX(xRight, yRight, height) - baseViewX) / 2;
		float yShiftRight = (mapCoordinateConverter.getViewY(xRight, yRight, height) - baseViewY) / 2;
		ArrayList<FloatIntObject> freightY = new ArrayList<>();
		int numberOfFreight;
		// get freight positions
		if (shipType == EMovableType.FERRY) {
			numberOfFreight = passengerList.size();
			if (numberOfFreight > maxNumberOfPassengers) {
				numberOfFreight = maxNumberOfPassengers;
			}
			for (int i = 0; i < numberOfFreight; i++) {
				freightY.add(new FloatIntObject(PASSENGER_POSITION_TO_FRONT[i] * yShiftForward
					+ PASSENGER_POSITION_TO_RIGHT[i] * yShiftRight, i));
			}
		} else {
			numberOfFreight = ship.getNumberOfCargoStacks();
			if (numberOfFreight > maxNumberOfStacks) {
				numberOfFreight = maxNumberOfStacks;
			}
			for (int i = 0; i < numberOfFreight; i++) {
				freightY.add(new FloatIntObject(CARGO_POSITION_TO_FRONT[i] * yShiftForward
					+ CARGO_POSITION_TO_RIGHT[i] * yShiftRight, i));
			}
		}
		// sort freight by view y
		if (freightY.size() > 0) {
			Collections.sort(freightY, (o1, o2) -> Float.compare(o2.getFloat(), o1.getFloat()));
		}

		ShortPoint2D shipPosition = ship.getPosition();

		if (shipType == EMovableType.FERRY) {
			// draw passengers behind the sail
			for (int i = 0; i < numberOfFreight; i++) {
				int j = freightY.get(i).getInt();
				float yShift = freightY.get(i).getFloat();
				if (yShift >= 0) {
					float xShift = PASSENGER_POSITION_TO_FRONT[j] * xShiftForward + PASSENGER_POSITION_TO_RIGHT[j] * xShiftRight;
					IMovable passenger = passengerList.get(j);
					Image image = this.imageMap.getImageForSettler(passenger.getMovableType(), EMovableAction.NO_ACTION,
						EMaterialType.NO_MATERIAL, getPassengerDirection(direction, shipPosition, i), 0
					);
					image.drawAt(glDrawContext, viewX + xShift, viewY + yShift + PASSENGER_DECK_HEIGHT, 0, color, shade);
				}
			}
		} else {
			// draw stacks behind the sail
			for (int i = 0; i < numberOfFreight; i++) {
				int j = freightY.get(i).getInt();
				float yShift = freightY.get(i).getFloat();
				if (yShift >= 0) {
					float xShift = CARGO_POSITION_TO_FRONT[j] * xShiftForward + CARGO_POSITION_TO_RIGHT[j] * xShiftRight;
					EMaterialType material = ship.getCargoType(j);
					int count = ship.getCargoCount(j);
					if (material != null && count > 0) {
						Sequence<? extends Image> seq = this.imageProvider.getSettlerSequence(OBJECTS_FILE, material.getStackIndex());
						Image image = seq.getImageSafe(count - 1, () -> Labels.getName(material, false));
						image.drawAt(glDrawContext, viewX + xShift, viewY + yShift + CARGO_DECK_HEIGHT, 0, color, shade);
					}
				}
			}
		}
		// draw sail
		drawShipLink(SHIP_IMAGE_FILE, sailSequence, shipImageDirection, glDrawContext, viewX, viewY, color, shade);
		if (shipType == EMovableType.FERRY) {
			// draw passengers in front of the sail
			for (int i = 0; i < numberOfFreight; i++) {
				int j = freightY.get(i).getInt();
				float yShift = freightY.get(i).getFloat();
				if (yShift < 0) {
					float xShift = PASSENGER_POSITION_TO_FRONT[j] * xShiftForward + PASSENGER_POSITION_TO_RIGHT[j] * xShiftRight;
					IMovable passenger = passengerList.get(j);
					Image image = this.imageMap.getImageForSettler(passenger.getMovableType(), EMovableAction.NO_ACTION,
						EMaterialType.NO_MATERIAL, getPassengerDirection(direction, shipPosition, i), 0
					);
					image.drawAt(glDrawContext, viewX + xShift, viewY + yShift + PASSENGER_DECK_HEIGHT, 0, color, shade);
				}
			}
		} else {
			// draw stacks in front of the sail
			for (int i = 0; i < numberOfFreight; i++) {
				int j = freightY.get(i).getInt();
				float yShift = freightY.get(i).getFloat();
				if (yShift < 0) {
					float xShift = CARGO_POSITION_TO_FRONT[j] * xShiftForward + CARGO_POSITION_TO_RIGHT[j] * xShiftRight;
					EMaterialType material = ship.getCargoType(j);
					int count = ship.getCargoCount(j);
					if (material != null && count > 0) {
						Sequence<? extends Image> seq = this.imageProvider.getSettlerSequence(OBJECTS_FILE, material.getStackIndex());
						Image image = seq.getImageSafe(count - 1, () -> Labels.getName(material, false));
						image.drawAt(glDrawContext, viewX + xShift, viewY + yShift + CARGO_DECK_HEIGHT, 0, color, shade);
					}
				}
			}
		}
		// draw ship front
		drawShipLink(SHIP_IMAGE_FILE, baseSequence + 2, shipImageDirection, glDrawContext, viewX, viewY, color, shade);
		if (ship.isSelected()) {
			drawSelectionMark(viewX, viewY, ship.getHealth() / shipType.getHealth());
		}
	}

	private EDirection getPassengerDirection(EDirection shipDirection, ShortPoint2D shipPosition, int seatIndex) { // make ferry passengers look around
		int x = shipPosition.x;
		int y = shipPosition.y;
		int slowerAnimationStep = animationStep / 32;
		return shipDirection.getNeighbor(((x + seatIndex + slowerAnimationStep) / 8 + (y + seatIndex + slowerAnimationStep) / 11 + seatIndex) % 3 - 1);
	}

	private void drawShipLink(int imageFile, int sequence, EDirection direction, GLDrawContext gl, float viewX, float viewY, Color color, float shade) {
		ImageLink shipLink = new OriginalImageLink(EImageLinkType.SETTLER, imageFile, sequence, direction.ordinal);
		Image image = imageProvider.getImage(shipLink);
		image.drawAt(gl, viewX, viewY, 0, color, shade);
	}

	private void drawObject(int x, int y, IMapObject object, float color) {
		EMapObjectType type = object.getObjectType();
		float progress = object.getStateProgress();

		switch (type) {
			case ARROW:
				drawArrow(context, (IArrowMapObject) object, color);
				break;
			case TREE_ADULT:
				drawTree(x, y, color);
				break;

			case TREE_DEAD:
				playSound(object, SOUND_FALLING_TREE, x, y);
				drawFallingTree(x, y, progress, color);
				break;

			case TREE_GROWING:
				drawGrowingTree(x, y, progress, color);
				break;

			case CORN_GROWING:
				drawGrowingCorn(x, y, object, color);
				break;
			case CORN_ADULT:
				drawCorn(x, y, color);
				break;
			case CORN_DEAD:
				drawDeadCorn(x, y, color);
				break;

			case WINE_GROWING:
				drawGrowingWine(x, y, object, color);
				break;
			case WINE_HARVESTABLE:
				drawHarvestableWine(x, y, color);
				break;
			case WINE_DEAD:
				drawDeadWine(x, y, color);
				break;

			case WINE_BOWL:
				drawWineBowl(x, y, object, color);
				break;

			case WAVES:
				drawWaves(x, y, color);
				break;

			case STONE:
				drawStones(x, y, (int) object.getStateProgress(), color);
				break;

			case CUT_OFF_STONE:
				drawStones(x, y, 0, color);
				break;

			case GHOST:
				drawPlayerableByProgress(x, y, object, color);
				playSound(object, SOUND_SETTLER_KILLED, x, y);
				break;

			case BUILDING_DECONSTRUCTION_SMOKE:
				drawByProgress(x, y, 0, 13, 38, object.getStateProgress(), color);
				playSound(object, SOUND_BUILDING_DESTROYED, x, y);
				break;

			case FOUND_COAL:
				drawByProgress(x, y, 0, OBJECTS_FILE, 94, object.getStateProgress(), color);
				break;

			case FOUND_GEMSTONE:
				drawByProgress(x, y, 0, OBJECTS_FILE, 95, object.getStateProgress(), color);
				break;

			case FOUND_GOLD:
				drawByProgress(x, y, 0, OBJECTS_FILE, 96, object.getStateProgress(), color);
				break;

			case FOUND_IRON:
				drawByProgress(x, y, 0, OBJECTS_FILE, 97, object.getStateProgress(), color);
				break;

			case FOUND_BRIMSTONE:
				drawByProgress(x, y, 0, OBJECTS_FILE, 98, object.getStateProgress(), color);
				break;

			case FOUND_NOTHING:
				drawByProgress(x, y, 0, OBJECTS_FILE, 99, object.getStateProgress(), color);
				break;

			case BUILDINGSITE_SIGN:
				drawByProgress(x, y, 0, OBJECTS_FILE, 93, object.getStateProgress(), color);
				break;

			case BUILDINGSITE_POST:
				drawByProgress(x, y, 0, OBJECTS_FILE, 92, object.getStateProgress(), color);
				break;

			case WORKAREA_MARK:
				drawByProgress(x, y, 0, OBJECTS_FILE, 91, object.getStateProgress(), color);
				break;

			case FLAG_DOOR:
				drawPlayerableWaving(x, y, 0, 63, object, color, "door");
				break;

			case CONSTRUCTION_MARK:
				drawConstructionMark(x, y, object, color);
				break;

			case FLAG_ROOF:
				drawRoofFlag(x, y, object, color);
				break;

			case BUILDING:
				IBuilding building = (IBuilding) object;
				if (building.getBuildingType() == EBuildingType.STOCK && building.getStateProgress() >= 0.99) {
					return;
				}
				drawBuilding(x, y, building, color);
				break;

			case PLACEMENT_BUILDING:
				drawPlacementBuilding(x, y, object, color);
				break;

			case STACK_OBJECT:
				drawStack(x, y, (IStackMapObject) object, color);
				break;

			case SMOKE:
				drawByProgressWithHeight(x, y, SMOKE_HEIGHT, progress, color);
				break;

			case PLANT_DECORATION:
				drawPlantDecoration(x, y, color);
				break;

			case DESERT_DECORATION:
				drawDesertDecoration(x, y, color);
				break;

			case PIG:
				drawPig(x, y, color);
				break;

			case DONKEY:
				drawDonkey(x, y, object, color);
				break;
			case FISH_DECORATION:
				drawDecorativeFish(x, y, color);
				break;

			case ATTACKABLE_TOWER:
				drawAttackableTower(x, y, object);
				break;

			case FERRY:
			case CARGO_SHIP:
				drawShipInConstruction(x, y, (IShipInConstruction) object);

			default:
				break;
		}
	}

	private void drawConstructionMark(int x, int y, IMapObject object, float color) {
		drawByProgress(x, y, CONSTRUCTION_MARK_Z, 4, 6, object.getStateProgress(), color);
	}

	private void drawRoofFlag(int x, int y, IMapObject object, float color) {
		drawPlayerableWaving(x, y, FLAG_ROOF_Z, 64, object, color, "roof");
	}

	private void drawPlacementBuilding(int x, int y, IMapObject object, float color) {
		ImageLink[] images = ((IBuilding) object).getBuildingType().getImages();
		Image image;
		for (ImageLink image1 : images) {
			image = imageProvider.getImage(image1);
			drawOnlyImage(image, x, y, PLACEMENT_BUILDING_Z, null, color);
		}
	}

	private void drawPlantDecoration(int x, int y, float color) {
		int step = (x * 13 + y * 233) % 8;
		Sequence<? extends Image> seq = this.imageProvider.getSettlerSequence(1, 27);
		draw(seq.getImageSafe(step, () -> "plant"), x, y, 0, color);
	}

	private void drawDesertDecoration(int x, int y, float color) {
		int step = (x * 13 + y * 233) % 5 + 10;
		Sequence<? extends Image> seq = this.imageProvider.getSettlerSequence(1, 27);
		draw(seq.getImageSafe(step, () -> "desert-decoration"), x, y, 0, color);
	}

	private void drawPig(int x, int y, float color) {
		Sequence<? extends Image> seq = this.imageProvider.getSettlerSequence(ANIMALS_FILE, PIG_SEQ);

		if (seq.length() > 0) {
			int i = getAnimationStep(x, y) / 2;
			int step = i % seq.length();
			draw(seq.getImageSafe(step, () -> "pig"), x, y, 0, color);
		}
	}

	private void drawDonkey(int x, int y, IMapObject object, float color) {
		int i = (getAnimationStep(x, y) / 20) % 6;
		Image image = imageProvider.getImage(new OriginalImageLink(EImageLinkType.SETTLER, 6, 17, 72 + i));
		draw(image, x, y, 0, getColor(object), color);
	}

	private void drawDecorativeFish(int x, int y, float color) {
		int step = getAnimationStep(x, y);
		Sequence<? extends Image> seq = this.imageProvider.getSettlerSequence(ANIMALS_FILE, FISH_SEQ);
		int substep = step % 1024;
		if (substep < 15) {
			int subseq = (step / 1024) % 4;
			draw(seq.getImageSafe(subseq * 15 + substep, () -> "fish-decoration"), x, y, 0, color);
		}
	}

	private void drawAttackableTower(int x, int y, IMapObject object) {
		IMovable movable = ((IAttackableTowerMapObject) object).getMovable();
		if (movable != null) {
			drawMovableAt(movable, x, y);
			playMovableSound(movable);
		}
	}

	private void forceSetup() {
		if (imageProvider == null) {
			imageProvider = ImageProvider.getInstance();
			imageMap = SettlerImageMap.getInstance();
		}
	}

	/**
	 * Draws any type of movable.
	 *
	 * @param movable
	 * 		The movable.
	 */
	public void draw(IMovable movable) {
		forceSetup();

		final ShortPoint2D pos = movable.getPosition();
		if (movable.getMovableType().isShip()) {
			drawShip(movable, pos.x, pos.y);
		} else {
			drawMovableAt(movable, pos.x, pos.y);
		}

		playMovableSound(movable);
	}

	private void playMovableSound(IMovable movable) {
		if (movable.isSoundPlayed()) {
			return;
		}
		int soundNumber = -1;
		float delay = movable.getMoveProgress();
		switch (movable.getAction()) {
			case ACTION1:
				switch (movable.getMovableType()) {
					case LUMBERJACK:
						if (delay > .8) {
							soundNumber = 0;
						}
						break;
					case BRICKLAYER:
						if (delay > .7) {
							soundNumber = 1;
						}
						break;
					case DIGGER:
						if (delay > .6) {
							soundNumber = 2;
						}
						break;
					case STONECUTTER:
						if (delay > .8) {
							soundNumber = 3;
						}
						break;
					case SAWMILLER:
						if (delay > .2) {
							soundNumber = 5;
						}
						break;
					case SMITH:
						if (delay > .7) {
							soundNumber = 6;
						}
						break;
					case FARMER:
						if (delay > .8) {
							soundNumber = 9;
						}
						break;
					case FISHERMAN:
						if (delay > .8) {
							soundNumber = 16;
						}
						break;
					case DOCKWORKER:
						if (delay > .8) {
							soundNumber = 20;
						}
						break;
					case HEALER:
						if (delay > .8) {
							soundNumber = 21;
						}
						break;
					case GEOLOGIST: // TODO: should also check grid.getResourceAmountAt(x, y)
						if (sound.random.nextInt(256) == 0) {
							soundNumber = 24;
						}
						break;
					case SWORDSMAN_L1:
					case SWORDSMAN_L2:
					case SWORDSMAN_L3:
						if (delay > .8) {
							soundNumber = 30;
						}
						break;
					case BOWMAN_L1:
					case BOWMAN_L2:
					case BOWMAN_L3:
						if (delay > .4) {
							soundNumber = 33;
						}
						break;
					case PIKEMAN_L1:
					case PIKEMAN_L2:
					case PIKEMAN_L3:
						soundNumber = 34;
						break;
					case MELTER:
						soundNumber = 38;
						break;
					case PIG_FARMER:
						if (delay > .4) {
							soundNumber = 39;
						}
						break;
					case DONKEY_FARMER:
						if (delay > .4) {
							soundNumber = 40;
						}
						break;
					case CHARCOAL_BURNER:
						if (delay > .8) {
							soundNumber = 45;
						}
						break;
				}
				break;
			case ACTION2:
				switch (movable.getMovableType()) {
					case FARMER:
						if (delay > .8) {
							soundNumber = 12;
						}
						break;
					case FISHERMAN:
						if (delay > .5) {
							soundNumber = 15;
						}
						break;
					case LUMBERJACK:
						if (delay > .8) {
							soundNumber = 36;
						}
						break;
				}
			case ACTION3:
				switch (movable.getMovableType()) {
					case FISHERMAN:
						if (delay > .95) {
							soundNumber = 17;
						}
						break;
				}
				break;

		}
		if (soundNumber >= 0) {
			sound.playSound(soundNumber, 1, movable.getPosition());
			movable.setSoundPlayed();
		}
	}

	private void drawMovableAt(IMovable movable, int x, int y) {
		byte fogStatus = visibleGrid != null ? visibleGrid[x][y] : CommonConstants.FOG_OF_WAR_VISIBLE;
		if (fogStatus <= CommonConstants.FOG_OF_WAR_EXPLORED) {
			return; // break
		}
		final float moveProgress = movable.getMoveProgress();
		Color color = context.getPlayerColor(movable.getPlayer().getPlayerId());
		float shade = MapObjectDrawer.getColor(fogStatus);
		Image image;
		int offX = context.getOffsetX();
		int offY = context.getOffsetY();
		float viewX;
		float viewY;
		int height = context.getHeight(x, y);

		// smith action
		EMovableType movableType = movable.getMovableType();
		if (movableType == EMovableType.SMITH && movable.getAction() == EMovableAction.ACTION3) {
			// draw smoke
			ShortPoint2D smokePosition = movable.getDirection().getNextHexPoint(movable.getPosition(), 2);
			int smokeX = smokePosition.x;
			int smokeY = smokePosition.y;
			if (movable.getDirection() == EDirection.NORTH_WEST) {
				smokeY--;
			}
			viewX = context.getConverter().getViewX(smokeX, smokeY, height);
			viewY = context.getConverter().getViewY(smokeX, smokeY, height);
			ImageLink link = new OriginalImageLink(EImageLinkType.SETTLER, 13, 43, (int) (moveProgress * 40));
			image = imageProvider.getImage(link);
			image.drawAt(context.getGl(), viewX+offX, viewY+offY, 0, color, shade);
		}

		// melter action
		if (movableType == EMovableType.MELTER && movable.getAction() == EMovableAction.ACTION1) {
			int number = (int) (moveProgress * 36);
			// draw molten metal
			int metalX = x - 2;
			int metalY = y - 5;
			viewX = context.getConverter().getViewX(metalX, metalY, height);
			viewY = context.getConverter().getViewY(metalX, metalY, height);
			int metal = (movable.getGarrisonedBuildingType() == EBuildingType.IRONMELT) ? 37 : 36;
			ImageLink link = new OriginalImageLink(EImageLinkType.SETTLER, 13, metal, number > 24 ? 24 : number);
			image = imageProvider.getImage(link);
			image.drawAt(context.getGl(), viewX+offX, viewY+offY, 0, color, shade);
			// draw smoke
			int smokeX = x - 9;
			int smokeY = y - 14;
			viewX = context.getConverter().getViewX(smokeX, smokeY, height);
			viewY = context.getConverter().getViewY(smokeX, smokeY, height);
			link = new OriginalImageLink(EImageLinkType.SETTLER, 13, 42, number > 35 ? 35 : number);
			image = imageProvider.getImage(link);
			image.drawAt(context.getGl(), viewX+offX, viewY+offY, SMOKE_Z, color, shade);
		}

		if (movable.getAction() == EMovableAction.WALKING) {
			int originX = x - movable.getDirection().getGridDeltaX();
			int originY = y - movable.getDirection().getGridDeltaY();
			viewX = betweenTilesX(originX, originY, x, y, moveProgress);
			viewY = betweenTilesY;
		} else {
			viewX = context.getConverter().getViewX(x, y, height);
			viewY = context.getConverter().getViewY(x, y, height);
		}
		image = this.imageMap.getImageForSettler(movable, moveProgress);
		image.drawAt(context.getGl(), viewX+offX, viewY+offY, 0, color, shade);

		if (movable.isSelected()) {
			drawSelectionMark(viewX+offX, viewY+offY, movable.getHealth() / movableType.getHealth());
		}
	}

	private float betweenTilesX(int startX, int startY, int destinationX, int destinationY, float progress) {
		float theight = context.getHeight(startX, startY);
		float dheight = context.getHeight(destinationX, destinationY);
		MapCoordinateConverter converter = context.getConverter();
		float x = (1 - progress) * converter.getViewX(startX, startY, theight)
			+ progress * converter.getViewX(destinationX, destinationY, dheight);
		betweenTilesY = (1 - progress) * converter.getViewY(startX, startY, theight)
			+ progress * converter.getViewY(destinationX, destinationY, dheight);
		return x;
	}

	private void drawSelectionMark(float viewX, float viewY, float healthPercentage) {
		Image image = ImageProvider.getInstance().getSettlerSequence(4, 7).getImageSafe(0, () -> "settler-selection-indicator");
		image.drawAt(context.getGl(), viewX, viewY + 20, MOVABLE_SELECTION_MARKER_Z, Color.BLACK, 1);

		Sequence<? extends Image> sequence = ImageProvider.getInstance().getSettlerSequence(4, 6);
		int healthId = Math.min((int) ((1 - healthPercentage) * sequence.length()), sequence.length() - 1);
		Image healthImage = sequence.getImageSafe(healthId, () -> "settler-health-indicator");
		healthImage.drawAt(context.getGl(), viewX, viewY + 38, MOVABLE_SELECTION_MARKER_Z, Color.BLACK, 1);
	}

	private void playSound(IMapObject object, int soundId, int x, int y) {
		if (object instanceof IBuilding.ISoundRequestable) {
			sound.playSound(soundId, 1, x, y);
		} else if (object instanceof ISoundable) {
			ISoundable soundable = (ISoundable) object;
			if (!soundable.isSoundPlayed()) {
				sound.playSound(soundId, 1, x, y);
				soundable.setSoundPlayed();
			}
		}
	}

	private void drawArrow(MapDrawContext context, IArrowMapObject object,
						   float color) {
		int sequence = 0;
		switch (object.getDirection()) {
			case SOUTH_WEST:
				sequence = 100;
				break;

			case WEST:
				sequence = 101;
				break;

			case NORTH_WEST:
				sequence = 102;
				break;

			case NORTH_EAST:
				sequence = 103;
				break;

			case EAST:
				sequence = 104;
				break;

			case SOUTH_EAST:
				sequence = 104;
				break;
		}

		float progress = object.getStateProgress();
		int index = Math.round(progress * 2);


		boolean onGround = progress >= 1;
		float x = betweenTilesX(object.getSourceX(), object.getSourceY(), object.getTargetX(), object.getTargetY(), progress) + context.getOffsetX();

		Image image = this.imageProvider.getSettlerSequence(OBJECTS_FILE, sequence).getImageSafe(index, () -> "arrow-" + object.getDirection() + "-" + progress);
		image.drawAt(context.getGl(), x, betweenTilesY + context.getOffsetY() + 20 * progress * (1 - progress) + 20, onGround?-.1f:0, null, color);
	}

	private void drawStones(int x, int y, int availableStones, float color) {
		Sequence<? extends Image> seq = this.imageProvider.getSettlerSequence(OBJECTS_FILE, STONE);
		int stones = seq.length() - availableStones - 1;
		draw(seq.getImageSafe(stones, () -> "stone" + availableStones), x, y, 0, color);
	}

	private void drawWaves(int x, int y, float color) {
		Sequence<? extends Image> seq = this.imageProvider.getSettlerSequence(OBJECTS_FILE, WAVES);
		int len = seq.length();
		int step = (animationStep / 2 + x / 2 + y / 2) % len;
		if (step < len) {
			draw(seq.getImageSafe(step, () -> "wave"), x, y, WAVES_Z, color); // waves must not be drawn on top of other things than water
		}
	}

	private void drawGrowingCorn(int x, int y, IMapObject object, float color) {
		Sequence<? extends Image> seq = this.imageProvider.getSettlerSequence(OBJECTS_FILE, CORN);
		int step = (int) (object.getStateProgress() * CORN_GROW_STEPS);
		draw(seq.getImageSafe(step, () -> "growing-corn"), x, y, 0, color);
	}

	private void drawCorn(int x, int y, float color) {
		Sequence<? extends Image> seq = this.imageProvider.getSettlerSequence(OBJECTS_FILE, CORN);
		draw(seq.getImageSafe(CORN_GROW_STEPS, () -> "grown-corn"), x, y, 0, color);
	}

	private void drawDeadCorn(int x, int y, float color) {
		Sequence<? extends Image> seq = this.imageProvider.getSettlerSequence(OBJECTS_FILE, CORN);
		draw(seq.getImageSafe(CORN_DEAD_STEP, () -> "dead-corn"), x, y, 0, color);
	}

	private void drawGrowingWine(int x, int y, IMapObject object, float color) {
		Sequence<? extends Image> seq = this.imageProvider.getSettlerSequence(OBJECTS_FILE, WINE);
		int step = (int) (object.getStateProgress() * WINE_GROW_STEPS);
		draw(seq.getImageSafe(step, () -> "growing-wine"), x, y, 0, color);
	}

	private void drawHarvestableWine(int x, int y, float color) {
		Sequence<? extends Image> seq = this.imageProvider.getSettlerSequence(OBJECTS_FILE, WINE);
		draw(seq.getImageSafe(WINE_GROW_STEPS, () -> "grown-wine"), x, y, 0, color);
	}

	private void drawDeadWine(int x, int y, float color) {
		Sequence<? extends Image> seq = this.imageProvider.getSettlerSequence(OBJECTS_FILE, WINE);
		draw(seq.getImageSafe(WINE_DEAD_STEP, () -> "dead-wine"), x, y, 0, color);
	}

	private void drawWineBowl(int x, int y, IMapObject object, float color) {
		Sequence<? extends Image> seq = this.imageProvider.getSettlerSequence(BUILDINGS_FILE, WINE_BOWL_SEQUENCE);
		int step = (int) (object.getStateProgress() * (WINE_BOWL_IMAGES - 1));
		draw(seq.getImageSafe(step, () -> "wine-bowl"), x, y, 0, color);
	}

	private void drawGrowingTree(int x, int y, float progress, float color) {
		Image image;
		if (progress < 0.33) {
			Sequence<? extends Image> seq = this.imageProvider.getSettlerSequence(OBJECTS_FILE, SMALL_GROWING_TREE);
			image = seq.getImageSafe(0, () -> "growing-tree-step1");
		} else {
			int treeType = getTreeType(x, y);
			Sequence<? extends Image> seq = this.imageProvider.getSettlerSequence(OBJECTS_FILE, TREE_CHANGING_SEQUENCES[treeType]);
			if (progress < 0.66) {
				image = seq.getImageSafe(TREE_SMALL, () -> "growing-tree-step2");
			} else {
				image = seq.getImageSafe(TREE_MEDIUM, () -> "growing-tree-step3");
			}
		}
		draw(image, x, y, 0, color);
	}

	private void drawFallingTree(int x, int y, float progress, float color) {
		int treeType = getTreeType(x, y);
		int imageStep;

		if (progress < TREE_CUT_1) {
			imageStep = (int) (progress * TREE_FALLING_SPEED);
			if (imageStep >= TREE_FALL_IMAGES) {
				imageStep = TREE_FALL_IMAGES - 1;
			}
		} else if (progress < TREE_CUT_2) {
			// cut image 1
			imageStep = TREE_FALL_IMAGES;
		} else if (progress < TREE_CUT_3) {
			// cut image 2
			imageStep = TREE_FALL_IMAGES + 1;
		} else if (progress < TREE_TAKEN) {
			// cut image 3
			imageStep = TREE_FALL_IMAGES + 2;
		} else {
			int relativeStep = (int) ((progress - TREE_TAKEN) / (1 - TREE_TAKEN) * TREE_ROT_IMAGES);
			imageStep = relativeStep + TREE_FALL_IMAGES + 3;
		}

		Sequence<? extends Image> seq = this.imageProvider.getSettlerSequence(OBJECTS_FILE, TREE_CHANGING_SEQUENCES[treeType]);
		draw(seq.getImageSafe(imageStep, () -> "dying-tree"), x, y, 0, color);
	}

	private void drawTree(int x, int y, float color) {
		int treeType = getTreeType(x, y);
		Sequence<? extends Image> seq = this.imageProvider.getSettlerSequence(OBJECTS_FILE, TREE_SEQUENCES[treeType]);

		int step = getAnimationStep(x, y) % seq.length();
		draw(seq.getImageSafe(step, () -> "grown-tree"), x, y, 0, color);
	}

	/**
	 * Draws a player border at a given position.
	 *
	 * @param x
	 * 		X position
	 * @param y
	 * 		Y position
	 * @param player
	 * 		The player.
	 */
	public void drawPlayerBorderObject(int x, int y, byte player) {
		// TODO: use instanced rendering for better android performance
		forceSetup();

		byte fogStatus = visibleGrid != null ? visibleGrid[x][y] : CommonConstants.FOG_OF_WAR_VISIBLE;
		if (fogStatus <= CommonConstants.FOG_OF_WAR_EXPLORED) {
			return; // break
		}
		Color color = context.getPlayerColor(player);
		draw(imageProvider.getSettlerSequence(FILE_BORDER_POST, 65).getImageSafe(0, () -> "border-indicator"), x, y, BORDER_STONE_Z, color);
	}

	private static int getTreeType(int x, int y) {
		return (x + x / 5 + y / 3 + y + y / 7) % TREE_TYPES;
	}

	private int getAnimationStep(int x, int y) {
		return 0xfffffff & (this.animationStep + x * 167 + y * 1223);
	}

	/**
	 * Increases the animation step for trees and other stuff.
	 */
	public void increaseAnimationStep() {
		this.animationStep = ((int) System.currentTimeMillis() / 100) & 0x7fffffff;
	}

	/**
	 * Draws a stack
	 *
	 * @param x
	 * 		The x coordinate of the building
	 * @param y
	 * 		The y coordinate of the building
	 * @param object
	 * 		The stack to draw.
	 * @param color
	 * 		Color to be drawn
	 */
	private void drawStack(int x, int y, IStackMapObject object, float color) {
		forceSetup();

		byte elements = object.getSize();
		if (elements > 0) {
			drawStackAtScreen(x, y, object.getMaterialType(), elements, color);
		}
	}

	/**
	 * Draws the stack directly to the screen.
	 *
	 * @param x
	 * 		The x coordinate of the building
	 * @param y
	 * 		The y coordinate of the building
	 * @param material
	 * 		The material the stack should have.
	 * @param count
	 * 		The number of elements on the stack
	 */
	private void drawStackAtScreen(int x, int y, EMaterialType material, int count, float color) {
		int stackIndex = material.getStackIndex();

		Sequence<? extends Image> seq = this.imageProvider.getSettlerSequence(OBJECTS_FILE, stackIndex);
		draw(seq.getImageSafe(count - 1, () -> Labels.getName(material, count != 1) + "@" + count), x, y, 0, color);
	}

	/**
	 * Gets the gray color for a given fog.
	 *
	 * @param fogStatus
	 * 		The fog of war value
	 * @return Fog of war transparency color value
	 */
	private static float getColor(int fogStatus) {
		return (float) fogStatus / CommonConstants.FOG_OF_WAR_VISIBLE;
	}

	/**
	 * Draws a given buildng to the context.
	 *
	 * @param x
	 * 		The x coordinate of the building
	 * @param y
	 * 		The y coordinate of the building
	 * @param building
	 * 		The building to draw
	 * @param color
	 * 		Gray color shade
	 */
	private void drawBuilding(int x, int y, IBuilding building, float color) {
		EBuildingType type = building.getBuildingType();

		float state = building.getStateProgress();

		if (state >= 0.99) {
			if (type == EBuildingType.SLAUGHTERHOUSE && ((IBuilding.ISoundRequestable) building).isSoundRequested()) {
				playSound(building, SOUND_SLAUGHTERHOUSE, x, y);
			}

			if (type == EBuildingType.MILL && ((IBuilding.IMill) building).isRotating()) {
				Sequence<? extends Image> seq = this.imageProvider.getSettlerSequence(MILL_FILE, MILL_SEQ);

				if (seq.length() > 0) {
					int i = getAnimationStep(x, y);
					int step = i % seq.length();
					drawOnlyImage(seq.getImageSafe(step, () -> "mill-" + step), x, y, 0, context.getPlayerColor(building.getPlayer().getPlayerId()), color);
					ImageLink[] images = type.getImages();
					if (images.length > 0) {
						Image image = imageProvider.getImage(images[0]);
						drawOnlyShadow(image, x, y);
					}
				}
				playSound(building, SOUND_MILL, x, y);

			} else {
				ImageLink[] images = type.getImages();
				if (images.length > 0) {
					Image image = imageProvider.getImage(images[0]);
					draw(image, x, y, 0, null, color, building.getBuildingType() == EBuildingType.MARKET_PLACE);
				}

				byte fow = visibleGrid != null ? visibleGrid[x][y] : CommonConstants.FOG_OF_WAR_VISIBLE;

				if (building instanceof IOccupied && fow > CommonConstants.FOG_OF_WAR_EXPLORED) {
					drawOccupiers(x, y, (IOccupied) building, color);
				}

				for (int i = 1; i < images.length; i++) {
					Image image = imageProvider.getImage(images[i]);
					draw(image, x, y, 0, color);
				}
			}
		} else if (state >= .01f) {
			drawBuildingConstruction(x, y, color, type, state);
		}

		if (building.isSelected()) {
			drawBuildingSelectMarker(x, y);
		}
	}

	private void drawBuildingConstruction(int x, int y, float color, EBuildingType type, float state) {
		boolean hasTwoConstructionPhases = type.getBuildImages().length > 0;

		boolean isInBuildPhase = hasTwoConstructionPhases && state < .5f;

		if (!isInBuildPhase && hasTwoConstructionPhases) {
			// draw the base build image
			for (ImageLink link : type.getBuildImages()) {
				Image image = imageProvider.getImage(link);
				draw(image, x, y, 0, color);
			}
		}

		ImageLink[] constructionImages = isInBuildPhase ? type.getBuildImages() : type.getImages();

		float maskState = hasTwoConstructionPhases ? (state * 2) % 1 : state;
		for (ImageLink link : constructionImages) {
			Image image = imageProvider.getImage(link);
			drawWithConstructionMask(x, y, maskState, image, color);
		}
	}

	/**
	 * Draws the occupiers of a building
	 *
	 * @param x
	 * 		The x coordinate of the building
	 * @param y
	 * 		The y coordinate of the building
	 * @param building
	 * 		The occupyed building
	 * @param baseColor
	 * 		The base color (gray shade).
	 */
	private void drawOccupiers(int x, int y, IOccupied building, float baseColor) {
		// this can cause a ConcurrentModificationException when
		// a soldier enters the tower!
		try {
			int height = context.getHeight(x, y);
			float towerX = context.getConverter().getViewX(x, y, height);
			float towerY = context.getConverter().getViewY(x, y, height);
			GLDrawContext gl = context.getGl();

			for (IBuildingOccupier occupier : building.getOccupiers()) {
				OccupierPlace place = occupier.getPlace();

				IMovable movable = occupier.getMovable();
				Color color = context.getPlayerColor(movable.getPlayer().getPlayerId());

				Image image;
				switch (place.getSoldierClass()) {
					case INFANTRY:
						OriginalImageLink imageLink = place.looksRight() ? INSIDE_BUILDING_RIGHT : INSIDE_BUILDING_LEFT;
						image = imageProvider.getImage(imageLink);
						((SettlerImage)image).setShadow(null);
						break;
					case BOWMAN:
					default:
						image = this.imageMap.getImageForSettler(movable, movable.getMoveProgress());
						break;
				}
				float viewX = towerX + place.getOffsetX() + context.getOffsetX();
				float viewY = towerY + place.getOffsetY() + context.getOffsetY();
				image.drawAt(gl, viewX, viewY, 0, color, baseColor);

				if (place.getSoldierClass() == ESoldierClass.BOWMAN) {
					playMovableSound(movable);
					if (movable.isSelected()) {
						drawSelectionMark(viewX, viewY, movable.getHealth() / movable.getMovableType().getHealth());
					}
				}
			}
		} catch (ConcurrentModificationException e) {
			// happens sometime, just ignore it.
		}
	}

	private void drawBuildingSelectMarker(int x, int y) {
		Image image = imageProvider.getSettlerSequence(SELECT_MARK_FILE, SELECT_MARK_SEQUENCE).getImageSafe(0, () -> "building-selection-indicator");
		draw(image, x, y, BUILDING_SELECTION_MARKER_Z, Color.BLACK);
	}

	private void drawWithConstructionMask(int x, int y, float maskState, Image unsafeImage, float color) {
		if (!(unsafeImage instanceof SingleImage)) {
			return; // should not happen
		}
		int height = context.getHeight(x, y);
		float viewX = context.getConverter().getViewX(x, y, height)+context.getOffsetX();
		float viewY = context.getConverter().getViewY(x, y, height)+context.getOffsetY();

		SingleImage image = (SingleImage) unsafeImage;
		// number of tiles in x direction, can be adjusted for performance
		int tiles = 10;

		float topLineBottom = 1 - maskState;
		float topLineTop = Math.max(0, topLineBottom - .1f);

		image.drawTriangle(context.getGl(), viewX, viewY, 0, 1, 1, 1, 0, topLineBottom, color);
		image.drawTriangle(context.getGl(), viewX, viewY, 1, 1, 1, topLineBottom, 0, topLineBottom, color);

		for (int i = 0; i < tiles; i++) {
			image.drawTriangle(context.getGl(), viewX, viewY, 1.0f / tiles * i,
				topLineBottom, 1.0f / tiles * (i + 1), topLineBottom, 1.0f / tiles * (i + .5f), topLineTop, color
			);
		}
	}

	private static final int DEAD_SETTLER_FILE = 12;
	private static final int DEAD_SETTLER_INDEX = 27;

	private void drawPlayerableByProgress(int x, int y, IMapObject object, float baseColor) {
		Sequence<? extends Image> sequence = this.imageProvider.getSettlerSequence(DEAD_SETTLER_FILE, DEAD_SETTLER_INDEX);
		int index = Math.min((int) (object.getStateProgress() * sequence.length()), sequence.length() - 1);
		Color color = getColor(object);
		draw(sequence.getImage(index, () -> "dead-settler"), x, y, 0, color, baseColor);
	}

	private Color getColor(IMapObject object) {
		Color color = null;
		if (object instanceof IPlayerable) {
			color = context.getPlayerColor(((IPlayerable) object).getPlayer().getPlayerId());
		}
		return color;
	}

	private void drawPlayerableWaving(int x, int y, float z, int sequenceIndex, IMapObject object, float baseColor, String at) {
		Sequence<? extends Image> sequence = this.imageProvider.getSettlerSequence(FLAG_FILE, sequenceIndex);
		int index = animationStep % sequence.length();
		Color color = getColor(object);
		draw(sequence.getImageSafe(index, () -> "flag-" + at), x, y, z, color, baseColor);
	}

	private void drawByProgress(int x, int y, float z, int file, int sequenceIndex, float progress, float color) {
		Sequence<? extends Image> sequence = this.imageProvider.getSettlerSequence(file, sequenceIndex);
		int index = Math.min((int) (progress * sequence.length()), sequence.length() - 1);
		draw(sequence.getImageSafe(index, null), x, y, z, color);
	}

	private static final int SMOKE_FILE = 13;
	private static final int SMOKE_INDEX = 42;

	private void drawByProgressWithHeight(int x, int y, int height, float progress, float color) {
		Sequence<? extends Image> sequence = this.imageProvider.getSettlerSequence(SMOKE_FILE, SMOKE_INDEX);
		int index = Math.min((int) (progress * sequence.length()), sequence.length() - 1);
		drawWithHeight(sequence.getImageSafe(index, null), x, y, height, color);
	}

	private void draw(Image image, int x, int y, float z, Color color) {
		int height = context.getHeight(x, y);
		float viewX = context.getConverter().getViewX(x, y, height)+context.getOffsetX();
		float viewY = context.getConverter().getViewY(x, y, height)+context.getOffsetY();

		image.drawAt(context.getGl(), viewX, viewY, z, color, 1);
	}

	private void draw(Image image, int x, int y, float z, Color color, float fowDim, boolean background) {
		if (background) {
			z -= 0.1f;
		}
		int height = context.getHeight(x, y);
		float viewX = context.getConverter().getViewX(x, y, height)+context.getOffsetX();
		float viewY = context.getConverter().getViewY(x, y, height)+context.getOffsetY();

		image.drawAt(context.getGl(), viewX, viewY, z, color, fowDim);
	}

	private void draw(Image image, int x, int y, float z, float fowDim) {
		draw(image, x, y, z,null, fowDim, false);
	}

	private void draw(Image image, int x, int y, float z, Color color, float fowDim) {
		draw(image, x, y, z, color, fowDim, false);
	}

	private void drawOnlyImage(Image image, int x, int y, float z, Color torsoColor, float color) {
		int height = context.getHeight(x, y);
		float viewX = context.getConverter().getViewX(x, y, height)+context.getOffsetX();
		float viewY = context.getConverter().getViewY(x, y, height)+context.getOffsetY();
		image.drawOnlyImageAt(context.getGl(), viewX, viewY, z, torsoColor, color);
	}

	private void drawOnlyShadow(Image image, int x, int y) {
		int height = context.getHeight(x, y);
		float viewX = context.getConverter().getViewX(x, y, height)+context.getOffsetX();
		float viewY = context.getConverter().getViewY(x, y, height)+context.getOffsetY();
		image.drawOnlyShadowAt(context.getGl(), viewX, viewY, 0);
	}

	private void drawWithHeight(Image image, int x, int y, int height, float color) {
		int baseHeight = context.getHeight(x, y);
		float viewX = context.getConverter().getViewX(x, y, baseHeight + height)+context.getOffsetX();
		float viewY = context.getConverter().getViewY(x, y, baseHeight + height)+context.getOffsetY();

		image.drawAt(context.getGl(), viewX, viewY, 0, null, color);
	}

	public void drawMoveToMarker(ShortPoint2D moveToMarker, float progress) {
		forceSetup();
		drawByProgress(moveToMarker.x, moveToMarker.y, 0, MARKER_FILE, MOVE_TO_MARKER_SEQUENCE, progress, 1);
	}

	public void drawGotoMarker(ShortPoint2D gotoMarker, Image image) {
		draw(image, gotoMarker.x, gotoMarker.y, FLAG_ROOF_Z,null, 1);
	}
}
