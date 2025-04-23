package com.repose.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

import com.repose.GameSettings;
import com.repose.net.packet.Packet;
import com.repose.net.packet.Packet.PacketSizeType;
import com.repose.util.ISAACRandomGenerator;

/**
 * The {@code ClientSession} class holds the information that a client has for
 * communicating with the server through the network.
 * 
 * @author Robert Guidry
 */
public final class ClientSession {

	/**
	 * The setting name for the buffer size.
	 */
	public static final String SETTING_BUFFER_SIZE_KEY = "buffer_size";

	/**
	 * The default value for the buffer size setting.
	 */
	private static final int SETTING_BUFFER_SIZE_VALUE = 5000;

	/**
	 * Initializes settings for the {@code ClientSession} class.
	 */
	public static final void initSettings() {
		GameSettings.setSetting(SETTING_BUFFER_SIZE_KEY, SETTING_BUFFER_SIZE_VALUE, GameSettings.CATEGORY_NETWORK);
	}

	/**
	 * the client socket for this client's session
	 */
	private final Socket socket;

	/**
	 * The input stream for this client's socket
	 */
	private final InputStream inputStream;

	/**
	 * The output stream for this client's socket.
	 */
	private final OutputStream outputStream;

	/**
	 * Has this session been closed?
	 */
	private boolean closed;

	/**
	 * The buffer that stores bytes after being read from the input stream.
	 */
	private final ByteBuffer inputBuffer;

	/**
	 * The buffer that stores bytes before being written to the output stream.
	 */
	private final ByteBuffer outputBuffer;

	/**
	 * The random number generator used to encode opcode values to the client.
	 */
	private ISAACRandomGenerator outgoingOpcodeRandom;

	/**
	 * The packets queued to be sent to the client.
	 */
	private final Queue<Packet> queuedOutgoingPackets;

	private final Queue<Packet> queuedIncomingPackets;

	/**
	 * Creates a new {@code ClientSession} instance with the specified socket as the
	 * underlying network communication.
	 * 
	 * @param socket the socket
	 * @throws IOException if an I/O error occurred
	 */
	ClientSession(Socket socket) throws IOException {
		this.socket = socket;
		this.inputStream = socket.getInputStream();
		this.outputStream = socket.getOutputStream();

		final int bufferSize = GameSettings.getSettingAsInt(SETTING_BUFFER_SIZE_KEY);
		this.inputBuffer = ByteBuffer.allocate(bufferSize);
		this.outputBuffer = ByteBuffer.allocate(bufferSize);

		this.queuedIncomingPackets = new LinkedList<>();
		this.queuedOutgoingPackets = new LinkedList<>();
	}

	/**
	 * Disconnects this session from the server. The socket is closed, and this
	 * session is marked as disconnected.
	 */
	public void close() {
		this.closed = true;
		try {
			this.socket.close();
		} catch (IOException e) {
		}
	}

	/**
	 * Returns true if the {@link #close()} method has been called.
	 * 
	 * @return true if this session is disconnected
	 */
	public boolean isClosed() {
		return this.closed;
	}

	/**
	 * Returns an estimate of the number of bytes available to be read in the input
	 * stream.
	 * 
	 * @return the number of bytes
	 * @throws IOException if an I/O error corrus
	 */
	public int available() throws IOException {
		if (this.isClosed())
			return 0;
		else
			return this.inputStream.available();
	}

	/**
	 * Reads a single byte directly from the input stream.
	 * 
	 * @return the byte
	 * @throws IOException if an I/O error occurs
	 */
	public int read() throws IOException {
		if (this.isClosed())
			return -1;
		return this.inputStream.read();
	}

	/**
	 * Writes the specified number of bytes to the buffer at the position directly
	 * from the input stream.
	 * 
	 * @param buffer the buffer
	 * @param offset the position
	 * @param length the number of bytes
	 * @throws IOException if an I/O error occurs
	 */
	public void read(byte[] buffer, int offset, int length) throws IOException {
		if (this.isClosed())
			return;

		final byte[] readBytes = this.inputStream.readNBytes(length);
		System.arraycopy(readBytes, 0, buffer, offset, length);
	}

	/**
	 * Reads a specified number of bytes to the input buffer.
	 * 
	 * @param nBytes the number of bytes
	 * @throws IOException if an I/O error occurs
	 */
	public void readToBuffer(int nBytes) throws IOException {
		read(this.getInputBuffer().array(), this.getInputBuffer().position(), nBytes);
	}

	/**
	 * Writes a single byte to the output buffer. Only the lower 8 bits are written
	 * from the value, the rest are discarded.
	 * 
	 * @param val the single byte value
	 */
	public void write(int val) {
		this.getOutputBuffer().put((byte) (val & 0xFF));
	}

