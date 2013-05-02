package networklib.server.actions.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import networklib.NetworkConstants;
import networklib.channel.Packet;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class ArrayOfMatchInfosPacket extends Packet {

	private MatchInfoPacket[] matches;

	public ArrayOfMatchInfosPacket() {
		super(NetworkConstants.Keys.ARRAY_OF_MATCHES);
	}

	public ArrayOfMatchInfosPacket(MatchInfoPacket[] matches) {
		this();
		this.matches = matches;
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeInt(matches.length);
		for (int i = 0; i < matches.length; i++) {
			matches[i].serialize(dos);
		}
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		int length = dis.readInt();
		matches = new MatchInfoPacket[length];

		for (int i = 0; i < length; i++) {
			matches[i] = new MatchInfoPacket();
			matches[i].deserialize(dis);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(matches);
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
		ArrayOfMatchInfosPacket other = (ArrayOfMatchInfosPacket) obj;
		if (!Arrays.equals(matches, other.matches))
			return false;
		return true;
	}

	public MatchInfoPacket[] getMatches() {
		return matches;
	}

	public void setMatches(MatchInfoPacket[] matches) {
		this.matches = matches;
	}

}
