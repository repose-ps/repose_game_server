package com.repose.game.entity.actor;

import com.repose.game.entity.Entity;
import com.repose.game.world.World;
import com.repose.game.world.map.Direction;
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
	 * The last facing direction of this actor
	 */
	private Direction lastDirection = Direction.NORTH;
	/**
	 * This actor's first movement direction.
	 */
	private Direction firstDirection = Direction.NONE;

	/**
	 * This actor's second movement direction.
	 */
	private Direction secondDirection = Direction.NONE;

	/**
	 * This actor's walking queue.
	 */
	private final WalkingQueue walkingQueue = new WalkingQueue(this);

	/**
	 * Is this actor teleporting?
	 */
	private boolean teleporting;

	/**
	 * If the stored teleport has not been set, returns {@code false};<br>
	 * {@code true} otherwise;
	 * 
	 * @return {@code true} if the teleport is set;<br>
	 *         {@code false} otherwise
	 */
	public boolean isTeleporting() {
		return this.teleporting;
	}

	/**
	 * Clears the stored variables after an update has taken place.
	 */
	public void clearStoredVariables() {
		this.teleporting = false;
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
	 * Teleports this mob to the specified coordinates, setting the appropriate
	 * flags and clearing the walking queue.
	 *
	 * @param x     the X coordinate
	 * @param y     the Y coordinate
	 * @param plane the vertical plane
	 */
	public void teleport(int x, int y, int plane) {
		this.teleport(new TilePosition(x, y, plane));
	}

	/**
	 * Teleports this mob to the specified {@link Position}, setting the appropriate
	 * flags and clearing the walking queue.
	 *
	 * @param position The position.
	 */
	public void teleport(TilePosition position) {
		this.setPosition(position);
		this.teleporting = true;
		this.walkingQueue.clear();
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

	/**
	 * Gets the last facing direction of this actor.
	 *
	 * @return The last direction this actor was facing.
	 */
	public Direction getLastDirection() {
		return this.lastDirection;
	}

	/**
	 * Set the last direction this actor was facing.
	 *
	 * @param lastDirection The direction to set.
	 */
	public void setLastDirection(Direction lastDirection) {
		this.lastDirection = lastDirection;
	}

	/**
	 * Sets the next movement {@link Direction}s for this actor.
	 *
	 * @param first  The first direction.
	 * @param second The second direction.
	 */
	public final void setDirections(Direction first, Direction second) {
		this.firstDirection = first;
		this.secondDirection = second;
	}

	/**
	 * Gets the first {@link Direction}.
	 *
	 * @return The direction.
	 */
	public final Direction getFirstDirection() {
		return this.firstDirection;
	}

	/**
	 * Gets this actor's second movement {@link Direction}.
	 *
	 * @return The direction.
	 */
	public final Direction getSecondDirection() {
		return this.secondDirection;
	}

	/**
	 * Gets this actor's {@link WalkingQueue}.
	 *
	 * @return The walking queue.
	 */
	public final WalkingQueue getWalkingQueue() {
		return this.walkingQueue;
	}
}
