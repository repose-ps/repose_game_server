// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
package com.repose.io.cache;

import java.math.BigInteger;

public class Buffer  {

	public static Buffer create() {
		return new Buffer(new byte[5000]);
	}

	public Buffer() {
		this(null);
	}

	public Buffer(byte buffer[]) {
		this.buffer = buffer;
		this.position = 0;
	}

	public void putByte(int i) {
		this.buffer[this.position++] = (byte) i;
	}

	public void putShortBE(int i) {
		this.buffer[this.position++] = (byte) (i >> 8);
		this.buffer[this.position++] = (byte) i;
	}

	public void putShortLE(int i) {
		this.buffer[this.position++] = (byte) i;
		this.buffer[this.position++] = (byte) (i >> 8);
	}

	public void put3Byte(int i) {
		this.buffer[this.position++] = (byte) (i >> 16);
		this.buffer[this.position++] = (byte) (i >> 8);
		this.buffer[this.position++] = (byte) i;
	}

	public void putIntBE(int i) {
		this.buffer[this.position++] = (byte) (i >> 24);
		this.buffer[this.position++] = (byte) (i >> 16);
		this.buffer[this.position++] = (byte) (i >> 8);
		this.buffer[this.position++] = (byte) i;
	}

	public void putIntLE(int i) {
		this.buffer[this.position++] = (byte) i;
		this.buffer[this.position++] = (byte) (i >> 8);
		this.buffer[this.position++] = (byte) (i >> 16);
		this.buffer[this.position++] = (byte) (i >> 24);
	}

	public void putLongBE(long l) {
		this.buffer[this.position++] = (byte) (int) (l >> 56);
		this.buffer[this.position++] = (byte) (int) (l >> 48);
		this.buffer[this.position++] = (byte) (int) (l >> 40);
		this.buffer[this.position++] = (byte) (int) (l >> 32);
		this.buffer[this.position++] = (byte) (int) (l >> 24);
		this.buffer[this.position++] = (byte) (int) (l >> 16);
		this.buffer[this.position++] = (byte) (int) (l >> 8);
		this.buffer[this.position++] = (byte) (int) l;
	}

	public void putString(String s) {
		final byte[] bytes = s.getBytes();
		System.arraycopy(bytes, 0, this.buffer, this.position, bytes.length);
		this.position += bytes.length;
		this.buffer[this.position++] = 10;
	}

	public void putBytes(byte buffer[], int off, int len) {
		for (int l = off; l < off + len; l++)
			this.buffer[this.position++] = buffer[l];
	}

	public void putLength(int i) {
		this.buffer[this.position - i - 1] = (byte) i;
	}

	public int getUnsignedByte() {
		return this.buffer[this.position++] & 0xff;
	}

	public byte getSignedByte() {
		return this.buffer[this.position++];
	}

	public int getUnsignedShortBE() {
		this.position += 2;
		return ((this.buffer[this.position - 2] & 0xff) << 8) + (this.buffer[this.position - 1] & 0xff);
	}

	public int getSignedShortBE() {
		this.position += 2;
		int i = ((this.buffer[this.position - 2] & 0xff) << 8) + (this.buffer[this.position - 1] & 0xff);
		if (i > 32767)
			i -= 0x10000;
		return i;
	}

	public int get3ByteBE() {
		this.position += 3;
		return ((this.buffer[this.position - 3] & 0xff) << 16) + ((this.buffer[this.position - 2] & 0xff) << 8)
				+ (this.buffer[this.position - 1] & 0xff);
	}

	public int getIntBE() {
		this.position += 4;
		return ((this.buffer[this.position - 4] & 0xff) << 24) + ((this.buffer[this.position - 3] & 0xff) << 16)
				+ ((this.buffer[this.position - 2] & 0xff) << 8) + (this.buffer[this.position - 1] & 0xff);
	}

	public long getLongBE() {
		long l = (long) getIntBE() & 0xffffffffL;
		long l1 = (long) getIntBE() & 0xffffffffL;
		return (l << 32) + l1;
	}

	public String getString() {
		int i = this.position;
		while (this.buffer[this.position++] != 10)
			;
		return new String(this.buffer, i, this.position - i - 1);
	}

	public byte[] getStringBytes() {
		int j = this.position;
		while (this.buffer[this.position++] != 10)
			;
		byte abyte0[] = new byte[this.position - j - 1];
		for (int k = j; k < this.position - 1; k++)
			abyte0[k - j] = this.buffer[k];

		return abyte0;
	}

	public void getBytes(byte buffer[], int off, int len) {
		for (int l = off; l < off + len; l++)
			buffer[l] = this.buffer[this.position++];

	}

	public void initBitBlock() {
		this.bitPosition = this.position * 8;
	}

	public int getBits(int val) {
		int k = this.bitPosition >> 3;
		int l = 8 - (this.bitPosition & 7);
		int i1 = 0;
		this.bitPosition += val;
		for (; val > l; l = 8) {
			i1 += (this.buffer[k++] & BIT_FLAGS[l]) << val - l;
			val -= l;
		}

		if (val == l)
			i1 += this.buffer[k] & BIT_FLAGS[l];
		else
			i1 += this.buffer[k] >> l - val & BIT_FLAGS[val];
		return i1;
	}

	public void endBitBlock() {
		this.position = (this.bitPosition + 7) / 8;
	}

