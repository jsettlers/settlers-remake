package jsettlers.logic.map.random.voronoi;

/**
 * This is a point on the beach line that separates the part of the beach line
 * in two areas. It is later converted into a voronoi separation line.
 * <p>
 * It is used to construct the binary search tree.
 * 
 * @author michael
 */
public class BeachSeparator implements BeachTreeItem {
	private BeachTreeItem leftChild;
	private BeachTreeItem rightChild;
	private BeachSeparator parent;

	@Override
	public BeachTreeItem getTopChild() {
		return leftChild;
	}

	@Override
	public BeachTreeItem getBottomChild() {
		return rightChild;
	}

	@Override
	public BeachSeparator getParent() {
		return parent;
	}

	public void replaceChild(BeachSeparator oldChild, BeachTreeItem newCHild) {
		if (oldChild.equals(leftChild)) {
			this.setLeftChild(leftChild);
		} else if (oldChild.equals(rightChild)) {
			this.setRightChild(newCHild);
		} else {
			throw new IllegalArgumentException(
			        "The node to be replaced is not a child of this node");
		}
	}

	private void setLeftChild(BeachTreeItem leftChild) {
		if (leftChild.getParent() != null) {
			throw new IllegalArgumentException(
			        "tried to add an already added node to the tree.");
		}
		this.leftChild = leftChild;
	}

	private void setRightChild(BeachTreeItem rightChild) {
		if (rightChild.getParent() != null) {
			throw new IllegalArgumentException(
			        "tried to add an already added node to the tree.");
		}
		this.rightChild = rightChild;
	}

}
