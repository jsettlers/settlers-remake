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
package jsettlers.input.tasks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
	private EBuildingType buildingType;
	private float ratio;

	public SetMaterialDistributionSettingsGuiTask() {
	}

	public SetMaterialDistributionSettingsGuiTask(byte playerId, ShortPoint2D managerPosition, EMaterialType materialType, EBuildingType buildingType, float ratio) {
		super(EGuiAction.SET_MATERIAL_DISTRIBUTION_SETTINGS, playerId);
		this.managerPosition = managerPosition;
		this.materialType = materialType;
		this.buildingType = buildingType;
		this.ratio = ratio;
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

	public EBuildingType getBuildingType() {
		return buildingType;
	}

	public float getRatio() {
		return ratio;
	}

	@Override
	protected void serializeTask(DataOutputStream dos) throws IOException {
		super.serializeTask(dos);
		SimpleGuiTask.serializePosition(dos, managerPosition);
		dos.writeByte(materialType.ordinal);
		dos.writeByte(buildingType.ordinal);
		dos.writeFloat(ratio);
	}

	@Override
	protected void deserializeTask(DataInputStream dis) throws IOException {
		super.deserializeTask(dis);
		managerPosition = SimpleGuiTask.deserializePosition(dis);
		materialType = EMaterialType.VALUES[dis.readByte()];
		buildingType = EBuildingType.VALUES[dis.readByte()];
		ratio = dis.readFloat();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;

		SetMaterialDistributionSettingsGuiTask that = (SetMaterialDistributionSettingsGuiTask) o;

		if (Float.compare(that.ratio, ratio) != 0)
			return false;
		if (managerPosition != null ? !managerPosition.equals(that.managerPosition) : that.managerPosition != null)
			return false;
		if (materialType != that.materialType)
			return false;
		return buildingType == that.buildingType;

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (managerPosition != null ? managerPosition.hashCode() : 0);
		result = 31 * result + (materialType != null ? materialType.hashCode() : 0);
		result = 31 * result + (buildingType != null ? buildingType.hashCode() : 0);
		result = 31 * result + (ratio != +0.0f ? Float.floatToIntBits(ratio) : 0);
		return result;
	}
}