package com.repose.io.cache.def;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;

/**
 * Writes data to files into a standardized format for the game.
 * 
 * @author Robert Guidry
 */
public final class DataWriter implements Closeable {

	private static final String VAR_WHITESPACE = "\t";
	private static final String VAR_SEPARATOR = " = ";
	private static final String ARRAY_SEPARATOR = ", ";

	/**
	 * The writer that writes the data.
	 */
	private final BufferedWriter writer;

	/**
	 * Marks if this writer has written already so that the first line written isn't
	 * blank.
	 */
	private boolean written;

	/**
	 * Stores the next category as a String so that empty categories are never
	 * written until a variable is written below it.
	 */
	private String storedCategory;

	/**
	 * Creates a new DataWriter instance with the specified writer instance.
	 * 
	 * @param writer the writer instance
	 * @throws IOException if an I/O error occurs
	 */
	public DataWriter(Writer writer) throws IOException {
		this.writer = new BufferedWriter(writer);
	}

	/**
	 * Writes directly to the writer.
	 * 
	 * @param string the string to write
	 */
	public void write(String string) throws IOException {
		this.writer.write(string);
		this.written = true;
	}

	/**
	 * Writes a new line to the writer.
	 */
	public void newLine() throws IOException {
		this.writer.newLine();
		this.written = true;
	}

	/**
	 * Writes a variable to the writer under the specified variable name.
	 * 
	 * @param name the variable name
	 * @param val  the value of the variable
	 */
	public void writeVar(String name, Object val) throws IOException {
		if (this.written) {
			this.newLine();
		}
		if (this.storedCategory != null) {
			if (this.written) {
				this.newLine();
			}
			this.write("[" + this.storedCategory + "]");
			this.newLine();
			this.storedCategory = null;
		}
		this.write(VAR_WHITESPACE + name + VAR_SEPARATOR + String.valueOf(val));
	}

	/**
	 * Stores a category to be written the next time a variable is written. If no
	 * variable is written or if another category is written before a variable is
	 * written, this category is not written.
	 * 
	 * @param name the name of the category
	 */
	public void writeCategory(String name) {
		this.storedCategory = name;
	}

	/**
	 * Writes an array of the paramater objects to the file.
	 * 
	 * @param <T>  any Object value
	 * @param name the name of the variable
	 * @param val  the array
	 */
	public <T> void writeVar(String name, T[] val) throws IOException {
		StringBuilder arrayBuilder = new StringBuilder();

		for (int i = 0; i < val.length; i++) {
			if (i != 0)
				arrayBuilder.append(ARRAY_SEPARATOR);
			arrayBuilder.append(val[i]);
		}

		this.writeVar(name, arrayBuilder.toString());
	}

	/**
	 * Writes an array of bytes to the file.
	 * 
	 * @param name the variable name
	 * @param val  the array
	 * @throws IOException if an I/O error occurs
	 */
	public void writeVar(String name, byte... val) throws IOException {
		StringBuilder arrayBuilder = new StringBuilder();

		for (int i = 0; i < val.length; i++) {
			if (i != 0)
				arrayBuilder.append(ARRAY_SEPARATOR);
			arrayBuilder.append(val[i]);
		}

		this.writeVar(name, arrayBuilder.toString());
	}

	/**
	 * Writes an array of shorts to the file.
	 * 
	 * @param name the variable name
	 * @param val  the array
	 * @throws IOException if an I/O error occurs
	 */
	public void writeVar(String name, short... val) throws IOException {
		StringBuilder arrayBuilder = new StringBuilder();

		for (int i = 0; i < val.length; i++) {
			if (i != 0)
				arrayBuilder.append(ARRAY_SEPARATOR);
			arrayBuilder.append(val[i]);
		}

		this.writeVar(name, arrayBuilder.toString());
	}

	/**
	 * Writes an array of integers to the file.
	 * 
	 * @param name the name of the variable
	 * @param val  the array
	 * @throws IOException if an I/O error occurs
	 */
	public void writeVar(String name, int... val) throws IOException {
		StringBuilder arrayBuilder = new StringBuilder();

		for (int i = 0; i < val.length; i++) {
			if (i != 0)
				arrayBuilder.append(ARRAY_SEPARATOR);
			arrayBuilder.append(val[i]);
		}

		this.writeVar(name, arrayBuilder.toString());
	}

	/**
	 * Writes an array of longs to the file.
	 * 
	 * @param name the variable name
	 * @param val  the array
	 * @throws IOException if an I/O error occurs
	 */
	public void writeVar(String name, long... val) throws IOException {
		StringBuilder arrayBuilder = new StringBuilder();

		for (int i = 0; i < val.length; i++) {
			if (i != 0)
				arrayBuilder.append(ARRAY_SEPARATOR);
			arrayBuilder.append(val[i]);
		}

		this.writeVar(name, arrayBuilder.toString());
	}

	/**
	 * Writes an array of floats to the file.
	 * 
	 * @param name the variable name
	 * @param val  the array
	 * @throws IOException if an I/O error occurs
	 */
	public void writeVar(String name, float... val) throws IOException {
		StringBuilder arrayBuilder = new StringBuilder();

		for (int i = 0; i < val.length; i++) {
			if (i != 0)
				arrayBuilder.append(ARRAY_SEPARATOR);
			arrayBuilder.append(Float.toString(val[i]));
		}

		this.writeVar(name, arrayBuilder.toString());
	}

	/**
	 * Writes an array of doubles to the file.
	 * 
	 * @param name the variable name
	 * @param val  the array
	 * @throws IOException if an I/O error occurs
	 */
	public void writeVar(String name, double... val) throws IOException {
		StringBuilder arrayBuilder = new StringBuilder();

		for (int i = 0; i < val.length; i++) {
			if (i != 0)
				arrayBuilder.append(ARRAY_SEPARATOR);
			arrayBuilder.append(Double.toString(val[i]));
		}

		this.writeVar(name, arrayBuilder.toString());
	}

	/**
	 * Writes an array of bytes to the file.
	 * 
	 * @param name the variable name
	 * @param val  the array
	 * @throws IOException if an I/O error occurs
	 */
	public void writeVar(String name, char... val) throws IOException {
		StringBuilder arrayBuilder = new StringBuilder();

		for (int i = 0; i < val.length; i++) {
			if (i != 0)
				arrayBuilder.append(ARRAY_SEPARATOR);
			arrayBuilder.append(Character.toString(val[i]));
		}

		this.writeVar(name, arrayBuilder.toString());
	}

	@Override
	public void close() throws IOException {
		this.writer.close();
	}
}
