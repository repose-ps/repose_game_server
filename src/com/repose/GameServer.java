package com.repose;

import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

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
	 * The logger used for the application to log to the console.
	 */
	private static final Logger LOGGER = Logger.getLogger(GameServer.class.toString());

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
			initialize();
			GameSettings.loadSettingsFile();
			start();
		} catch (IOException e) {
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
		} catch (IOException e) {
			e.printStackTrace();
			exit();
			return;
		}
	}

	/**
	 * Initializes code in classes. Note that this is not for loading resources.
	 */
	private static void initialize() {
		initLogger();
		LoginServer.initialize();
		ClientSession.initialize();

		LOGGER.info("Initialized server code.");
	}

	/**
	 * Sets the initial settings of the game server logger.
	 */
	private static void initLogger() {
		LOGGER.setUseParentHandlers(false);
		LOGGER.setLevel(Level.FINEST);
		LOGGER.addHandler(new Handler() {

			private static SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.SSS");

			@Override
			public void publish(LogRecord record) {
				if (!this.isLoggable(record))
					return;

				// build the message to log
				StringBuilder message = new StringBuilder();

				// level
				message.append("[");
				message.append(record.getLevel().toString());
				message.append("]");

				// time
				message.append("[");
				message.append(dateFormat.format(record.getMillis()));
				message.append("]: ");

				// message
				message.append(record.getMessage());

				// source
				message.append(" [");
				message.append(record.getSourceClassName());
				message.append(".");
				message.append(record.getSourceMethodName());
				message.append("()");
				message.append("]");

				// the stream we are going to print to
				final PrintStream consoleStream;
				if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
					consoleStream = System.err;
				} else {
					consoleStream = System.out;
				}

				// print the built message to the console
				consoleStream.println(message.toString());
			}

			@Override
			public void flush() {
			}

			@Override
			public void close() throws SecurityException {
			}
		});
		LOGGER.info("Logger created!");
	}

	/**
	 * Returns the application's logger instance.
	 * 
	 * @return the logger instance
	 */
	public static Logger getLogger() {
		return LOGGER;
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
	 * The GameServer class is not instantiated.
	 */
	private GameServer() {
	}

}
