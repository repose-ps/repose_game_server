package com.repose.game.entity.player;

public enum PlayerAppearance {
	/**
	 * The 'head' model, which consists of hairstyles.
	 */
	HEAD(0),

	/**
	 * The clothing worn on the player's upper body.
	 */
	TORSO(1),

	/**
	 * The sleeves on the player's upper body.
	 */
	ARMS(2),

	/**
	 * The wrists of the player's model.
	 */
	HANDS(3),

	/**
	 * The clothing worn on the player's lower body.
	 */
	LEGS(4),

	/**
	 * The feet model.
	 */
	FEET(5),

	/**
	 * The 'jaw' model, which consists of beards.
	 */
	JAW(6);

	/**
	 * The index of the appearance value in the player's appearance array.
	 */
	private final int arrayIndex;

	/**
	 * Creates a new PlayerModelPart instance. The specified array index refers to
	 * the PlayerModel class's appearance array.
	 * 
	 * @param arrayIndex the appearance array index
	 */
	PlayerAppearance(int arrayIndex) {
		this.arrayIndex = arrayIndex;
	}

	/**
	 * Returns the index of the appearance value in the player's appearance array.
	 * 
	 * @return the index
	 */
	public int getArrayIndex() {
		return this.arrayIndex;
	}

}
