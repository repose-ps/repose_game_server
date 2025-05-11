package com.repose.net.packet.in;

import java.nio.ByteBuffer;

import com.repose.game.Account;
import com.repose.net.packet.Packet;

/**
 * The IncomingPacketHandler interface interprets incoming packets.
 */
public interface IncomingPacketHandler {

	/**
	 * Interprets an incoming packet sent by the client.
	 * 
	 * @param account      the account instance
	 * @param packet       tha packet sent
	 * @param packetBuffer the
	 */
	public abstract void handlePacket(Account account, Packet packet, ByteBuffer packetBuffer);

	public static String readString(ByteBuffer buffer) {
		final int start = buffer.position();
		while (buffer.get() != 10)
			;
		return new String(buffer.array(), start, buffer.position() - 1);
	}

}
