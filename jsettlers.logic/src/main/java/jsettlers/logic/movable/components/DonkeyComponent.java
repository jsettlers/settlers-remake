package jsettlers.logic.movable.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java8.util.stream.Collectors;
import java8.util.stream.Stream;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.trading.MarketBuilding;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.Requires;
import jsettlers.logic.movable.strategies.trading.ITradeBuilding;

/**
 * @author homoroselaps
 */

@Requires({MovableComponent.class})
public class DonkeyComponent extends Component {
	private static final long serialVersionUID = -4747039405397703303L;

	private ITradeBuilding          market;
	private Iterator<ShortPoint2D> waypoints;
	private ShortPoint2D           nextWaypoint;

	public ITradeBuilding getMarket() {
		return market;
	}

	public void setMarket(ITradeBuilding market) {
		assert market != null : "market should not be null, use reset() instead";
		this.market = market;
		this.waypoints = market.getWaypointsIterator();
		this.nextWaypoint = waypoints != null ? waypoints.next() : null;
	}

	public void resetMarket() {
		this.market = null;
		this.waypoints = null;
	}

	public ShortPoint2D getNextWaypoint() {
		ShortPoint2D last = nextWaypoint;
		this.nextWaypoint = waypoints != null ? waypoints.next() : null;
		return last;
	}

	public boolean hasNextWaypoint() {
		return nextWaypoint != null;
	}

	public ShortPoint2D peekNextWaypoint() {
		return nextWaypoint;
	}

	public ITradeBuilding findTradeBuildingWithWork() {
		List<? extends ITradeBuilding> tradeBuilding = getTradersWithWork().filter(ITradeBuilding::needsTrader).collect(Collectors.toList());

		if (!tradeBuilding.isEmpty()) { // randomly distribute the donkeys onto the markets needing them
			return tradeBuilding.get(MatchConstants.random().nextInt(tradeBuilding.size()));
		} else {
			return null;
		}
	}

	protected Stream<MarketBuilding> getTradersWithWork() {
		return MarketBuilding.getAllMarkets(entity.movableComponent().getPlayer());
	}
}
