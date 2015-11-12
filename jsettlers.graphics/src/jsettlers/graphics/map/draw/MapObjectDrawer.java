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
package jsettlers.graphics.map.draw;

import go.graphics.GLDrawContext;

import java.util.ConcurrentModificationException;

import jsettlers.common.Color;
import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.IBuilding.IOccupyed;
import jsettlers.common.buildings.IBuildingOccupyer;
import jsettlers.common.buildings.OccupyerPlace;
import jsettlers.common.images.AnimationSequence;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IArrowMapObject;
import jsettlers.common.mapobject.IAttackableTowerMapObject;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.mapobject.IStackMapObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.ESoldierClass;
import jsettlers.common.movable.IMovable;
import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.sound.ISoundable;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.SingleImage;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.map.draw.settlerimages.SettlerImageMap;
import jsettlers.graphics.map.geometry.MapCoordinateConverter;
import jsettlers.graphics.sequence.Sequence;
import jsettlers.graphics.sound.SoundManager;

/**
 * This class handles drawing of objects on the map.
 *
 * @author michael
 */
public class MapObjectDrawer {

	private static final OriginalImageLink INSIDE_BUILDING_RIGHT =
			new OriginalImageLink(EImageLinkType.SETTLER, 12, 28, 1);
	private static final OriginalImageLink INSIDE_BUILDING_LEFT =
			new OriginalImageLink(EImageLinkType.SETTLER, 12, 28, 0);

	private static final int OBJECTS_FILE = 1;
	private static final int BUILDINGS_FILE = 13;

	private static final int TREE_TYPES = 7;

	private static final int[] TREE_SEQUENCES = new int[] {
			1, 2, 4, 7, 8, 16, 17,
	};
	private static final int[] TREE_CHANGING_SEQUENCES = new int[] {
			3, 3, 6, 9, 9, 18, 18,
	};

	/**
	 * First images in tree cutting sequence
	 */
	private static final int TREE_FALL_IMAGES = 4;

	/**
	 * Tree falling speed. bigger => faster.
	 */
	private static final float TREE_FALLING_SPEED = 1 / 0.001f;
	/**
	 *
	 */
	private static final int TREE_ROT_IMAGES = 4;

	/**
	 *
	 */
	private static final int TREE_SMALL = 12;

	/**
	 *
	 */
	private static final int TREE_MEDIUM = 11;

	private static final int SMALL_GROWING_TREE = 22;

	private static final int CORN = 23;
	private static final int CORN_GROW_STEPS = 7;
	private static final int CORN_DEAD_STEP = 8;

	private static final int WINE = 25;
	private static final int WINE_GROW_STEPS = 3;
	private static final int WINE_DEAD_STEP = 0;

	private static final int WINE_BOWL_SEQUENCE = 46;
	private static final int WINE_BOWL_IMAGES = 9;

	private static final int WAVES = 26;

	private static final int FILE_BORDERPOST = 13;

	private static final int STONE = 31;

	private static final int SELECTMARK_SEQUENCE = 11;

	private static final int SELECTMARK_FILE = 4;

	private static final int MILL_FILE = 13;

	private static final int MILL_SEQ = 15;

	private static final int PIG_SEQ = 0;

	private static final int ANIMALS_FILE = 6;
	private static final int FISH_SEQ = 7;
	private static final AnimationSequence TREE_TEST_SEQUENCE =
			new AnimationSequence("tree_test", 0, 5);
	private static final int MOVE_TO_MARKER_SEQUENCE = 0;
	private static final int MARKER_FILE = 3;

	private static final float CONSTRUCTION_MARK_Z = 0.92f;
	private static final float PLACEMENT_BUILDING_Z = 0.91f;
	private static final float MOVABLE_SELECTION_MARKER_Z = 0.9f;
	private static final float BUILDING_SELECTION_MARKER_Z = 0.9f;
	private static final float FLAG_ROOF_Z = 0.89f;

	int animationStep = 0;

