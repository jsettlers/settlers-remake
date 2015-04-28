/*******************************************************************************
 * Copyright (c) 2015
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
package jsettlers.common.statistics;

import java.util.List;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;

/**
 * This class represents the statistics for a given player. It also contains the possibility to access and change player settings taht affect the
 * game.
 * 
 * @author michael
 */
public interface IPlayerStatistics {
	/**
	 * Gets a list of things the given material is needed for.
	 * 
	 * @param type
	 *            The material we want to know about.
	 * @return The list of consumers for this material.
	 */
	public List<IConsuming> getConsumers(EMaterialType type);

	/**
	 * Gets the number of materials that are available in the current partition. This is just used for information purposes.
	 * 
	 * @return The number of materials.
	 */
	public int getMaterialCount(EMaterialType material);

	/**
	 * Gets the number of movables that have the given type.
	 * 
	 * @param movable
	 *            The movable type to count
	 * @return The number of movables of that type.
	 */
	public int getMovableCount(EMovableType movable);

	/**
	 * Gets the rate settings for converting bearers to bricklayers.
	 * 
	 * @return
	 */
	public IConversionRate getBricklayerConverstionRate();

	/**
	 * Gets the rate settings for converting bearers to diggers.
	 * 
	 * @return
	 */
	public IConversionRate getDiggerConverstionRate();

	/**
	 * Gets the minimum rate for all bearers. At least this part of the overall population needs to be bearers.
	 * 
	 * @return The rate from 0..1.
	 */
	public float getMinimumBearerRate();

	/**
	 * Sets the bearer rate. The rate may be clamped internally by the logic.
	 * 
	 * @param rate
	 *            The rate, ranging from 0..1.
	 */
	public void setMinimumBearerRate(float rate);
}
