package com.repose.net;

import java.io.IOException;

import com.repose.util.ISAACRandomGenerator;

/**
 * The {@code PacketDecoder} class interprets incoming packets from the client
 * by decoding the opcode sent and passing the data to the session linked to
 * this class.
 * 
 * @author Robert Guidry
 */
public final class PacketDecoder {

	/**
	 * The amount of bytes sent with each packet's opcode.
	 */
	private static final int[] PACKET_SIZES = { //
			0, 12, 0, 6, 6, 0, 0, 0, 2, 0, // 0
			0, 0, 0, 2, 0, 0, 0, 0, 0, 4, // 10
			0, 0, 0, 0, 6, 0, 0, 0, -1, 0, // 20
			0, 4, 0, 0, 0, 0, 8, 0, 0, 0, // 30
			0, 0, 2, 0, 0, 2, 0, 0, 0, -1, // 40
			6, 0, 0, 0, 6, 6, -1, 8, 0, 0, // 50
			0, 0, 0, 0, 0, 0, 0, 2, 0, 0, // 60
			0, 6, 0, 0, 0, 4, 0, 6, 0, 2, // 70
			2, 0, 0, 8, 0, 0, 0, 0, 0, 0, // 80
			0, 6, 0, 0, 0, 4, 0, 0, 0, 0, // 90
			6, 0, 0, 0, 4, 0, 0, 0, 0, 0, // 100
			0, 0, 2, 0, 0, 0, 2, 0, 0, 1, // 110
			8, 0, 0, 7, 0, 0, 0, 0, 0, 0, // 120
			0, 0, 0, 0, 0, 0, 6, 0, 0, 0, // 130
			4, 8, 0, 8, 0, 0, 0, 0, 0, 0, // 140
			0, 0, 12, 0, 0, 0, 0, 4, 6, 0, // 150
			8, 6, 0, 13, 0, 1, 0, 0, 0, 0, // 160
			0, -1, 0, 3, 0, 0, 3, 6, 0, 0, // 170
			0, 6, 0, 0, 10, 0, 0, 1, 0, 0, // 180
			0, 0, 0, 0, 2, 0, 0, 4, 0, 0, // 190
			0, 0, 0, 6, 0, 0, 8, 0, 0, 0, // 200
			8, 12, 0, -1, 0, 0, 0, 8, 0, 0, // 210
			0, 0, 3, 0, 0, 0, 2, 9, 6, 0, // 220
			6, 6, 0, 2, 0, 0, 0, 0, 0, 0, // 230
			0, 6, 0, 0, 0, 2, 0, -1, 0, 0, // 240
			0, 0, 0, 0, 0, 0, // 250
	};

	/**
	 * The session this class is decoding packets for.
	 */
	private final ClientSession session;

	/**
	 * The random number generator for incoming opcode values.
	 */
	private final ISAACRandomGenerator incomingOpcodeRandom;

	/**
	 * Creates a new PacketDecoder instance for the specified session with the
	 * isaacSeed being used for both decoding opcode values and also to set the
	 * encoder for the session.
	 * 
	 * @param session   the session
	 * @param isaacSeed the ISAAC seed
	 */
	public PacketDecoder(ClientSession session, int[] isaacSeed) {
		this.session = session;
		this.incomingOpcodeRandom = new ISAACRandomGenerator(isaacSeed);
		for (int i = 0; i < isaacSeed.length; i++) {
			isaacSeed[i] += 50;
		}
		session.setOutgoingIsaac(isaacSeed);
		try {
			startDecoding();
		} catch (Exception e) {
			this.session.close();
		}
	}

	/**
	 * Starts the loop that decodes packets from the client session.
	 * 
	 * @throws InterruptedException
	 */
	private void startDecoding() throws IOException, InterruptedException {
		while (!this.session.isClosed()) {
			if (this.session.available() < 1) {
				Thread.sleep(50L);
				continue;
			}

			// read the packet header
			final int opcode = this.session.read() - this.incomingOpcodeRandom.nextInt() & 0xFF;
			final int size;
			if (PACKET_SIZES[opcode] == -1) {
				size = this.session.read();
			} else {
				size = PACKET_SIZES[opcode];
			}

			// read the payload
			this.session.getInputBuffer().rewind();
			if (size > 0) {
				this.session.readToBuffer(size);
			}

			// create a packet from the data
			final byte[] payload = new byte[size];
			this.session.getInputBuffer().get(payload);
			
			// TODO process packet
			final Packet packet = new Packet(opcode, payload);
		}
	}

}
