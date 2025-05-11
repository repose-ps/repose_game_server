// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
package com.repose.io.cache;

import com.repose.io.cache.bzip2.Bzip2Decompressor;

public class Archive {

	public byte outputData[];
	public int fileCount;
	public int hashes[];
	public int decompressedSizes[];
	public int compressedSizes[];
	public int initialOffsets[];
	public boolean decompressed;

	public Archive(byte data[]) {
		Buffer buffer = new Buffer(data);
		int compressedLength = buffer.get3ByteBE();
		int decompressedLength = buffer.get3ByteBE();
		if (decompressedLength != compressedLength) {
			byte output[] = new byte[compressedLength];
			Bzip2Decompressor.decompress(output, compressedLength, data, decompressedLength, 6);
			this.outputData = output;
			buffer = new Buffer(this.outputData);
			this.decompressed = true;
		} else {
			this.outputData = data;
			this.decompressed = false;
		}
		this.fileCount = buffer.getUnsignedShortBE();
		this.hashes = new int[this.fileCount];
		this.decompressedSizes = new int[this.fileCount];
		this.compressedSizes = new int[this.fileCount];
		this.initialOffsets = new int[this.fileCount];
		int offset = buffer.position + this.fileCount * 10;
		for (int index = 0; index < this.fileCount; index++) {
			this.hashes[index] = buffer.getIntBE();
			this.decompressedSizes[index] = buffer.get3ByteBE();
			this.compressedSizes[index] = buffer.get3ByteBE();
			this.initialOffsets[index] = offset;
			offset += this.compressedSizes[index];
		}

	}

	public byte[] decompressFile(String name) {
		int hash = 0;
		name = name.toUpperCase();
		for (int c = 0; c < name.length(); c++)
			hash = (hash * 61 + name.charAt(c)) - 32;

		for (int file = 0; file < this.fileCount; file++)
			if (this.hashes[file] == hash) {
				final byte[] output = new byte[this.decompressedSizes[file]];
				if (!this.decompressed) {
					Bzip2Decompressor.decompress(output, this.decompressedSizes[file], this.outputData,
							this.compressedSizes[file], this.initialOffsets[file]);
				} else {
					for (int l = 0; l < this.decompressedSizes[file]; l++)
						output[l] = this.outputData[this.initialOffsets[file] + l];

				}
				return output;
			}

		return null;
	}
}
