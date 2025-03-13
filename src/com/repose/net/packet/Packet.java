package com.repose.net.packet;

import java.util.Arrays;

import com.repose.net.packet.in.PacketReceiver;

/**
 * The {@code Packet} class represents information communicated between the
 * server and client. It contains an opcode which determines how the data is
 * interpreted, and the payload which contains the data.
 * 
 * @author Robert Guidry
 */
public final class Packet {

	/**
	 * The PacketSizeType enum indicates the way the size of the packet is
	 * communicated.
	 */
	public static enum PacketSizeType {

		/**
		 * A packet with a fixed amount of bytes.
		 */
		FIXED,

		/**
		 * A packet with 1 byte of size information.
		 */
		VARIABLE_BYTE,

		/**
		 * A packet with 2 bytes of size information.
		 */
		VARIABLE_SHORT
	}

	/**
	 * The operation code.
	 */
	private final int opcode;

	/**
	 * The size type of the packet.
	 */
	private final PacketSizeType sizeType;

	/**
	 * The data.
	 */
	private final byte[] payload;

	/**
	 * Creates a new Packet instance with an empty payload.
	 * 
	 * @param opcode the packet's operation code
	 */
	public Packet(int opcode) {
		this(opcode, PacketSizeType.FIXED, new byte[0]);
	}

	/**
	 * Creates a new Packet instance with a fixed size type.
	 * 
	 * @param opcode  the packet's operation code
	 * @param payload the packet's data
	 */
	public Packet(int opcode, byte[] payload) {
		this(opcode, PacketSizeType.FIXED, payload);
	}

	/**
	 * Creates a new Packet instance.
	 * 
	 * @param opcode  the packet's operation code
	 * @param payload the packet's data
	 */
	public Packet(int opcode, PacketSizeType sizeType, byte[] payload) {
		if (payload == null)
			payload = new byte[0];
		this.opcode = opcode;
		this.sizeType = sizeType;
		this.payload = payload;
	}

	/**
	 * Returns the operation code for this packet.
	 * 
	 * @return the operation code
	 */
	public int getOpcode() {
		return this.opcode;
	}

	/**
	 * Returns the size type for this packet.
	 * 
	 * @return the size type
	 */
	public PacketSizeType getSizeType() {
		return this.sizeType;
	}

	/**
	 * Returns this packet's data.
	 * 
	 * @return the data
	 */
	public byte[] getPayload() {
		return this.payload;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("packet[");
		builder.append("opcode=");
		builder.append(this.getOpcode());
		builder.append(", sizeType=");
		builder.append(this.getSizeType());
		builder.append(", expectedSize=" + PacketReceiver.PACKET_SIZE[this.getOpcode()]);
		builder.append(", payload=");
		builder.append(Arrays.toString(this.payload));
		builder.append("]");
		return builder.toString();
	}

}
