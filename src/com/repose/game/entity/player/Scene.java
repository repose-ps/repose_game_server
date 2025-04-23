package com.repose.game.entity.player;

import com.repose.game.world.World;
import com.repose.net.packet.out.PacketBuilder;

/**
 * Represents a end-user's loaded scene.
 */
public final class Scene {
	/**
	 * This number represents the amount of regions excluding the center region that
	 * are loaded in each cardinal direction.
	 */
	public static final int LOADED_MAP_REGION_RADIUS = 6;

	/**
	 * This number represents how many regions away from the boundary of the
	 * player's map that a new center region will be loaded.
	 */
	public static final int LOADED_MAP_REGION_BOUNDARY = 2;
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
	 * The player loading this scene.
	 */
	private final Player player;

	/**
	 * Used to store update blocks for players being added and updated in the scene.
	 */
	private final PacketBuilder queuedUpdateBlocks = new PacketBuilder();

	/**
	 * The players in this scene.
	 */
	private final PlayerModel[] scenePlayers = new PlayerModel[256];

	/**
	 * The amount of players in this scene.
	 */
	private int scenePlayerCount;

	public Scene(Player player) {
		this.player = player;
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
	public void clearStoredVariables() {
		this.mapUpdating = false;
		this.queuedUpdateBlocks.clear();
	}

	/**
	 * Appends an update block to the queued update blocks.
	 * 
	 * @param player the player to queue an update block for
	 */
	public void appendUpdateBlock(PlayerModel player, boolean forceAppearance) {
		player.appendUpdateBlock(this.queuedUpdateBlocks, forceAppearance);
	}

	/**
	 * Adds a player to the array of players in this scene.
	 * 
	 * @param player the player to add
	 * @return {@code true} if the player was added to the scene; {@code false}
	 *         otherwise
	 */
	public boolean addPlayerToScene(PlayerModel player) {
		// same player validation
		if (this.player.equals(player)) {
			return false;
		}

		// add the player to the array
		this.scenePlayers[this.scenePlayerCount++] = player;
		return true;
	}

	/**
	 * @return true if an update block has been written to this scene.
	 */
	public boolean hasQueuedUpdateBlocks() {
		return this.queuedUpdateBlocks.getBuffer().position() > 0;
	}

	/**
	 * Appends this player's local appearance and movement to the scene update.
	 * 
	 * @param builder the packet builder
	 */
	public void appendLocalPlayerUpdate(PacketBuilder builder) {
		final boolean teleporting = this.player.isTeleporting();
		final boolean updating = this.player.isUpdating();
		final boolean walking = false; // TODO
		final boolean running = false; // TODO

		if (!teleporting && !updating && !walking && !running) {
			builder.putBit(false); // we are not sending an update
			return;
		}

		builder.putBit(true); // we are sending an update

		if (teleporting) {
			builder.putBits(2, 3); // teleport update type
			builder.putBit(true); // discard walking queue == 1
			builder.putBits(2, this.player.getStoredTeleport().getPlane());
			builder.putBits(7, this.player.getLocalY());
			builder.putBits(7, this.player.getLocalX());
			builder.putBit(this.player.isUpdating());
		} else if (running) {
			builder.putBits(2, 2); // running update type
			builder.putBits(3, 0); // TODO direction 1
			builder.putBits(3, 0); // TODO direction 2
			builder.putBit(this.player.isUpdating());
		} else if (walking) {
			builder.putBits(2, 1); // walking update type
			builder.putBits(3, 0); // TODO direction
			builder.putBit(this.player.isUpdating());
		} else if (updating) {
			builder.putBits(2, 0); // block update update type
		}

		if (updating) {
			this.appendUpdateBlock(this.player, false);
		}

	}

	/**
	 * Checks if the map needs to be updated.
	 */
	public void updateMap() {
		final int deltaRegionX = this.loadedCenterRegionX - this.player.getRegionX();
		final int deltaRegionY = this.loadedCenterRegionY - this.player.getRegionY();

		final int absDeltaX = Math.abs(deltaRegionX);
		final int absDeltaY = Math.abs(deltaRegionY);
		final int maxDelta = LOADED_MAP_REGION_RADIUS - LOADED_MAP_REGION_BOUNDARY;

		if (absDeltaX >= maxDelta || absDeltaY >= maxDelta) {
			this.mapUpdating = true;
			this.loadedCenterRegionX = this.player.getRegionX();
			this.loadedCenterRegionY = this.player.getRegionY();
		}
	}

	/**
	 * Adds nearby players to this player's scene.
	 * 
	 * @param builder the packet builder instance to append the added player bits to
	 */
	public void addNewPlayers(PacketBuilder builder) {
		World.forEachAccount(account -> {
			final PlayerModel other = account.getPlayer();
			final Player player = this.player;

			// check if the account is the appropriate amount of tiles away.
			final int delta = player.deltaDistance(other);
			if (delta < -16 || delta >= 16) {
				return;
			}

			// check if player is in scene already
			for (int i = 0; i < this.scenePlayerCount; i++) {
				if (this.scenePlayers[i].equals(other)) {
					return;
				}
			}

			// attempt to add the player to the scene
			boolean added = this.addPlayerToScene(other);
			if (!added)
				return;

			// append the update block and force an appearance update
			this.appendUpdateBlock(other, true);

			// calculate delta position
			int deltaX = other.getX() - player.getX();
			if (deltaX < 0)
				deltaX += 32;
			int deltaY = other.getY() - player.getY();
			if (deltaY < 0)
				deltaY += 32;

			// append bits
			builder.putBits(11, other.getWorldIndex());
			builder.putBits(5, deltaX);
			builder.putBit(true); // update block
			builder.putBit(true); // discard walking queue
			builder.putBits(5, deltaY);
		});
		if (this.hasQueuedUpdateBlocks())
			builder.putBits(11, 2047);
	}

	public void writeUpdateBlocks(PacketBuilder builder) {
		if (!this.hasQueuedUpdateBlocks())
			return;
		builder.putBytes(this.queuedUpdateBlocks.getBuffer().array(), 0,
				this.queuedUpdateBlocks.getBuffer().position());
	}

	public void updateOldPlayers(PacketBuilder builder) {
		final int oldPlayerCount = this.scenePlayerCount;
		this.scenePlayerCount = 0;

		builder.putBits(8, oldPlayerCount);

		for (int i = 0; i < oldPlayerCount; i++) {
			final PlayerModel other = this.scenePlayers[i];

			final boolean blockUpdate = other.isUpdating();
			final boolean walking = false; // TODO
			final boolean running = false; // TODO
			final boolean teleporting = this.scenePlayers[i].isTeleporting();

			final int delta = this.player.deltaDistance(other);
			final boolean outOfVision = delta < -16 || delta >= 16;

			final boolean removing = outOfVision || teleporting;

			final boolean sendingUpdate = removing || running || walking || blockUpdate;
			builder.putBit(sendingUpdate);
			if (!sendingUpdate) {
				continue;
			}

			final boolean added = this.addPlayerToScene(other);

			if (outOfVision || teleporting || !added) {
				builder.putBits(2, 3); // remove update
				continue;
			}

			this.addPlayerToScene(other);
			if (running) {
				builder.putBits(2, 2); // running update
				builder.putBits(3, 0); // TODO direction1
				builder.putBits(3, 0); // TODO direction2
				builder.putBit(blockUpdate);
			} else if (walking) {
				builder.putBits(2, 1); // walking update
				builder.putBits(3, 0);// TODO direction
				builder.putBit(blockUpdate);
			} else {
				builder.putBits(2, 0); // block update update
			}

			if (blockUpdate) {
				this.appendUpdateBlock(other, false);
			}

		}
	}

}
