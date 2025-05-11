package com.repose;

import java.io.IOException;
import java.util.logging.Logger;

import com.repose.commons.CommonLogger;
import com.repose.game.world.World;
import com.repose.io.cache.Cache;
import com.repose.net.ClientSession;
import com.repose.net.LoginServer;

/**
 * The {@code GameServer} class represents the main class of the application. It
 * handles the application entry point, initializing the server and its
 * resources, and starting the application's main loops.
 * 
 * @author Robert Guidry
 */
public final class GameServer {

	/**
	 * Is the server application currently running?
	 */
	private static boolean running;

	/**
	 * The entry point to the application.
	 * 
	 * @param args the program arguments specified at runtime
	 */
	public static void main(String[] args) {
		try {
			initSettings();
			GameSettings.loadSettingsFile();
			initialize();
			loadData();
			start();
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
	}

	/**
	 * Exits the application.
	 */
	public static void exit() {
		running = false;
		System.exit(0);
	}

	/**
	 * Starts the application after initialization and resource loading.
	 */
	private static void start() {
		running = true;
		try {
			LoginServer.start();
			World.start();
		} catch (IOException e) {
			e.printStackTrace();
			exit();
			return;
		}
	}

	/**
	 * Initializes game code for classes after having the settings values loaded
	 * from the file.
	 */
	private static void initialize() {
		World.initialize();
	}

	/**
	 * Initializes settings values in classes.
	 */
	public static void initSettings() {
		LoginServer.initSettings();
		ClientSession.initSettings();
		World.initSettings();

		getLogger().info("Initialized server code.");
	}

	/**
	 * Loads game data files from the file system.
	 */
	public static void loadData() throws Exception {
		Cache.load();
	}
	
	/**
	 * Returns the application's logger instance.
	 * 
	 * @return the logger instance
	 */
	public static Logger getLogger() {
		return CommonLogger.getLogger();
	}

	/**
	 * Returns {@code true} if the server is currently still running.
	 * 
	 * @return {@code true} if the server is running
	 */
	public static boolean isRunning() {
		return running;
	}

	/**
	 * Sends a FINEST level log message to the server logger.
	 * 
	 * @param message the message to send
	 */
	public static void debug(String message) {
		getLogger().finest("[DEBUG]: " + message);
	}

	/**
	 * The GameServer class is not instantiated.
	 */
	private GameServer() {
	}

}
