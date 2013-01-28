package jsettlers.common.utils.collections.list;

/**
 * This class represents a single item of a {@link DoubleLinkedList}.
 * 
 * @author Andreas Eberle
 * 
 */
public class DoubleLinkedListItem<T extends DoubleLinkedListItem<T>> {
	T prev;
	T next;
}
