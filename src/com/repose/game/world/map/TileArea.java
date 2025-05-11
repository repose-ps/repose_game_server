package com.repose.game.world.map;

import java.util.Objects;

/**
 * Represents an area of the map with a top-left position and a width and
 * height.
 */
public class TileArea extends TilePosition {

	/**
	 * The amount of tiles on the X-axis.
	 */
	private int width;

	/**
	 * The amount of tiles on the Y-axis.
	 */
	private int height;

	public void setWidth(int width) {
		this.width = width;
	}

	public void setSize(int width, int height) {
		this.setWidth(width);
		this.setHeight(height);
	}

	/**
	 * Returns the maximum X value (inclusive) of this area.
	 * 
	 * @return the max X value
	 */
	public int getMaxX() {
		return this.getX() + this.getWidth() - 1;
	}

	/**
	 * Returns the maximum Y value (inclusive) of this area.
	 * 
	 * @return the max Y value
	 */
	public int getMaxY() {
		return this.getY() + this.getHeight() - 1;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	/**
	 * Returns a String representation of this TileArea instance.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("tile_area=[");
		builder.append("x=" + this.getX());
		builder.append(", y=" + this.getY());
		builder.append(", plane=" + this.getPlane());
		builder.append(", width=" + this.getWidth());
		builder.append(", height=" + this.getHeight());
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Returns a hash of the X,Y,plane,width,height coordinates of this TileArea
	 * instance.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.getX(), this.getY(), this.getPlane(), this.getWidth(), this.getHeight());
	}

	/**
	 * Returns true if the specified object is not null and an instance of TileArea
	 * and the coordinates are equal to this instance's.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || !(o instanceof TileArea))
			return false;
		final TileArea ta = (TileArea) o;
		return this.getX() == ta.getX() && this.getY() == ta.getY() && this.getPlane() == ta.getPlane()
				&& this.getWidth() == ta.getWidth() && this.getHeight() == ta.getHeight();
	}
}
