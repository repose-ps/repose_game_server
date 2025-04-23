package com.repose.game.item;

/**
 * The Item class represents an in-game item as seen on a user interface. It
 * contains the value for the item's ID and it's amount.
 * 
 * @author Robert Guidry
 */
public class ItemStack {

	/**
	 * The cache index (ID) of the item in this stack.
	 */
	private int itemId;

	/**
	 * The amount of items in this stack.
	 */
	private int amount;

	/**
	 * Creates an ItemStack instance with an item ID of 0 and amount of 0.
	 */
	public ItemStack() {
		this(0, 0);
	}

	/**
	 * Creates a new ItemStack instance with the specified item ID and amount.
	 * 
	 * @param itemId the specified item ID (cache index)
	 * @param amount the amount of items
	 */
	public ItemStack(int itemId, int amount) {
		this.setStack(itemId, amount);
	}

	/**
	 * Returns a new ItemStack instance with this stack's item ID and amount values.
	 * 
	 * @return the new ItemStack instance
	 */
	public ItemStack getStack() {
		return new ItemStack(this.getItemId(), this.getAmount());
	}

	/**
	 * Sets the item ID and amount to the specified values.
	 * 
	 * @param itemId the item ID value
	 * @param amount the amount value
	 */
	public void setStack(int itemId, int amount) {
		this.setItemId(itemId);
		this.setAmount(amount);
	}

	/**
	 * Sets the item ID and amount of this stack to the values of the specified item
	 * stack.
	 * 
	 * @param stack the specified item stack
	 */
	public void setStack(ItemStack stack) {
		this.setStack(stack.getItemId(), stack.getAmount());
	}

	/**
	 * Returns the ID of this stack's item.
	 * 
	 * @return the ID
	 */
	public int getItemId() {
		return this.itemId;
	}

	/**
	 * Returns the amount of items in this stack.
	 * 
	 * @return the amount
	 */
	public int getAmount() {
		return this.amount;
	}

	/**
	 * Sets the ID of this stack's item.
	 * 
	 * @param itemId the ID
	 */
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	/**
	 * Sets the amount of items in this stack.
	 * 
	 * @param amount the amount
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

}