	/**
	 * Writes a specified number of bytes at the position from the specified array
	 * to the output buffer.
	 * 
	 * @param buffer the array
	 * @param offset the position
	 * @param length the number of bytes
	 */
	public void write(byte[] buffer, int offset, int length) {
		this.getOutputBuffer().put(buffer, offset, length);
	}

	/**
	 * Flushes the output buffer's bytes to the output stream up to the current
	 * position. The buffer's position is then reset to 0.
	 */
	public void flush() throws IOException {
		this.outputStream.write(this.outputBuffer.array(), 0, this.outputBuffer.position());
		this.outputBuffer.rewind();
	}

	/**
	 * Returns the {@link ByteBuffer} instance used for incoming data from the
	 * client.
	 * 
	 * @return the {@code ByteBuffer} instance.
	 */
	public ByteBuffer getInputBuffer() {
		return this.inputBuffer;
	}

	/**
	 * Returns the {@link ByteBuffer} instance used for outgoing data to the client.
	 * 
	 * @return the {@code ByteBuffer} instance.
	 */
	public ByteBuffer getOutputBuffer() {
		return this.outputBuffer;
	}

	/**
	 * Creates a new ISAACRandomGenerator instance from the specified ISAAC seed.
	 * 
	 * @param seed the ISAAC seed
	 */
	public void setOutgoingIsaac(int[] seed) {
		this.outgoingOpcodeRandom = new ISAACRandomGenerator(seed);
	}

	/**
	 * Writes a String to the output buffer Jagex's way. It does this by writing all
	 * the String's bytes, then appending a value of {@code 10} at the end to
	 * indicate the end of the String.
	 * 
	 * @param str the String to write
	 */
	public void writeJagString(String str) {
		this.getOutputBuffer().put(str.getBytes());
		this.getOutputBuffer().put((byte) 10);
	}

	/**
	 * Reads a String from the input buffer by reading it the way Jagex writes them.
	 * It does this by reading the bytes until it reaches a value of {@code 10},
	 * which indicates the end of the String.
	 * 
	 * @return the buffer's String value
	 */
	public String readJagString() {
		final int start = this.getInputBuffer().position();
		while (this.getInputBuffer().get() != 10)
			;
		final int len = this.getInputBuffer().position() - 1 - start;
		return new String(this.getInputBuffer().array(), start, len);
	}

	/**
	 * Writes an opcode to this session's output buffer using the ISAAC random
	 * generator.
	 * 
	 * @param opcode the opcode value
	 */
	private void writeOpcode(int opcode) {
		this.write((byte) (opcode + this.outgoingOpcodeRandom.nextInt()));
	}

	/**
	 * Adds an incoming packet instance to this session's incoming packet queue.
	 * 
	 * @param packet the incoming packet
	 */
	public void queueIncomingPacket(Packet packet) {
		this.queuedIncomingPackets.add(packet);
	}

	/**
	 * Adds an outgoing packet instance to this session's outgoing packet queue.
	 * 
	 * @param packet the outgoing packet
	 */
	public void queueOutgoingPacket(Packet packet) {
		this.queuedOutgoingPackets.add(packet);
	}

	/**
	 * Polls the next packet in the queued incoming packet queue.
	 * 
	 * @return the next packet, or null if the queue is empty
	 */
	public Packet pollPacketQueue() {
		return this.queuedIncomingPackets.poll();
	}

	/**
	 * Writes and flushes all of the outgoing packets for this session.
	 */
	public void writeOutgoingPackets() {
		Packet packet;
		boolean disconnecting = false;
		while ((packet = this.queuedOutgoingPackets.poll()) != null) {

			// validate packet size
			final int size = packet.getPayload().length;
			if (size > this.getOutputBuffer().capacity()) {
				throw new IndexOutOfBoundsException("Packet too large: " + size);
			}

			// validate the amount of space in the out buffer
			// flush if necessary
			final int maxPosition = this.getOutputBuffer().position() + size + 3;
			if (maxPosition >= this.getOutputBuffer().capacity()) {
				try {
					flush();
				} catch (IOException e) {
					close();
					break;
				}
			}

			// write the encrypted opcode
			writeOpcode(packet.getOpcode());

			// write the size if applicable
			switch (packet.getSizeType()) {
			case PacketSizeType.VARIABLE_BYTE:
				this.getOutputBuffer().put((byte) size);
				break;
			case PacketSizeType.VARIABLE_SHORT:
				this.getOutputBuffer().mark();
				this.getOutputBuffer().putShort((short) size);
				break;
			case PacketSizeType.FIXED:
				break;
			}

			// write the packet's payload
			this.getOutputBuffer().put(packet.getPayload());

			if (packet.getOpcode() == 5) {
				disconnecting = true;
				return;
			}
		}
		try {
			flush();
		} catch (IOException e) {
			close();
		}

		if (disconnecting) {
			close();
		}
	}
}