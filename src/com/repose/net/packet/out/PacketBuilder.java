package com.repose.net.packet.out;

import java.nio.ByteBuffer;

import com.repose.GameSettings;
import com.repose.net.ClientSession;
import com.repose.net.packet.Packet;
import com.repose.net.packet.Packet.PacketSizeType;

/**
 * The PacketBuilder class is a utility class for constructing {@link Packet}
 * instances.
 * 
 * @author Robert Guidry
 */
public final class PacketBuilder {

	/**
	 * Bit masks for the bit writer
	 */
	private static final int bitMaskOut[] = new int[32];
	static {
		for (int i = 0; i < 32; i++)
			bitMaskOut[i] = (1 << i) - 1;
	}

	/**
	 * The buffer where the packet's data is stored.
	 */
	private ByteBuffer buffer;

	/**
	 * The packet operation code.
	 */
	private int opcode;

	/**
	 * The size type for the packet.
	 */
	private PacketSizeType sizeType;

	private int bitPosition = -1;

	/**
	 * Creates a new PacketBuilder instance.
	 */
	public PacketBuilder(int opcode, PacketSizeType sizeType) {
		this.opcode = opcode;
		this.sizeType = sizeType;
		final int bufferSize = GameSettings.getSettingAsInt(ClientSession.SETTING_BUFFER_SIZE_KEY);
		this.buffer = ByteBuffer.allocate(bufferSize);
	}

	/**
	 * Returns the buffer used for this packet builder.
	 * 
	 * @return the buffer
	 */
	public ByteBuffer getBuffer() {
		return this.buffer;
	}

	/**
	 * Adds a byte to this packet's buffer.
	 * 
	 * @param b the byte
	 */
	public PacketBuilder putByte(int b) {
		this.buffer.put((byte) (b & 0xFF));
		return this;
	}

	/**
	 * Adds a short to this packet's buffer.
	 * 
	 * @param s the short
	 */
	public PacketBuilder putShort(int s) {
		this.buffer.putShort((short) (s & 0xFFFF));
		return this;
	}

	/**
	 * Adds an int to this packet's buffer.
	 * 
	 * @param i the int
	 */
	public PacketBuilder putInt(int i) {
		this.buffer.putInt(i);
		return this;
	}

	/**
	 * Adds a long to this packet's buffer.
	 * 
	 * @param l the long
	 */
	public PacketBuilder putLong(long l) {
		this.buffer.putLong(l);
		return this;
	}

	/**
	 * Writes a String to the buffer.
	 * 
	 * @param string the String
	 */
	public PacketBuilder putString(String string) {
		this.getBuffer().put(string.getBytes());
		this.getBuffer().put((byte) 10);
		return this;
	}

	/**
	 * Creates a new Packet from this packet builder's data.
	 * 
	 * @return the new Packet instance
	 */
	public Packet toPacket() {
		final byte[] payload = new byte[this.buffer.position()];
		System.arraycopy(this.buffer.array(), 0, payload, 0, this.buffer.position());
		return new Packet(this.opcode, this.sizeType, payload);
	}

	/**
	 * Starts the block for writing bits, in which only bits can be written to the
	 * buffer.
	 */
	public PacketBuilder startBitBlock() {
		if (this.bitPosition != -1)
			throw new IllegalArgumentException("bit block is already started");
		this.bitPosition = this.getBuffer().position() * 8;
		return this;
	}

	/**
	 * Adds bits to the buffer. If the bit block is not started, an error is thrown.
	 * 
	 * @param numBits the number of bits to write
	 * @param value   the value of the bits
	 */
	public PacketBuilder putBits(int numBits, int value) {
		if (this.bitPosition == -1)
			throw new IllegalArgumentException("bit block is not started");
		int bytePos = this.bitPosition >> 3;
		int bitOffset = 8 - (this.bitPosition & 7);
		this.bitPosition += numBits;

		final byte[] buffer = this.getBuffer().array();
		for (; numBits > bitOffset; bitOffset = 8) {
			buffer[bytePos] &= ~bitMaskOut[bitOffset];
			buffer[bytePos++] |= (value >> (numBits - bitOffset)) & bitMaskOut[bitOffset];

			numBits -= bitOffset;
		}
		if (numBits == bitOffset) {
			buffer[bytePos] &= ~bitMaskOut[bitOffset];
			buffer[bytePos] |= value & bitMaskOut[bitOffset];
		} else {
			buffer[bytePos] &= ~(bitMaskOut[numBits] << (bitOffset - numBits));
			buffer[bytePos] |= (value & bitMaskOut[numBits]) << (bitOffset - numBits);
		}

		return this;
	}

	/**
	 * Stores a single boolean value as the next bit.
	 * 
	 * @param value the bit value, true = 1, false = 0
	 */
	public PacketBuilder putBit(boolean value) {
		return this.putBits(1, value ? 1 : 0);
	}

	/**
	 * Ends the bit block, which allows bytes to be written.
	 */
	public PacketBuilder endBitBlock() {
		if (this.bitPosition == -1)
			throw new IllegalArgumentException("bit block is not started");
		this.buffer.position((this.bitPosition + 7) / 8);
		this.bitPosition = -1;
		return this;
	}
}
