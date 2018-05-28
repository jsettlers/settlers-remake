package jsettlers.logic.movable.simplebehaviortree;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Stack;

public class Tick<T> implements Serializable {
	private static final long serialVersionUID = 3673558738736795584L;

	public final Root<T> root;
	public final T       target;

	private final Stack<Node<T>> openNodes      = new Stack<>();
	private       boolean        blockOpenNodes = false;

	public Tick(T target, Root<T> root) {
		this.root = root;
		this.target = target;
	}

	public NodeStatus tick() {
		LinkedList<Node<T>> lastOpenNodes = new LinkedList<>(openNodes);
		openNodes.clear();
		NodeStatus state = root.execute(this);
		for (Node<T> node : openNodes) {
			if (lastOpenNodes.size() <= 0) {
				break;
			}
			if (node == lastOpenNodes.peek()) {
				lastOpenNodes.removeFirst();
			} else {
				break;
			}
		}
		blockOpenNodes = true;
		for (Node<T> node : lastOpenNodes) {
			node.close(this);
		}
		blockOpenNodes = false;
		return state;
	}

	public void visitNode(Node<T> node) {
		if (!blockOpenNodes) { openNodes.push(node); }
	}

	public void tickNode(Node<T> node) {
	}

	public void leaveNode(Node<T> node) {
		if (!blockOpenNodes) { openNodes.pop(); }
	}
}
