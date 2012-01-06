package jsettlers.logic.map.newGrid.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.PriorityQueue;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.shapes.MapNeighboursArea;
import jsettlers.common.map.shapes.MapShapeFilter;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.newGrid.landscape.EResourceType;
import jsettlers.logic.map.newGrid.movable.IHexMovable;
import jsettlers.logic.objects.PigObject;
import jsettlers.logic.objects.RessourceSignMapObject;
import jsettlers.logic.objects.SelfDeletingMapObject;
import jsettlers.logic.objects.StandardMapObject;
import jsettlers.logic.objects.arrow.ArrowObject;
import jsettlers.logic.objects.building.BuildingWorkAreaMarkObject;
import jsettlers.logic.objects.building.ConstructionMarkObject;
import jsettlers.logic.objects.corn.Corn;
import jsettlers.logic.objects.stack.StackMapObject;
import jsettlers.logic.objects.stone.Stone;
import jsettlers.logic.objects.tree.AdultTree;
import jsettlers.logic.objects.tree.Tree;
import jsettlers.logic.timer.ITimerable;
import jsettlers.logic.timer.Timer100Milli;
import synchronic.timer.NetworkTimer;

/**
 * This class manages the MapObjects on the grid. It handles timed events like growth interrupts of a tree or deletion of arrows.
 * 
 * @author Andreas Eberle
 * 
 */
public class MapObjectsManager implements ITimerable, Serializable {
	private static final long serialVersionUID = 1833055351956872224L;

	private final IMapObjectsManagerGrid grid;
	private final PriorityQueue<TimeEvent> timingQueue = new PriorityQueue<TimeEvent>();

