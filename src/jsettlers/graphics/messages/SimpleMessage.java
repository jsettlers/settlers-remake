package jsettlers.graphics.messages;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.graphics.localization.Labels;

/**
 * This is a message that states that the user was attacked by an other player.
 * 
 * @author michael
 */
public class SimpleMessage implements Message {
	private final byte sender;
	private final ISPosition2D pos;
	private final String message;
	private final EMessageType type;

	public SimpleMessage(EMessageType type, String message, byte sender,
	        ISPosition2D pos) {
		this.type = type;
		this.message = message;
		this.sender = sender;
		this.pos = pos;
	}

	@Override
	public EMessageType getType() {
		return type;
	}

	@Override
	public int getAge() {
		// TODO
		return 0;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public byte getSender() {
		return sender;
	}

	@Override
	public ISPosition2D getPosition() {
		return pos;
	}

	public static Message attacked(byte otherplayer, ISPosition2D pos) {
		String message = Labels.getString("attacked");
		return new SimpleMessage(EMessageType.ATTACKED, message, otherplayer,
		        pos);
	}

	public static Message foundMinerals(EMaterialType type, ISPosition2D pos) {
		String message = Labels.getString("minerals_" + type.toString());
		return new SimpleMessage(EMessageType.MINERALS, message, (byte) -1, pos);
	}
}
