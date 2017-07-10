/*******************************************************************************
 * Copyright (c) 2017
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
package jsettlers.logic.movable.strategies.trading;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.trading.HarborBuilding;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.MovableStrategy;

/**
 *
 * @author Rudolf Polzer
 *
 */
public class CargoBoatStrategy extends MovableStrategy {
    private static final long serialVersionUID = 1L;

    private static final short SHIP_WAYPOINT_SEARCH_RADIUS = 50;

    private EShipState state = EShipState.JOBLESS;

    private IShipHarbor harbor;
    private Iterator<ShortPoint2D> waypoints;

    private Movable ship;

    public CargoBoatStrategy(Movable movable) {
        super(movable);
        ship = movable;
    }

    @Override
    protected void action() {
        switch (state) {
            case JOBLESS:
                if (this.ship.getStateProgress() < 0.99) { // ship not ready
                    break;
                }
                this.harbor = findNextHarborNeedingShip();

                if (this.harbor == null) { // no harbor found
                    break;
                }

            case INIT_GOING_TO_HARBOR:
                if (harbor.needsShip() && super.goToPos(harbor.getShipWayStart())) {
                    state = EShipState.GOING_TO_HARBOR;
                } else {
                    reset();
                }
                break;

            case GOING_TO_HARBOR:
                int cargoTotal = 0;
                int cargoCount;
                EMaterialType material;
                for (int stack = 0; stack < ship.getNumberOfStacks(); stack++) {
                    if (ship.getCargoCount(stack) == 0) {
                        material = harbor.tryToTakeShipMaterial();
                        if (material != null) {
                            ship.setCargoType(material, stack);
                            cargoCount = 1 + harbor.tryToTakeFurtherMaterial(material, 7);
                            ship.setCargoCount(cargoCount, stack);
                            cargoTotal += cargoCount;
                        }
                    }
                }
                if (cargoTotal == 0) {
                    reset();
                    break;
                } else {
                    this.waypoints = harbor.getWaypointsIterator();
                    state = EShipState.GOING_TO_TARGET;
                }

            case GOING_TO_TARGET:
                if (!goToNextWaypoint()) { // no waypoint left
                    dropMaterialIfPossible();
                    waypoints = null;
                    state = EShipState.INIT_GOING_TO_HARBOR;
                }
                break;

            default:
                break;
        }
    }

    private boolean goToNextWaypoint() {
        while (waypoints.hasNext()) {
            ShortPoint2D nextPosition = waypoints.next();
            if (super.preSearchPath(true, nextPosition.x, nextPosition.y, SHIP_WAYPOINT_SEARCH_RADIUS, ESearchType.VALID_FREE_POSITION)) {
                super.followPresearchedPath();
                return true;
            }
        }

        return false;
    }

    private void reset() {
        dropMaterialIfPossible();
        harbor = null;
        waypoints = null;
        state = EShipState.JOBLESS;
    }

    private void dropMaterialIfPossible() {
        int cargoCount;
        EMaterialType material;
        for (int stack = 0; stack < ship.getNumberOfStacks(); stack++) {
            cargoCount = ship.getCargoCount(stack);
            material = ship.getCargoType(stack);
            while (cargoCount > 0) {
                super.getGrid().dropMaterial(movable.getPos(), material, true, true);
                cargoCount--;
            }
            ship.setCargoCount(0, stack);
        }
    }

    private IShipHarbor findNextHarborNeedingShip() {
        if (this.harbor != null && this.harbor.needsShip()) {
            return this.harbor;
        }

        Iterable<? extends IShipHarbor> harbors = HarborBuilding.getAllHarbors(movable.getPlayer());
        List<IShipHarbor> harborsNeedingShips = new ArrayList<>();

        for (IShipHarbor currHarbor : harbors) {
            if (currHarbor.needsShip()) {
                harborsNeedingShips.add(currHarbor);
            }
        }

        if (!harborsNeedingShips.isEmpty()) {
            // randomly distribute the ships onto the harbors needing them
            return harborsNeedingShips.get(MatchConstants.random().nextInt(harborsNeedingShips.size()));
        } else {
            return null;
        }
    }

    private enum EShipState {
        JOBLESS,
        INIT_GOING_TO_HARBOR,
        GOING_TO_HARBOR,
        GOING_TO_TARGET,
        DEAD
    }
}
