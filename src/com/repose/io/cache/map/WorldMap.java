package com.repose.io.cache.map;

import java.util.HashMap;
import java.util.Map;

import com.repose.GameServer;
import com.repose.game.entity.GameObject;
import com.repose.io.cache.Buffer;
import com.repose.io.cache.def.GameObjectDefinition;

/**
 * Represents the data that is contained within the cache for the world's
 * objects and terrain.
 * 
 * @author Robert Guidry
 */
public final class WorldMap {

	public static final int PLANE_COUNT = 4;

	private static final Map<Integer, MapChunk[]> chunkPlaneMap = new HashMap<>();

	private static Integer getChunkHash(int chunkX, int chunkY) {
		return (chunkX << 8) + (chunkY & 0xFF);
	}

	public static void loadMapChunks(byte[][] objectData, int[][] chunkCoordinates) {
		final long start = System.currentTimeMillis();
		final int chunkCount = objectData.length;

		for (int c = 0; c < chunkCount; c++) {
			// create chunk instances
			final int chunkX = chunkCoordinates[c][0];
			final int chunkY = chunkCoordinates[c][1];
			final MapChunk[] chunks = new MapChunk[PLANE_COUNT];

			for (int plane = 0; plane < chunks.length; plane++) {
				chunks[plane] = new MapChunk(chunkX, chunkY, plane);
			}

			// init clipping data
			for (int plane = 0; plane < PLANE_COUNT; plane++) {
				for (int regionX = 0; regionX < MapRegion.TILE_DIMENSION; regionX++) {
					for (int regionY = 0; regionY < MapRegion.TILE_DIMENSION; regionY++) {
						final int regionIndex = regionY * MapRegion.TILE_DIMENSION + regionX;
						for (int i = 0; i < chunks[plane].regions[regionIndex].tiles.length; i++) {
							chunks[plane].regions[regionIndex].tiles[i].clippingData &= ~0x1000000;
						}
					}
				}
			}
			chunkPlaneMap.put(getChunkHash(chunkX, chunkY), chunks);
		}

		for (int c = 0; c < chunkCount; c++) {
			final int chunkX = chunkCoordinates[c][0];
			final int chunkY = chunkCoordinates[c][1];
			final MapChunk[] chunks = chunkPlaneMap.get(getChunkHash(chunkX, chunkY));

			// load objects
			Buffer buffer = new Buffer(objectData[c]);
			int objectId = -1;
			for (;;) {
				int objectIdOffset = buffer.getSmart2();
				if (objectIdOffset == 0) {
					break;
				}
				objectId += objectIdOffset;
				int position = 0;
				for (;;) {
					int positionOffset = buffer.getSmart2();
					if (positionOffset == 0) {
						break;
					}
					position += positionOffset - 1;
					int tileY = position & 0x3f;
					int tileX = position >> 6 & 0x3f;
					int tilePlane = position >> 12;
					int hash = buffer.getUnsignedByte();
					int type = hash >> 2;
					int orientation = hash & 0x3;
					addObjectCollision(chunks[tilePlane], tileX, tileY, objectId, orientation, type);

					final int absX = chunks[tilePlane].getX() + tileX;
					final int absY = chunks[tilePlane].getY() + tileY;

					final GameObject object = new GameObject(objectId, absX, absY, tilePlane, type, orientation);
					getTile(absX, absY, tilePlane).tileObjects.put(type, object);
				}
			}
		}
		GameServer.getLogger()
				.info("Loaded " + chunkCount + " map chunks in " + (System.currentTimeMillis() - start) + "ms.");
	}

