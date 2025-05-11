package com.repose.io.cache.map;

import java.util.HashMap;
import java.util.Map;

import com.repose.game.entity.GameObject;
import com.repose.game.world.map.TilePosition;

public class MapTile extends TilePosition {

	public int clippingData = 0x1000000;

	public Map<Integer, GameObject> tileObjects = new HashMap<>();
	
	public MapTile(int absX, int absY, int plane) {
		this.setX(absX);
		this.setY(absY);
		this.setPlane(plane);
	}

	@Override
	public String toString() {
		return "map_tile=[" + super.toString() + ", clippingData=" + this.clippingData + "]";
	}

	@Override
	public boolean equals(Object o) {
		return (this == o);
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}
}
