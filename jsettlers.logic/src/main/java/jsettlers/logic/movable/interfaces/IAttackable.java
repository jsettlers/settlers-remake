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
package jsettlers.logic.movable.interfaces;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.player.IPlayer;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;

/**
 * This interface defines the methods needed by soldiers to able to attack the implementor of this interface.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IAttackable extends ILocatable, IInformable {
	/**
	 * Hits this attackable with the given strength.
	 * 
	 * @param strength
	 *            The strength of the hit.
	 * @param attackerPos
	 *            The position of the attacker.
	 */
	void receiveHit(float strength, ShortPoint2D attackerPos, byte attackingPlayer);

	/**
	 * Used to check if the movable is still alive
	 *
	 * @return true if the movable is still alive, false otherwise.
	 */
	boolean isAlive();

	/**
	 * Gets the UiPlayer of this {@link IAttackable}.
	 *
	 * @return UiPlayer of this {@link IAttackable}.
	 */
	IPlayer getPlayer();

	/**
	 * 
	 * @return true if this movable is currently attackable.<br>
	 *         false otherwise.
	 */
	boolean isAttackable();

	/**
	 * 
	 * @return Gets the {@link EMovableType} of this {@link IAttackable}.
	 */
	EMovableType getMovableType();

	/**
	 * 
	 * @return true if this {@link IAttackable} represents a tower or the defender of a tower.
	 */
	boolean isTower();

}
