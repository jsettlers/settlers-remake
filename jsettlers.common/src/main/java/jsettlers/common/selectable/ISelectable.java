/*******************************************************************************
 * Copyright (c) 2015, 2016
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

import jsettlers.common.player.IPlayerable;

/**
 * This interface defines something that can be selected and therefore has an selection status.
 * 
 * @author michael
 */
public interface ISelectable extends IPlayerable{
	/**
	 * Returns whether this object is currently selected.
	 * <p>
	 * This information is not used by the interface to show the object in the UI, you also have to set a {@link SelectionSet} in the interface
	 * connector.
	 * 
	 * @return The selection state of the item.
	 */
	boolean isSelected();

	/**
	 * Sets the selection flag of the given item.
	 * 
	 * @param selected
	 *            The selection status.
	 */
	void setSelected(boolean selected);

	/**
	 * get the selection type.
	 * 
	 * @return {@link ESelectionType}
	 */
	ESelectionType getSelectionType();

	/**
	 * Returns whether this object is currently wounded.
	 * @return If it is wounded or not.
	 */
	boolean isWounded();
}
