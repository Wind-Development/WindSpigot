package net.minecraft.server;

// CraftBukkit start
import java.util.List;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.entity.CraftVillager;
import org.bukkit.entity.HumanEntity;
// CraftBukkit end

public class InventoryMerchant implements IInventory {

	private final IMerchant merchant;
	private ItemStack[] itemsInSlots = new ItemStack[3];
	private final EntityHuman player;
	private MerchantRecipe recipe;
	private int e;

	// CraftBukkit start - add fields and methods
	public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
	private int maxStack = MAX_STACK;

	@Override
	public ItemStack[] getContents() {
		return this.itemsInSlots;
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
	public void setMaxStackSize(int i) {
		maxStack = i;
	}

	@Override
	public org.bukkit.inventory.InventoryHolder getOwner() {
		return (CraftVillager) ((EntityVillager) this.merchant).getBukkitEntity();
	}
	// CraftBukkit end

	public InventoryMerchant(EntityHuman entityhuman, IMerchant imerchant) {
		this.player = entityhuman;
		this.merchant = imerchant;
	}

	@Override
	public int getSize() {
		return this.itemsInSlots.length;
	}

	@Override
	public ItemStack getItem(int i) {
		return this.itemsInSlots[i];
	}

	@Override
	public ItemStack splitStack(int i, int j) {
		if (this.itemsInSlots[i] != null) {
			ItemStack itemstack;

			if (i == 2) {
				itemstack = this.itemsInSlots[i];
				this.itemsInSlots[i] = null;
				return itemstack;
			} else if (this.itemsInSlots[i].count <= j) {
				itemstack = this.itemsInSlots[i];
				this.itemsInSlots[i] = null;
				if (this.e(i)) {
					this.h();
				}

				return itemstack;
			} else {
				itemstack = this.itemsInSlots[i].cloneAndSubtract(j);
				if (this.itemsInSlots[i].count == 0) {
					this.itemsInSlots[i] = null;
				}

				if (this.e(i)) {
					this.h();
				}

				return itemstack;
			}
		} else {
			return null;
		}
	}

	private boolean e(int i) {
		return i == 0 || i == 1;
	}

	@Override
	public ItemStack splitWithoutUpdate(int i) {
		if (this.itemsInSlots[i] != null) {
			ItemStack itemstack = this.itemsInSlots[i];

			this.itemsInSlots[i] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	@Override
	public void setItem(int i, ItemStack itemstack) {
		this.itemsInSlots[i] = itemstack;
		if (itemstack != null && itemstack.count > this.getMaxStackSize()) {
			itemstack.count = this.getMaxStackSize();
		}

		if (this.e(i)) {
			this.h();
		}

	}

	@Override
	public String getName() {
		return "mob.villager";
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
	public int getMaxStackSize() {
		return maxStack; // CraftBukkit
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		return this.merchant.v_() == entityhuman;
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
	public void update() {
		this.h();
	}

	public void h() {
		this.recipe = null;
		ItemStack itemstack = this.itemsInSlots[0];
		ItemStack itemstack1 = this.itemsInSlots[1];

		if (itemstack == null) {
			itemstack = itemstack1;
			itemstack1 = null;
		}

		if (itemstack == null) {
			this.setItem(2, null);
		} else {
			MerchantRecipeList merchantrecipelist = this.merchant.getOffers(this.player);

			if (merchantrecipelist != null) {
				MerchantRecipe merchantrecipe = merchantrecipelist.a(itemstack, itemstack1, this.e);

				if (merchantrecipe != null && !merchantrecipe.h()) {
					this.recipe = merchantrecipe;
					this.setItem(2, merchantrecipe.getBuyItem3().cloneItemStack());
				} else if (itemstack1 != null) {
					merchantrecipe = merchantrecipelist.a(itemstack1, itemstack, this.e);
					if (merchantrecipe != null && !merchantrecipe.h()) {
						this.recipe = merchantrecipe;
						this.setItem(2, merchantrecipe.getBuyItem3().cloneItemStack());
					} else {
						this.setItem(2, null);
					}
				} else {
					this.setItem(2, null);
				}
			}
		}

		this.merchant.a_(this.getItem(2));
	}

	public MerchantRecipe getRecipe() {
		return this.recipe;
	}

	public void d(int i) {
		this.e = i;
		this.h();
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
		for (int i = 0; i < this.itemsInSlots.length; ++i) {
			this.itemsInSlots[i] = null;
		}

	}
}