	private ImageProvider imageProvider;
	private final SoundManager sound;

	private final MapDrawContext context;

	private SettlerImageMap imageMap;
	private float betweenTilesY;

	public MapObjectDrawer(MapDrawContext context, SoundManager sound) {
		this.context = context;
		this.sound = sound;
	}

	/**
	 * Draws a map object at a given position.
	 *
	 * @param context
	 *            The context.
	 * @param map
	 *            For these damned arrwos
	 * @param pos
	 *            THe position to draw the object.
	 * @param object
	 *            The object (tree, ...) to draw.
	 */
	public void drawMapObject(IGraphicsGrid map, int x, int y, IMapObject object) {
		forceSetup();

		byte fogstatus = context.getVisibleStatus(x, y);
		if (fogstatus == 0) {
			return; // break
		}
		float color = getColor(fogstatus);

		EMapObjectType type = object.getObjectType();

		float progress = object.getStateProgress();

		if (type == EMapObjectType.ARROW) {
			drawArrow(context, (IArrowMapObject) object, color);
		} else {
			float z;

			switch (type) {

			case TREE_ADULT:
				if (context.ENABLE_ORIGINAL) {
					drawTree(x, y, color);
				} else {
					drawTreeTest(x, y, color);
				}
				break;

			case TREE_DEAD:
				// TODO: falling tree sound.
				playSound(object, 4);
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
				drawStones(x, y, object, color);
				break;

			case GHOST:
				drawPlayerableByProgress(x, y, 12, 27, object, color);
				playSound(object, 35);
				break;

			case BUILDING_DECONSTRUCTION_SMOKE:
				drawByProgress(x, y, 13, 38, object.getStateProgress(),
						color);
				playSound(object, 36);
				break;

			case FOUND_COAL:
				drawByProgress(x, y, OBJECTS_FILE, 94, object.getStateProgress(),
						color);
				break;

			case FOUND_GEMSTONE:
				drawByProgress(x, y, OBJECTS_FILE, 95, object.getStateProgress(),
						color);
				break;

			case FOUND_GOLD:
				drawByProgress(x, y, OBJECTS_FILE, 96, object.getStateProgress(),
						color);
				break;

			case FOUND_IRON:
				drawByProgress(x, y, OBJECTS_FILE, 97, object.getStateProgress(),
						color);
				break;

			case FOUND_BRIMSTONE:
				drawByProgress(x, y, OBJECTS_FILE, 98, object.getStateProgress(),
						color);
				break;

			case FOUND_NOTHING:
				drawByProgress(x, y, OBJECTS_FILE, 99, object.getStateProgress(),
						color);
				break;

			case BUILDINGSITE_SIGN:
				drawByProgress(x, y, OBJECTS_FILE, 93, object.getStateProgress(),
						color);
				break;

			case BUILDINGSITE_POST:
				drawByProgress(x, y, OBJECTS_FILE, 92, object.getStateProgress(),
						color);
				break;

			case WORKAREA_MARK:
				drawByProgress(x, y, OBJECTS_FILE, 91, object.getStateProgress(),
						color);
				break;

			case FLAG_DOOR:
				drawPlayerableWaving(x, y, 13, 63, object, color);
				break;

			case CONSTRUCTION_MARK:
				z = context.getDrawBuffer().getZ();
				context.getDrawBuffer().setZ(CONSTRUCTION_MARK_Z);
				drawByProgress(x, y, 4, 6, object.getStateProgress(), color);
				context.getDrawBuffer().setZ(z);
				break;

			case FLAG_ROOF:
				z = context.getDrawBuffer().getZ();
				context.getDrawBuffer().setZ(FLAG_ROOF_Z);
				drawPlayerableWaving(x, y, 13, 64, object, color);
				context.getDrawBuffer().setZ(z);
				break;

			case BUILDING:
				drawBuilding(x, y, (IBuilding) object, color);
				break;

			case PLACEMENT_BUILDING:
				z = context.getDrawBuffer().getZ();
				context.getDrawBuffer().setZ(PLACEMENT_BUILDING_Z);
				drawBuilding(x, y, (IBuilding) object, color);
				context.getDrawBuffer().setZ(z);
				break;

			case STACK_OBJECT:
				drawStack(x, y, (IStackMapObject) object, color);
				break;

			case SMOKE:
				drawByProgress(x, y, 13, 42, progress, color);
				break;

			case WINE:
				drawByProgress(x, y, 1, 25, progress, color);
				break;

			case PLANT_DECORATION: {
				int step = (x * 13 + y * 233) % 8;

				Sequence<? extends Image> seq =
						this.imageProvider.getSettlerSequence(1, 27);

				draw(seq.getImageSafe(step), x, y, color);
			}
				break;

			case DESERT_DECORATION: {
				int step = (x * 13 + y * 233) % 5 + 10;

				Sequence<? extends Image> seq =
						this.imageProvider.getSettlerSequence(1, 27);

				draw(seq.getImageSafe(step), x, y, color);
			}
				break;

			case PIG: {
				Sequence<? extends Image> seq =
						this.imageProvider.getSettlerSequence(ANIMALS_FILE,
								PIG_SEQ);

				if (seq.length() > 0) {
					int i = getAnimationStep(x, y) / 2;
					int step = i % seq.length();
					draw(seq.getImageSafe(step), x, y, color);
				}
			}
				break;

			case DONKEY: {
				int i = (getAnimationStep(x, y) / 20) % 6;
				Image image = imageProvider.getImage(new OriginalImageLink(EImageLinkType.SETTLER, 6, 17, 72 + i));
				draw(image, x, y, getColor(object), color);
			}
				break;

			case FISH_DECORATION: {
				int step = getAnimationStep(x, y);
				Sequence<? extends Image> seq =
						this.imageProvider.getSettlerSequence(ANIMALS_FILE,
								FISH_SEQ);
				int substep = step % 1024;
				if (substep < 15) {
					int subseq = (step / 1024) % 4;
					draw(seq.getImageSafe(subseq * 15 + substep), x, y,
							color);
				}
			}
				break;

			case ATTACKABLE_TOWER: {
				IMovable movable =
						((IAttackableTowerMapObject) object).getMovable();
				if (movable != null) {
					drawMovableAt(movable, x, y);
					playMovableSound(movable);
				}
			}
				break;

			default:
				break;
			}
		}
		if (object.getNextObject() != null) {
			drawMapObject(map, x, y, object.getNextObject());
		}
	}

