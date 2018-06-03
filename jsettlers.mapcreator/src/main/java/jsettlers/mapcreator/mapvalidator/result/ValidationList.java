/*******************************************************************************
 * Copyright (c) 2015 - 2016
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
package jsettlers.mapcreator.mapvalidator.result;

import java.util.ArrayList;
import java.util.List;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.mapvalidator.result.fix.AbstractFix;

/**
 * List with validation errors
 * 
 * @author Andreas Butti
 *
 */
public class ValidationList {

	/**
	 * Group with one header and multiple errors
	 */
	private static class Group {

		/**
		 * Header of this group
		 */
		private final ErrorHeader header;

		/**
		 * Special entries without position
		 */
		private final List<ErrorEntry> entriesWithoutPositions = new ArrayList<>();

		/**
		 * Entries of this group
		 */
		private final List<ErrorEntry> entries = new ArrayList<>();

		/**
		 * Constructor
		 * 
		 * @param header
		 *            Header of this group
		 */
		public Group(ErrorHeader header) {
			this.header = header;
		}

		/**
		 * Try to group events logically by position
		 * 
		 * @return Grouped list
		 */
		public LocalGroupList groupSimilar() {
			LocalGroupList list = new LocalGroupList();
			for (ErrorEntry e : entries) {
				list.putIntoGroup(e);
			}

			return list;
		}

		/**
		 * @return true if this group contains entries
		 */
		public boolean isEmpty() {
			return entriesWithoutPositions.isEmpty() && entries.isEmpty();
		}

		/**
		 * @return true if this group contains at least one error, false if it contains only warnings
		 */
		public boolean containsErrors() {
			for (ErrorEntry e : entriesWithoutPositions) {
				if (e.isError()) {
					return true;
				}
			}

			for (ErrorEntry e : entries) {
				if (e.isError()) {
					return true;
				}
			}

			return false;
		}

	}

	/**
	 * Group with events near on the map
	 */
	private static class LocaleGroup {

		/**
		 * Max distance on map
		 */
		private static final int MAX_DISTANCE = 10;

		/**
		 * Entries of this group
		 */
		private final List<ErrorEntry> entries = new ArrayList<>();

		/**
		 * left
		 */
		private int x1;
		/**
		 * right
		 */
		private int x2;

		/**
		 * top
		 */
		private int y1;

		/**
		 * bottom
		 */
		private int y2;

		/**
		 * Constructor
		 * 
		 * @param entry
		 *            Entry to group
		 */
		public LocaleGroup(ErrorEntry entry) {
			this.entries.add(entry);
			ShortPoint2D pos = entry.getPosition();
			int x = pos.x;
			x1 = x;
			x2 = x;

			int y = pos.y;
			y1 = y;
			y2 = y;
		}

		/**
		 * Add an entry to the list
		 * 
		 * @param entry
		 *            Entry
		 */
		public void add(ErrorEntry entry) {
			this.entries.add(entry);
			ShortPoint2D pos = entry.getPosition();

			int x = pos.x;
			x1 = Math.min(x1, x);
			x2 = Math.max(x2, x);

			int y = pos.y;
			y1 = Math.min(y1, y);
			y2 = Math.max(y2, y);
		}

		/**
		 * Check if this Entry fits into this group
		 * 
		 * @param entry
		 *            Entry
		 * @return true if yes
		 */
		public boolean matchesGroup(ErrorEntry entry) {
			ShortPoint2D pos = entry.getPosition();

			return isPointInRange(pos.x, pos.y);
		}

		/**
		 * Checks if this point is near this group
		 * 
		 * @param x
		 *            X
		 * @param y
		 *            Y
		 * @return true if yes
		 */
		public boolean isPointInRange(int x, int y) {
			// left
			if (x1 > x + MAX_DISTANCE) {
				return false;
			}
			// right
			if (x2 < x - MAX_DISTANCE) {
				return false;
			}
			// top
			if (y1 > y + MAX_DISTANCE) {
				return false;
			}
			// bottom
			return y2 >= y - MAX_DISTANCE;

		}

	}

	/**
	 * List of groups
	 */
	private static class LocalGroupList extends ArrayList<LocaleGroup> {
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor
		 */
		public LocalGroupList() {
		}

		/**
		 * Put the entry in a group, if none is matching, create one
		 * 
		 * @param e
		 */
		public void putIntoGroup(ErrorEntry e) {
			for (LocaleGroup g : this) {
				if (g.matchesGroup(e)) {
					g.add(e);
					return;
				}
			}

			add(new LocaleGroup(e));
		}

	}

	/**
	 * List with all grouped error entries
	 */
	private final List<Group> list = new ArrayList<>();

	/**
	 * Current group, to current header
	 */
	private Group currentGroup = null;

	/**
	 * Constructor
	 */
	public ValidationList() {
	}

	/**
	 * Add a header entry, will be automatically removed if there are no followed tasks
	 * 
	 * @param header
	 *            Header
	 * @param fix
	 *            Fix, if any
	 */
	public void addHeader(String header, AbstractFix fix) {
		currentGroup = new Group(new ErrorHeader(header, fix));
		list.add(currentGroup);
	}

	/**
	 * Add an error entry
	 * 
	 * @param additionalErrorData
	 *            Used for special cases... Can be anything, needs a special implementation in the sidebar also
	 * @param text
	 *            Text to display
	 * @param error
	 *            true for error, false for warning
	 * @param pos
	 *            Position
	 * @param typeId
	 *            Type ID of the error, all errors of the same type at nearly the same position are grouped
	 */
	public void addError(Object additionalErrorData, String text, boolean error, ShortPoint2D pos, String typeId) {
		ErrorEntry entry = new ErrorEntry(additionalErrorData, text, error, pos, typeId);
		if (pos == null) {
			currentGroup.entriesWithoutPositions.add(entry);
		} else {
			currentGroup.entries.add(entry);
		}
	}

	/**
	 * Prepare the list for displaying in the JList
	 * 
	 * @return Model to display
	 */
	public ValidationListModel toListModel() {
		ValidationListModel model = new ValidationListModel();

		for (Group g : list) {
			if (g.isEmpty()) {
				continue;
			}

			// Errors? Or only warnings?
			final boolean error = g.containsErrors();
			model.addElement(g.header);

			LocalGroupList groupedList = g.groupSimilar();

			for (LocaleGroup lgl : groupedList) {
				// only use first entry of the group
				ErrorEntry element = lgl.entries.get(0);
				model.addElement(element);
			}

			// but add all special entries without position
			for (ErrorEntry e : g.entriesWithoutPositions) {
				model.addElement(e);
			}

			g.header.setError(error);
		}

		model.prepareToDisplay();
		return model;
	}

}
