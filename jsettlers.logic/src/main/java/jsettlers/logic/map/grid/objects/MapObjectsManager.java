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
package jsettlers.logic.map.grid.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.PriorityQueue;

import jsettlers.common.landscape.EResourceType;
import jsettlers.common.map.shapes.HexGridArea;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IAttackableTowerMapObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.player.IPlayer;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.stack.IStackSizeSupplier;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.interfaces.IInformable;
import jsettlers.logic.objects.DonkeyMapObject;
import jsettlers.logic.objects.PigObject;
import jsettlers.logic.objects.RessourceSignMapObject;
import jsettlers.logic.objects.SelfDeletingMapObject;
import jsettlers.logic.objects.SoundableSelfDeletingObject;
import jsettlers.logic.objects.StandardMapObject;
import jsettlers.logic.objects.WineBowlMapObject;
import jsettlers.logic.objects.arrow.ArrowObject;
import jsettlers.logic.objects.building.BuildingWorkAreaMarkObject;
import jsettlers.logic.objects.building.ConstructionMarkObject;
import jsettlers.logic.objects.building.InformableMapObject;
import jsettlers.logic.objects.growing.Corn;
import jsettlers.logic.objects.growing.Wine;
import jsettlers.logic.objects.growing.tree.AdultTree;
import jsettlers.logic.objects.growing.tree.Tree;
import jsettlers.logic.objects.stack.StackMapObject;
import jsettlers.logic.objects.stone.Stone;
import jsettlers.logic.player.Player;
import jsettlers.logic.timer.IScheduledTimerable;
import jsettlers.logic.timer.RescheduleTimer;

import java8.util.Optional;

/**
 * This class manages the MapObjects on the grid. It handles timed events like growth interrupts of a tree or deletion of arrows.
 *
 * @author Andreas Eberle
 */
public final class MapObjectsManager implements IScheduledTimerable, Serializable {
	private static final long serialVersionUID = 1833055351956872224L;

	private final IMapObjectsManagerGrid grid;
	private final PriorityQueue<TimeEvent> timingQueue = new PriorityQueue<>();

	private boolean killed = false;