	private void forceSetup() {
		if (imageProvider == null) {
			imageProvider = ImageProvider.getInstance();
			imageMap = SettlerImageMap.getInstance();
		}
	}

	/**
	 * Draws a movable
	 *
	 * @param movable
	 *            The movable.
	 */
	public void draw(IMovable movable) {
		forceSetup();

		final ShortPoint2D pos = movable.getPos();
		drawMovableAt(movable, pos.x, pos.y);

		playMovableSound(movable);
	}

	private void playMovableSound(IMovable movable) {
		if (!movable.isSoundPlayed()) {
			final EAction action = movable.getAction();
			if (action == EAction.ACTION1) {
				playSoundAction1(movable.getMovableType());
				movable.setSoundPlayed();
			} else if (action == EAction.ACTION2) {
				playSoundAction2(movable.getMovableType());
				movable.setSoundPlayed();
			}
		}
	}

	private void playSoundAction1(EMovableType type) {
		switch (type) {
		case LUMBERJACK:
			sound.playSound(1, 1, 1);
			break;
		case STONECUTTER:
			sound.playSound(3, 1, 1);
			break;
		case DIGGER:
			sound.playSound(2, 1, 1);
			break;
		case SAWMILLER:
			sound.playSound(5, 1, 1);
			break;
		case SMITH:
			sound.playSound(6, 1, 1);
			break;
		case FARMER:
			sound.playSound(12, 1, 1);
			break;
		case SWORDSMAN_L1:
		case SWORDSMAN_L2:
		case SWORDSMAN_L3:
			sound.playSound(30, 1, 1);
			break;
		case BOWMAN_L1:
		case BOWMAN_L2:
		case BOWMAN_L3:
			sound.playSound(33, 1, 1);
			break;
		}
	}