	public MapObjectsManager(IMapObjectsManagerGrid grid) {
		this.grid = grid;
		Timer100Milli.add(this);
	}

	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		Timer100Milli.add(this);
	}

	@Override
	public void timerEvent() {
		int gameTime = NetworkTimer.getGameTime();

		TimeEvent curr = null;
		curr = timingQueue.peek();
		while (curr != null && curr.isOutDated(gameTime)) {
			timingQueue.poll();
			if (curr.shouldRemoveObject()) {
				ISPosition2D pos = curr.mapObject.getPos();
				removeMapObject(pos.getX(), pos.getY(), curr.mapObject);
			} else {
				curr.getMapObject().changeState();
			}

			curr = timingQueue.peek();
		}

	}

	@Override
	public void kill() {
		Timer100Milli.remove(this);
	}

	public boolean executeSearchType(ISPosition2D pos, ESearchType type) {
		switch (type) {
		case CUTTABLE_TREE:
			return cutTree(pos);

		case CUTTABLE_STONE:
			cutStone(pos);
			return true;

		case PLANTABLE_TREE:
			return plantTree(new ShortPoint2D(pos.getX(), pos.getY() + 1));

		case CUTTABLE_CORN:
			return cutCorn(pos);

		case PLANTABLE_CORN:
			return plantCorn(pos);

		default:
			System.err.println("can't handle search type in executeSearchType(): " + type);
			return false;
		}
	}

	private void cutStone(ISPosition2D pos) {
		short x = (short) (pos.getX() - 1);
		short y = (short) (pos.getY() + 1);
		AbstractHexMapObject stone = grid.getMapObject(x, y, EMapObjectType.STONE);
		stone.cutOff();

		if (!stone.canBeCut()) {
			addSelfDeletingMapObject(pos, EMapObjectType.CUT_OFF_STONE, Stone.DECOMPOSE_DELAY, (byte) -1);
			removeMapObjectType(x, y, EMapObjectType.STONE);
		}
	}

	private boolean plantTree(ISPosition2D pos) {
		Tree tree = new Tree(pos);
		addMapObject(pos, tree);
		timingQueue.offer(new TimeEvent(tree, Tree.GROWTH_DURATION, false));
		return true;
	}

	private boolean plantCorn(ISPosition2D pos) {
		grid.setLandscape(pos.getX(), pos.getY(), ELandscapeType.EARTH);
		for (ISPosition2D curr : new MapShapeFilter(new MapNeighboursArea(pos), grid.getWidth(), grid.getHeight())) {
			grid.setLandscape(curr.getX(), curr.getY(), ELandscapeType.EARTH);
		}
		Corn corn = new Corn(pos);
		addMapObject(pos, corn);
		timingQueue.offer(new TimeEvent(corn, Corn.GROWTH_DURATION, false));
		timingQueue.offer(new TimeEvent(corn, Corn.GROWTH_DURATION + Corn.DECOMPOSE_DURATION, false));
		timingQueue.offer(new TimeEvent(corn, Corn.GROWTH_DURATION + Corn.DECOMPOSE_DURATION + Corn.REMOVE_DURATION, true));
		return true;
	}

	private boolean cutCorn(ISPosition2D pos) {
		short x = pos.getX();
		short y = pos.getY();
		if (grid.isInBounds(x, y)) {
			AbstractObjectsManagerObject corn = (AbstractObjectsManagerObject) grid.getMapObject(x, y, EMapObjectType.CORN_ADULT);
			if (corn.cutOff()) {
				timingQueue.offer(new TimeEvent(corn, Corn.REMOVE_DURATION, true));
				return true;
			}
		}
		return false;
	}

	private boolean cutTree(ISPosition2D pos) {
		short x = (short) (pos.getX() - 1);
		short y = (short) (pos.getY() - 1);
		if (grid.isInBounds(x, y)) {
			AbstractObjectsManagerObject tree = (AbstractObjectsManagerObject) grid.getMapObject(x, y, EMapObjectType.TREE_ADULT);
			if (tree.cutOff()) {
				timingQueue.offer(new TimeEvent(tree, Tree.DECOMPOSE_DURATION, true));
				return true;
			}
		}
		return false;
	}

	private boolean addMapObject(ISPosition2D pos, AbstractHexMapObject mapObject) {
		for (RelativePoint point : mapObject.getBlockedTiles()) {
			short x = point.calculateX(pos.getX());
			short y = point.calculateY(pos.getY());
			if (!grid.isInBounds(x, y) || grid.isBlocked(x, y)) {
				return false;
			}
		}

		grid.addMapObject(pos.getX(), pos.getY(), mapObject);

		setBlockedForObject(pos.getX(), pos.getY(), mapObject, true);
		return true;
	}

	public void removeMapObjectType(short x, short y, EMapObjectType mapObjectType) {
		AbstractHexMapObject removed = grid.removeMapObjectType(x, y, mapObjectType);

		if (removed != null) {
			setBlockedForObject(x, y, removed, false);
		}
	}

	public void removeMapObject(short x, short y, AbstractHexMapObject mapObject) {
		boolean removed = grid.removeMapObject(x, y, mapObject);

		if (removed) {
			setBlockedForObject(x, y, mapObject, false);
		}
	}

	private void setBlockedForObject(short oldX, short oldY, AbstractHexMapObject mapObject, boolean blocked) {
		for (RelativePoint point : mapObject.getBlockedTiles()) {
			short newX = point.calculateX(oldX);
			short newY = point.calculateY(oldY);
			if (grid.isInBounds(newX, newY)) {
				grid.setBlocked(newX, newY, blocked);
			}
		}
	}

	public void addStone(ISPosition2D pos, int capacity) {
		addMapObject(pos, new Stone(capacity));
	}

	public void plantAdultTree(ISPosition2D pos) {
		addMapObject(pos, new AdultTree(pos));
	}

	public void addArrowObject(IHexMovable enemyPos, ISPosition2D ownPos, float strength) {
		ArrowObject arrow = new ArrowObject(enemyPos, ownPos, strength);
		addMapObject(enemyPos.getPos(), arrow);
		timingQueue.offer(new TimeEvent(arrow, arrow.getEndTime(), false));
		timingQueue.offer(new TimeEvent(arrow, arrow.getEndTime() + ArrowObject.DECOMPOSE_DELAY, true));
	}

	public void addSimpleMapObject(ISPosition2D pos, EMapObjectType objectType, boolean blocking, byte player) {
		addMapObject(pos, new StandardMapObject(objectType, blocking, player));
	}

	public void addBuildingWorkAreaObject(ISPosition2D pos, float progress) {
		addMapObject(pos, new BuildingWorkAreaMarkObject(progress));
	}

	public void addSelfDeletingMapObject(ISPosition2D pos, EMapObjectType mapObjectType, float duration, byte player) {
		SelfDeletingMapObject object = new SelfDeletingMapObject(pos, mapObjectType, duration, player);
		addMapObject(pos, object);
		timingQueue.add(new TimeEvent(object, duration, true));
	}

	public void addRessourceSign(ISPosition2D pos, EResourceType resourceType, float amount) {
		RessourceSignMapObject object = new RessourceSignMapObject(pos, resourceType, amount);
		addMapObject(pos, object);
		timingQueue.add(new TimeEvent(object, RessourceSignMapObject.getLivetime(), true));
	}

	public void setConstructionMarking(ISPosition2D pos, byte value) {
		if (value >= 0) {
			ConstructionMarkObject markObject = (ConstructionMarkObject) grid.getMapObject(pos.getX(), pos.getY(), EMapObjectType.CONSTRUCTION_MARK);
			if (markObject == null) {
				addMapObject(pos, new ConstructionMarkObject(value));
			} else {
				markObject.setConstructionValue(value);
			}
		} else {
			removeMapObjectType(pos.getX(), pos.getY(), EMapObjectType.CONSTRUCTION_MARK);
		}
	}

	public boolean canPush(ISPosition2D position) {
		StackMapObject stackObject = (StackMapObject) grid.getMapObject(position.getX(), position.getY(), EMapObjectType.STACK_OBJECT);
		int sum = 0;

		while (stackObject != null) { // find correct stack
			sum += stackObject.getSize();
			AbstractHexMapObject object = stackObject.getNextObject();
			if (object != null) {
				stackObject = (StackMapObject) object.getMapObject(EMapObjectType.STACK_OBJECT);
			} else {
				stackObject = null;
			}
		}

		return sum < Constants.STACK_SIZE;
	}

	public final boolean canPop(short x, short y, EMaterialType materialType) {
		StackMapObject stackObject = getStackAtPosition(x, y, materialType);

		return stackObject != null && (stackObject.getMaterialType() == materialType || materialType == null) && !stackObject.isEmpty();
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
		if (grid.getMapObject(x, y, EMapObjectType.STACK_OBJECT) == null) {
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

	public final boolean hasStealableMaterial(short x, short y) {
		StackMapObject stackObject = (StackMapObject) grid.getMapObject(x, y, EMapObjectType.STACK_OBJECT);

		while (stackObject != null) { // find all stacks
			if (stackObject.hasUnstolen()) {
				return true;
			}

			stackObject = (StackMapObject) grid.getMapObject(x, y, EMapObjectType.STACK_OBJECT);
		}

		return false;
	}

	public final EMaterialType stealMaterialAt(short x, short y) {
		return popMaterialTypeAt(x, y, null);
	}

	private StackMapObject getStackAtPosition(short x, short y, EMaterialType materialType) {
		StackMapObject stackObject = (StackMapObject) grid.getMapObject(x, y, EMapObjectType.STACK_OBJECT);

		while (stackObject != null && stackObject.getMaterialType() != materialType && materialType != null) { // find correct stack
			AbstractHexMapObject object = stackObject.getNextObject();
			if (object != null) {
				stackObject = (StackMapObject) object.getMapObject(EMapObjectType.STACK_OBJECT);
			} else {
				stackObject = null;
			}
		}
		return stackObject;
	}

	public void addBuildingTo(ISPosition2D position, AbstractHexMapObject newBuilding) {
		addMapObject(position, newBuilding);
	}

	public void placePig(ISPosition2D pos, boolean place) {
		if (place) {
			AbstractHexMapObject pig = grid.getMapObject(pos.getX(), pos.getY(), EMapObjectType.PIG);
			if (pig == null) {
				addMapObject(pos, new PigObject());
			}
		} else {
			removeMapObjectType(pos.getX(), pos.getY(), EMapObjectType.PIG);
		}
	}

	public boolean isPigThere(ISPosition2D pos) {
		AbstractHexMapObject pig = grid.getMapObject(pos.getX(), pos.getY(), EMapObjectType.PIG);
		return pig != null;
	}

	public boolean isPigAdult(ISPosition2D pos) {
		AbstractHexMapObject pig = grid.getMapObject(pos.getX(), pos.getY(), EMapObjectType.PIG);
		return pig != null && pig.canBeCut();
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
			this.eventTime = (int) (NetworkTimer.getGameTime() + duration * 1000);
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

}
