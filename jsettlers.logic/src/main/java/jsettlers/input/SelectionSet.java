/*******************************************************************************
 * Copyright (c) 2015 - 2018
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
package jsettlers.input;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java8.util.function.Predicate;
import java8.util.stream.Collectors;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.selectable.ESelectionType;
import jsettlers.common.selectable.ISelectable;
import jsettlers.common.selectable.ISelectionSet;

/**
 * Defines a selection set that automatically ensures the selection type.
 *
 * @author Andreas Eberle
 *
 */
public final class SelectionSet implements ISelectionSet {
	private final List<ISelectable> set           = new ArrayList<>();
	private       ESelectionType    selectionType = ESelectionType.values()[0];

	public SelectionSet() {
	}

	public SelectionSet(List<? extends ISelectable> selected) {
		addAll(selected);
	}

	private SelectionSet(ESelectionType selectionType, List<ISelectable> selected) {
		this.selectionType = selectionType;
		this.set.addAll(selected);
	}

	static SelectionSet createFromFilteredSelectionSetKeepingType(SelectionSet base, Predicate<? super ISelectable> predicate) {
		return new SelectionSet(base.selectionType, base.stream().filter(predicate).filter(selectable -> selectable.getSelectionType() == base.selectionType).collect(Collectors.toList()));
	}

	static SelectionSet createFromFilteredSelectionSetUpdatingType(SelectionSet base, Predicate<ISelectable> predicate) {
		return new SelectionSet(base.stream().filter(predicate).collect(Collectors.toList()));
	}

	public void addAll(List<? extends ISelectable> selected) {
		for (ISelectable curr : selected) {
			add(curr);
		}
	}

	public SelectionSet(ISelectable selectable) {
		add(selectable);
	}

	/**
	 * This methods decides if the given {@link ISelectable} can be added to this selection set or not.
	 *
	 * @param selectable
	 * The selectable to add
	 */
	public synchronized void add(ISelectable selectable) {
		ESelectionType selectionType = selectable.getSelectionType();
		if (selectionType.priority < this.selectionType.priority) {
			return; // selectable is of lower priority
		} else if (selectionType.priority > this.selectionType.priority) {
			setSelected(false);
			this.set.clear();
			this.selectionType = selectionType;
		}

		if (selectionType.maxSelected > set.size()) {
			set.add(selectable);
		}
	}

	public boolean isEmpty() {
		return set.isEmpty();
	}

	@Override
	public Iterator<ISelectable> iterator() {
		return set.iterator();
	}

	@Override
	public boolean contains(ISelectable selectable) {
		return set.contains(selectable);
	}

	@Override
	public int getSize() {
		return set.size();
	}

	@Override
	public ESelectionType getSelectionType() {
		return selectionType;
	}

	@Override
	public synchronized int getMovableCount(EMovableType type) {
		int ctr = 0;
		for (ISelectable curr : set) {
			if (curr instanceof IMovable && ((IMovable) curr).getMovableType() == type) {
				ctr++;
			}
		}
		return ctr;
	}

	@Override
	public ISelectable get(int idx) {
		return set.get(idx);
	}

	/**
	 * calls the {@link ISelectable}.setSelected(selected) method with the given argument for all {@link ISelectable}s in the set.
	 *
	 * @param selected
	 * New selected state
	 */
	public synchronized void setSelected(boolean selected) {
		for (ISelectable curr : set) {
			curr.setSelected(selected);
		}
	}

	/**
	 * Gets the single selected element if we are only selecting one single element.
	 *
	 * @return That element. <code>null</code> if multiple or no elements are selected.
	 */
	public ISelectable getSingle() {
		return set.size() == 1 ? set.get(0) : null;
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		return "SelectionSet [set=" + set.subList(0, Math.min(set.size(), maxLen)) + ", selectionType=" + selectionType + "]";
	}

	public Stream<ISelectable> stream() {
		return StreamSupport.stream(set);
	}
}
