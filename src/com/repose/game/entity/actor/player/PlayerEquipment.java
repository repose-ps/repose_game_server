package com.repose.game.entity.actor.player;

import com.repose.game.item.ItemInventory;
import com.repose.game.item.ItemStack;

/**
 * The equipment class represents the item equipment a {@code PlayerModel} is
 * wearing.
 */
public class PlayerEquipment {

	/**
	 * Represents an equipment slot in the PlayerEquipment class.
	 */
	public static enum EquipmentSlot {
		HAT(0),
		CAPE(1),
		NECK(2),
		WEAPON(3),
		BODY(4),
		SHIELD(5),
		LEGS(7),
		HANDS(9),
		FEET(10),
		RING(12),
		ARROWS(13);

		private final int slotIndex;

		EquipmentSlot(int slotIndex) {
			this.slotIndex = slotIndex;
		}

		public int getSlotIndex() {
			return this.slotIndex;
		}
	}

	/**
	 * The amount of inventory slots in this equipment's inventory instance.
	 */
	public static final int SIZE = 14;

	/**
	 * The inventory of items containing the equipped items.
	 */
	private final ItemInventory equipmentInventory;

	/**
	 * The player instance that this equipment belongs to.
	 */
	private final PlayerModel player;

	public PlayerEquipment(PlayerModel player) {
		this.equipmentInventory = new ItemInventory(SIZE);
		this.player = player;
	}

	/**
	 * Returns true if the item in the specified slot has an item ID of 0 or less,
	 * or the amount is less than one. It will return false otherwise.
	 * 
	 * @param slot the equipment slot
	 * @return
	 */
	public boolean isEmpty(EquipmentSlot slot) {
		final ItemStack item = this.getInventory().getItem(slot.getSlotIndex());
		return item.getItemId() <= 0 || item.getAmount() < 1;
	}

	/**
	 * Returns the item at the specified equipment slot's slot index. It is a new
	 * instance of the underlying inventory's slot item.
	 * 
	 * @param slot the inventory index
	 * @return the new item instance
	 */
	public ItemStack getItem(EquipmentSlot slot) {
		return this.getInventory().getItem(slot.getSlotIndex());
	}

	/**
	 * Equips an item to the specified item slot.
	 * 
	 * @param slot  the slot to equip to
	 * @param stack the item to equip
	 */
	public void equipItem(EquipmentSlot slot, ItemStack stack) {
		// TODO actual equipping logic

		this.getInventory().setItem(slot.getSlotIndex(), stack);
		this.player.requestAppearanceUpdate();
	}

	/**
	 * Returns the underlying ItemInventory instance.
	 * 
	 * @return the instance
	 */
	public ItemInventory getInventory() {
		return this.equipmentInventory;
	}

}
