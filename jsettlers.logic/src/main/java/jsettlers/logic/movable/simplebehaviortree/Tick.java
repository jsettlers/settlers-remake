package jsettlers.logic.movable.simplebehaviortree;
import java.util.LinkedList;
import java.util.Stack;

import jsettlers.logic.movable.simplebehaviortree.nodes.Node;
import jsettlers.logic.movable.simplebehaviortree.nodes.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.nodes.Root;

public class Tick<T> {
	public Root Root;
	public T Target;
	private Stack<Node> openNodes = new Stack<>();
	private boolean blockOpenNodes = false;
	
	public Tick(T target, Root root) {
		this.Root = root;
		this.Target = target; 
	}

	
	public NodeStatus Tick() {
		LinkedList<Node> lastOpenNodes = new LinkedList<Node>(openNodes);
		openNodes.clear();
		NodeStatus state = Root.execute(this);
		for(Node node : openNodes) {
			if (lastOpenNodes.size() <= 0) break;
			if (node == lastOpenNodes.peek())
				lastOpenNodes.removeFirst();
			else
				break;
		}
		blockOpenNodes = true;
		for(Node node : lastOpenNodes) {
			node.close(this);
		}
		blockOpenNodes = false;
		return state;
	}
	
	public void EnterNode(Node node) {
		if (!blockOpenNodes)
			openNodes.push(node);
	}
	
	public void OpenNode(Node node) {
		
	}
	
	public void TickNode(Node node) {
		
	}
	
	public void CloseNode(Node node) {
		if (!blockOpenNodes)
			openNodes.pop();
	}
	
	public void ExitNode(Node node) {
		
	}
	

}
