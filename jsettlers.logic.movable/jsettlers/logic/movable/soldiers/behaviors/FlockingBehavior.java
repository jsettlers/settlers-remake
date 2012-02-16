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

	private static byte AVG_POS_FACTOR = 2;
	private static byte AVG_DIR_FACTOR = 2;
	private static byte TO_CLOSE_FACTOR = 15;

	private static int MINIMUM_ACTING_BARRIER = 100;

	private static byte LEADER_FACTOR = 1;

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
				toCloseDist += (SEPARATION_RADIUS - neighbors.getCurrRadius()) * (SEPARATION_RADIUS - neighbors.getCurrRadius());
				toCloseCtr++;
			}

			ctr++;
		}

		if (ctr > 0) {
			int deltaX = 0;
			int deltaY = 0;

			deltaX += AVG_POS_FACTOR * (sumPosX / ctr - pos.getX());
			deltaY += AVG_POS_FACTOR * (sumPosY / ctr - pos.getY());

			deltaX += dirX * AVG_DIR_FACTOR;
			deltaY += dirY * AVG_DIR_FACTOR;

			if (leader != null) {
				ISPosition2D leaderDirectedPos = leader.getDirection().getNextTilePoint(leader.getPos(), 7);
				int leaderDist = Math.abs(pos.getX() - leaderDirectedPos.getX()) + Math.abs(pos.getY() - leaderDirectedPos.getY());

				deltaX += (leaderDirectedPos.getX() - pos.getX()) * leaderDist * LEADER_FACTOR;
				deltaY += (leaderDirectedPos.getY() - pos.getY()) * leaderDist * LEADER_FACTOR;
			}

			if (toCloseCtr > 0) {
				toCloseDist = toCloseDist / toCloseCtr;
				EDirection dir = EDirection.getApproxDirection(0, 0, sumToCloseX / toCloseCtr - pos.getX(), sumToCloseY / toCloseCtr - pos.getY())
						.getInverseDirection();

				deltaX += dir.getGridDeltaX() * toCloseDist * TO_CLOSE_FACTOR;
				deltaY += dir.getGridDeltaY() * toCloseDist * TO_CLOSE_FACTOR;
			}

			if (Math.abs(deltaX) + Math.abs(deltaY) > MINIMUM_ACTING_BARRIER) {
				EDirection newDir = EDirection.getApproxDirection(0, 0, deltaX, deltaY);
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
