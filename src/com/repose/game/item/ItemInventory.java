package com.repose.game.item;

/**
 * An ItemInventory represents a collection of ItemStack instances. It also
 * provides utility for manipulating the ItemStack collection.
 * 
 * @author Robert Guidry
 */
public class ItemInventory {

	/**
	 * The collection of item stacks in this inventory.
	 */
	private final ItemStack[] inventory;

	/**
	 * Creates a new ItemInventory instance with the specified size representing the
	 * size of the backing array of this inventory.
	 * 
	 * @param size the specified size
	 */
	public ItemInventory(int size) {
		// create and fill inventory array
		this.inventory = new ItemStack[size];
		for (int i = 0; i < size; i++) {
			this.inventory[i] = new ItemStack();
		}
	}

	/**
	 * Returns the size of the backing ItemStack array.
	 * 
	 * @return the size
	 */
	public int getSize() {
		return this.inventory.length;
	}

	/**
	 * Returns a new instance of this inventory's ItemStack instance.
	 * 
	 * @param slot the array index of the inventory's ItemStack instance
	 * @return a new instance of the inventory's ItemStack instance
	 */
	public ItemStack getItem(int slot) {
		return this.inventory[slot].getStack();
	}

	/**
	 * Sets the item stack of the specified slot.
	 * 
	 * @param slot  the inventory slot
	 * @param stack the item stack to put in the slot
	 */
	public void setItem(int slot, ItemStack stack) {
		this.inventory[slot].setStack(stack);
	}

}
