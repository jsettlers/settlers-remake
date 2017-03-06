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
package jsettlers.logic.map.grid.partition.manager.materials.interfaces;

import jsettlers.common.position.ILocatable;
import jsettlers.logic.map.grid.partition.manager.materials.offers.EOfferPriority;

/**
 * This interface defines the methods needed by a {@link IManagerBearer} to be able to carry an offer to a request.
 *
 * @author Andreas Eberle
 */
public interface IMaterialRequest extends ILocatable {
	/**
	 * Signals that this {@link IMaterialRequest} is in delivery.
	 */
	void deliveryAccepted();

	/**
	 * Signals that the delivery has successfully been handled.
	 */
	void deliveryFulfilled();

	/**
	 * Signals that the delivery has been aborted.
	 */
	void deliveryAborted();

	/**
	 * Checks if the request is still active.
	 *
	 * @return Returns true if the request is still active,<br>
	 * otherwise it returns false.
	 */
	boolean isActive();

	EOfferPriority getMinimumAcceptedOfferPriority();
}
