package com.repose.game.world.map;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Objects;

/**
 * Represents a single tile's coordinates within the game world.
 */
public class TilePosition {

	/**
	 * The tile X coordinate.
	 */
	private int x;

	/**
	 * The tile Y coordinate.
	 */
	private int y;

	/**
	 * The vertical plane index.
	 */
	private int plane;

	/**
	 * Creates a new TilePosition instance with coordinates {@code (0,0,0)}.
	 */
	public TilePosition() {
		this(0, 0, 0);
	}

	/**
	 * Creates a new TilePosition instance with coordinates {@code (X,Y,0)}.
	 * 
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 */
	public TilePosition(int x, int y) {
		this(x, y, 0);
	}

	/**
	 * Creates a new TilePosition instance with coordinates {@code (X,Y,plane)}.
	 * 
	 * @param x     the X coordinate
	 * @param y     the Y coordinate
	 * @param plane the vertical plane index
	 */
	public TilePosition(int x, int y, int plane) {
		this.setPosition(x, y, plane);
	}

	/**
	 * Returns this tile's region X coordinate.
	 * 
	 * @return the region X coordinate
	 */
	public int getRegionX() {
		return this.getX() / MapRegion.TILE_DIMENSION;
	}

	/**
	 * Returns this tile's region Y coordinate.
	 * 
	 * @return the region Y coordinate
	 */
	public int getRegionY() {
		return this.getY() / MapRegion.TILE_DIMENSION;
	}

	/**
	 * Returns this tile's chunk X coordinate.
	 * 
	 * @return the chunk X coordinate
	 */
	public int getChunkX() {
		return this.getX() / MapChunk.TILE_DIMENSION;
	}

	/**
	 * Returns this tile's chunk Y coordinate.
	 * 
	 * @return the chunk Y coordinate
	 */
	public int getChunkY() {
		return this.getY() / MapChunk.TILE_DIMENSION;
	}

	/**
	 * Sets the X,Y coordinates of this position to the specified point's X,Y
	 * coordinates on the same vertical plane.
	 * 
	 * @param point the specified point
	 */
	public void set2DPosition(Point2D point) {
		this.set2DPosition((int) point.getX(), (int) point.getY());
	}

	/**
	 * Sets the tile coordinates of this position to the specified X,Y coordinates
	 * on the current vertical plane index.
	 * 
	 * @param x the X value
	 * @param y the Y value
	 */
	public void set2DPosition(int x, int y) {
		setPosition(x, y, this.getPlane());
	}

	/**
	 * Sets the coordinates of this position to the specified tile's coordinates.
	 * 
	 * @param position the specified tile
	 */
	public void setPosition(TilePosition position) {
		this.setPosition(position.getX(), position.getY(), position.getPlane());
	}

	/**
	 * Sets the tile coordinates of this position to the specified X,Y coordinates
	 * and plane index.
	 * 
	 * @param x     the X coordinate
	 * @param y     the Y coordinate
	 * @param plane the plane index
	 */
	public void setPosition(int x, int y, int plane) {
		this.setX(x);
		this.setY(y);
		this.setPlane(plane);
	}

	/**
	 * Returns this tile's coordinates as a new TilePosition instance.
	 * 
	 * @return the instance
	 */
	public TilePosition getPosition() {
		TilePosition copy = new TilePosition();
		copy.setPosition(this);
		return copy;
	}

	/**
	 * Returns the 2D {@code (X,Y)} coordinates of this tile.
	 * 
	 * @return the coordinates as a {@link Point}
	 */
	public Point getPoint() {
		return new Point(this.getX(), this.getY());
	}

	/**
	 * Returns the tile X coordinate of this position.
	 * 
	 * @return the coordinate
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * Sets the tile X coordinate of this position to the specified value.
	 * 
	 * @param x the value
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Returns the tile Y coordinate of this position.
	 * 
	 * @return the y the coordinate
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * Sets the tile Y coordinate of this position to the specified value.
	 * 
	 * @param y the value
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Returns the vertical plane index of this position.
	 * 
	 * @return the index
	 */
	public int getPlane() {
		return this.plane;
	}

	/**
	 * Sets the vertical plane index of this position to the specified value.
	 * 
	 * @param plane the plane to set
	 */
	public void setPlane(int plane) {
		this.plane = plane;
	}

	/**
	 * Returns a String representation of this TilePosition instance.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("tile_position=[");
		builder.append("x=" + this.getX());
		builder.append(", y=" + this.getY());
		builder.append(", plane=" + this.getPlane());
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Returns a hash of the X,Y,plane coordinates of this TilePosition instance.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.getX(), this.getY(), this.getPlane());
	}

	/**
	 * Returns true if the specified object is not null and an instance of
	 * TilePosition and the coordinates are equal to this instance's.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || !(o instanceof TilePosition))
			return false;
		final TilePosition tp = (TilePosition) o;
		return this.getX() == tp.getX() && this.getY() == tp.getY() && this.getPlane() == tp.getPlane();
	}

}
