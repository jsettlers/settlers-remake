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
package jsettlers.common.selectable;

import java.util.EnumMap;
import java.util.Map;

import jsettlers.common.movable.EMovableType;

import java8.util.stream.Stream;
import jsettlers.common.player.ECivilisation;
import jsettlers.common.player.IInGamePlayer;
import jsettlers.common.player.IPlayer;

/**
 * Interface for sets of selectables.<br>
 * A class of this interface can only be of one {@link ESelectionType}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface ISelectionSet extends Iterable<ISelectable> {

	/**
	 * Checks whether the given object is selected by this set.
	 * 
	 * @param selectable
	 *            The selectable to be checked.
	 * @return true if and only if it is selected.
	 */
	boolean contains(ISelectable selectable);

	/**
	 * Gives the number of selected elements.
	 * 
	 * @return number of elements selected by this SelectionSet
	 */
	int getSize();

	/**
	 * 
	 * @return {@link ESelectionType} of this selection set
	 */
	ESelectionType getSelectionType();

	/**
	 * counts the movables in the set of the given type.
	 * 
	 * @param type
	 * @param playerStatistic
	 * @return
	 */
	int getMovableCount(EMovableType type, Map<IPlayer, Integer> playerStatistic);

	/**
	 * gives the selected at given index.
	 * 
	 * @param idx
	 * @return
	 */
	ISelectable get(int idx);

	/**
	 * Stream over the elements of this set
	 * 
	 * @return Stream over the set's elements
	 */
	Stream<ISelectable> stream();
}
