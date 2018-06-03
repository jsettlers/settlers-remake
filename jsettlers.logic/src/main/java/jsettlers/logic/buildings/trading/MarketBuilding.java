/*******************************************************************************
 * Copyright (c) 2016 - 2018
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
package jsettlers.logic.buildings.trading;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java8.util.stream.Stream;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.player.Player;

import static java8.util.stream.StreamSupport.stream;

/**
 *
 * @author Andreas Eberle
 *
 */
public class MarketBuilding extends TradingBuilding {
	private static final List<MarketBuilding> ALL_MARKETS = new ArrayList<>();

	public static Stream<MarketBuilding> getAllMarkets(final Player player) {
		return stream(ALL_MARKETS).filter(building -> building.getPlayer() == player);
	}

	public static void clearState() {
		ALL_MARKETS.clear();
	}

	@SuppressWarnings("unchecked")
	public static void readStaticState(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ALL_MARKETS.addAll((Collection<? extends MarketBuilding>) ois.readObject());
	}

	public static void writeStaticState(ObjectOutputStream oos) throws IOException {
		oos.writeObject(ALL_MARKETS);
	}

	public MarketBuilding(EBuildingType type, Player player, ShortPoint2D position, IBuildingsGrid buildingsGrid) {
		super(type, player, position, buildingsGrid);
		ALL_MARKETS.add(this);
	}

	@Override
	protected ShortPoint2D getWaypointsStartPosition() {
		return super.pos;
	}


	@Override
	public boolean isSeaTrading() {
		return false;
	}

	@Override
	protected void killedEvent() {
		super.killedEvent();
		ALL_MARKETS.remove(this);
	}

	@Override
	public ShortPoint2D getPickUpPosition() {
		return getDoor();
	}
}
