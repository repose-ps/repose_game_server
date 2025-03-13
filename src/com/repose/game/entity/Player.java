package com.repose.game.entity;

import com.repose.game.Account;
import com.repose.game.world.map.MapRegion;

/**
 * The {@code Player} class represents a {@link PlayerCharacter} instance that
 * is controlled by an {@link Account}.
 */
public final class Player extends PlayerCharacter {

	/**
	 * This number represents the amount of regions excluding the center region that
	 * are loaded in each cardinal direction.
	 */
	private static final int LOADED_MAP_REGION_RADIUS = 6;

	/**
	 * This number represents how many regions away from the boundary of the
	 * player's map that a new center region will be loaded.
	 */
	private static final int LOADED_MAP_REGION_BOUNDARY = 2;

	/**
	 * Do the map regions surrounding the player need to be updated on the player's
	 * client?
	 */
	private boolean mapUpdating;

	/**
	 * The X coordinate for the center chunk of this player's loaded map scene.
	 */
	private int loadedCenterRegionX;

	/**
	 * The Y coordinate for the center chunk of this player's loaded map scene.
	 */
	private int loadedCenterRegionY;

	/**
	 * Creates a new Player instance.
	 */
	public Player() {
		this.getStoredTeleport().setPosition(3222, 3222, 0);
	}
	
	/**
	 * Returns this player's X coordinate within their own loaded map area.
	 * 
	 * @return the local X coordinate
	 */
	public int getLocalX() {
		final int topLeftRegionX = this.getLoadedCenterRegionX() - LOADED_MAP_REGION_RADIUS;
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
		final int topLeftRegionY = this.getLoadedCenterRegionY() - LOADED_MAP_REGION_RADIUS;
		final int topLeftAbsY = topLeftRegionY * MapRegion.TILE_DIMENSION;
		final int localY = this.getY() - topLeftAbsY;
		return localY;
	}

	/**
	 * If a new map needs to be loaded around the player, this will return true.
	 * 
	 * @return {@code true} if the map needs to be loaded
	 */
	public boolean isMapUpdating() {
		return this.mapUpdating;
	}

	/**
	 * Returns the X coordinate of the region in the center of this player's loaded
	 * regions.
	 * 
	 * @return the X region coordinate
	 */
	public int getLoadedCenterRegionX() {
		return this.loadedCenterRegionX;
	}

	/**
	 * Returns the Y coordinate of the region in the center of this player's loaded
	 * regions.
	 * 
	 * @return the Y region coordinate
	 */
	public int getLoadedCenterRegionY() {
		return this.loadedCenterRegionY;
	}

	/**
	 * Clears the stored variables after an update has taken place.
	 */
	@Override
	public void clearStoredVariables() {
		super.clearStoredVariables();
		this.mapUpdating = false;
	}

	/**
	 * Updates this player's position and checks if a map update needs to be sent.
	 */
	@Override
	public void updatePosition() {
		super.updatePosition();

		// update the center region if necessary
		final int deltaRegionX = this.loadedCenterRegionX - this.getRegionX();
		final int deltaRegionY = this.loadedCenterRegionY - this.getRegionY();

		final int absDeltaX = Math.abs(deltaRegionX);
		final int absDeltaY = Math.abs(deltaRegionY);
		final int maxDelta = LOADED_MAP_REGION_RADIUS - LOADED_MAP_REGION_BOUNDARY;

		if (absDeltaX >= maxDelta || absDeltaY >= maxDelta) {
			this.mapUpdating = true;
			this.loadedCenterRegionX = this.getRegionX();
			this.loadedCenterRegionY = this.getRegionY();
		}
	}
}
