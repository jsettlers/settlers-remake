package jsettlers.input.tasks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;

/**
 * This task is used to set the distribution settings for a material in a manager.
 * 
 * @author Andreas Eberle
 */
public class SetMaterialDistributionSettingsGuiTask extends SimpleGuiTask {
	private ShortPoint2D managerPosition;
	private EMaterialType materialType;
	private float[] probabilities;

	public SetMaterialDistributionSettingsGuiTask() {
	}

	public SetMaterialDistributionSettingsGuiTask(byte playerId, ShortPoint2D managerPosition, EMaterialType materialType, float[] probabilities) {
		super(EGuiAction.SET_MATERIAL_DISTRIBUTION_SETTINGS, playerId);
		this.managerPosition = managerPosition;
		this.materialType = materialType;
		this.probabilities = fixProbabilities(probabilities);
	}

	private static float[] fixProbabilities(float[] probabilities) {
		float sum = 0;
		for (float f : probabilities) {
			sum += Math.max(f, 0);
		}

		float offset = 0;
		float factor = 0;
		if (sum <= 0.0001f) {
			offset = 1.0f / probabilities.length;
		} else {
			factor = 1.01f / sum;
		}

		float[] array = new float[probabilities.length];
		for (int i = 0; i < array.length; i++) {
			array[i] = Math.max(probabilities[i], 0) * factor + offset;
		}
		return array;
	}

	/**
	 * @return Returns a position occupied by the manager this settings shall be used for.
	 */
	public ShortPoint2D getManagerPosition() {
		return managerPosition;
	}

	/**
	 * @return Returns the {@link EMaterialType} this settings shall be used for.
	 */
	public EMaterialType getMaterialType() {
		return materialType;
	}

	/**
	 * @return Returns the new distribution probabilities. The values correspond to the {@link EBuildingType} given by
	 *         MaterialsOfBuildings.getBuildingTypesRequestingMaterial( {@link #getMaterialType()}).
	 */
	public float[] getProbabilities() {
		return probabilities;
	}

	@Override
	protected void serializeTask(DataOutputStream dos) throws IOException {
		super.serializeTask(dos);
		SimpleGuiTask.serializePosition(dos, managerPosition);
		dos.writeByte(materialType.ordinal);

		dos.writeInt(probabilities.length);
		for (int i = 0; i < probabilities.length; i++) {
			dos.writeFloat(probabilities[i]);
		}
	}

	@Override
	protected void deserializeTask(DataInputStream dis) throws IOException {
		super.deserializeTask(dis);
		managerPosition = SimpleGuiTask.deserializePosition(dis);
		materialType = EMaterialType.values[dis.readByte()];

		int length = dis.readInt();
		probabilities = new float[length];
		for (int i = 0; i < length; i++) {
			probabilities[i] = dis.readFloat();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((managerPosition == null) ? 0 : managerPosition.hashCode());
		result = prime * result + ((materialType == null) ? 0 : materialType.hashCode());
		result = prime * result + Arrays.hashCode(probabilities);
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
		SetMaterialDistributionSettingsGuiTask other = (SetMaterialDistributionSettingsGuiTask) obj;
		if (managerPosition == null) {
			if (other.managerPosition != null)
				return false;
		} else if (!managerPosition.equals(other.managerPosition))
			return false;
		if (materialType != other.materialType)
			return false;
		if (!Arrays.equals(probabilities, other.probabilities))
			return false;
		return true;
	}
}