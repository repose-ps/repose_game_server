package com.repose.net;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import com.repose.GameServer;
import com.repose.GameSettings;
import com.repose.commons.LoginConstants;
import com.repose.game.Account;
import com.repose.game.entity.player.PlayerModel;
import com.repose.game.world.World;
import com.repose.net.packet.in.PacketReceiver;

/**
 * The {@code LoginServer} class handles incoming login attempts from user
 * clients.
 * 
 * @author Robert Guidry
 */
public final class LoginServer implements Runnable {

	/**
	 * The key for the login port setting.
	 */
	private static final String SETTING_LOGIN_PORT_KEY = "login_port";

	/**
	 * The default value for the login port setting.
	 */
	private static final int SETTING_LOGIN_PORT_VALUE = 43594;

	/**
	 * The key for the client so timeout setting.
	 */
	private static final String SETTING_CLIENT_SO_TIMEOUT_KEY = "client_so_timeout";

	/**
	 * The default value for the client SO timeout.
	 */
	private static final int SETTING_CLIENT_SO_TIMEOUT_VALUE = (int) TimeUnit.SECONDS.toMillis(30L);

	/**
	 * The RSA block modpow mod value.
	 */
	private static final BigInteger RSA_MODULUS = new BigInteger(
			"99703639055418116132637476155324673153841122940239956816151757083572730851602614878982860067991831949698759605747420005185273892449039291821730166014553550731190625474422621454863005910951872067954316663971883211198763936320497470891261965611464045276117456120317654761378199008838332751032438559842232198613");

	/**
	 * The RSA block modpow exponent value
	 */
	private static final BigInteger RSA_EXPONENT = new BigInteger(
			"43638693173095212661651276145954355357757536826209821036863223462528678427648430756613574768761336398692861588279321133691456566030707349531721301662335180843304046852390353009317726838021821225945846231275773507139951981544445388623189463140381966812844167115931771681306766013699111821859643650353959611591");

	/**
	 * Starts the login server's thread to start accepting incoming connections from
	 * clients.
	 */
	public static void start() throws IOException {
		// start and run the login server thread
		final int port = GameSettings.getSettingAsInt(SETTING_LOGIN_PORT_KEY);
		Thread thread = new Thread(new LoginServer(port));
		thread.start();

	}

	/**
	 * Initializes settings for the LoginServer class.
	 */
	public static void initSettings() {
		final String category = GameSettings.CATEGORY_NETWORK;
		GameSettings.setSetting(SETTING_LOGIN_PORT_KEY, SETTING_LOGIN_PORT_VALUE, category);
		GameSettings.setSetting(SETTING_CLIENT_SO_TIMEOUT_KEY, SETTING_CLIENT_SO_TIMEOUT_VALUE, category);
	}

	/**
	 * The socket listening for incoming login attempts.
	 */
	private final ServerSocket socket;

	/**
	 * Creates a new LoginServer instance that listens on the specified port.
	 * 
	 * @param port the port
	 */
	private LoginServer(int port) throws IOException {
		this.socket = new ServerSocket(port);
	}

	/**
	 * Blocks until an incoming connection is received or the server is closed.
	 */
	@Override
	public final void run() {
		GameServer.getLogger().fine("Login server listening on port: " + this.socket.getLocalPort() + "...");

		final int soTimeoutTime = GameSettings.getSettingAsInt(SETTING_CLIENT_SO_TIMEOUT_KEY);
		while (GameServer.isRunning()) {
			final ClientSession session;
			try {
				final Socket socket = this.socket.accept();
				socket.setSoTimeout(soTimeoutTime);
				session = new ClientSession(socket);
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}

			// start new thread to continue listening for connections while decoding
			// this new thread will eventually start decoding packets from the session
			new Thread(() -> {
				try {
					decodeLogin(session);
				} catch (IOException e) {
					e.printStackTrace();
					session.close();
					return;
				}
			}).start();
		}
	}

