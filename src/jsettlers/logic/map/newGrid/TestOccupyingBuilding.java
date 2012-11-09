package jsettlers.logic.map.newGrid;

import jsettlers.common.CommonConstants;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.player.Player;

class TestOccupyingBuilding implements IOccupyingBuilding {

	private final ShortPoint2D pos;
	private Player player;

	TestOccupyingBuilding(ShortPoint2D pos, Player player) {
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
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player newPlayer) {
		this.player = newPlayer;
	}

}
