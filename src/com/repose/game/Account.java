package com.repose.game;

import com.repose.net.ClientSession;

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

}
