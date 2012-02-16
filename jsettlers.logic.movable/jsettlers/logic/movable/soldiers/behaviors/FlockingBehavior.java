package jsettlers.logic.movable.soldiers.behaviors;

import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.map.newGrid.MainGrid.MovableNeighborIterator;
import jsettlers.logic.map.newGrid.movable.IHexMovable;

/**
 * An implementation of a flocking behavior
 * 
 * @author Andreas Eberle
 * 
 */
public class FlockingBehavior extends SoldierBehavior {
	private final IHexMovable leader;

	protected FlockingBehavior(ISoldierBehaviorable soldier, IHexMovable leader) {
		super(soldier);
		this.leader = leader;
	}

	private static final long serialVersionUID = -4687811664290054133L;

	private static final byte FLOCK_RADIUS = 10;
	private static final int SEPARATION_RADIUS = FLOCK_RADIUS / 3;

	private static final byte AVG_POS_FACTOR = 0;
	private static final byte AVG_DIR_FACTOR = 0;
	private static final byte TO_CLOSE_FACTOR = 5;

	private static final int MINIMUM_ACTING_BARRIER = 50;

	private static final byte LEADER_FACTOR = 5;

	@Override
	public SoldierBehavior calculate(ISPosition2D pos, IPathCalculateable pathCalcable) {
		MovableNeighborIterator neighbors = super.getGrid().getNeighborsIterator(pos, FLOCK_RADIUS);

		int sumPosX = 0;
		int sumPosY = 0;
		int sumToCloseX = 0;
		int sumToCloseY = 0;

		int dirX = 0;
		int dirY = 0;
		int ctr = 0;
		int toCloseCtr = 0;
		int toCloseDist = 0;

		while (neighbors.hasNext()) {
			IHexMovable next = neighbors.next();
			EDirection neighborDir = next.getDirection();
			dirX += neighborDir.getGridDeltaX();
			dirY += neighborDir.getGridDeltaY();
			ISPosition2D neighborPos = next.getPos();
			sumPosX += neighborPos.getX();
			sumPosY += neighborPos.getY();

			if (neighbors.getCurrRadius() < SEPARATION_RADIUS) {
				sumToCloseX += neighborPos.getX();
				sumToCloseY += neighborPos.getY();
				toCloseDist += (SEPARATION_RADIUS - neighbors.getCurrRadius());
				toCloseCtr++;
			}

			ctr++;
		}

		if (ctr > 0) {
			EDirection avgPosDirection = EDirection.getApproxDirection(pos.getX(), pos.getY(), sumPosX / ctr, sumPosY / ctr);
			EDirection othersDirDirection = EDirection.getApproxDirection(100, 100, 100 + dirX * 10, 100 + dirY * 10);
			ISPosition2D leaderDirectedPos = leader.getDirection().getNextTilePoint(leader.getPos(), 7);
			EDirection leaderDir = EDirection.getApproxDirection(pos, leaderDirectedPos);
			int leaderDist = Math.abs(pos.getX() - leaderDirectedPos.getX()) + Math.abs(pos.getY() - leaderDirectedPos.getY());

			int toCloseDx;
			int toCloseDy;
			if (toCloseCtr > 0) {
				EDirection toCloseOppositeDir = EDirection.getApproxDirection(pos.getX(), pos.getY(), sumToCloseX / toCloseCtr,
						sumToCloseY / toCloseCtr).getInverseDirection();
				toCloseDx = toCloseOppositeDir.getGridDeltaX();
				toCloseDy = toCloseOppositeDir.getGridDeltaY();

				toCloseDist = toCloseDist * toCloseDist / toCloseCtr;
			} else {
				toCloseDx = 0;
				toCloseDy = 0;
			}

			int newDirX = (avgPosDirection.getGridDeltaX() * AVG_POS_FACTOR + othersDirDirection.getGridDeltaX() * AVG_DIR_FACTOR + toCloseDx
					* toCloseDist * TO_CLOSE_FACTOR + leaderDir.getGridDeltaX() * leaderDist * LEADER_FACTOR);
			int newDirY = (avgPosDirection.getGridDeltaY() * AVG_POS_FACTOR + othersDirDirection.getGridDeltaY() * AVG_DIR_FACTOR + toCloseDy
					* toCloseDist * TO_CLOSE_FACTOR + leaderDir.getGridDeltaY() * leaderDist * LEADER_FACTOR);

			if (Math.abs(newDirX) + Math.abs(newDirY) > MINIMUM_ACTING_BARRIER) {
				EDirection newDir = EDirection.getApproxDirection(500, 500, 500 + newDirX, 500 + newDirY);
				// System.out.println("new direction: " + newDir);

				super.goToTile(newDir.getNextHexPoint(pos));
				return this;
			}
		}

		super.setAction(EAction.NO_ACTION, -1);
		return this;
	}

	@Override
	public void pathRequestFailed() {
		// TODO Auto-generated method stub

	}

}
