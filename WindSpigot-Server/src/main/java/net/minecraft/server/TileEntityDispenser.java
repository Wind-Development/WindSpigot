package net.minecraft.server;

// CraftBukkit start
import java.util.List;
import java.util.Random;

import ga.windpvp.windspigot.random.FastRandom;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
// CraftBukkit end

public class TileEntityDispenser extends TileEntityContainer implements IInventory {

	private static final Random f = new FastRandom();
	private ItemStack[] items = new ItemStack[9];
	protected String a;

	// CraftBukkit start - add fields and methods
	public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
	private int maxStack = MAX_STACK;

	@Override
	public ItemStack[] getContents() {
		return this.items;
	}

	@Override
	public void onOpen(CraftHumanEntity who) {
		transaction.add(who);
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
	public void setMaxStackSize(int size) {
		maxStack = size;
	}
	// CraftBukkit end

	public TileEntityDispenser() {
	}

	@Override
	public int getSize() {
		return 9;
	}

	@Override
	public ItemStack getItem(int i) {
		return this.items[i];
	}

	@Override
	public ItemStack splitStack(int i, int j) {
		if (this.items[i] != null) {
			ItemStack itemstack;

			if (this.items[i].count <= j) {
				itemstack = this.items[i];
				this.items[i] = null;
				this.update();
				return itemstack;
			} else {
				itemstack = this.items[i].cloneAndSubtract(j);
				if (this.items[i].count == 0) {
					this.items[i] = null;
				}

				this.update();
				return itemstack;
			}
		} else {
			return null;
		}
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

	public int m() {
		int i = -1;
		int j = 1;

		for (int k = 0; k < this.items.length; ++k) {
			if (this.items[k] != null && TileEntityDispenser.f.nextInt(j++) == 0) {
				if (this.items[k].count == 0) {
					continue; // CraftBukkit
				}
				i = k;
			}
		}

		return i;
	}

	@Override
	public void setItem(int i, ItemStack itemstack) {
		this.items[i] = itemstack;
		if (itemstack != null && itemstack.count > this.getMaxStackSize()) {
			itemstack.count = this.getMaxStackSize();
		}

		this.update();
	}

	public int addItem(ItemStack itemstack) {
		for (int i = 0; i < this.items.length; ++i) {
			if (this.items[i] == null || this.items[i].getItem() == null) {
				this.setItem(i, itemstack);
				return i;
			}
		}

		return -1;
	}

	@Override
	public String getName() {
		return this.hasCustomName() ? this.a : "container.dispenser";
	}

	public void a(String s) {
		this.a = s;
	}

	@Override
	public boolean hasCustomName() {
		return this.a != null;
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);

		this.items = new ItemStack[this.getSize()];

		for (int i = 0; i < nbttaglist.size(); ++i) {
			NBTTagCompound nbttagcompound1 = nbttaglist.get(i);
			int j = nbttagcompound1.getByte("Slot") & 255;

			if (j >= 0 && j < this.items.length) {
				this.items[j] = ItemStack.createStack(nbttagcompound1);
			}
		}

		if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
			this.a = nbttagcompound.getString("CustomName");
		}

	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.items.length; ++i) {
			if (this.items[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();

				nbttagcompound1.setByte("Slot", (byte) i);
				this.items[i].save(nbttagcompound1);
				nbttaglist.add(nbttagcompound1);
			}
		}

		nbttagcompound.set("Items", nbttaglist);
		if (this.hasCustomName()) {
			nbttagcompound.setString("CustomName", this.a);
		}

	}

	@Override
	public int getMaxStackSize() {
		return maxStack; // CraftBukkit
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		return this.world.getTileEntity(this.position) != this ? false
				: entityhuman.e(this.position.getX() + 0.5D, this.position.getY() + 0.5D,
						this.position.getZ() + 0.5D) <= 64.0D;
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
	public String getContainerName() {
		return "minecraft:dispenser";
	}

	@Override
	public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
		return new ContainerDispenser(playerinventory, this);
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