	private static void addObjectCollision(MapChunk chunk, int x, int y, int objectId, int face, int type) {
		GameObjectDefinition objectDefinition = GameObjectDefinition.gameObjectCache[objectId];
		if (type == 22) {
			if (objectDefinition.solid && objectDefinition.hasActions) {
				markBlocked(chunk, x, y);
			}
		} else if (type == 10 || type == 11) {
			if (objectDefinition.solid) {
				markSolidOccupant(chunk, x, y, objectDefinition.sizeX, objectDefinition.sizeY, face,
						objectDefinition.walkable);
			}
		} else if (type >= 12) {
			if (objectDefinition.solid)
				markSolidOccupant(chunk, x, y, objectDefinition.sizeX, objectDefinition.sizeY, face,
						objectDefinition.walkable);
		} else if (type == 0) {
			if (objectDefinition.solid)
				markWall(chunk, x, y, face, type, objectDefinition.walkable);
		} else if (type == 1) {
			if (objectDefinition.solid)
				markWall(chunk, x, y, face, type, objectDefinition.walkable);
		} else if (type == 2) {
			if (objectDefinition.solid)
				markWall(chunk, x, y, face, type, objectDefinition.walkable);
		} else if (type == 3) {
			if (objectDefinition.solid)
				markWall(chunk, x, y, face, type, objectDefinition.walkable);
		} else if (type == 9) {
			if (objectDefinition.solid)
				markSolidOccupant(chunk, x, y, objectDefinition.sizeX, objectDefinition.sizeY, face,
						objectDefinition.walkable);
		}
	}

	public static boolean tileIsCached(int absX, int absY) {
		return getTile(absX, absY, 0) != null;
	}

	public static MapTile getTile(int absX, int absY, int plane) {
		final int chunkX = absX / MapChunk.TILE_DIMENSION;
		final int chunkY = absY / MapChunk.TILE_DIMENSION;
		final int chunkLocalX = absX % MapChunk.TILE_DIMENSION;
		final int chunkLocalY = absY % MapChunk.TILE_DIMENSION;

		MapChunk[] chunkPlanes = chunkPlaneMap.get(getChunkHash(chunkX, chunkY));

		if (chunkPlanes == null) {
			chunkPlanes = new MapChunk[4];
			for (int i = 0; i < chunkPlanes.length; i++) {
				chunkPlanes[i] = new MapChunk(chunkX, chunkY, i);
			}
			chunkPlaneMap.put(getChunkHash(chunkX, chunkY), chunkPlanes);
		}

		final MapChunk chunk = chunkPlanes[plane];
		return getTile(chunk, chunkLocalX, chunkLocalY);
	}

	private static MapTile getTile(MapChunk chunk, int chunkLocalX, int chunkLocalY) {
		if (chunkLocalX < 0 || chunkLocalY < 0 || chunkLocalX >= MapChunk.TILE_DIMENSION
				|| chunkLocalY >= MapChunk.TILE_DIMENSION) {
			return getTile(chunk.getX() + chunkLocalX, chunk.getY() + chunkLocalY, chunk.getPlane());
		}

		final int regionX = chunkLocalX / MapRegion.TILE_DIMENSION;
		final int regionY = chunkLocalY / MapRegion.TILE_DIMENSION;
		final int regionLocalX = chunkLocalX % MapRegion.TILE_DIMENSION;
		final int regionLocalY = chunkLocalY % MapRegion.TILE_DIMENSION;

		final MapRegion region = chunk.regions[regionY * MapChunk.REGION_DIMENSION + regionX];
		return region.tiles[regionLocalY * MapRegion.TILE_DIMENSION + regionLocalX];
	}

	private static void markBlocked(MapChunk chunk, int x, int y) {
		final MapTile tile = getTile(chunk, x, y);
		tile.clippingData |= 0x200000;
	}

	private static void set(MapChunk chunk, int x, int y, int flag) {
		final MapTile tile = getTile(chunk, x, y);
		tile.clippingData |= flag;
	}

	private static void markSolidOccupant(MapChunk chunk, int x, int y, int width, int height, int orientation,
			boolean impenetrable) {
		int occupied = 256;
		if (impenetrable)
			occupied += 0x20000;
		if (orientation == 1 || orientation == 3) {
			int temp = width;
			width = height;
			height = temp;
		}
		for (int _x = x; _x < x + width; _x++)
			for (int _y = y; _y < y + height; _y++)
				set(chunk, _x, _y, occupied);
	}

