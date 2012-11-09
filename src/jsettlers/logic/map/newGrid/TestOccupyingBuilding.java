package jsettlers.logic.map.newGrid;

import jsettlers.common.CommonConstants;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.position.ShortPoint2D;

class TestOccupyingBuilding implements IOccupyingBuilding {

	private final ShortPoint2D pos;
	private byte player;

	TestOccupyingBuilding(ShortPoint2D pos, byte player) {
		this.pos = pos;
		this.player = player;
	}

	@Override
	public ShortPoint2D getPos() {
		return pos;
	}

	@Override
	public IMapArea getOccupyablePositions() {
		return new MapCircle(pos, CommonConstants.TOWER_RADIUS);
	}

	@Override
	public byte getPlayerId() {
		return player;
	}

	public void setPlayer(int newPlayer) {
		this.player = (byte) newPlayer;
	}

}
