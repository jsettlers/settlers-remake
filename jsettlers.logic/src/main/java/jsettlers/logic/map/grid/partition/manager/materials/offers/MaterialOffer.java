/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.logic.map.grid.partition.manager.materials.offers;

import java.io.Serializable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.partition.manager.materials.MaterialsManager;
import jsettlers.logic.map.grid.partition.manager.materials.interfaces.IMaterialOffer;
import jsettlers.logic.map.grid.partition.manager.materials.offers.list.IListManageable;
import jsettlers.logic.map.grid.partition.manager.materials.offers.list.IPrioritizable;

/**
 * This class is used by {@link MaterialsManager} to store offers of materials.
 *
 * @author Andreas Eberle
 */
public class MaterialOffer implements Serializable, ILocatable, IPrioritizable<EOfferPriority>, IListManageable, IMaterialOffer {
	private final ShortPoint2D position;
	private final EMaterialType materialType;

	private IOffersCountListener countChangedListener;
	private EOfferPriority priority;
	private byte amount = 0;
	private byte inDistribution = 0;

	MaterialOffer(ShortPoint2D position, EMaterialType materialType, IOffersCountListener countChangedListener, EOfferPriority priority, byte amount) {
		this.position = position;
		this.materialType = materialType;

		this.countChangedListener = countChangedListener;
		this.priority = priority;
		this.amount = amount;

		countChangedListener.offersCountChanged(materialType, amount);
	}

	@Override
	public ShortPoint2D getPosition() {
		return position;
	}

	/**
	 * Increases the amount and returns the new value.
	 *
	 * @return
	 */
	public void incrementAmount() {
		++amount;
		countChangedListener.offersCountChanged(materialType, +1);
	}

	public byte getAmount() {
		return amount;
	}

	public int getAvailable() {
		return Math.max(0, amount - inDistribution);
	}

	@Override
	public EOfferPriority getPriority() {
		return priority;
	}

	@Override
	public void updatePriority(EOfferPriority priority) {
		this.priority = priority;
	}

	@Override
	public boolean isActive() {
		return getAvailable() > 0;
	}

	@Override
	public boolean canBeRemoved() {
		return amount <= 0;
	}

	@Override
	public String toString() {
		return "MaterialOffer{" + "position=" + position + ", priority=" + priority + ", amount=" + amount + '}';
	}

	@Override
	public void distributionAccepted() {
		inDistribution++;
		countChangedListener.offersCountChanged(materialType, -1);
	}

	@Override
	public void distributionAborted() {
		inDistribution--;
		countChangedListener.offersCountChanged(materialType, +1);
	}

	@Override
	public void offerTaken() {
		inDistribution--;
		amount--;
	}

	@Override
	public boolean isStillValid(EOfferPriority minimumAcceptedPriority) {
		return amount >= inDistribution && priority.ordinal() >= minimumAcceptedPriority.ordinal();
	}

	public void changeOffersCountListener(IOffersCountListener newCountChangedListener) {
		int countedMaterials = amount - inDistribution;
		this.countChangedListener.offersCountChanged(materialType, -countedMaterials);
		newCountChangedListener.offersCountChanged(materialType, countedMaterials);
		this.countChangedListener = newCountChangedListener;
	}
}
