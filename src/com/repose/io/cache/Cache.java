package com.repose.io.cache;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;

import com.repose.GameServer;
import com.repose.io.cache.def.GameObjectDefinition;
import com.repose.io.cache.map.WorldMap;

/**
 * The main class for loading the cache from the file system.
 */
public final class Cache {

	/**
	 * The amount of index files.
	 */
	private static final int INDEX_COUNT = 5;

	/**
	 * The amount of archives in the first file cache.
	 */
	private static final int ARCHIVE_COUNT = 9;

	/**
	 * The name of the cache's data file.
	 */
	private static final String DATA_FILE_NAME = "main_file_cache.dat";

	/**
	 * The prefix to the name of the cache's index files.
	 */
	private static final String INDEX_FILE_PREFIX = "main_file_cache.idx";

	/**
	 * The data file which contains the cache resources.
	 */
	private static RandomAccessFile cacheDataFile;

	/**
	 * The index files which contains the positions of data in the cache data file.
	 */
	private static RandomAccessFile[] cacheIndexFiles;

	/**
	 * The index files parsed by the file cache system to allow access to their
	 * files.
	 */
	private static FileCache[] fileCaches;

	/**
	 * 
	 */
	private static Archive[] archives;

	/**
	 * Returns the directory that contains the cache files.
	 * 
	 * @return the directory
	 */
	public static String getCacheDir() {
		return "data/cache/";
	}

	/**
	 * Loads the files from the cache used in the server.
	 */
	public static void load() throws IOException {
		final long start = System.currentTimeMillis();
		loadCacheFiles();
		loadDefinitions();
		loadMaps();
		GameServer.getLogger().info("Loaded cache in " + (System.currentTimeMillis() - start) + "ms.");
		
		if(GameServer.getLogger().getLevel().equals(Level.FINEST)) {
			final long startDump = System.currentTimeMillis();
			GameObjectDefinition.dump("data/cache/out/game_object.txt");
			GameServer.getLogger().info("Dumped cache data in " + (System.currentTimeMillis() - startDump) + "ms.");
		}
	}

	/**
	 * Loads the cache files from the cache directory.
	 */
	private static void loadCacheFiles() throws IOException {
		final String dataFile = getCacheDir() + DATA_FILE_NAME;
		cacheDataFile = new RandomAccessFile(dataFile, "rw");

		cacheIndexFiles = new RandomAccessFile[INDEX_COUNT];
		fileCaches = new FileCache[INDEX_COUNT];
		for (int i = 0; i < INDEX_COUNT; i++) {
			final String indexFile = getCacheDir() + INDEX_FILE_PREFIX + i;
			cacheIndexFiles[i] = new RandomAccessFile(indexFile, "rw");
			fileCaches[i] = new FileCache(i + 1, cacheDataFile, cacheIndexFiles[i]);
		}
	}

	private static Archive getArchive(int index) {
		if (archives == null) {
			archives = new Archive[ARCHIVE_COUNT];
		}
		if (archives[index] != null) {
			return archives[index];
		}

		archives[index] = new Archive(fileCaches[0].decompress(index));
		return archives[index];
	}

	private static void loadMaps() {
		final Archive archive = getArchive(5);
		final Buffer buffer = new Buffer(archive.decompressFile("map_index"));
		final int mapCount = buffer.buffer.length / 7;

		final byte[][] objectData = new byte[mapCount][];
		final int[][] mapCoordinates = new int[mapCount][];

		byte[] gzipInputBuffer = new byte[65000];

		for (int i = 0; i < mapCount; i++) {
			final int hash = buffer.getUnsignedShortBE();
			final int mapY = hash & 0xFF;
			final int mapX = hash >> 8;
			buffer.getUnsignedShortBE();
			final int objectIndex = buffer.getUnsignedShortBE();
			buffer.position++; // preload maps (not used server-side)

			objectData[i] = fileCaches[4].decompress(objectIndex);

			int position = 0;
			try {
				GZIPInputStream gzipinputstream = new GZIPInputStream(new ByteArrayInputStream(objectData[i]));
				do {
					if (position == gzipInputBuffer.length)
						throw new RuntimeException("buffer overflow!");
					int read = gzipinputstream.read(gzipInputBuffer, position, gzipInputBuffer.length - position);
					if (read == -1)
						break;
					position += read;
				} while (true);
			} catch (IOException _ex) {
				throw new RuntimeException("error unzipping");
			}
			objectData[i] = new byte[position];
			for (int a = 0; a < position; a++)
				objectData[i][a] = gzipInputBuffer[a];

			mapCoordinates[i] = new int[] { mapX, mapY };
		}

		WorldMap.loadMapChunks(objectData, mapCoordinates);
	}

	private static void loadDefinitions() {
		GameObjectDefinition.load(getArchive(2));
	}
}
