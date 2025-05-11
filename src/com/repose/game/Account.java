package com.repose.game;

import com.repose.game.entity.actor.player.Player;
import com.repose.net.ClientSession;
import com.repose.net.packet.Packet;
import com.repose.net.packet.Packet.PacketSizeType;
import com.repose.net.packet.in.IncomingPacketDispatcher;
import com.repose.net.packet.out.PacketBuilder;

/**
 * The {@code Account} class represents a client's logged in profile. It
 * contains all the information related to both their profile on the server as
 * well as their character in the game.
 * 
 * @author Robert Guidry
 */
public final class Account {

	/**
	 * The network session that connects the account's client to the server.
	 */
	private final ClientSession session;

	/**
	 * The username used to login to this account instance.
	 */
	private final String username;

	/**
	 * The password used to login to this account instance.
	 */
	private final String password;

	/**
	 * The player instance associated with this account's profile.
	 */
	private final Player player;

	/**
	 * Creates a new Account instance with the underlying session for communication
	 * between the account's client and server.
	 * 
	 * @param username the account's login username
	 * @param password the account's login password
	 * @param session  the underlying session
	 */
	public Account(String username, String password, ClientSession session) {
		this.session = session;
		this.username = username;
		this.password = password;
		this.player = new Player();
		this.player.setUsername(username);
	}

	/**
	 * Sends a message to this account's chat.
	 * 
	 * @param message the message
	 */
	public void sendChatMessage(String message) {
		PacketBuilder builder = new PacketBuilder(63, PacketSizeType.VARIABLE_BYTE);
		builder.putString(message);
		this.getSession().queueOutgoingPacket(builder.toPacket());
	}

	/**
	 * Sends the queued incoming packets to the {@code IncomingPacketDispatcher}
	 * class to be processed.
	 */
	public void processIncomingPackets() {
		Packet packet;
		while ((packet = this.session.pollPacketQueue()) != null) {
			IncomingPacketDispatcher.dispatch(this, packet);
		}
	}

	/**
	 * Updates this Account instance on the current tick to its current state.
	 */
	public void update() {
		processIncomingPackets();
	}

	/**
	 * Returns the network session that connects this account's client to the
	 * server.
	 * 
	 * @return the network session
	 */
	public ClientSession getSession() {
		return this.session;
	}

	/**
	 * Returns the username used to login to this account.
	 * 
	 * @return the username
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * Returns the password used to login to this account.
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Returns the player character instance for this account's profile.
	 * 
	 * @return the player character instance
	 */
	public Player getPlayer() {
		return this.player;
	}

}
