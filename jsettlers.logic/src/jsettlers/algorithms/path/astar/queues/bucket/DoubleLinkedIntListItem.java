package jsettlers.algorithms.path.astar.queues.bucket;

import jsettlers.common.utils.collections.list.DoubleLinkedListItem;

/**
 * This class represents a single item of a {@link DoubleLinkedIntList}.
 * 
 * @author Andreas Eberle
 * 
 */
public final class DoubleLinkedIntListItem extends DoubleLinkedListItem<DoubleLinkedIntListItem> {
	private static final long serialVersionUID = -4196178838347323078L;

	public final int value;

	public DoubleLinkedIntListItem(int value) {
		this.value = value;
		assert value >= 0;
	}

	public DoubleLinkedIntListItem() {
		value = -1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DoubleLinkedIntListItem other = (DoubleLinkedIntListItem) obj;
		if (value != other.value)
			return false;
		return true;
	}
}