	public int getSmart1() {
		int i = this.buffer[this.position] & 0xff;
		if (i < 128)
			return getUnsignedByte() - 64;
		else
			return getUnsignedShortBE() - 49152;
	}

	public int getSmart2() {
		int i = this.buffer[this.position] & 0xff;
		if (i < 128)
			return getUnsignedByte();
		else
			return getUnsignedShortBE() - 32768;
	}

	public void encryptRSA(BigInteger exponent, BigInteger mod) {
		int j = this.position;
		this.position = 0;
		byte unencryptedBytes[] = new byte[j];
		getBytes(unencryptedBytes, 0, j);
		BigInteger unencryptedBigInt = new BigInteger(unencryptedBytes);
		BigInteger encryptedBigInt = unencryptedBigInt.modPow(exponent, mod);
		byte encryptedBytes[] = encryptedBigInt.toByteArray();
		this.position = 0;
		putByte(encryptedBytes.length);
		putBytes(encryptedBytes, 0, encryptedBytes.length);
	}

	public void putByteOffset(int i) {
		this.buffer[this.position++] = (byte) (i + 128);
	}

	public void putByteNegative(int i) {
		this.buffer[this.position++] = (byte) (-i);
	}

	public void putByteSubtracted(int i) {
		this.buffer[this.position++] = (byte) (128 - i);
	}

	public int getUnsignedByteOffset() {
		return this.buffer[this.position++] - 128 & 0xff;
	}

	public int getUnsignedByteNegated() {
		return -this.buffer[this.position++] & 0xff;
	}

	public int getUnsignedByteSubtracted() {
		return 128 - this.buffer[this.position++] & 0xff;
	}

	public byte getSignedByteOffset() {
		return (byte) (this.buffer[this.position++] - 128);
	}

	public byte getSignedByteNegated() {
		return (byte) (-this.buffer[this.position++]);
	}

	public byte getSignedByteSubtracted() {
		return (byte) (128 - this.buffer[this.position++]);
	}

	public void putShortOffsetBE(int i) {
		this.buffer[this.position++] = (byte) (i >> 8);
		this.buffer[this.position++] = (byte) (i + 128);
	}

	public void putShortOffsetLE(int j) {
		this.buffer[this.position++] = (byte) (j + 128);
		this.buffer[this.position++] = (byte) (j >> 8);
	}

	public int getUnsignedShortLE() {
		this.position += 2;
		return ((this.buffer[this.position - 1] & 0xff) << 8) + (this.buffer[this.position - 2] & 0xff);
	}

	public int getUnsignedShortOffsetBE() {
		this.position += 2;
		return ((this.buffer[this.position - 2] & 0xff) << 8) + (this.buffer[this.position - 1] - 128 & 0xff);
	}

	public int getUnsignedShortOffsetLE() {
		this.position += 2;
		return ((this.buffer[this.position - 1] & 0xff) << 8) + (this.buffer[this.position - 2] - 128 & 0xff);
	}

	public int getSignedShortLE() {
		this.position += 2;
		int j = ((this.buffer[this.position - 1] & 0xff) << 8) + (this.buffer[this.position - 2] & 0xff);
		if (j > 32767)
			j -= 0x10000;
		return j;
	}

	public int getSignedShortOffsetBE() {
		this.position += 2;
		int i = ((this.buffer[this.position - 2] & 0xff) << 8) + (this.buffer[this.position - 1] - 128 & 0xff);
		if (i > 32767)
			i -= 0x10000;
		return i;
	}

	public int get3ByteME() {
		this.position += 3;
		return ((this.buffer[this.position - 2] & 0xff) << 16) + ((this.buffer[this.position - 3] & 0xff) << 8)
				+ (this.buffer[this.position - 1] & 0xff);
	}

	public int getIntLE() {
		this.position += 4;
		return ((this.buffer[this.position - 1] & 0xff) << 24) + ((this.buffer[this.position - 2] & 0xff) << 16)
				+ ((this.buffer[this.position - 3] & 0xff) << 8) + (this.buffer[this.position - 4] & 0xff);
	}

	public int getIntME1() {
		this.position += 4;
		return ((this.buffer[this.position - 2] & 0xff) << 24) + ((this.buffer[this.position - 1] & 0xff) << 16)
				+ ((this.buffer[this.position - 4] & 0xff) << 8) + (this.buffer[this.position - 3] & 0xff);
	}

	public int getIntME2() {
		this.position += 4;
		return ((this.buffer[this.position - 3] & 0xff) << 24) + ((this.buffer[this.position - 4] & 0xff) << 16)
				+ ((this.buffer[this.position - 1] & 0xff) << 8) + (this.buffer[this.position - 2] & 0xff);
	}

	public void getBytesLE(byte buffer[], int offset, int len) {
		for (int k = (offset + len) - 1; k >= offset; k--)
			buffer[k] = this.buffer[this.position++];

	}

	public void getBytesOffset(byte buffer[], int off, int len) {
		for (int l = off; l < off + len; l++)
			buffer[l] = (byte) (this.buffer[this.position++] - 128);

	}

	public byte buffer[];
	public int position;
	public int bitPosition;
	public static final int BIT_FLAGS[] = { 0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767,
			65535, 0x1ffff, 0x3ffff, 0x7ffff, 0xfffff, 0x1fffff, 0x3fffff, 0x7fffff, 0xffffff, 0x1ffffff, 0x3ffffff,
			0x7ffffff, 0xfffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff, -1 };

}
