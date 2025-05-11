package com.repose.net.packet.in;

import java.nio.ByteBuffer;

import com.repose.GameServer;
import com.repose.commons.PacketConstants;
import com.repose.game.Account;
import com.repose.net.packet.Packet;
import com.repose.net.packet.in.impl.ChatCommandPacketHandler;
import com.repose.net.packet.in.impl.WalkingPacketHandler;

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
		HANDLERS[PacketConstants.CHAT_COMMAND_OPCODE] = new ChatCommandPacketHandler();

		final WalkingPacketHandler walking = new WalkingPacketHandler();
		HANDLERS[PacketConstants.GAME_SCREEN_WALKING_OPCODE] = walking;
		HANDLERS[PacketConstants.INTERACTION_WALKING_OPCODE] = walking;
		HANDLERS[PacketConstants.MINIMAP_WALKING_OPCODE] = walking;

		final IncomingPacketHandler silentPacket = new IncomingPacketHandler() {

			@Override
			public void handlePacket(Account account, Packet packet, ByteBuffer packetBuffer) {
				// do nothing
			}
		};

		// ignore these packets (for now)
		HANDLERS[PacketConstants.CAMERA_MOVED_OPCODE] = silentPacket;

		// probably never handled
		HANDLERS[PacketConstants.CLICKING_GAME_WINDOW] = silentPacket; // clicking screen
		HANDLERS[PacketConstants.DEFAULT_UPDATE_OPCODE] = silentPacket; // something with updating?
		HANDLERS[PacketConstants.CLIENT_FOCUS_CHANGED_OPCODE] = silentPacket; // client gaining/losing focus
		HANDLERS[PacketConstants.IDLE_TIMEOUT_OPCODE] = silentPacket; // client idle
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
