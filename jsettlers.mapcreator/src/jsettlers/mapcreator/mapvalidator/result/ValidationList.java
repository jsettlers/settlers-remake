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
		 */
		public LocaleGroup(ErrorEntry entry) {
			this.entries.add(entry);
			int x = entry.getPos().x;
			x1 = x;
			x2 = x;

			int y = entry.getPos().y;
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

			int x = entry.getPos().x;
			x1 = Math.min(x1, x);
			x2 = Math.max(x2, x);

			int y = entry.getPos().y;
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
			ShortPoint2D pos = entry.getPos();

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
			if (y2 < y - MAX_DISTANCE) {
				return false;
			}

			return true;
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
	private List<Group> list = new ArrayList<>();

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
	 * @param text
	 *            Text to display
	 * @param pos
	 *            Position
	 * @param typeId
	 *            Type ID of the error, all errors of the same type at nearly the same position are grouped
	 */
	public void addError(String text, ShortPoint2D pos, String typeId) {
		currentGroup.entries.add(new ErrorEntry(text, pos, typeId));
	}

	/**
	 * Prepare the list for displaying in the JList
	 * 
	 * @return
	 */
	public ValidationListModel toListModel() {
		ValidationListModel model = new ValidationListModel();

		for (Group g : list) {
			if (g.entries.isEmpty()) {
				continue;
			}

			model.addElement(g.header);

			LocalGroupList groupedList = g.groupSimilar();

			for (LocaleGroup lgl : groupedList) {
				// only use first entry of the group
				model.addElement(lgl.entries.get(0));
			}
		}

		model.prepareToDisplay();
		return model;
	}

}
