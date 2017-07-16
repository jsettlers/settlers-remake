/*******************************************************************************
 * Copyright (c) 2015 - 2017
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

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;

/**
 * Requests to change the number of requested materials for a trading building
 * 
 * @author Michael Zangl
 */
public class ChangeTradingRequestGuiTask extends SimpleBuildingGuiTask {
	private EMaterialType material;
	private int amount;
	private boolean relative;

	public ChangeTradingRequestGuiTask() {
	}

	public ChangeTradingRequestGuiTask(EGuiAction guiAction, byte playerId, ShortPoint2D bildingPos, EMaterialType material, int amount,
			boolean relative) {
		super(guiAction, playerId, bildingPos);
		this.material = material;
		this.amount = amount;
		this.relative = relative;
	}

	public EMaterialType getMaterial() {
		return material;
	}

	public int getAmount() {
		return amount;
	}

	public boolean isRelative() {
		return relative;
	}

	@Override
	protected void serializeTask(DataOutputStream dos) throws IOException {
		super.serializeTask(dos);
		dos.writeByte(material.ordinal);
		dos.writeInt(amount);
		dos.writeBoolean(relative);
	}

	@Override
	protected void deserializeTask(DataInputStream dis) throws IOException {
		super.deserializeTask(dis);
		material = EMaterialType.VALUES[dis.readByte()];
		amount = dis.readInt();
		relative = dis.readBoolean();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + amount;
		result = prime * result + ((material == null) ? 0 : material.hashCode());
		result = prime * result + (relative ? 1231 : 1237);
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
		ChangeTradingRequestGuiTask other = (ChangeTradingRequestGuiTask) obj;
		if (amount != other.amount)
			return false;
		if (material != other.material)
			return false;
		return relative == other.relative;
	}

	@Override
	public String toString() {
		return "ChangeTradingRequestGuiTask [material=" + material + ", amount=" + amount + ", relative=" + relative + ", getBuildingPos()="
				+ getBuildingPos() + ", getGuiAction()=" + getGuiAction() + ", getPlayer()=" + getPlayerId() + "]";
	}
}