	private void playSoundAction2(EMovableType type) {
		// currently there is nobody who needs this.
	}

	private void drawMovableAt(IMovable movable, int x, int y) {
		byte fogstatus = context.getVisibleStatus(x, y);
		if (fogstatus <= CommonConstants.FOG_OF_WAR_EXPLORED) {
			return; // break
		}

		final float moveProgress = movable.getMoveProgress();
		final Image image =
				this.imageMap.getImageForSettler(movable, moveProgress);

		Color color = context.getPlayerColor(movable.getPlayerId());
		float shade = MapObjectDrawer.getColor(fogstatus);

		float viewX;
		float viewY;
		if (movable.getAction() == EAction.WALKING) {
			int originx = x - movable.getDirection().getGridDeltaX();
			int originy = y - movable.getDirection().getGridDeltaY();
			viewX = betweenTilesX(originx, originy, x, y, moveProgress);
			viewY = betweenTilesY;
		} else {
			int height = context.getHeight(x, y);
			viewX = context.getConverter().getViewX(x, y, height);
			viewY = context.getConverter().getViewY(x, y, height);
		}
		image.drawAt(context.getGl(), context.getDrawBuffer(), viewX, viewY,
				color, shade);

		if (movable.isSelected()) {
			drawSelectionMark(viewX, viewY, movable.getHealth() / movable.getMovableType().getHealth());
		}
	}

	private float betweenTilesX(int startx, int starty, int destinationx,
			int destinationy, float progress) {
		float theight = context.getHeight(startx, starty);
		float dheight = context.getHeight(destinationx, destinationy);
		MapCoordinateConverter converter = context.getConverter();
		float x =
				(1 - progress)
						* converter.getViewX(startx, starty, theight)
						+ progress
						* converter.getViewX(destinationx, destinationy,
								dheight);
		betweenTilesY =
				(1 - progress)
						* converter.getViewY(startx, starty, theight)
						+ progress
						* converter.getViewY(destinationx, destinationy,
								dheight);
		return x;
	}

	private void drawSelectionMark(float viewX, float viewY, float healthPercentage) {
		float z = context.getDrawBuffer().getZ();
		context.getDrawBuffer().setZ(MOVABLE_SELECTION_MARKER_Z);

		Image image =
				ImageProvider.getInstance().getSettlerSequence(4, 7)
						.getImageSafe(0);
		image.drawAt(context.getGl(), context.getDrawBuffer(), viewX,
				viewY + 20, -1);

		Sequence<? extends Image> sequence =
				ImageProvider.getInstance().getSettlerSequence(4, 6);
		int healthId =
				Math.min((int) ((1 - healthPercentage) * sequence.length()),
						sequence.length() - 1);
		Image healthImage = sequence.getImageSafe(healthId);
		healthImage.drawAt(context.getGl(), context.getDrawBuffer(), viewX,
				viewY + 38, -1);

		context.getDrawBuffer().setZ(z);
	}

