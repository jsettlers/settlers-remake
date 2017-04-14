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

    public IDonkeyMarket getMarket() {
        return market;
    }

    public void setMarket(IDonkeyMarket market) {
        this.market = market;
        this.waypoints = market.getWaypointsIterator();
    }

    public ShortPoint2D getNextWaypoint() {
        return waypoints != null ? waypoints.next() : null;
    }

    public boolean hasNextWaypoint() {
        return waypoints != null ? waypoints.hasNext() : false;
    }

    public IDonkeyMarket findNextMarketNeedingDonkey() {
        if (this.market != null && this.market.needsDonkey()) {
            return this.market;
        }

        Iterable<? extends IDonkeyMarket> markets = MarketBuilding.getAllMarkets(entity.movC().getPlayer());
        List<IDonkeyMarket> marketsNeedingDonkeys = new ArrayList<IDonkeyMarket>();

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
