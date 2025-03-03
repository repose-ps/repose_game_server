package com.repose.net;

/**
 * The {@code Packet} class represents information communicated between the
 * server and client. It contains an opcode which determines how the data is
 * interpreted, and the payload which contains the data.
 * 
 * @author Robert Guidry
 */
public final class Packet {

	/**
	 * The operation code.
	 */
	private final int opcode;

	/**
	 * The data.
	 */
	private final byte[] payload;

	/**
	 * Creates a new Packet instance.
	 * 
	 * @param opcode  the packet's operation code
	 * @param payload the packet's data
	 */
	public Packet(int opcode, byte[] payload) {
		this.opcode = opcode;
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
	 * Returns this packet's data.
	 * 
	 * @return the data
	 */
	public byte[] getPayload() {
		return this.payload;
	}

}
