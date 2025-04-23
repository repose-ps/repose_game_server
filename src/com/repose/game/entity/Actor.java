package com.repose.game.entity;

import com.repose.game.world.World;
import com.repose.game.world.map.TilePosition;

/**
 * An Actor represents a thing an {@link Entity} in the game world which has an
 * HP bar and has built in movement (Walking). Generally this represents
 * 'living' things, but there are exceptions to this.
 * 
 * @author Robert Guidry
 */
public abstract class Actor extends Entity {

	/**
	 * The index of this Actor in the {@link World} array.
	 */
	private int worldIndex = -1;

	/**
	 * The stored teleport is where the actor will move in the next update tick.
	 */
	private final TilePosition storedTeleport = new TilePosition();

	/**
	 * Updates the position of this Actor in the game world if they are moving or
	 * teleporting.
	 */
	protected void updatePosition() {
		if (isTeleporting()) {
			this.setPosition(this.getStoredTeleport());
		} else {
			// TODO walking, running
			return;
		}
	}

	/**
	 * If the stored teleport has not been set, returns {@code false};<br>
	 * {@code true} otherwise;
	 * 
	 * @return {@code true} if the teleport is set;<br>
	 *         {@code false} otherwise
	 */
	public boolean isTeleporting() {
		final TilePosition tile = this.getStoredTeleport();
		return tile.getX() != 0 || tile.getY() != 0 || tile.getPlane() != 0;
	}

	/**
	 * Clears the stored variables after an update has taken place.
	 */
	public void clearStoredVariables() {
		this.storedTeleport.setPosition(0, 0, 0);
	}

	/**
	 * Returns the index of this Actor in the {@link World} array.
	 * 
	 * @return the {@code World} array index
	 */
	public int getWorldIndex() {
		return this.worldIndex;
	}

	/**
	 * Sets the value of this actor's world index to the specified value.
	 * 
	 * @param worldIndex the value
	 */
	public void setWorldIndex(int worldIndex) {
		this.worldIndex = worldIndex;
	}

	/**
	 * Returns the tile this Actor will teleport to in the next tick. Note that the
	 * returned TilePosition instance is the one this Actor class looks at.
	 * 
	 * @return the tile
	 */
	public TilePosition getStoredTeleport() {
		return this.storedTeleport;
	}

	/**
	 * If this Actor has something to update on the client-side, this will be set to
	 * true.
	 * 
	 * @return true if the Actor needs to update
	 */
	public boolean isUpdating() {
		return false;
	}

}
