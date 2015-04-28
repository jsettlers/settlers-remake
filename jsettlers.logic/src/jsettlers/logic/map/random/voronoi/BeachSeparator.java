/*******************************************************************************
 * Copyright (c) 2015
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
package jsettlers.logic.map.random.voronoi;

/**
 * This is a point on the beach line that separates the part of the beach line in two areas. It is later converted into a voronoi separation line.
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
