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
package jsettlers.logic.map.grid.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.PriorityQueue;

import jsettlers.common.landscape.EResourceType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IAttackableTowerMapObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
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
import jsettlers.logic.stack.IStackSizeSupplier;
import jsettlers.logic.timer.IScheduledTimerable;
import jsettlers.logic.timer.RescheduleTimer;
import jsettlers.network.synchronic.random.RandomSingleton;

/**
 * This class manages the MapObjects on the grid. It handles timed events like growth interrupts of a tree or deletion of arrows.
 * 
 * @author Andreas Eberle
 * 
 */
public final class MapObjectsManager implements IScheduledTimerable, Serializable {
	private static final long serialVersionUID = 1833055351956872224L;

	private final IMapObjectsManagerGrid grid;
	private final PriorityQueue<TimeEvent> timingQueue = new PriorityQueue<TimeEvent>();

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

		int gameTime = MatchConstants.clock.getTime();

		TimeEvent curr = null;
		curr = timingQueue.peek();
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
		EResourceType resourceType = grid.getRessourceTypeAt(pos.x, pos.y);
		byte resourceAmount = grid.getRessourceAmountAt(pos.x, pos.y);

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
				addSelfDeletingMapObject(pos, EMapObjectType.CUT_OFF_STONE, Stone.DECOMPOSE_DELAY, null);
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

	private final boolean addMapObject(ShortPoint2D pos, AbstractHexMapObject mapObject) {
		return addMapObject(pos.x, pos.y, mapObject);
	}

	private final boolean addMapObject(int x, int y, AbstractHexMapObject mapObject) {
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
		addMapObject(pos, new Stone(capacity));
	}

	public void plantAdultTree(ShortPoint2D pos) {
		addMapObject(pos, new AdultTree(pos));
	}

	/**
	 * Adds an arrow object to the map flying from
	 * 
	 * @param attackedPos
	 *            Attacked position.
	 * @param shooterPos
	 *            Position of the shooter.
	 * @param shooterPlayer
	 *            The player of the shooter.
	 * @param hitStrength
	 *            Strength of the hit.
	 */
	public void addArrowObject(ShortPoint2D attackedPos, ShortPoint2D shooterPos, byte shooterPlayerId, float hitStrength) {
		ArrowObject arrow = new ArrowObject(grid, attackedPos, shooterPos, shooterPlayerId, hitStrength);
		addMapObject(attackedPos, arrow);
		schedule(arrow, arrow.getEndTime(), false);
		schedule(arrow, arrow.getEndTime() + ArrowObject.MIN_DECOMPOSE_DELAY * (1 + RandomSingleton.nextF()), true);
	}

	public void addSimpleMapObject(ShortPoint2D pos, EMapObjectType objectType, boolean blocking, Player player) {
		addMapObject(pos, new StandardMapObject(objectType, blocking, player != null ? player.playerId : -1));
	}

	public void addBuildingWorkAreaObject(ShortPoint2D pos, float radius) {
		addMapObject(pos, new BuildingWorkAreaMarkObject(radius));
	}

	public void addWineBowl(ShortPoint2D pos, IStackSizeSupplier wineStack) {
		addMapObject(pos, new WineBowlMapObject(wineStack));
	}

	public void addSelfDeletingMapObject(ShortPoint2D pos, EMapObjectType mapObjectType, float duration, Player player) {
		SelfDeletingMapObject object;
		byte playerId = player != null ? player.playerId : -1;

		switch (mapObjectType) {
		case GHOST:
		case BUILDING_DECONSTRUCTION_SMOKE:
			object = new SoundableSelfDeletingObject(pos, mapObjectType, duration, playerId);
			break;
		default:
			object = new SelfDeletingMapObject(pos, mapObjectType, duration, playerId);
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

	private final static StackMapObject getNextStackObject(StackMapObject stack) {
		AbstractHexMapObject next = stack.getNextObject();
		if (next != null) {
			return (StackMapObject) next.getMapObject(EMapObjectType.STACK_OBJECT);
		} else {
			return null;
		}
	}

	public boolean pushMaterial(short x, short y, EMaterialType materialType) {
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

	public final EMaterialType getMaterialTypeAt(short x, short y) {
		StackMapObject stackObject = (StackMapObject) grid.getMapObject(x, y, EMapObjectType.STACK_OBJECT);

		if (stackObject == null) {
			return null;
		} else {
			return stackObject.getMaterialType();
		}
	}

	private final void removeStackObject(short x, short y, StackMapObject stackObject) {
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

	private StackMapObject getStackAtPosition(short x, short y, EMaterialType materialType) {
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

	public boolean feedDonkeyAt(ShortPoint2D position, byte playerId) {
		AbstractHexMapObject object = grid.getMapObject(position.x, position.y, EMapObjectType.DONKEY);
		DonkeyMapObject donkey;
		boolean result;
		if (object != null) {
			donkey = (DonkeyMapObject) object;
			result = donkey.feed();
		} else {
			donkey = new DonkeyMapObject(position, playerId);
			addMapObject(position, donkey);
			result = true;
		}

		if (donkey.isFullyFed()) {
			// release it to the world.
			grid.spawnDonkey(position, playerId);
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
		 * 
		 * @param mapObject
		 * @param duration
		 *            in seconds
		 * @param shouldRemove
		 *            if true, the map object will be removed after this event
		 */
		protected TimeEvent(AbstractObjectsManagerObject mapObject, float duration, boolean shouldRemove) {
			this.mapObject = mapObject;
			this.shouldRemove = shouldRemove;
			this.eventTime = (int) (MatchConstants.clock.getTime() + duration * 1000);
		}

		public boolean isOutDated(int gameTime) {
			return gameTime > eventTime;
		}

		private AbstractObjectsManagerObject getMapObject() {
			return mapObject;
		}

		public boolean shouldRemoveObject() {
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
	 *            Position the map object will be added.
	 * @param attackableTowerMapObject
	 *            The object to be added. NOTE: This object must be an instance of {@link IAttackableTowerMapObject}!
	 */
	public void addAttackableTowerObject(ShortPoint2D position, AbstractHexMapObject attackableTowerMapObject) {
		assert attackableTowerMapObject instanceof IAttackableTowerMapObject;

		this.addMapObject(position, attackableTowerMapObject);
	}

	/**
	 * Adds a map object that informs the given {@link IInformable} about attackable enemies in the area.
	 * 
	 * @param position
	 *            The position the object should be added.
	 * @param informable
	 *            The {@link IInformable} that will be informed of enemies.
	 */
	public void addInformableMapObjectAt(ShortPoint2D position, IInformable informable) {
		this.addMapObject(position, new InformableMapObject(informable));
	}

}
