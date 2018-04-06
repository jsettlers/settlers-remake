/*
 * Copyright (c) 2018
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
 */
package jsettlers.common.menu.messages;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;

/**
 * This is a messageLabel that states that the user was attacked by an other player.
 *
 * @author Michael Zangl
 */
public class SimpleMessage implements IMessage {
	private final byte sender;
	private final ShortPoint2D pos;
	private final String messageLabel;
	private final EMessageType type;
	private int age;

	/**
	 * Creates a new simple chat messageLabel.
	 *
	 * @param type
	 * 		The messageLabel type.
	 * @param messageLabel
	 * 		The messageLabel string to display.
	 * @param sender
	 * 		The sender of the messageLabel
	 * @param pos
	 * 		The position the messageLabel was sent from.
	 */
	private SimpleMessage(EMessageType type, String messageLabel, byte sender, ShortPoint2D pos) {
		this.type = type;
		this.messageLabel = messageLabel;
		this.sender = sender;
		this.pos = pos;
		this.age = 0;
	}

	@Override
	public EMessageType getType() {
		return type;
	}

	@Override
	public int getAge() {
		return this.age;
	}

	@Override
	public int ageBy(int interval) {
		this.age += interval;
		return this.age;
	}

	@Override
	public String getMessageLabel() {
		return messageLabel;
	}

	@Override
	public byte getSender() {
		return sender;
	}

	@Override
	public ShortPoint2D getPosition() {
		return pos;
	}

	@Override
	public boolean duplicates(IMessage m) {
		if ((m.getSender() == this.sender)
				&& m.getMessageLabel().equals(this.messageLabel)
				&& m.getType() == this.type) {
			if ((this.type == EMessageType.ATTACKED) || (this.type == EMessageType.MINERALS)) {
				if (m.getAge() < MESSAGE_TTL / 6) {
					return this.pos.getOnGridDistTo(m.getPosition()) < MESSAGE_DIST_THRESHOLD;
				}
			} else {
				return this.pos.equals(m.getPosition());
			}
		}
		return false;
	}

	/**
	 * Creates a new attacked-messageLabel.
	 *
	 * @param otherplayer
	 * 		The attacking player
	 * @param pos
	 * 		The position that player attacked on.
	 * @return THe messageLabel.
	 */
	public static IMessage attacked(byte otherplayer, ShortPoint2D pos) {
		return new SimpleMessage(EMessageType.ATTACKED, "attacked", otherplayer, pos);
	}

	/**
	 * Create a new messageLabel if a geologist found minerals.
	 *
	 * @param type
	 * 		The type of minerals.
	 * @param pos
	 * 		The position
	 * @return The messageLabel object
	 */
	public static IMessage foundMinerals(EMaterialType type, ShortPoint2D pos) {
		return new SimpleMessage(EMessageType.MINERALS, "minerals_" + type.toString(), (byte) -1, pos);
	}

	/**
	 * Create a new messageLabel that a building cannot find any more work.
	 *
	 * @param building
	 * 		The building
	 * @return THe messageLabel object
	 */
	public static IMessage cannotFindWork(IBuilding building) {
		return new SimpleMessage(EMessageType.NOTHING_FOUND_IN_SEARCH_AREA, "cannot_find_work_" + building.getBuildingType(), (byte) -1, building.getPos());
	}

}
