package net.minecraft.server;

// CraftBukkit start
import java.util.List;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
// CraftBukkit end

public class InventoryCrafting implements IInventory {

	private final ItemStack[] items;
	private final int b;
	private final int c;
	private final Container d;

	// CraftBukkit start - add fields
	public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
	public IRecipe currentRecipe;
	public IInventory resultInventory;
	private EntityHuman owner;

	@Override
	public ItemStack[] getContents() {
		return this.items;
	}

	@Override
	public void onOpen(CraftHumanEntity who) {
		transaction.add(who);
	}

	public InventoryType getInvType() {
		return items.length == 4 ? InventoryType.CRAFTING : InventoryType.WORKBENCH;
	}

	@Override
	public void onClose(CraftHumanEntity who) {
		transaction.remove(who);
	}

	@Override
	public List<HumanEntity> getViewers() {
		return transaction;
	}

	@Override
	public org.bukkit.inventory.InventoryHolder getOwner() {
		return (owner == null) ? null : owner.getBukkitEntity();
	}

	@Override
	public void setMaxStackSize(int size) {
		int maxStack = size;
		resultInventory.setMaxStackSize(size);
	}

	public InventoryCrafting(Container container, int i, int j, EntityHuman player) {
		this(container, i, j);
		this.owner = player;
	}
	// CraftBukkit end

	public InventoryCrafting(Container container, int i, int j) {
		int k = i * j;

		this.items = new ItemStack[k];
		this.d = container;
		this.b = i;
		this.c = j;
	}

	@Override
	public int getSize() {
		return this.items.length;
	}

	@Override
	public ItemStack getItem(int i) {
		return i >= this.getSize() ? null : this.items[i];
	}

	public ItemStack c(int i, int j) {
		return i >= 0 && i < this.b && j >= 0 && j <= this.c ? this.getItem(i + j * this.b) : null;
	}

	@Override
	public String getName() {
		return "container.crafting";
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
	public ItemStack splitWithoutUpdate(int i) {
		if (this.items[i] != null) {
			ItemStack itemstack = this.items[i];

			this.items[i] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	@Override
	public ItemStack splitStack(int i, int j) {
		if (this.items[i] != null) {
			ItemStack itemstack;

			if (this.items[i].count <= j) {
				itemstack = this.items[i];
				this.items[i] = null;
				this.d.a(this);
				return itemstack;
			} else {
				itemstack = this.items[i].cloneAndSubtract(j);
				if (this.items[i].count == 0) {
					this.items[i] = null;
				}

				this.d.a(this);
				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public void setItem(int i, ItemStack itemstack) {
		this.items[i] = itemstack;
		this.d.a(this);
	}

	@Override
	public int getMaxStackSize() {
		return 64;
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

	public int h() {
		return this.c;
	}

	public int i() {
		return this.b;
	}
}