	private static void markWall(MapChunk chunk, int x, int y, int orientation, int position, boolean impenetrable) {
		if (position == 0) {
			if (orientation == 0) {
				set(chunk, x, y, 128);
				set(chunk, x - 1, y, 8);
			}
			if (orientation == 1) {
				set(chunk, x, y, 2);
				set(chunk, x, y + 1, 32);
			}
			if (orientation == 2) {
				set(chunk, x, y, 8);
				set(chunk, x + 1, y, 128);
			}
			if (orientation == 3) {
				set(chunk, x, y, 32);
				set(chunk, x, y - 1, 2);
			}
		}
		if (position == 1 || position == 3) {
			if (orientation == 0) {
				set(chunk, x, y, 1);
				set(chunk, x - 1, y + 1, 16);
			}
			if (orientation == 1) {
				set(chunk, x, y, 4);
				set(chunk, x + 1, y + 1, 64);
			}
			if (orientation == 2) {
				set(chunk, x, y, 16);
				set(chunk, x + 1, y - 1, 1);
			}
			if (orientation == 3) {
				set(chunk, x, y, 64);
				set(chunk, x - 1, y - 1, 4);
			}
		}
		if (position == 2) {
			if (orientation == 0) {
				set(chunk, x, y, 130);
				set(chunk, x - 1, y, 8);
				set(chunk, x, y + 1, 32);
			}
			if (orientation == 1) {
				set(chunk, x, y, 10);
				set(chunk, x, y + 1, 32);
				set(chunk, x + 1, y, 128);
			}
			if (orientation == 2) {
				set(chunk, x, y, 40);
				set(chunk, x + 1, y, 128);
				set(chunk, x, y - 1, 2);
			}
			if (orientation == 3) {
				set(chunk, x, y, 160);
				set(chunk, x, y - 1, 2);
				set(chunk, x - 1, y, 8);
			}
		}
		if (impenetrable) {
			if (position == 0) {
				if (orientation == 0) {
					set(chunk, x, y, 0x10000);
					set(chunk, x - 1, y, 4096);
				}
				if (orientation == 1) {
					set(chunk, x, y, 1024);
					set(chunk, x, y + 1, 16384);
				}
				if (orientation == 2) {
					set(chunk, x, y, 4096);
					set(chunk, x + 1, y, 0x10000);
				}
				if (orientation == 3) {
					set(chunk, x, y, 16384);
					set(chunk, x, y - 1, 1024);
				}
			}
			if (position == 1 || position == 3) {
				if (orientation == 0) {
					set(chunk, x, y, 512);
					set(chunk, x - 1, y + 1, 8192);
				}
				if (orientation == 1) {
					set(chunk, x, y, 2048);
					set(chunk, x + 1, y + 1, 32768);
				}
				if (orientation == 2) {
					set(chunk, x, y, 8192);
					set(chunk, x + 1, y - 1, 512);
				}
				if (orientation == 3) {
					set(chunk, x, y, 32768);
					set(chunk, x - 1, y - 1, 2048);
				}
			}
			if (position == 2) {
				if (orientation == 0) {
					set(chunk, x, y, 0x10400);
					set(chunk, x - 1, y, 4096);
					set(chunk, x, y + 1, 16384);
				}
				if (orientation == 1) {
					set(chunk, x, y, 5120);
					set(chunk, x, y + 1, 16384);
					set(chunk, x + 1, y, 0x10000);
				}
				if (orientation == 2) {
					set(chunk, x, y, 20480);
					set(chunk, x + 1, y, 0x10000);
					set(chunk, x, y - 1, 1024);
				}
				if (orientation == 3) {
					set(chunk, x, y, 0x14000);
					set(chunk, x, y - 1, 1024);
					set(chunk, x - 1, y, 4096);
				}
			}
		}
	}
}
