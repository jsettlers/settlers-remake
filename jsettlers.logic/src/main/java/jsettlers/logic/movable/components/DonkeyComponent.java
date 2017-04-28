package jsettlers.logic.movable.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.trading.MarketBuilding;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.Requires;
import jsettlers.logic.movable.strategies.trading.IDonkeyMarket;

/**
 * @author homoroselaps
 */

@Requires({MovableComponent.class})
public class DonkeyComponent extends Component {
    private static final long serialVersionUID = -4747039405397703303L;
    private IDonkeyMarket market;
    private Iterator<ShortPoint2D> waypoints;
    private ShortPoint2D nextWaypoint;

    public IDonkeyMarket getMarket() {
        return market;
    }

    public void setMarket(IDonkeyMarket market) {
        assert market != null: "market should not be null, use reset() instead";
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

    public IDonkeyMarket findNextMarketNeedingDonkey() {
        if (this.market != null && this.market.needsDonkey()) {
            return this.market;
        }

        Iterable<? extends IDonkeyMarket> markets = MarketBuilding.getAllMarkets(entity.movC().getPlayer());
        List<IDonkeyMarket> marketsNeedingDonkeys = new ArrayList<>();

        for (IDonkeyMarket currMarket : markets) {
            if (currMarket.needsDonkey()) {
                marketsNeedingDonkeys.add(currMarket);
            }
        }

        if (!marketsNeedingDonkeys.isEmpty()) {
            // randomly distribute the donkeys onto the markets needing them
            return marketsNeedingDonkeys.get(MatchConstants.random().nextInt(marketsNeedingDonkeys.size()));
        } else {
            return null;
        }
    }
}