	private void playSound(IMapObject object, int soundid) {
		if (object instanceof ISoundable) {
			ISoundable soundable = (ISoundable) object;
			if (!soundable.isSoundPlayed()) {
				sound.playSound(soundid, 1, 1);
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

		float x =
				betweenTilesX(object.getSourceX(), object.getSourceY(),
						object.getTargetX(), object.getTargetY(), progress);

		int iColor = Color.getABGR(color, color, color, 1);

		boolean onGround = progress >= 1;
		float z = 0;
		if (onGround) {
			z = context.getDrawBuffer().getZ();
			context.getDrawBuffer().setZ(-.1f);
			iColor &= 0x7fffffff;
		}
		Image image =
				this.imageProvider.getSettlerSequence(OBJECTS_FILE, sequence)
						.getImageSafe(index);
		image.drawAt(context.getGl(), context.getDrawBuffer(), x, betweenTilesY
				+ 20 * progress * (1 - progress) + 20, iColor);
		if (onGround) {
			context.getDrawBuffer().setZ(z);
		}
	}

	private void drawStones(int x, int y, IMapObject object, float color) {
		Sequence<? extends Image> seq =
				this.imageProvider.getSettlerSequence(OBJECTS_FILE, STONE);
		int stones = (int) (seq.length() - object.getStateProgress() - 1);
		draw(seq.getImageSafe(stones), x, y, color);
	}

	private void drawWaves(int x, int y, float color) {
		Sequence<? extends Image> seq =
				this.imageProvider.getSettlerSequence(OBJECTS_FILE, WAVES);
		int len = seq.length();
		int step = (animationStep / 2 + x / 2 + y / 2) % len;
		if (step < len) {
			draw(seq.getImageSafe(step), x, y, color);
		}
	}

	private void drawGrowingCorn(int x, int y, IMapObject object, float color) {
		Sequence<? extends Image> seq =
				this.imageProvider.getSettlerSequence(OBJECTS_FILE, CORN);
		int step = (int) (object.getStateProgress() * CORN_GROW_STEPS);
		draw(seq.getImageSafe(step), x, y, color);
	}

	private void drawCorn(int x, int y, float color) {
		Sequence<? extends Image> seq =
				this.imageProvider.getSettlerSequence(OBJECTS_FILE, CORN);
		int step = CORN_GROW_STEPS;
		draw(seq.getImageSafe(step), x, y, color);
	}

	private void drawDeadCorn(int x, int y, float color) {
		Sequence<? extends Image> seq =
				this.imageProvider.getSettlerSequence(OBJECTS_FILE, CORN);
		draw(seq.getImageSafe(CORN_DEAD_STEP), x, y, color);
	}

	private void drawGrowingWine(int x, int y, IMapObject object, float color) {
		Sequence<? extends Image> seq = this.imageProvider.getSettlerSequence(OBJECTS_FILE, WINE);
		int step = (int) (object.getStateProgress() * WINE_GROW_STEPS);
		draw(seq.getImageSafe(step), x, y, color);
	}

	private void drawHarvestableWine(int x, int y, float color) {
		Sequence<? extends Image> seq = this.imageProvider.getSettlerSequence(OBJECTS_FILE, WINE);
		int step = WINE_GROW_STEPS;
		draw(seq.getImageSafe(step), x, y, color);
	}

	private void drawDeadWine(int x, int y, float color) {
		Sequence<? extends Image> seq = this.imageProvider.getSettlerSequence(OBJECTS_FILE, WINE);
		draw(seq.getImageSafe(WINE_DEAD_STEP), x, y, color);
	}

	private void drawWineBowl(int x, int y, IMapObject object, float color) {
		Sequence<? extends Image> seq = this.imageProvider.getSettlerSequence(BUILDINGS_FILE, WINE_BOWL_SEQUENCE);
		int step = (int) (object.getStateProgress() * (WINE_BOWL_IMAGES - 1));
		draw(seq.getImageSafe(step), x, y, color);
	}

	private void drawGrowingTree(int x, int y, float progress, float color) {
		Image image;
		if (progress < 0.33) {
			Sequence<? extends Image> seq =
					this.imageProvider.getSettlerSequence(OBJECTS_FILE,
							SMALL_GROWING_TREE);
			image = seq.getImageSafe(0);
		} else {
			int treeType = getTreeType(x, y);
			Sequence<? extends Image> seq =
					this.imageProvider.getSettlerSequence(OBJECTS_FILE,
							TREE_CHANGING_SEQUENCES[treeType]);
			if (progress < 0.66) {
				image = seq.getImageSafe(TREE_SMALL);
			} else {
				image = seq.getImageSafe(TREE_MEDIUM);
			}
		}
		draw(image, x, y, color);
	}

	private void drawFallingTree(int x, int y, float progress, float color) {
		int treeType = getTreeType(x, y);
		int imageStep = 0;

		if (progress < IMapObject.TREE_CUT_1) {
			imageStep = (int) (progress * TREE_FALLING_SPEED);
			if (imageStep >= TREE_FALL_IMAGES) {
				imageStep = TREE_FALL_IMAGES - 1;
			}
		} else if (progress < IMapObject.TREE_CUT_2) {
			// cut image 1
			imageStep = TREE_FALL_IMAGES;
		} else if (progress < IMapObject.TREE_CUT_3) {
			// cut image 2
			imageStep = TREE_FALL_IMAGES + 1;
		} else if (progress < IMapObject.TREE_TAKEN) {
			// cut image 3
			imageStep = TREE_FALL_IMAGES + 2;
		} else {
			int relativeStep =
					(int) ((progress - IMapObject.TREE_TAKEN)
							/ (1 - IMapObject.TREE_TAKEN) * TREE_ROT_IMAGES);

			imageStep = relativeStep + TREE_FALL_IMAGES + 3;
		}

		Sequence<? extends Image> seq =
				this.imageProvider.getSettlerSequence(OBJECTS_FILE,
						TREE_CHANGING_SEQUENCES[treeType]);
		draw(seq.getImageSafe(imageStep), x, y, color);
	}

	private void drawTree(int x, int y, float color) {
		int treeType = getTreeType(x, y);
		Sequence<? extends Image> seq =
				this.imageProvider.getSettlerSequence(OBJECTS_FILE,
						TREE_SEQUENCES[treeType]);

		int step = getAnimationStep(x, y) % seq.length();
		draw(seq.getImageSafe(step), x, y, color);
	}

	private void drawTreeTest(int x, int y, float color) {
		int step = getAnimationStep(x, y) % TREE_TEST_SEQUENCE.getLength();
		draw(imageProvider.getImage(TREE_TEST_SEQUENCE.getImage(step)), x, y,
				color);
	}

	/**
	 * gets a 0 or a 1.
	 *
	 * @param pos
	 * @return
	 */
	// private static int get01(int x, int y) {
	// return (x * 677 + y) % 2;
	// }

	/**
	 * Draws a player border at a given position.
	 *
	 * @param player
	 *            The player.
	 */
	public void drawPlayerBorderObject(int x, int y, byte player) {
		forceSetup();

		byte fogstatus = context.getVisibleStatus(x, y);
		if (fogstatus <= CommonConstants.FOG_OF_WAR_EXPLORED) {
			return; // break
		}
		float base = getColor(fogstatus);
		Color color = context.getPlayerColor(player);

		draw(imageProvider.getSettlerSequence(FILE_BORDERPOST, 65)
				.getImageSafe(0), x, y, color, base);
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
		this.animationStep =
				((int) System.currentTimeMillis() / 100) & 0x7fffffff;
	}

	/**
	 * Draws a stack
	 *
	 * @param context
	 *            The context to draw with
	 * @param object
	 *            The stack to draw.
	 */
	public void drawStack(int x, int y, IStackMapObject object, float color) {
		forceSetup();

		byte elements = object.getSize();
		if (elements > 0) {
			drawStackAtScreen(x, y, object.getMaterialType(), elements, color);
		}
	}

	/**
	 * Draws the stack directly to the screen.
	 *
	 * @param glDrawContext
	 *            The gl context to draw at.
	 * @param material
	 *            The material the stack should have.
	 * @param count
	 *            The number of elements on the stack
	 */
	private void drawStackAtScreen(int x, int y, EMaterialType material,
			int count, float color) {
		int stackIndex = material.getStackIndex();

		Sequence<? extends Image> seq =
				this.imageProvider.getSettlerSequence(OBJECTS_FILE, stackIndex);
		draw(seq.getImageSafe(count - 1), x, y, color);
	}

	/**
	 * Gets the gray color for a given fog.
	 *
	 * @param fogstatus
	 * @return
	 */
	public static float getColor(int fogstatus) {
		return (float) fogstatus / CommonConstants.FOG_OF_WAR_VISIBLE;
	}

	/**
	 * Draws a given buildng to the context.
	 *
	 * @param context
	 * @param building
	 * @param color
	 *            Gray color shade
	 */
	private void drawBuilding(int x, int y, IBuilding building, float color) {
		EBuildingType type = building.getBuildingType();

		float state = building.getStateProgress();

		if (state >= 0.99) {
			if (type == EBuildingType.MILL
					&& ((IBuilding.IMill) building).isRotating()) {
				Sequence<? extends Image> seq =
						this.imageProvider.getSettlerSequence(MILL_FILE,
								MILL_SEQ);

				if (seq.length() > 0) {
					int i = getAnimationStep(x, y);
					int step = i % seq.length();
					draw(seq.getImageSafe(step), x, y, color);
				}
				playSound(building, 42);

			} else {
				ImageLink[] images = type.getImages();
				if (images.length > 0) {
					Image image = imageProvider.getImage(images[0]);
					draw(image, x, y, color);
				}

				if (building instanceof IBuilding.IOccupyed
						&& context.getVisibleStatus(x, y) > CommonConstants.FOG_OF_WAR_EXPLORED) {
					drawOccupiers(x, y, (IBuilding.IOccupyed) building, color);
				}

				for (int i = 1; i < images.length; i++) {
					Image image = imageProvider.getImage(images[i]);
					draw(image, x, y, color);
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
				draw(image, x, y, color);
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
	 *            The x coordinate of the building
	 * @param y
	 * @param building
	 *            The occupyed building
	 * @param basecolor
	 *            The base color (gray shade).
	 */
	private void drawOccupiers(int x, int y, IOccupyed building, float basecolor) {
		// this can cause a ConcurrentModificationException when
		// a soldier enters the tower!
		try {
			int height = context.getHeight(x, y);
			float towerX = context.getConverter().getViewX(x, y, height);
			float towerY = context.getConverter().getViewY(x, y, height);
			GLDrawContext gl = context.getGl();

			for (IBuildingOccupyer occupyer : building.getOccupyers()) {
				OccupyerPlace place = occupyer.getPlace();

				IMovable movable = occupyer.getMovable();
				Color color = context.getPlayerColor(movable.getPlayerId());

				Image image;
				switch (place.getSoldierClass()) {
				case INFANTRY:
					OriginalImageLink imageLink =
							place.looksRight() ? INSIDE_BUILDING_RIGHT
									: INSIDE_BUILDING_LEFT;
					image = imageProvider.getImage(imageLink);
					break;
				case BOWMAN:
				default:
					image =
							this.imageMap.getImageForSettler(movable,
									movable.getMoveProgress());
				}
				float viewX = towerX + place.getOffsetX();
				float viewY = towerY + place.getOffsetY();
				image.drawAt(gl, context.getDrawBuffer(), viewX, viewY, color,
						basecolor);

				if (place.getSoldierClass() == ESoldierClass.BOWMAN) {
					playMovableSound(movable);
					if (movable.isSelected()) {
						drawSelectionMark(viewX, viewY, movable.getHealth());
					}
				}
			}
		} catch (ConcurrentModificationException e) {
			// happens sometime, just ignore it.
		}
	}

	private void drawBuildingSelectMarker(int x, int y) {
		float z = context.getDrawBuffer().getZ();
		context.getDrawBuffer().setZ(BUILDING_SELECTION_MARKER_Z);

		Image image =
				imageProvider.getSettlerSequence(SELECTMARK_FILE,
						SELECTMARK_SEQUENCE).getImageSafe(0);
		draw(image, x, y, -1);

		context.getDrawBuffer().setZ(z);
	}

	private void drawWithConstructionMask(int x, int y, float maskState,
			Image unsafeimage, float color) {
		if (!(unsafeimage instanceof SingleImage)) {
			return; // should not happen
		}
		int height = context.getHeight(x, y);
		float viewX = context.getConverter().getViewX(x, y, height);
		float viewY = context.getConverter().getViewY(x, y, height);
		int iColor = Color.getABGR(color, color, color, 1);

		SingleImage image = (SingleImage) unsafeimage;
		// number of tiles in x direction, can be adjusted for performance
		int tiles = 6;

		float toplineBottom = 1 - maskState;
		float toplineTop = Math.max(0, toplineBottom - .1f);

		image.drawTriangle(context.getGl(), context.getDrawBuffer(), viewX,
				viewY, 0, 1, 1, 1, 0, toplineBottom, iColor);
		image.drawTriangle(context.getGl(), context.getDrawBuffer(), viewX,
				viewY, 1, 1, 1, toplineBottom, 0, toplineBottom, iColor);

		for (int i = 0; i < tiles; i++) {
			image.drawTriangle(context.getGl(), context.getDrawBuffer(), viewX,
					viewY, 1.0f / tiles * i, toplineBottom, 1.0f / tiles
							* (i + 1), toplineBottom, 1.0f / tiles * (i + .5f),
					toplineTop, iColor);
		}
	}

	private void drawPlayerableByProgress(int x, int y, int file,
			int sequenceIndex, IMapObject object, float basecolor) {
		Sequence<? extends Image> sequence =
				this.imageProvider.getSettlerSequence(file, sequenceIndex);
		int index =
				Math.min((int) (object.getStateProgress() * sequence.length()),
						sequence.length() - 1);
		Color color = getColor(object);
		draw(sequence.getImage(index), x, y, color, basecolor);
	}

	private Color getColor(IMapObject object) {
		Color color = null;
		if (object instanceof IPlayerable) {
			color =
					context.getPlayerColor(((IPlayerable) object).getPlayerId());
		}
		return color;
	}

	private void drawPlayerableWaving(int x, int y, int file,
			int sequenceIndex, IMapObject object, float basecolor) {
		Sequence<? extends Image> sequence =
				this.imageProvider.getSettlerSequence(file, sequenceIndex);
		int index = animationStep % sequence.length();
		Color color = getColor(object);
		draw(sequence.getImageSafe(index), x, y, color, basecolor);
	}

	private void drawByProgress(int x, int y, int file, int sequenceIndex,
			float progress, float color) {

		Sequence<? extends Image> sequence =
				this.imageProvider.getSettlerSequence(file, sequenceIndex);
		int index =
				Math.min((int) (progress * sequence.length()),
						sequence.length() - 1);
		draw(sequence.getImageSafe(index), x, y, color);
	}

	private void draw(Image image, int x, int y, Color color, float basecolor) {
		int height = context.getHeight(x, y);
		float viewX = context.getConverter().getViewX(x, y, height);
		float viewY = context.getConverter().getViewY(x, y, height);

		image.drawAt(context.getGl(), context.getDrawBuffer(), viewX, viewY,
				color, basecolor);
	}

	private void draw(Image image, int x, int y, float color) {
		int iColor = Color.getABGR(color, color, color, 1);
		draw(image, x, y, iColor);
	}

	private void draw(Image image, int x, int y, int color) {
		int height = context.getHeight(x, y);
		float viewX = context.getConverter().getViewX(x, y, height);
		float viewY = context.getConverter().getViewY(x, y, height);

		image.drawAt(context.getGl(), context.getDrawBuffer(), viewX, viewY,
				color);
	}

	public void drawMoveToMarker(ShortPoint2D moveToMarker, float progress) {
		forceSetup();

		drawByProgress(moveToMarker.x, moveToMarker.y, MARKER_FILE,
				MOVE_TO_MARKER_SEQUENCE, progress, 1);
	}
}
