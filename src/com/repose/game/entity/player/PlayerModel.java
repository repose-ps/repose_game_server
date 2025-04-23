package com.repose.game.entity.player;

import java.util.Arrays;

import com.repose.game.entity.Actor;
import com.repose.game.entity.player.PlayerEquipment.EquipmentSlot;
import com.repose.net.packet.out.PacketBuilder;

/**
 * The {@code PlayerCharacter} class represents a player character model and its
 * representation in the game world.
 * 
 * @author Robert Guidry
 */
public abstract class PlayerModel extends Actor {

	/**
	 * The default value for the body model indices.
	 */
	private static final int[] DEFAULT_BODY_MODELS = { 7, 25, 29, 35, 39, 44, 14 };

	/**
	 * The default value for the body model colors.
	 */
	private static final int[] DEFAULT_BODY_COLORS = { 7, 8, 9, 5, 0 };

	/**
	 * The characters used to encode usernames.
	 */
	private static final char USERNAME_ENCODING_CHARS[] = { '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
			'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9' };

	public static long encodeUsernameAsLong(String username) {
		long l = 0L;
		for (int i = 0; i < username.length(); i++) {
			char c = username.charAt(i);
			l *= 37L;
			if (c >= 'A' && c <= 'Z')
				l += (1 + c) - 65;
			else if (c >= 'a' && c <= 'z')
				l += (1 + c) - 97;
			else if (c >= '0' && c <= '9')
				l += (27 + c) - 48;
		}
		while (l % 37L == 0L && l != 0L)
			l /= 37L;
		return l;
	}

	public static String decodeUsernameAsLong(long encodedUsername) {
		if (encodedUsername <= 0L || encodedUsername >= 0x5b5b57f8a98a5dd1L)
			return "invalid_name";
		if (encodedUsername % 37L == 0L)
			return "invalid_name";
		int charCount = 0;
		char usernameChars[] = new char[12];
		while (encodedUsername != 0L) {
			long l1 = encodedUsername;
			encodedUsername /= 37L;
			usernameChars[11 - charCount++] = USERNAME_ENCODING_CHARS[(int) (l1 - encodedUsername * 37L)];
		}
		return new String(usernameChars, 12 - charCount, charCount);
	}

	/**
	 * The body type (called gender in client) of this player character model.
	 */
	private int bodyType;

	/**
	 * The model indices for the PC's character model.
	 */
	private int[] bodyModels = Arrays.copyOf(DEFAULT_BODY_MODELS, DEFAULT_BODY_MODELS.length);

	/**
	 * The model color indices for the PC's character model.
	 */
	private int[] bodyModelColors = Arrays.copyOf(DEFAULT_BODY_COLORS, DEFAULT_BODY_COLORS.length);

	/**
	 * The NPC index that this player character is taking on the appearance of. By
	 * default this value is -1.
	 */
	private int npcTransmorphId = -1;

	/**
	 * The item that this player character has equipped in the array index's as the
	 * slot value.
	 */
	private final PlayerEquipment equipment;

	/**
	 * The name of this player model.
	 */
	private String username;

	/**
	 * Has the appearance of this player model changed since the last tick?
	 */
	private boolean appearanceChanged;

	/**
	 * Creates a new PlayerModel instance.
	 */
	public PlayerModel() {
		this.equipment = new PlayerEquipment(this);
		this.username = "null";
		this.appearanceChanged = true;
	}

	@Override
	public void clearStoredVariables() {
		super.clearStoredVariables();
		this.appearanceChanged = false;
	}

	@Override
	public boolean isUpdating() {
		if (super.isUpdating())
			return true;

		return this.appearanceChanged;
	}

	/**
	 * Appends this player model's update block to the specified builder.
	 * 
	 * @param builder the builder
	 */
	public void appendUpdateBlock(PacketBuilder builder, boolean forceAppearance) {
		int mask = 0;

		if (this.appearanceChanged || forceAppearance) {
			mask |= 4;
		}

		if (mask > 0xFF) {
			mask |= 0x20;
			builder.putByte(mask);
			builder.putByte(mask >> 8);
		} else {
			builder.putByte(mask);
		}

		if (this.appearanceChanged || forceAppearance) {
			PacketBuilder appearanceBuffer = new PacketBuilder();
			appendAppearanceUpdate(appearanceBuffer);
			final byte[] payload = appearanceBuffer.toPacket().getPayload();
			builder.putByte(payload.length);
			builder.putBytes(payload, 0, payload.length);
		}

	}

