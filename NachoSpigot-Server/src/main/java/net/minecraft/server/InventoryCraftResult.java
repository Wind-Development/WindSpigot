package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
// CraftBukkit end

public class InventoryCraftResult implements IInventory {

	private ItemStack[] items = new ItemStack[1];

	// CraftBukkit start
	private int maxStack = MAX_STACK;

	@Override
	public ItemStack[] getContents() {
		return this.items;
	}

	@Override
	public org.bukkit.inventory.InventoryHolder getOwner() {
		return null; // Result slots don't get an owner
	}

	// Don't need a transaction; the InventoryCrafting keeps track of it for us
	@Override
	public void onOpen(CraftHumanEntity who) {
	}

	@Override
	public void onClose(CraftHumanEntity who) {
	}

	@Override
	public java.util.List<HumanEntity> getViewers() {
		return new java.util.ArrayList<HumanEntity>();
	}

	@Override
	public void setMaxStackSize(int size) {
		maxStack = size;
	}
	// CraftBukkit end

	public InventoryCraftResult() {
	}

	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public ItemStack getItem(int i) {
		return this.items[0];
	}

	@Override
	public String getName() {
		return "Result";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public IChatBaseComponent getScoreboardDisplayName() {
		return this.hasCustomName() ? new ChatComponentText(this.getName())
				: new ChatMessage(this.getName(), new Object[0]);
	}

	@Override
	public ItemStack splitStack(int i, int j) {
		if (this.items[0] != null) {
			ItemStack itemstack = this.items[0];

			this.items[0] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	@Override
	public ItemStack splitWithoutUpdate(int i) {
		if (this.items[0] != null) {
			ItemStack itemstack = this.items[0];

			this.items[0] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	@Override
	public void setItem(int i, ItemStack itemstack) {
		this.items[0] = itemstack;
	}

	@Override
	public int getMaxStackSize() {
		return maxStack; // CraftBukkit
	}

	@Override
	public void update() {
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		return true;
	}

	@Override
	public void startOpen(EntityHuman entityhuman) {
	}

	@Override
	public void closeContainer(EntityHuman entityhuman) {
	}

	@Override
	public boolean b(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public int getProperty(int i) {
		return 0;
	}

	@Override
	public void b(int i, int j) {
	}

	@Override
	public int g() {
		return 0;
	}

	@Override
	public void l() {
		for (int i = 0; i < this.items.length; ++i) {
			this.items[i] = null;
		}

	}
}
