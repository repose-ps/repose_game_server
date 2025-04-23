package com.repose.net.packet.in;

import java.nio.ByteBuffer;

import com.repose.GameServer;
import com.repose.game.Account;
import com.repose.net.packet.Packet;

/**
 * Dispatches incoming packets for account instances to the appropriate
 * IncomingPacketHandler.
 * 
 * @author Robert Guidry
 */
public final class IncomingPacketDispatcher {

	/**
	 * The array of reach packet handler with the index being the opcode of the
	 * packet.
	 */
	private static final IncomingPacketHandler[] HANDLERS = new IncomingPacketHandler[256];

	static {
	}

	/**
	 * Handles an incoming packet to be processed by the specified account.
	 * 
	 * @param account the account
	 * @param packet  the incoming packet
	 */
	public static void dispatch(Account account, Packet packet) {
		final IncomingPacketHandler handler = HANDLERS[packet.getOpcode()];

		if (handler == null) {
			GameServer.getLogger().fine("Unhandled packet: " + packet.toString());
		} else {
			final ByteBuffer buffer = ByteBuffer.wrap(packet.getPayload());
			handler.handlePacket(account, packet, buffer);
		}
	}

}
