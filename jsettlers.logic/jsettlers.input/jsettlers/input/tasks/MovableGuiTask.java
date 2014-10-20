package jsettlers.input.tasks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class MovableGuiTask extends SimpleGuiTask {
	private List<Integer> selection;

	public MovableGuiTask() {
	}

	public MovableGuiTask(EGuiAction action, byte playerId, List<Integer> selection) {
		super(action, playerId);
		this.selection = selection;
	}

	public List<Integer> getSelection() {
		return selection;
	}

	@Override
	protected void serializeTask(DataOutputStream dos) throws IOException {
		super.serializeTask(dos);

		dos.writeInt(selection.size());
		for (Integer curr : selection) {
			dos.writeInt(curr);
		}
	}

	@Override
	protected void deserializeTask(DataInputStream dis) throws IOException {
		super.deserializeTask(dis);

		int numberOfElements = dis.readInt();
		selection = new ArrayList<Integer>(numberOfElements);
		for (int i = 0; i < numberOfElements; i++) {
			selection.add(dis.readInt());
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((selection == null) ? 0 : selection.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MovableGuiTask other = (MovableGuiTask) obj;
		if (selection == null) {
			if (other.selection != null)
				return false;
		} else if (!selection.equals(other.selection))
			return false;
		return true;
	}
}
