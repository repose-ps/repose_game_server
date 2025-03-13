package com.repose;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.repose.util.CollectionUtil;

/**
 * The {@code GameSettings} class handles initial settings that are used by the
 * game server.
 * 
 * @author Robert Guidry
 */
public final class GameSettings {

	/**
	 * The network settings category name as a String.
	 */
	public static final String CATEGORY_NETWORK = "network";

	/**
	 * The world settings category name as a String.
	 */
	public static final String CATEGORY_WORLD = "world";

	/**
	 * The name of the file used to read and write the settings to.
	 */
	private static final String SETTINGS_FILE_NAME = ".game_settings";

	/**
	 * The map that contains the name of the setting (key) and the value of the
	 * named setting.
	 */
	private static final Map<String, String> settingsMap = new HashMap<>();

	/**
	 * The category that settings are stored in.
	 */
	private static final Map<String, String> settingsCategoryMap = new HashMap<>();

	/**
	 * Returns the value of the specified setting key name, or {@code null} if their
	 * is no value assigned to the setting.
	 * 
	 * @param key the setting key name
	 * @return the value, or {@code null} if no value is assigned to the key
	 */
	public static String getSetting(String key) {
		return settingsMap.get(key);
	}

	/**
	 * Returns the value of the specified key name as an int value using
	 * {@link Integer#parseInt(String)}, or {@code 0} if their is no value assigned
	 * to the key.
	 * 
	 * @param key the setting key
	 * @return the int value, or 0 if there is no associated value for the key
	 */
	public static int getSettingAsInt(String key) {
		final String value = getSetting(key);
		if (value == null)
			return 0;
		return Integer.parseInt(value);
	}

	/**
	 * Returns the value of the specified key name as a long value using
	 * {@link Long#parseLong(String)}, or {@code 0} if their is no value assigned to
	 * the key.
	 * 
	 * @param key the setting key
	 * @return the long value, or 0 if there is no associated value for the key
	 */
	public static long getSettingAsLong(String key) {
		final String value = getSetting(key);
		if (value == null)
			return 0;
		return Integer.parseInt(value);
	}

	/**
	 * Sets a setting value in the settings map.
	 * 
	 * @param key   the setting's key name
	 * @param value the setting's value
	 */
	public static void setSetting(String key, Object value) {
		if (value == null) {
			return;
		}
		settingsMap.put(key, value.toString());
	}

	/**
	 * Sets a setting value in the settings map under the specified category.
	 * 
	 * @param key      the setting's key name
	 * @param value    the setting's value
	 * @param category the setting's category name
	 */
	public static void setSetting(String key, Object value, String category) {
		setSetting(key, value);
		settingsCategoryMap.put(key, category);
	}

	/**
	 * Loads the values from the settings file into the settings map.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	public static void loadSettingsFile() throws IOException {
		final File file = new File(SETTINGS_FILE_NAME);

		// create the file if it does not exist
		if (!file.exists()) {
			file.createNewFile();
		}

		// create a list of keys that are not set by the settings file
		// which will be written later
		Set<String> unsetKeys = new HashSet<>();
		unsetKeys.addAll(settingsMap.keySet());

		// read the settings file lines
		List<String> lines = Files.readAllLines(Paths.get(SETTINGS_FILE_NAME));

		// parse each line
		for (String line : lines) {
			if (!line.contains("="))
				continue;
			final String[] split = line.split("=");
			final String key = split[0].trim();
			final String value = line.substring(line.indexOf("=") + 1).trim();
			unsetKeys.remove(key);
			setSetting(key, value);
		}

		if (unsetKeys.size() > 0) {
			final Logger logger = GameServer.getLogger();
			for (String key : unsetKeys) {
				logger.warning("unset default setting: " + key + "=" + settingsMap.get(key));
			}
		}
		writeSettingsFile();
	}

	/**
	 * Writes the values of the settings map to the settings file.
	 */
	private static void writeSettingsFile() throws IOException {
		// create and fill 2 collections for categorized settings and not
		final Map<String, Set<String>> categorizedSettings = new HashMap<>();
		final Set<String> uncategorizedSettings = new HashSet<>();
		for (String settingKey : settingsMap.keySet()) {
			if (settingsCategoryMap.containsKey(settingKey)) {
				final String category = settingsCategoryMap.get(settingKey);
				if (!categorizedSettings.containsKey(category)) {
					categorizedSettings.put(category, new HashSet<>());
				}
				categorizedSettings.get(category).add(settingKey);
			} else {
				uncategorizedSettings.add(settingKey);
			}
		}

		// create the writer for writing the settings file
		final BufferedWriter writer = new BufferedWriter(new FileWriter(new File(GameSettings.SETTINGS_FILE_NAME)));
		writer.write("[SETTINGS FILE]");

		// create list of alphabetized categories
		final List<String> alphabetizedCategories = CollectionUtil.getSortedList(categorizedSettings.keySet());

		// write the categorized settings
		for (String category : alphabetizedCategories) {
			// write category to file
			writer.newLine();
			writer.newLine();
			writer.write("[" + category + "]");

			// create list of alphabetized settings
			final List<String> alphabetizedSettings = CollectionUtil.getSortedList(categorizedSettings.get(category));

			// write each setting
			for (String setting : alphabetizedSettings) {
				writer.newLine();
				writer.write(setting);
				writer.write("=");
				writer.write(settingsMap.get(setting));
			}

		}

		// write the uncategorized settings
		if (uncategorizedSettings.size() > 0) {
			writer.newLine();
			writer.newLine();
			writer.write("[misc]");

			final List<String> alphabetizedSettings = CollectionUtil.getSortedList(uncategorizedSettings);
			for (String setting : alphabetizedSettings) {
				writer.newLine();
				writer.write(setting);
				writer.write("=");
				writer.write(settingsMap.get(setting));
			}
		}

		// finish writing
		writer.close();
	}

}
