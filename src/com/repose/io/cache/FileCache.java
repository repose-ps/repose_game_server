// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
package com.repose.io.cache;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileCache {

	private static final int maxFileSize = 0x927c0;
	public static byte buffer[] = new byte[520];
	public RandomAccessFile dataFile;
	public RandomAccessFile indexFile;
	public int storeId;

	public FileCache(int storeId, RandomAccessFile dataFile, RandomAccessFile indexFile) {
		this.storeId = storeId;
		this.dataFile = dataFile;
		this.indexFile = indexFile;
	}

	public synchronized byte[] decompress(int index) {
		try {
			seek(index * 6, this.indexFile);
			int in;
			for (int r = 0; r < 6; r += in) {
				in = this.indexFile.read(buffer, r, 6 - r);
				if (in == -1)
					return null;
			}

			int size = ((buffer[0] & 0xff) << 16) + ((buffer[1] & 0xff) << 8) + (buffer[2] & 0xff);
			int sector = ((buffer[3] & 0xff) << 16) + ((buffer[4] & 0xff) << 8) + (buffer[5] & 0xff);
			if (size < 0 || size > maxFileSize)
				return null;
			if (sector <= 0 || (long) sector > this.dataFile.length() / 520L)
				return null;
			byte decompressed[] = new byte[size];
			int read = 0;
			for (int part = 0; read < size; part++) {
				if (sector == 0)
					return null;
				seek(sector * 520, this.dataFile);
				int r = 0;
				int unread = size - read;
				if (unread > 512)
					unread = 512;
				int in_;
				for (; r < unread + 8; r += in_) {
					in_ = this.dataFile.read(buffer, r, (unread + 8) - r);
					if (in_ == -1)
						return null;
				}

				int decompressedIndex = ((buffer[0] & 0xff) << 8) + (buffer[1] & 0xff);
				int decompressedPart = ((buffer[2] & 0xff) << 8) + (buffer[3] & 0xff);
				int decompressedSector = ((buffer[4] & 0xff) << 16) + ((buffer[5] & 0xff) << 8) + (buffer[6] & 0xff);
				int decompressedStoreId = buffer[7] & 0xff;
				if (decompressedIndex != index || decompressedPart != part || decompressedStoreId != this.storeId)
					return null;
				if (decompressedSector < 0 || (long) decompressedSector > this.dataFile.length() / 520L)
					return null;
				for (int i = 0; i < unread; i++)
					decompressed[read++] = buffer[i + 8];

				sector = decompressedSector;
			}

			return decompressed;
		} catch (IOException _ex) {
			return null;
		}
	}

	public synchronized boolean put(int size, byte data[], int index) {
		boolean exists = put(data, index, true, size);
		if (!exists)
			exists = put(data, index, false, size);
		return exists;
	}

	public synchronized boolean put(byte data[], int index, boolean exists, int size) {
		try {
			int sector;
			if (exists) {
				seek(index * 6, this.indexFile);
				int in;
				for (int r = 0; r < 6; r += in) {
					in = this.indexFile.read(buffer, r, 6 - r);
					if (in == -1)
						return false;
				}

				sector = ((buffer[3] & 0xff) << 16) + ((buffer[4] & 0xff) << 8) + (buffer[5] & 0xff);
				if (sector <= 0 || (long) sector > this.dataFile.length() / 520L)
					return false;
			} else {
				sector = (int) ((this.dataFile.length() + 519L) / 520L);
				if (sector == 0)
					sector = 1;
			}
			buffer[0] = (byte) (size >> 16);
			buffer[1] = (byte) (size >> 8);
			buffer[2] = (byte) size;
			buffer[3] = (byte) (sector >> 16);
			buffer[4] = (byte) (sector >> 8);
			buffer[5] = (byte) sector;
			seek(index * 6, this.indexFile);
			this.indexFile.write(buffer, 0, 6);
			int written = 0;
			for (int part = 0; written < size; part++) {
				int decompressedSector = 0;
				if (exists) {
					seek(sector * 520, this.dataFile);
					int read;
					int in;
					for (read = 0; read < 8; read += in) {
						in = this.dataFile.read(buffer, read, 8 - read);
						if (in == -1)
							break;
					}

					if (read == 8) {
						int decompressedIndex = ((buffer[0] & 0xff) << 8) + (buffer[1] & 0xff);
						int decompressedPart = ((buffer[2] & 0xff) << 8) + (buffer[3] & 0xff);
						decompressedSector = ((buffer[4] & 0xff) << 16) + ((buffer[5] & 0xff) << 8)
								+ (buffer[6] & 0xff);
						int decompressedStoreId = buffer[7] & 0xff;
						if (decompressedIndex != index || decompressedPart != part
								|| decompressedStoreId != this.storeId)
							return false;
						if (decompressedSector < 0 || (long) decompressedSector > this.dataFile.length() / 520L)
							return false;
					}
				}
				if (decompressedSector == 0) {
					exists = false;
					decompressedSector = (int) ((this.dataFile.length() + 519L) / 520L);
					if (decompressedSector == 0)
						decompressedSector++;
					if (decompressedSector == sector)
						decompressedSector++;
				}
				if (size - written <= 512)
					decompressedSector = 0;
				buffer[0] = (byte) (index >> 8);
				buffer[1] = (byte) index;
				buffer[2] = (byte) (part >> 8);
				buffer[3] = (byte) part;
				buffer[4] = (byte) (decompressedSector >> 16);
				buffer[5] = (byte) (decompressedSector >> 8);
				buffer[6] = (byte) decompressedSector;
				buffer[7] = (byte) this.storeId;
				seek(sector * 520, this.dataFile);
				this.dataFile.write(buffer, 0, 8);
				int unwritten = size - written;
				if (unwritten > 512)
					unwritten = 512;
				this.dataFile.write(data, written, unwritten);
				written += unwritten;
				sector = decompressedSector;
			}

			return true;
		} catch (IOException _ex) {
			return false;
		}
	}

	public synchronized void seek(int i, RandomAccessFile randomaccessfile) throws IOException {
		if (i < 0 || i > 0x3c00000) {
			System.out.println("Badseek - pos:" + i + " len:" + randomaccessfile.length());
			i = 0x3c00000;
			try {
				Thread.sleep(1000L);
			} catch (Exception _ex) {
			}
		}
		randomaccessfile.seek(i);
	}

}