	/**
	 * Appends this player model's appearance to the update block.
	 * 
	 * @param builder the packet builder to append the appearance to
	 */
	private void appendAppearanceUpdate(PacketBuilder builder) {
		builder.putByte(this.getBodyType());
		builder.putByte(-1); // TODO skull icon
		builder.putByte(-1); // TODO prayer icon

		if (this.getNpcTransmorphId() != -1) {
			builder.putByte(0xFF);
			builder.putByte(0xFF);
			builder.putShort(this.getNpcTransmorphId());
		} else {
			writeAppearanceSlot(builder, EquipmentSlot.HAT, null);
			writeAppearanceSlot(builder, EquipmentSlot.CAPE, null);
			writeAppearanceSlot(builder, EquipmentSlot.NECK, null);
			writeAppearanceSlot(builder, EquipmentSlot.WEAPON, null);
			writeAppearanceSlot(builder, EquipmentSlot.BODY, PlayerAppearance.TORSO);
			writeAppearanceSlot(builder, EquipmentSlot.SHIELD, null);
			writeAppearanceSlot(builder, null, PlayerAppearance.ARMS);
			writeAppearanceSlot(builder, EquipmentSlot.LEGS, PlayerAppearance.LEGS);
			writeAppearanceSlot(builder, null, PlayerAppearance.HEAD);
			writeAppearanceSlot(builder, EquipmentSlot.HANDS, PlayerAppearance.HANDS);
			writeAppearanceSlot(builder, EquipmentSlot.FEET, PlayerAppearance.FEET);
			writeAppearanceSlot(builder, null, PlayerAppearance.JAW);
		}

		for (int i = 0; i < this.bodyModelColors.length; i++) {
			builder.putByte(this.bodyModelColors[i]);
		}

		// TODO movement animations
		builder.putShort(808); // stand
		builder.putShort(823); // turn
		builder.putShort(819); // walk
		builder.putShort(820); // turn around
		builder.putShort(821); // turn right
		builder.putShort(822); // turn left
		builder.putShort(824); // run
		builder.putLong(PlayerModel.encodeUsernameAsLong(this.getUsername()));
		// TODO combat level / skill level
		builder.putByte(126); // combat level
		builder.putShort(0);
	}

	/**
	 * Writes an appearance value as either an equipment slot item or a player model
	 * appearance.
	 * 
	 * @param builder    the builder to write to.
	 * @param slot       the item equipment slot
	 * @param appearance the appearance slot
	 */
	private void writeAppearanceSlot(PacketBuilder builder, EquipmentSlot slot, PlayerAppearance appearance) {
		final int equipmentOffset = 0x200;
		final int appearanceOffset = 0x100;
		final int empty = 0;
		if (slot != null && !this.getEquipment().isEmpty(slot)) {
			builder.putShort(equipmentOffset + this.getEquipment().getItem(slot).getItemId());
		} else {
			if (appearance != null) {
				builder.putShort(appearanceOffset + this.bodyModels[appearance.getArrayIndex()]);
			} else {
				builder.putByte(empty);
			}
		}
	}

	/**
	 * Makes this player model have an appearance update on the next tick.
	 */
	public void requestAppearanceUpdate() {
		this.appearanceChanged = true;
	}

	/**
	 * Returns the integer value for this PC's body type. <br>
	 * 0 = masculine body type<br>
	 * 1 = feminine body type
	 * 
	 * @return the value
	 */
	public int getBodyType() {
		return this.bodyType;
	}

	/**
	 * Sets the integer value for this PC's body type<br>
	 * 0 = masculine body type<br>
	 * 1 = feminine body type
	 * 
	 * @param bodyType the value
	 */
	public void setBodyType(int bodyType) {
		this.bodyType = bodyType;
		this.requestAppearanceUpdate();
	}

	public int[] getBodyModels() {
		return this.bodyModels;
	}

	public void setBodyModel(PlayerAppearance appearance, int modelId) {
		this.bodyModels[appearance.getArrayIndex()] = modelId;
	}

	public int[] getBodyModelColors() {
		return this.bodyModelColors;
	}

	public void setBodyColor(int index, int colorId) {
		this.bodyModelColors[index] = colorId;
	}

	/**
	 * Returns the NPC index this player character is using for its appearance, or
	 * -1 if it is not set.
	 * 
	 * @return the NPC index
	 */
	public int getNpcTransmorphId() {
		return this.npcTransmorphId;
	}

	/**
	 * Sets the value of the NPC index this player character is using for its
	 * appearance, or -1 if it is not set.
	 * 
	 * @param npcTransmorphId the NPC index
	 */
	public void setNpcTransmorphId(int npcTransmorphId) {
		this.npcTransmorphId = npcTransmorphId;
		this.requestAppearanceUpdate();
	}

	/**
	 * Returns this player model's equipment inventory.
	 * 
	 * @return the equipment inventory
	 */
	public PlayerEquipment getEquipment() {
		return this.equipment;
	}

	/**
	 * Returns the name displayed on this player model.
	 * 
	 * @return the name
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * Sets the name displayed on this player model to the specified value.
	 * 
	 * @param username the value
	 */
	public void setUsername(String username) {
		if (username == null)
			username = "null";
		if (username.equalsIgnoreCase(this.username))
			return;

		this.username = username;
		this.requestAppearanceUpdate();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof PlayerModel))
			return false;

		final String username = ((PlayerModel) o).getUsername();
		return this.getUsername().equalsIgnoreCase(username);
	}

}
