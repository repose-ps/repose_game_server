package com.repose.game.world;

import java.util.concurrent.TimeUnit;

public interface WorldSettings {

	/**
	 * The key for the maximum accounts setting.
	 */
	public static final String SETTING_MAX_ACCOUNTS_KEY = "max_accounts";

	/**
	 * The default value for the maximum accounts setting.
	 */
	public static final int SETTING_MAX_PLAYERS_VALUE = 2000;

	/**
	 * The key for the tick period which is how often the world class calls the tick
	 * method.
	 */
	public static final String SETTING_TICK_PERIOD_KEY = "tick_period";

	/**
	 * The default value for the tick period which is how often the world class
	 * calls the tick method.
	 */
	public static final long SETTING_TICK_PERIOD_VALUE = 600;

	/**
	 * The key for the tick unit key which is the time unit used with the tick
	 * period setting.
	 */
	public static final String SETTING_TICK_UNIT_KEY = "tick_unit";

	/**
	 * The default value for the tick unit key which is the time unit used with the
	 * tick period setting.
	 */
	public static final String SETTING_TICK_UNIT_VALUE = TimeUnit.MILLISECONDS.toString();

}
