package com.repose.util;

/**
 * The {@code ISAACRandomGenerator} class generates a random number based on a
 * seed. This class is used to encode/decode packets for client/server
 * communication. The code of this class was taken from the client, and as such
 * is currently obfuscated.
 * 
 * @author Jagex
 */
public class ISAACRandomGenerator {

	public ISAACRandomGenerator(int seed[]) {
		this.anIntArray524 = new int[256];
		this.anIntArray523 = new int[256];
		for (int j = 0; j < seed.length; j++)
			this.anIntArray523[j] = seed[j];

		method298();
	}

	public int nextInt() {
		if (this.anInt522-- == 0) {
			method297();
			this.anInt522 = 255;
		}
		return this.anIntArray523[this.anInt522];
	}

	public void method297() {
		this.anInt526 += ++this.anInt527;
		for (int i = 0; i < 256; i++) {
			int j = this.anIntArray524[i];
			if ((i & 3) == 0)
				this.anInt525 ^= this.anInt525 << 13;
			else if ((i & 3) == 1)
				this.anInt525 ^= this.anInt525 >>> 6;
			else if ((i & 3) == 2)
				this.anInt525 ^= this.anInt525 << 2;
			else if ((i & 3) == 3)
				this.anInt525 ^= this.anInt525 >>> 16;
			this.anInt525 += this.anIntArray524[i + 128 & 0xff];
			int k;
			this.anIntArray524[i] = k = this.anIntArray524[(j & 0x3fc) >> 2] + this.anInt525 + this.anInt526;
			this.anIntArray523[i] = this.anInt526 = this.anIntArray524[(k >> 8 & 0x3fc) >> 2] + j;
		}

	}

	public void method298() {
		int i1;
		int j1;
		int k1;
		int l1;
		int i2;
		int j2;
		int k2;
		int l = i1 = j1 = k1 = l1 = i2 = j2 = k2 = 0x9e3779b9;
		for (int i = 0; i < 4; i++) {
			l ^= i1 << 11;
			k1 += l;
			i1 += j1;
			i1 ^= j1 >>> 2;
			l1 += i1;
			j1 += k1;
			j1 ^= k1 << 8;
			i2 += j1;
			k1 += l1;
			k1 ^= l1 >>> 16;
			j2 += k1;
			l1 += i2;
			l1 ^= i2 << 10;
			k2 += l1;
			i2 += j2;
			i2 ^= j2 >>> 4;
			l += i2;
			j2 += k2;
			j2 ^= k2 << 8;
			i1 += j2;
			k2 += l;
			k2 ^= l >>> 9;
			j1 += k2;
			l += i1;
		}

		for (int j = 0; j < 256; j += 8) {
			l += this.anIntArray523[j];
			i1 += this.anIntArray523[j + 1];
			j1 += this.anIntArray523[j + 2];
			k1 += this.anIntArray523[j + 3];
			l1 += this.anIntArray523[j + 4];
			i2 += this.anIntArray523[j + 5];
			j2 += this.anIntArray523[j + 6];
			k2 += this.anIntArray523[j + 7];
			l ^= i1 << 11;
			k1 += l;
			i1 += j1;
			i1 ^= j1 >>> 2;
			l1 += i1;
			j1 += k1;
			j1 ^= k1 << 8;
			i2 += j1;
			k1 += l1;
			k1 ^= l1 >>> 16;
			j2 += k1;
			l1 += i2;
			l1 ^= i2 << 10;
			k2 += l1;
			i2 += j2;
			i2 ^= j2 >>> 4;
			l += i2;
			j2 += k2;
			j2 ^= k2 << 8;
			i1 += j2;
			k2 += l;
			k2 ^= l >>> 9;
			j1 += k2;
			l += i1;
			this.anIntArray524[j] = l;
			this.anIntArray524[j + 1] = i1;
			this.anIntArray524[j + 2] = j1;
			this.anIntArray524[j + 3] = k1;
			this.anIntArray524[j + 4] = l1;
			this.anIntArray524[j + 5] = i2;
			this.anIntArray524[j + 6] = j2;
			this.anIntArray524[j + 7] = k2;
		}

		for (int k = 0; k < 256; k += 8) {
			l += this.anIntArray524[k];
			i1 += this.anIntArray524[k + 1];
			j1 += this.anIntArray524[k + 2];
			k1 += this.anIntArray524[k + 3];
			l1 += this.anIntArray524[k + 4];
			i2 += this.anIntArray524[k + 5];
			j2 += this.anIntArray524[k + 6];
			k2 += this.anIntArray524[k + 7];
			l ^= i1 << 11;
			k1 += l;
			i1 += j1;
			i1 ^= j1 >>> 2;
			l1 += i1;
			j1 += k1;
			j1 ^= k1 << 8;
			i2 += j1;
			k1 += l1;
			k1 ^= l1 >>> 16;
			j2 += k1;
			l1 += i2;
			l1 ^= i2 << 10;
			k2 += l1;
			i2 += j2;
			i2 ^= j2 >>> 4;
			l += i2;
			j2 += k2;
			j2 ^= k2 << 8;
			i1 += j2;
			k2 += l;
			k2 ^= l >>> 9;
			j1 += k2;
			l += i1;
			this.anIntArray524[k] = l;
			this.anIntArray524[k + 1] = i1;
			this.anIntArray524[k + 2] = j1;
			this.anIntArray524[k + 3] = k1;
			this.anIntArray524[k + 4] = l1;
			this.anIntArray524[k + 5] = i2;
			this.anIntArray524[k + 6] = j2;
			this.anIntArray524[k + 7] = k2;
		}

		method297();
		this.anInt522 = 256;
	}

	public int anInt522;
	public int anIntArray523[];
	public int anIntArray524[];
	public int anInt525;
	public int anInt526;
	public int anInt527;
}
