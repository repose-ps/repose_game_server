package com.repose.game.world;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.repose.GameServer;
import com.repose.GameSettings;
import com.repose.commons.LoginConstants;
import com.repose.commons.PacketConstants;
import com.repose.game.Account;
import com.repose.net.ClientSession;
import com.repose.net.packet.Packet.PacketSizeType;
import com.repose.net.packet.out.PacketBuilder;

/**
 * The {@code World} class handles updating the game world and the entities
 * inside of it.
 * 
 * @author Robert Guidry
 */
public final class World {

	/**
	 * The accounts that have finished logging in but still need to be added to the
	 * world.
	 */
	private static Queue<Account> accountsLoggingIn = new LinkedList<>();

	/**
	 * Accounts that are currently in the world.
	 */
	private static Account[] accounts;

	/**
	 * Initializes the settings for the World class.
	 */
	public static void initSettings() {
		final String category = GameSettings.CATEGORY_WORLD;
		GameSettings.setSetting(WorldSettings.SETTING_MAX_ACCOUNTS_KEY, WorldSettings.SETTING_MAX_PLAYERS_VALUE,
				category);
		GameSettings.setSetting(WorldSettings.SETTING_TICK_PERIOD_KEY, WorldSettings.SETTING_TICK_PERIOD_VALUE,
				category);
		GameSettings.setSetting(WorldSettings.SETTING_TICK_UNIT_KEY, WorldSettings.SETTING_TICK_UNIT_VALUE, category);
	}

	/**
	 * Initializes the World class post-settings loading.
	 */
	public static void initialize() {
		final int maxAccounts = GameSettings.getSettingAsInt(WorldSettings.SETTING_MAX_ACCOUNTS_KEY);
		accounts = new Account[maxAccounts];
	}

	/**
	 * Adds an account to the queue of accounts waiting to be added to the world.
	 * 
	 * @param account the account
	 */
	public static void addAccount(Account account) {
		synchronized (accountsLoggingIn) {
			accountsLoggingIn.add(account);
		}
	}

	public static void removeAccount(Account account) {
		// remove from world
		final int worldIndex = account.getPlayer().getWorldIndex();

		if (worldIndex == -1)
			return;

		if (accounts[worldIndex] != null && accounts[worldIndex].equals(account)) {
			accounts[worldIndex] = null;
		}
		account.getPlayer().setWorldIndex(-1);
		account.getSession().close();
		System.out.println("Logout for " + account.getUsername());
	}

	public static void start() {
		final long tickPeriod = GameSettings.getSettingAsLong(WorldSettings.SETTING_TICK_PERIOD_KEY);
		final TimeUnit tickUnit = TimeUnit.valueOf(GameSettings.getSetting(WorldSettings.SETTING_TICK_UNIT_KEY));
		final Runnable task = () -> {
			try {
				tick();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		};
		final long delay = 0L;

		// start the world tick
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(task, delay, tickPeriod, tickUnit);
	}

	/**
	 * Advances the game world by one tick, updating all of the entities contained.
	 */
	private static final void tick() {
		// add new accounts
		synchronized (accountsLoggingIn) {
			addAccountsLoggingIn();
		}

		// update the accounts
		forEachAccount(account -> account.update());

		// handle queued account packets
		forEachAccount(account -> account.processIncomingPackets());

		// update account's player movement
		forEachAccount(account -> {
			account.getPlayer().getWalkingQueue().pulse();
			account.getPlayer().getScene().updateMap();
		});

		// remove accounts no longer connected
		removeDisconnectedAccounts();

		// write scene packet
		forEachAccount(account -> World.writeUpdateScenePlayersPacket(account));

		// send each accounts outgoing packets
		forEachAccount(account -> account.getSession().writeOutgoingPackets());

		// clear update flags for accounts
		forEachAccount(account -> account.getPlayer().clearStoredVariables());

		// remove accounts no longer connected
		removeDisconnectedAccounts();
	}

	/**
	 * Streams each non-null account to accept the specified consumer.
	 * 
	 * @param consumer the consumer
	 */
	public static void forEachAccount(Consumer<Account> consumer) {
		Arrays.stream(accounts).filter(Objects::nonNull).forEach(account -> consumer.accept(account));
	}

	private static void removeDisconnectedAccounts() {
		for (int i = 1; i < accounts.length; i++) {
			if (accounts[i] == null || !accounts[i].getSession().isClosed())
				continue;
			GameServer.getLogger().finer("Logout for " + accounts[i].getUsername());
			accounts[i].getPlayer().setWorldIndex(-1);
			accounts[i].getPlayer().getPosition().setPosition(0, 0, 0);
			accounts[i] = null;
		}
	}

	/**
	 * Adds all the accounts that have logged in since the last tick.
	 */
	private static void addAccountsLoggingIn() {
		Account account;
		while ((account = accountsLoggingIn.poll()) != null) {

			int index = -1;
			// determine the account's index
			for (int i = 1; i < accounts.length; i++) {
				if (accounts[i] == null) {
					index = i;
					break;
				}
			}

			// write login response
			final ClientSession session = account.getSession();
			if (index != -1) {
				// write response
				session.write(LoginConstants.RESPONSE_LOGIN_ACCEPTED);
				session.write(0); // TODO privilege
				session.write(0); // flagged
				accounts[index] = account;
				account.getPlayer().setWorldIndex(index);

				GameServer.getLogger().finer("Login from " + account.getUsername());
			} else {
				session.write(LoginConstants.RESPONSE_WORLD_FULL);
			}
			try {
				session.flush();
				if (index == -1) {
					session.close();
				}
			} catch (IOException e) {
				session.close();
			}
		}
	}

	private static void writeUpdateScenePlayersPacket(Account account) {
		// update the loaded map first
		if (account.getPlayer().getScene().isMapUpdating()) {
			PacketBuilder mapBuilder = new PacketBuilder(PacketConstants.LOAD_MAP_AROUND_PLAYER_OPCODE,
					PacketSizeType.FIXED);
			mapBuilder.putShort(account.getPlayer().getScene().getLoadedCenterRegionX());
			mapBuilder.putShort(account.getPlayer().getScene().getLoadedCenterRegionY());
			account.getSession().queueOutgoingPacket(mapBuilder.toPacket());
		}

		// we're going to create an empty update that just sets the player's position.
		PacketBuilder sceneBuilder = new PacketBuilder(PacketConstants.UPDATE_SCENE_PLAYERS_OPCODE,
				PacketSizeType.VARIABLE_SHORT);

		sceneBuilder.startBitBlock();
		account.getPlayer().getScene().appendLocalPlayerUpdate(sceneBuilder);
		account.getPlayer().getScene().updateOldPlayers(sceneBuilder);
		account.getPlayer().getScene().addNewPlayers(sceneBuilder);
		sceneBuilder.endBitBlock();

		if (account.getPlayer().getScene().hasQueuedUpdateBlocks()) {
			account.getPlayer().getScene().writeUpdateBlocks(sceneBuilder);
		}

		account.getSession().queueOutgoingPacket(sceneBuilder.toPacket());
	}

	/**
	 * The World class is non instantiated.
	 */
	private World() {
	}
}
