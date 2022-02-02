package net.minecraft.server;

// CraftBukkit start
import java.util.List;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
// CraftBukkit end

public class InventoryLargeChest implements ITileInventory {

	private String a;
	public ITileInventory left;
	public ITileInventory right;

	// CraftBukkit start - add fields and methods
	public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();

	@Override
	public ItemStack[] getContents() {
		ItemStack[] result = new ItemStack[this.getSize()];
		for (int i = 0; i < result.length; i++) {
			result[i] = this.getItem(i);
		}
		return result;
	}

	@Override
	public void onOpen(CraftHumanEntity who) {
		this.left.onOpen(who);
		this.right.onOpen(who);
		transaction.add(who);
	}

	@Override
	public void onClose(CraftHumanEntity who) {
		this.left.onClose(who);
		this.right.onClose(who);
		transaction.remove(who);
	}

	@Override
	public List<HumanEntity> getViewers() {
		return transaction;
	}

	@Override
	public org.bukkit.inventory.InventoryHolder getOwner() {
		return null; // This method won't be called since CraftInventoryDoubleChest doesn't defer to
						// here
	}

	@Override
	public void setMaxStackSize(int size) {
		this.left.setMaxStackSize(size);
		this.right.setMaxStackSize(size);
	}
	// CraftBukkit end

	public InventoryLargeChest(String s, ITileInventory itileinventory, ITileInventory itileinventory1) {
		this.a = s;
		if (itileinventory == null) {
			itileinventory = itileinventory1;
		}

		if (itileinventory1 == null) {
			itileinventory1 = itileinventory;
		}

		this.left = itileinventory;
		this.right = itileinventory1;
		if (itileinventory.r_()) {
			itileinventory1.a(itileinventory.i());
		} else if (itileinventory1.r_()) {
			itileinventory.a(itileinventory1.i());
		}

	}

	@Override
	public int getSize() {
		return this.left.getSize() + this.right.getSize();
	}

	public boolean a(IInventory iinventory) {
		return this.left == iinventory || this.right == iinventory;
	}

	@Override
	public String getName() {
		return this.left.hasCustomName() ? this.left.getName()
				: (this.right.hasCustomName() ? this.right.getName() : this.a);
	}

	@Override
	public boolean hasCustomName() {
		return this.left.hasCustomName() || this.right.hasCustomName();
	}

	@Override
	public IChatBaseComponent getScoreboardDisplayName() {
		return this.hasCustomName() ? new ChatComponentText(this.getName())
				: new ChatMessage(this.getName(), new Object[0]);
	}

	@Override
	public ItemStack getItem(int i) {
		return i >= this.left.getSize() ? this.right.getItem(i - this.left.getSize()) : this.left.getItem(i);
	}

	@Override
	public ItemStack splitStack(int i, int j) {
		return i >= this.left.getSize() ? this.right.splitStack(i - this.left.getSize(), j)
				: this.left.splitStack(i, j);
	}

	@Override
	public ItemStack splitWithoutUpdate(int i) {
		return i >= this.left.getSize() ? this.right.splitWithoutUpdate(i - this.left.getSize())
				: this.left.splitWithoutUpdate(i);
	}

	@Override
	public void setItem(int i, ItemStack itemstack) {
		if (i >= this.left.getSize()) {
			this.right.setItem(i - this.left.getSize(), itemstack);
		} else {
			this.left.setItem(i, itemstack);
		}

	}

	@Override
	public int getMaxStackSize() {
		return Math.min(this.left.getMaxStackSize(), this.right.getMaxStackSize()); // CraftBukkit - check both sides
	}

	@Override
	public void update() {
		this.left.update();
		this.right.update();
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		return this.left.a(entityhuman) && this.right.a(entityhuman);
	}

	@Override
	public void startOpen(EntityHuman entityhuman) {
		this.left.startOpen(entityhuman);
		this.right.startOpen(entityhuman);
	}

	@Override
	public void closeContainer(EntityHuman entityhuman) {
		this.left.closeContainer(entityhuman);
		this.right.closeContainer(entityhuman);
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
	public boolean r_() {
		return this.left.r_() || this.right.r_();
	}

	@Override
	public void a(ChestLock chestlock) {
		this.left.a(chestlock);
		this.right.a(chestlock);
	}

	@Override
	public ChestLock i() {
		return this.left.i();
	}

	@Override
	public String getContainerName() {
		return this.left.getContainerName();
	}

	@Override
	public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
		return new ContainerChest(playerinventory, this, entityhuman);
	}

	@Override
	public void l() {
		this.left.l();
		this.right.l();
	}
}
