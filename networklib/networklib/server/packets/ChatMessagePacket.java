package networklib.server.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import networklib.channel.packet.Packet;

/**
 * This subclass of {@link Packet} represents a chat message. It can be used to send the id of the author and the message to others.
 * 
 * @author Andreas Eberle
 * 
 */
public class ChatMessagePacket extends Packet {

	private String authorId;
	private String message;

	public ChatMessagePacket() {
	}

	public ChatMessagePacket(String authorId, String message) {
		this.authorId = authorId;
		this.message = message;
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeUTF(authorId);
		dos.writeUTF(message);
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		authorId = dis.readUTF();
		message = dis.readUTF();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authorId == null) ? 0 : authorId.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChatMessagePacket other = (ChatMessagePacket) obj;
		if (authorId == null) {
			if (other.authorId != null)
				return false;
		} else if (!authorId.equals(other.authorId))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}

	/**
	 * @return the authorId
	 */
	public String getAuthorId() {
		return authorId;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

}
