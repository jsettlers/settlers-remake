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
}
