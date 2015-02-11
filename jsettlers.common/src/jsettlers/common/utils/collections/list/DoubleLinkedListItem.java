package jsettlers.common.utils.collections.list;

import java.io.Serializable;

/**
 * This class represents a single item of a {@link DoubleLinkedList}.
 * 
 * @author Andreas Eberle
 * 
 */
public class DoubleLinkedListItem<T extends DoubleLinkedListItem<T>> implements Serializable {
	private static final long serialVersionUID = 4539587339826435945L;

	transient T prev;
	transient T next;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((next == null) ? 0 : next.hashCode());
		result = prime * result + ((prev == null) ? 0 : prev.hashCode());
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
		DoubleLinkedListItem<?> other = (DoubleLinkedListItem<?>) obj;
		if (next == null) {
			if (other.next != null)
				return false;
		} else if (!next.equals(other.next))
			return false;
		if (prev == null) {
			if (other.prev != null)
				return false;
		} else if (!prev.equals(other.prev))
			return false;
		return true;
	}
}
