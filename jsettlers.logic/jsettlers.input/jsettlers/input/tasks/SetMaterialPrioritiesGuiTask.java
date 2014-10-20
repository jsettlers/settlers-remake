package jsettlers.input.tasks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;

/**
 * This {@link Action} is used to set the priority order of {@link EMaterialType}s.
 * 
 * @author Andreas Eberle
 */
public class SetMaterialPrioritiesGuiTask extends SimpleGuiTask {
	private ShortPoint2D managerPosition;
	private EMaterialType[] materialTypeForPriority;

	public SetMaterialPrioritiesGuiTask() {
	}

	/**
	 * Creates a new {@link SetMaterialPrioritiesGuiTask} to change the priorities of {@link EMaterialType}s.
	 * 
	 * @param playerId
	 *            Id of the player that sends the task.
	 * @param managerPosition
	 *            The position of the manager whose settings shall be changed.
	 * @param materialTypeForPriority
	 *            An array of all droppable {@link EMaterialType}s. The first element has the highest priority, the last one hast the lowest.
	 */
	public SetMaterialPrioritiesGuiTask(byte playerId, ShortPoint2D managerPosition, EMaterialType[] materialTypeForPriority) {
		super(EGuiAction.SET_MATERIAL_PRIORITIES, playerId);

		assert materialTypeForPriority.length == EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS : "The given material types for priorities may only contain droppable materials";

		this.managerPosition = managerPosition;
		this.materialTypeForPriority = materialTypeForPriority;
	}

	/**
	 * @return Returns the position of the manager whose settings will be changed.
	 */
	public ShortPoint2D getManagerPosition() {
		return managerPosition;
	}

	/**
	 * @return Returns an array of droppable {@link EMaterialType}s where the first element has the highest priority.
	 */
	public EMaterialType[] getMaterialTypeForPriority() {
		return materialTypeForPriority;
	}

	@Override
	protected void serializeTask(DataOutputStream dos) throws IOException {
		super.serializeTask(dos);
		SimpleGuiTask.serializePosition(dos, managerPosition);

		dos.writeInt(materialTypeForPriority.length);
		for (int i = 0; i < materialTypeForPriority.length; i++) {
			dos.writeByte(materialTypeForPriority[i].ordinal);
		}
	}

	@Override
	protected void deserializeTask(DataInputStream dis) throws IOException {
		super.deserializeTask(dis);
		managerPosition = SimpleGuiTask.deserializePosition(dis);

		int length = dis.readInt();
		materialTypeForPriority = new EMaterialType[length];
		for (int i = 0; i < length; i++) {
			materialTypeForPriority[i] = EMaterialType.values[dis.readByte()];
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((managerPosition == null) ? 0 : managerPosition.hashCode());
		result = prime * result + Arrays.hashCode(materialTypeForPriority);
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
		SetMaterialPrioritiesGuiTask other = (SetMaterialPrioritiesGuiTask) obj;
		if (managerPosition == null) {
			if (other.managerPosition != null)
				return false;
		} else if (!managerPosition.equals(other.managerPosition))
			return false;
		if (!Arrays.equals(materialTypeForPriority, other.materialTypeForPriority))
			return false;
		return true;
	}
}
