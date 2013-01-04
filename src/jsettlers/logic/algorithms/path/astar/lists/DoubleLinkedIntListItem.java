package jsettlers.logic.algorithms.path.astar.lists;

public final class DoubleLinkedIntListItem {

	public final int value;
	DoubleLinkedIntListItem prev;
	DoubleLinkedIntListItem next;

	public DoubleLinkedIntListItem(int value) {
		this.value = value;
		assert value >= 0;
	}

	public DoubleLinkedIntListItem() {
		value = -1;
		prev = this;
		next = this;
	}
}
