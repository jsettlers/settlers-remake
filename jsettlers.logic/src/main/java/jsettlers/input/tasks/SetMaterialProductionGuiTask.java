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

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.action.SetMaterialProductionAction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author codingberlin
 */
public class SetMaterialProductionGuiTask extends SimpleGuiTask {

	private float ratio;
	private SetMaterialProductionAction.EMaterialProductionType productionType;
	private EMaterialType materialType;
	private ShortPoint2D position;

	public SetMaterialProductionGuiTask() {}

	public SetMaterialProductionGuiTask(byte playerId, ShortPoint2D position, EMaterialType materialType, SetMaterialProductionAction
			.EMaterialProductionType productionType, float ratio) {
		super(EGuiAction.SET_MATERIAL_PRODUCTION, playerId);
		this.ratio = ratio;
		this.productionType = productionType;
		this.materialType = materialType;
		this.position = position;
	}

	public float getRatio() {
		return ratio;
	}

	public SetMaterialProductionAction.EMaterialProductionType getProductionType() {
		return productionType;
	}

	public EMaterialType getMaterialType() {
		return materialType;
	}

	public ShortPoint2D getPosition() {
		return position;
	}

	@Override
	protected void serializeTask(DataOutputStream dos) throws IOException {
		super.serializeTask(dos);
		SimpleGuiTask.serializePosition(dos, position);
		dos.writeInt(materialType.ordinal());
		dos.writeInt(productionType.ordinal());
		dos.writeFloat(ratio);
	}

	@Override
	protected void deserializeTask(DataInputStream dis) throws IOException {
		super.deserializeTask(dis);
		position = SimpleGuiTask.deserializePosition(dis);
		materialType = EMaterialType.VALUES[dis.readInt()];
		productionType = SetMaterialProductionAction.EMaterialProductionType.VALUES[dis.readInt()];
		ratio = dis.readFloat();
	}
}