	/**
	 * Decodes the client's login packet, validating the information sent and adding
	 * them to a collection of clients attempting to login on the next network
	 * cycle.
	 * 
	 * @param session the client
	 */
	private final void decodeLogin(ClientSession session) throws IOException {
		final int handshakeCode = session.read();
		final int usernameHash = session.read();

		// write 8 bytes which are discarded
		session.write(new byte[8], 0, 8);

		// invalid handshake code
		if (handshakeCode != LoginConstants.HANDSHAKE_CODE) {
			session.write(LoginConstants.RESPONSE_MALFORMED_LOGIN);
			session.flush();
			session.close();
			return;
		}

		// we have accepted the handshake
		session.write(LoginConstants.RESPONSE_HANDSHAKE_ACCEPTED);

		// write our half of the ISAAC seed, this will be sent back to us later.
		final int seed1 = (int) (Math.random() * 99999999D);
		final int seed2 = (int) (Math.random() * 99999999D);
		session.getOutputBuffer().putInt(seed1);
		session.getOutputBuffer().putInt(seed2);
		session.flush(); // flush buffer to stream

		// validate the login opcode
		final int loginOpcode = session.read();
		if (loginOpcode != LoginConstants.LOGIN_OPCODE_NEW_CONNECTION
				&& loginOpcode != LoginConstants.LOGIN_OPCODE_RECONNECTION) {
			session.write(LoginConstants.RESPONSE_MALFORMED_LOGIN);
			session.flush();
			session.close();
			return;
		}

		// read the packet's bytes to the buffer
		final int packetSize = session.read();
		session.readToBuffer(packetSize);

		// decode the packet information before the RSA block
		final int byteVersion = session.getInputBuffer().get() & 0xFF;
		final int shortVersion = session.getInputBuffer().getShort() & 0xFFFF;
//		final boolean lowMemory = session.getInputBuffer().get() == 1;
		session.getInputBuffer().get(); // lowmem == 1

		// read CRC values which are discarded
		for (int i = 0; i < 9; i++)
			session.getInputBuffer().getInt();

		// validate the versions
		boolean incorrectVersion = false;
		incorrectVersion |= byteVersion != LoginConstants.GAME_VERSION_BYTE;
		incorrectVersion |= shortVersion != LoginConstants.GAME_VERSION_SHORT;

		if (incorrectVersion) {
			session.write(LoginConstants.RESPONSE_INVALID_GAME_VERSION);
			session.flush();
			session.close();
			return;
		}

		// decode the RSA block and store in buffer
		final int encryptedRsaSize = session.getInputBuffer().get() & 0xFF;
		final byte[] encryptedRsaBytes = new byte[encryptedRsaSize];
		session.getInputBuffer().get(encryptedRsaBytes);
		final BigInteger encryptedRsaInt = new BigInteger(encryptedRsaBytes);
		final BigInteger decryptedRsaInt = encryptedRsaInt.modPow(RSA_EXPONENT, RSA_MODULUS);
		final byte[] decryptedRsaBytes = decryptedRsaInt.toByteArray();

		// write the decrypted bytes to the buffer
		session.getInputBuffer().rewind();
		System.arraycopy(decryptedRsaBytes, 0, session.getInputBuffer().array(), 0, decryptedRsaBytes.length);

		// validate rsa code
		final int rsaCode = session.getInputBuffer().get() & 0xFF;
		if (rsaCode != LoginConstants.RSA_CODE) {
			session.write(LoginConstants.RESPONSE_MALFORMED_LOGIN);
			session.flush();
			session.close();
			return;
		}

		// ISAAC seed
		final int[] isaacSeed = new int[4];
		for (int i = 0; i < 4; i++) {
			isaacSeed[i] = session.getInputBuffer().getInt();
		}
		session.getInputBuffer().getInt(); // uid

		// login details
		final String username = session.readJagString();
		final String password = session.readJagString();

		long nameAsLong = PlayerModel.encodeUsernameAsLong(username);
		int serverNameHash = (int) (nameAsLong >> 16 & 31L);

		if (usernameHash != serverNameHash) {
			session.write(LoginConstants.RESPONSE_MALFORMED_LOGIN);
			session.flush();
			session.close();
			return;
		}

		// TODO profile / login validation

		// create the account instance
		final Account account = new Account(username, password, session);
		World.addAccount(account);

		// start decoding packets
		new PacketReceiver(account, isaacSeed);
	}

}
