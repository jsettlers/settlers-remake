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
package jsettlers.logic.buildings.stack;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.stack.multi.StockSettings;
import jsettlers.logic.map.grid.partition.manager.materials.interfaces.IOfferEmptiedListener;
import jsettlers.logic.map.grid.partition.manager.materials.offers.EOfferPriority;
import jsettlers.logic.map.grid.partition.manager.materials.requests.MaterialRequestObject;

import java.io.Serializable;

/**
 * This interface defines the methods a grid must supply that it can be used by a {@link RequestStack}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IRequestsStackGrid extends Serializable {

	/**
	 * Requests the given {@link EMaterialType} with the conditions supplied through the {@link MaterialRequestObject}.
	 * 
	 * @param materialType
	 *            The {@link EMaterialType} to be requested.
	 * @param requestObject
	 *            The {@link MaterialRequestObject} specifying the requests conditions like amount and so on.
	 */
	void request(EMaterialType materialType, MaterialRequestObject requestObject);

	boolean hasMaterial(ShortPoint2D position, EMaterialType materialType);

	byte getStackSize(ShortPoint2D position, EMaterialType materialType);

	/**
	 * This method creates a new offer for every material of the given {@link EMaterialType} that is currently located at the given position.
	 * 
	 * @param position
	 * @param materialType
	 */
	void createOffersForAvailableMaterials(ShortPoint2D position, EMaterialType materialType);

	/**
	 * Pops a materials of the given type from the given location.
	 * 
	 * @param position
	 *            The location to pop the material.
	 * @param materialType
	 *            The {@link EMaterialType} type to be popped.
	 * @return if <code>true</code>, the material has been popped. If <code>false</code>, no material of this type has been found at the given
	 *         position.
	 */
	boolean popMaterial(ShortPoint2D position, EMaterialType materialType);

	StockSettings getPartitionStockSettings(ShortPoint2D position);

	void offer(ShortPoint2D position, EMaterialType materialType, EOfferPriority priority, IOfferEmptiedListener offerListener);

	void updateOfferPriorities(ShortPoint2D position, EMaterialType materialType, EOfferPriority newPriority);
}
