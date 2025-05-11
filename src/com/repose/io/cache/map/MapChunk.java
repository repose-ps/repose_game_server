package com.repose.io.cache.map;

import com.repose.game.world.map.TileArea;

/**
 * Represents a 64x64 area of the world map. Used to contain the map regions.
 */
public final class MapChunk extends TileArea {

	/**
	 * The amount of tiles in a chunk for each dimension.
	 */
	public static final int TILE_DIMENSION = 64;

	/**
	 * The amount of regions in a chunk for each dimension.
	 */
	public static final int REGION_DIMENSION = 8;

	public final MapRegion[] regions;

	public MapChunk(int chunkX, int chunkY, int plane) {
		this.setX(chunkX * TILE_DIMENSION);
		this.setY(chunkY * TILE_DIMENSION);
		this.setPlane(plane);
		this.setWidth(TILE_DIMENSION);
		this.setHeight(TILE_DIMENSION);

		final int baseRegionX = this.getRegionX();
		final int baseRegionY = this.getRegionY();

		this.regions = new MapRegion[REGION_DIMENSION * REGION_DIMENSION];
		for (int y = 0; y < REGION_DIMENSION; y++) {
			for (int x = 0; x < REGION_DIMENSION; x++) {
				final int index = y * REGION_DIMENSION + x;
				this.regions[index] = new MapRegion(baseRegionX + x, baseRegionY + y, plane);
			}
		}
	}

	@Override
	public String toString() {
		return "map_chunk=[" + super.toString() + "]";
	}

	@Override
	public boolean equals(Object o) {
		return (this == o);
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}
}
