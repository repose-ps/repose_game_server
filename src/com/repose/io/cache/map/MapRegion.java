package com.repose.io.cache.map;

import com.repose.game.world.map.TileArea;

/**
 * The {@code MapRegion} class represents an 8x8 area of the map. These regions
 * are made from dividing {@link MapChunk} instances into an 8x8 area of
 * regions.
 */
public final class MapRegion extends TileArea {

	public static final int TILE_DIMENSION = 8;

	public final MapTile[] tiles;

	public MapRegion(int regionX, int regionY, int plane) {
		this.setX(regionX * TILE_DIMENSION);
		this.setY(regionY * TILE_DIMENSION);
		this.setPlane(plane);
		this.setWidth(TILE_DIMENSION);
		this.setHeight(TILE_DIMENSION);

		final int baseAbsX = this.getX();
		final int baseAbsY = this.getY();

		this.tiles = new MapTile[TILE_DIMENSION * TILE_DIMENSION];
		for (int y = 0; y < TILE_DIMENSION; y++) {
			for (int x = 0; x < TILE_DIMENSION; x++) {
				this.tiles[y * TILE_DIMENSION + x] = new MapTile(baseAbsX + x, baseAbsY + y, plane);
			}
		}
	}

	@Override
	public String toString() {
		return "map_region=[" + super.toString() + "]";
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
