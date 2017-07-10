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

    private EMaterialType materialType1;
    private EMaterialType materialType2;
    private EMaterialType materialType3;
    private int materialCount1 = 0;
    private int materialCount2 = 0;
    private int materialCount3 = 0;

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
                if (this.materialCount1 == 0) {
                    this.materialType1 = harbor.tryToTakeShipMaterial();
                    if (this.materialType1 != null) {
                        this.materialCount1 = 1 + harbor.tryToTakeFurtherMaterial(this.materialType1, 7);
                    }
                }
                if (this.materialCount2 == 0) {
                    this.materialType2 = harbor.tryToTakeShipMaterial();
                    if (this.materialType2 != null) {
                        this.materialCount2 = 1 + harbor.tryToTakeFurtherMaterial(this.materialType2, 7);
                    }
                }
                if (this.materialCount3 == 0) {
                    this.materialType3 = harbor.tryToTakeShipMaterial();
                    if (this.materialType3 != null) {
                        this.materialCount3 = 1 + harbor.tryToTakeFurtherMaterial(this.materialType3, 7);
                    }
                }
                if (this.materialCount1 + this.materialCount2 + this.materialCount3 == 0) {
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
        if (this.materialCount1 > 0) {
            for (; this.materialCount1 > 0; this.materialCount1--) {
                super.getGrid().dropMaterial(movable.getPos(), materialType1, true, true);
            }
            for (; this.materialCount2 > 0; this.materialCount2--) {
                super.getGrid().dropMaterial(movable.getPos(), materialType2, true, true);
            }
            for (; this.materialCount3 > 0; this.materialCount3--) {
                super.getGrid().dropMaterial(movable.getPos(), materialType3, true, true);
            }
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