	public MapObjectsManager(IMapObjectsManagerGrid grid) {
		this.grid = grid;
		RescheduleTimer.add(this, 100);
	}

	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
	}

	@Override
	public int timerEvent() {
		if (killed) {
			return -1;
		}

		int gameTime = MatchConstants.clock().getTime();

		TimeEvent curr = timingQueue.peek();
		while (curr != null && curr.isOutDated(gameTime)) {
			timingQueue.poll();
			if (curr.shouldRemoveObject()) {
				removeMapObject(curr.mapObject.getX(), curr.mapObject.getY(), curr.mapObject);
			} else {
				curr.getMapObject().changeState();
			}

			curr = timingQueue.peek();
		}

		return 100;
	}

	@Override
	public void kill() {
		killed = true;
	}

	public boolean executeSearchType(ShortPoint2D pos, ESearchType type) {
		switch (type) {
		case PLANTABLE_TREE:
			return plantTree(new ShortPoint2D(pos.x, pos.y + 1));
		case CUTTABLE_TREE:
			return cutTree(pos);

		case CUTTABLE_STONE:
			cutStone(pos);
			return true;

		case PLANTABLE_CORN:
			return plantCorn(pos);
		case CUTTABLE_CORN:
			return cutCorn(pos);

		case PLANTABLE_WINE:
			return plantWine(pos);
		case HARVESTABLE_WINE:
			return harvestWine(pos);

		case RESOURCE_SIGNABLE:
			return addRessourceSign(pos);

		default:
			System.err.println("ERROR: Can't handle search type in executeSearchType(): " + type);
			return false;
		}
	}

	private boolean addRessourceSign(ShortPoint2D pos) {
		EResourceType resourceType = grid.getResourceTypeAt(pos.x, pos.y);
		byte resourceAmount = grid.getResourceAmountAt(pos.x, pos.y);

		RessourceSignMapObject object = new RessourceSignMapObject(pos, resourceType, resourceAmount
				/ ((float) Constants.MAX_RESOURCE_AMOUNT_PER_POSITION));
		addMapObject(pos, object);
		timingQueue.add(new TimeEvent(object, RessourceSignMapObject.getLivetime(), true));

		return true;
	}

	private void cutStone(ShortPoint2D pos) {
		short x = (short) (pos.x - 1);
		short y = (short) (pos.y + 1);
		AbstractHexMapObject stone = grid.getMapObject(x, y, EMapObjectType.STONE);

		if (stone != null) {
			stone.cutOff();

			if (!stone.canBeCut()) {
				addSelfDeletingMapObject(new ShortPoint2D(x, y), EMapObjectType.CUT_OFF_STONE, Stone.DECOMPOSE_DELAY, null);
				removeMapObjectType(x, y, EMapObjectType.STONE);
			}
		}
	}

	private boolean plantTree(ShortPoint2D pos) {
		Tree tree = new Tree(pos);
		addMapObject(pos, tree);
		schedule(tree, Tree.GROWTH_DURATION, false);
		return true;
	}

	private boolean cutTree(ShortPoint2D pos) {
		short x = (short) (pos.x - 1);
		short y = (short) (pos.y - 1);
		if (grid.isInBounds(x, y)) {
			AbstractObjectsManagerObject tree = (AbstractObjectsManagerObject) grid.getMapObject(x, y, EMapObjectType.TREE_ADULT);
			if (tree != null && tree.cutOff()) {
				schedule(tree, Tree.DECOMPOSE_DURATION, true);
				return true;
			}
		}
		return false;
	}

	private boolean plantCorn(ShortPoint2D pos) {
		Corn corn = new Corn(pos);
		addMapObject(pos, corn);
		schedule(corn, Corn.GROWTH_DURATION, false);
		schedule(corn, Corn.GROWTH_DURATION + Corn.DECOMPOSE_DURATION, false);
		schedule(corn, Corn.GROWTH_DURATION + Corn.DECOMPOSE_DURATION + Corn.REMOVE_DURATION, true);
		return true;
	}

	private boolean cutCorn(ShortPoint2D pos) {
		short x = pos.x;
		short y = pos.y;
		if (grid.isInBounds(x, y)) {
			AbstractObjectsManagerObject corn = (AbstractObjectsManagerObject) grid.getMapObject(x, y, EMapObjectType.CORN_ADULT);
			if (corn != null && corn.cutOff()) {
				schedule(corn, Corn.REMOVE_DURATION, true);
				return true;
			}
		}
		return false;
	}

	private boolean plantWine(ShortPoint2D pos) {
		Wine wine = new Wine(pos);
		addMapObject(pos, wine);
		schedule(wine, Wine.GROWTH_DURATION, false);
		schedule(wine, Wine.GROWTH_DURATION + Wine.DECOMPOSE_DURATION, false);
		schedule(wine, Wine.GROWTH_DURATION + Wine.DECOMPOSE_DURATION + Wine.REMOVE_DURATION, true);
		return true;
	}

	private boolean harvestWine(ShortPoint2D pos) {
		short x = pos.x;
		short y = pos.y;
		if (grid.isInBounds(x, y)) {
			AbstractObjectsManagerObject wine = (AbstractObjectsManagerObject) grid.getMapObject(x, y, EMapObjectType.WINE_HARVESTABLE);
			if (wine != null && wine.cutOff()) {
				schedule(wine, Wine.REMOVE_DURATION, true);
				return true;
			}
		}
		return false;
	}

	public boolean addMapObject(ShortPoint2D pos, AbstractHexMapObject mapObject) {
		return addMapObject(pos.x, pos.y, mapObject);
	}

	private boolean addMapObject(int x, int y, AbstractHexMapObject mapObject) {
		for (RelativePoint point : mapObject.getBlockedTiles()) {
			int currX = point.calculateX(x);
			int currY = point.calculateY(y);
			if (!grid.isInBounds(currX, currY) || grid.isBlocked(currX, currY)) {
				return false;
			}
		}

		grid.addMapObject(x, y, mapObject);
		mapObject.handlePlacement(x, y, this, grid);
		return true;
	}

	public void removeMapObjectType(int x, int y, EMapObjectType mapObjectType) {
		removeMapObject(x, y, grid.getMapObject(x, y, mapObjectType));
	}

	public void removeMapObject(int x, int y, AbstractHexMapObject mapObject) {
		boolean removed = grid.removeMapObject(x, y, mapObject);

		if (removed) {
			mapObject.handleRemove(x, y, this, grid);
		}
	}

	public void addStone(ShortPoint2D pos, int capacity) {
		if (capacity > 0) {
			addMapObject(pos, new Stone(capacity));
		} else {
			addSelfDeletingMapObject(pos, EMapObjectType.CUT_OFF_STONE, Stone.DECOMPOSE_DELAY, null);
		}
	}

	public void plantAdultTree(ShortPoint2D pos) {
		addMapObject(pos, new AdultTree(pos));
	}

	/**
	 * Adds an arrow object to the map flying from
	 *
	 * @param attackedPos
	 * 		Attacked position.
	 * @param shooterPos
	 * 		Position of the shooter.
	 * @param shooterPlayerId
	 * 		The player of the shooter.
	 * @param hitStrength
	 * 		Strength of the hit.
	 */
	public void addArrowObject(ShortPoint2D attackedPos, ShortPoint2D shooterPos, byte shooterPlayerId, float hitStrength) {
		ArrowObject arrow = new ArrowObject(grid, attackedPos, shooterPos, shooterPlayerId, hitStrength);
		addMapObject(attackedPos, arrow);
		schedule(arrow, arrow.getEndTime(), false);
		schedule(arrow, arrow.getEndTime() + ArrowObject.MIN_DECOMPOSE_DELAY * (1 + MatchConstants.random().nextFloat()), true);
	}

	public void addSimpleMapObject(ShortPoint2D pos, EMapObjectType objectType, boolean blocking, Player player) {
		addMapObject(pos, new StandardMapObject(objectType, blocking, player));
	}

	public void addBuildingWorkAreaObject(int x, int y, float radius) {
		addMapObject(x, y, new BuildingWorkAreaMarkObject(radius));
	}

	public void addWineBowl(ShortPoint2D pos, IStackSizeSupplier wineStack) {
		addMapObject(pos, new WineBowlMapObject(wineStack));
	}

	public void addSelfDeletingMapObject(ShortPoint2D pos, EMapObjectType mapObjectType, float duration, IPlayer player) {
		SelfDeletingMapObject object;

		switch (mapObjectType) {
		case GHOST:
		case BUILDING_DECONSTRUCTION_SMOKE:
			object = new SoundableSelfDeletingObject(pos, mapObjectType, duration, player);
			break;
		default:
			object = new SelfDeletingMapObject(pos, mapObjectType, duration, player);
			break;
		}
		addMapObject(pos, object);
		timingQueue.add(new TimeEvent(object, duration, true));
	}

	public void setConstructionMarking(int x, int y, byte value) {
		if (value >= 0) {
			ConstructionMarkObject markObject = (ConstructionMarkObject) grid.getMapObject(x, y, EMapObjectType.CONSTRUCTION_MARK);
			if (markObject == null) {
				addMapObject(x, y, new ConstructionMarkObject(value));
			} else {
				markObject.setConstructionValue(value);
			}
		} else {
			removeMapObjectType(x, y, EMapObjectType.CONSTRUCTION_MARK);
		}
	}

	public boolean canPush(ShortPoint2D position) {
		StackMapObject stackObject = (StackMapObject) grid.getMapObject(position.x, position.y, EMapObjectType.STACK_OBJECT);
		int sum = 0;

		while (stackObject != null) { // find correct stack
			sum += stackObject.getSize();

			stackObject = getNextStackObject(stackObject);
		}

		return sum < Constants.STACK_SIZE;
	}

	public final boolean canPop(short x, short y, EMaterialType materialType) {
		StackMapObject stackObject = getStackAtPosition(x, y, materialType);

		return stackObject != null && (stackObject.getMaterialType() == materialType || materialType == null) && !stackObject.isEmpty();
	}

	public final void markStolen(short x, short y, boolean mark) {
		if (mark) {
			StackMapObject stack = getStackAtPosition(x, y, null);
			while (stack != null) {
				if (stack.hasUnstolen()) {
					stack.incrementStolenMarks();
					break;
				}

				stack = getNextStackObject(stack);
			}
		} else {
			StackMapObject stack = getStackAtPosition(x, y, null);
			while (stack != null) {
				if (stack.hasStolenMarks()) {
					stack.decrementStolenMarks();
					break;
				}

				stack = getNextStackObject(stack);
			}
		}
	}

	private static StackMapObject getNextStackObject(StackMapObject stack) {
		AbstractHexMapObject next = stack.getNextObject();
		if (next != null) {
			return (StackMapObject) next.getMapObject(EMapObjectType.STACK_OBJECT);
		} else {
			return null;
		}
	}

	public boolean pushMaterial(int x, int y, EMaterialType materialType) {
		assert materialType != null : "material type can never be null here";

		StackMapObject stackObject = getStackAtPosition(x, y, materialType);

		if (stackObject == null) {
			grid.addMapObject(x, y, new StackMapObject(materialType, (byte) 1));
			grid.setProtected(x, y, true);
			return true;
		} else {
			if (stackObject.isFull()) {
				return false;
			} else {
				stackObject.increment();
				return true;
			}
		}
	}

	public ShortPoint2D pushMaterialForced(int x, int y, EMaterialType materialType) {
		return HexGridArea.stream(x, y, 0, 200)
				.filterBounds(grid.getWidth(), grid.getHeight())
				.filter((currX, currY) -> canForcePushMaterial(currX, currY, materialType))
				.iterateForResult((currX, currY) -> {
					pushMaterial(currX, currY, materialType);
					return Optional.of(new ShortPoint2D(currX, currY));
				}).orElse(null);
	}

	/**
	 * Checks if there is no stack of another material at the given location and there is space on a stack of this material type.
	 *
	 * @param x
	 * @param y
	 * @param materialType
	 * @return
	 */
	private boolean canForcePushMaterial(int x, int y, EMaterialType materialType) {
		if (grid.isBlocked(x, y)) {
			return false;
		}

		byte size = 0;

		StackMapObject stackObject = (StackMapObject) grid.getMapObject(x, y, EMapObjectType.STACK_OBJECT);
		while (stackObject != null) {
			if (stackObject.getMaterialType() != materialType) {
				return false;
			} else {
				size += stackObject.getSize();
			}
			stackObject = getNextStackObject(stackObject);
		}

		if (size == 0) {
			return !grid.isProtected(x, y); // if there is no stack yet, don't create a stack on protected locations
		} else {
			return size < Constants.STACK_SIZE; // if there is a stack of this material already, check if it is not full
		}
	}

	public final boolean popMaterial(short x, short y, EMaterialType materialType) {
		return popMaterialTypeAt(x, y, materialType) != null;
	}

	private EMaterialType popMaterialTypeAt(short x, short y, EMaterialType materialType) {
		StackMapObject stackObject = getStackAtPosition(x, y, materialType);

		if (stackObject == null) {
			return null;
		} else {
			if (stackObject.isEmpty()) {
				removeStackObject(x, y, stackObject);
				return null;
			} else {
				stackObject.decrement();
				if (stackObject.isEmpty()) { // remove empty stack object
					removeStackObject(x, y, stackObject);
				}
				return stackObject.getMaterialType();
			}
		}
	}

	private void removeStackObject(short x, short y, StackMapObject stackObject) {
		removeMapObject(x, y, stackObject);
		if (!grid.isBuildingAreaAt(x, y) && grid.getMapObject(x, y, EMapObjectType.STACK_OBJECT) == null) {
			grid.setProtected(x, y, false); // no other stack, so remove protected
		}
	}

	public final byte getStackSize(short x, short y, EMaterialType materialType) {
		StackMapObject stackObject = getStackAtPosition(x, y, materialType);
		if (stackObject == null) {
			return 0;
		} else {
			return stackObject.getSize();
		}
	}

	public final boolean hasStealableMaterial(int x, int y) {
		StackMapObject stackObject = (StackMapObject) grid.getMapObject(x, y, EMapObjectType.STACK_OBJECT);

		while (stackObject != null) { // find all stacks
			if (stackObject.hasUnstolen()) {
				return true;
			}

			stackObject = getNextStackObject(stackObject);
		}

		return false;
	}

	public final EMaterialType stealMaterialAt(short x, short y) {
		return popMaterialTypeAt(x, y, null);
	}

	private StackMapObject getStackAtPosition(int x, int y, EMaterialType materialType) {
		StackMapObject stackObject = (StackMapObject) grid.getMapObject(x, y, EMapObjectType.STACK_OBJECT);

		while (stackObject != null && stackObject.getMaterialType() != materialType && materialType != null) { // find correct stack
			stackObject = getNextStackObject(stackObject);
		}
		return stackObject;
	}

	public void addBuildingTo(ShortPoint2D position, AbstractHexMapObject newBuilding) {
		addMapObject(position, newBuilding);
	}

	public void placePig(ShortPoint2D pos, boolean place) {
		if (place) {
			AbstractHexMapObject pig = grid.getMapObject(pos.x, pos.y, EMapObjectType.PIG);
			if (pig == null) {
				addMapObject(pos, new PigObject());
			}
		} else {
			removeMapObjectType(pos.x, pos.y, EMapObjectType.PIG);
		}
	}

	public boolean isPigThere(ShortPoint2D pos) {
		AbstractHexMapObject pig = grid.getMapObject(pos.x, pos.y, EMapObjectType.PIG);
		return pig != null;
	}

	public boolean isPigAdult(ShortPoint2D pos) {
		AbstractHexMapObject pig = grid.getMapObject(pos.x, pos.y, EMapObjectType.PIG);
		return pig != null && pig.canBeCut();
	}

	public boolean feedDonkeyAt(ShortPoint2D position, Player player) {
		AbstractHexMapObject object = grid.getMapObject(position.x, position.y, EMapObjectType.DONKEY);
		DonkeyMapObject donkey;
		boolean result;
		if (object != null) {
			donkey = (DonkeyMapObject) object;
			result = donkey.feed();
		} else {
			donkey = new DonkeyMapObject(position, player);
			addMapObject(position, donkey);
			result = true;
		}

		if (donkey.isFullyFed()) {
			// release it to the world.
			grid.spawnDonkey(position, player);
			removeMapObjectType(position.x, position.y, EMapObjectType.DONKEY);
		} else {
			timingQueue.add(new TimeEvent(donkey, DonkeyMapObject.FEED_TIME, false));
		}
		return result;
	}

	public void addWaves(short x, short y) {
		grid.addMapObject(x, y, new DecorationMapObject(EMapObjectType.WAVES));
	}

	public void addFish(short x, short y) {
		grid.addMapObject(x, y, new DecorationMapObject(EMapObjectType.FISH_DECORATION));
	}

	private static class TimeEvent implements Comparable<TimeEvent>, Serializable {
		private static final long serialVersionUID = -4439126418530597713L;

		private final AbstractObjectsManagerObject mapObject;
		private final int eventTime;
		private final boolean shouldRemove;

		/**
		 * @param mapObject
		 * 		map object
		 * @param duration
		 * 		in seconds
		 * @param shouldRemove
		 * 		if true, the map object will be removed after this event
		 */
		TimeEvent(AbstractObjectsManagerObject mapObject, float duration, boolean shouldRemove) {
			this.mapObject = mapObject;
			this.shouldRemove = shouldRemove;
			this.eventTime = (int) (MatchConstants.clock().getTime() + duration * 1000);
		}

		boolean isOutDated(int gameTime) {
			return gameTime > eventTime;
		}

		private AbstractObjectsManagerObject getMapObject() {
			return mapObject;
		}

		boolean shouldRemoveObject() {
			return shouldRemove;
		}

		@Override
		public int compareTo(TimeEvent o) {
			return this.eventTime - o.eventTime;
		}
	}

	private boolean schedule(AbstractObjectsManagerObject object, float duration, boolean remove) {
		return timingQueue.offer(new TimeEvent(object, duration, remove));
	}

	/**
	 * Adds an attackable tower map object to the grid.
	 *
	 * @param position
	 * 		Position the map object will be added.
	 * @param attackableTowerMapObject
	 * 		The object to be added. NOTE: This object must be an instance of {@link IAttackableTowerMapObject}!
	 */
	public void addAttackableTowerObject(ShortPoint2D position, AbstractHexMapObject attackableTowerMapObject) {
		assert attackableTowerMapObject instanceof IAttackableTowerMapObject;
		this.addMapObject(position, attackableTowerMapObject);
	}

	/**
	 * Adds a map object that informs the given {@link IInformable} about attackable enemies in the area.
	 *
	 * @param position
	 * 		The position the object should be added.
	 * @param informable
	 * 		The {@link IInformable} that will be informed of enemies.
	 */
	public void addInformableMapObjectAt(ShortPoint2D position, IInformable informable) {
		this.addMapObject(position, new InformableMapObject(informable));
	}
}
