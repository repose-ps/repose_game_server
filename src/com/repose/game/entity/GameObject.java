package com.repose.game.entity;

import com.repose.game.world.map.TilePosition;

public class GameObject extends Entity {

	private int orientation;
	private int objectType;
	private int objectId;

	public GameObject(int objectId, int absX, int absY, int plane, int type, int orientation) {
		this.objectId = objectId;
		this.setPosition(absX, absY, plane);
		this.objectType = type;
		this.orientation = orientation;
	}

	public int getObjectID() {
		return this.objectId;
	}

	public int getOrientation() {
		return this.orientation;
	}

	public int getObjectType() {
		return this.objectType;
	}

	@Override
	public String toString() {
		return "game_object=[" + super.toString() + ", id=" + this.getObjectID() + ", orientation="
				+ this.getOrientation() + ", type=" + this.getObjectType() + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof GameObject))
			return false;
		final GameObject obj = (GameObject) o;
		if (!((TilePosition) obj).equals(this))
			return false;

		if (this.getOrientation() != obj.getOrientation() || this.getObjectType() != obj.getObjectType())
			return false;

		return true;
	}

}
