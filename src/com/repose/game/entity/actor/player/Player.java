package com.repose.game.entity.actor.player;

import java.util.concurrent.ThreadLocalRandom;

import com.repose.game.Account;
import com.repose.io.cache.map.MapRegion;

/**
 * The {@code Player} class represents a {@link PlayerModel} instance that is
 * controlled by an {@link Account}.
 */
public final class Player extends PlayerModel {

	private final Scene scene;

	/**
	 * Creates a new Player instance.
	 */
	public Player() {
		this.teleport(3222, 3222, 0);
		this.scene = new Scene(this);

		// TODO remove debug
		final int[] array = this.getBodyModelColors();
		for (int i = 0; i < array.length; i++) {
			final int color = array[i] == 0 ? 0 : ThreadLocalRandom.current().nextInt(array[i]);
			this.getBodyModelColors()[i] = color;
		}
	}

	@Override
	public void setWorldIndex(int index) {
		super.setWorldIndex(index);

		this.teleport(3222, 3222 + index, 0); // TODO debug for multiplayer rendering
	}

	/**
	 * Returns this player's X coordinate within their own loaded map area.
	 * 
	 * @return the local X coordinate
	 */
	public int getLocalX() {
		final int topLeftRegionX = this.getScene().getLoadedCenterRegionX() - Scene.LOADED_MAP_REGION_RADIUS;
		final int topLeftAbsX = topLeftRegionX * MapRegion.TILE_DIMENSION;
		final int localX = this.getX() - topLeftAbsX;
		return localX;
	}

	/**
	 * Returns this player's Y coordinate within their own loaded map area.
	 * 
	 * @return the local Y coordinate
	 */
	public int getLocalY() {
		final int topLeftRegionY = this.getScene().getLoadedCenterRegionY() - Scene.LOADED_MAP_REGION_RADIUS;
		final int topLeftAbsY = topLeftRegionY * MapRegion.TILE_DIMENSION;
		final int localY = this.getY() - topLeftAbsY;
		return localY;
	}

	/**
	 * Clears the stored variables after an update has taken place.
	 */
	@Override
	public void clearStoredVariables() {
		super.clearStoredVariables();
		this.getScene().clearStoredVariables();
	}

	public Scene getScene() {
		return this.scene;
	}

}
