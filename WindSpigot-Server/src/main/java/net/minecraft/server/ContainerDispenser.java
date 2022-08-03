package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
// CraftBukkit end

public class ContainerDispenser extends Container {

	public IInventory items;
	// CraftBukkit start
	private CraftInventoryView bukkitEntity = null;
	private PlayerInventory player;
	// CraftBukkit end

	public ContainerDispenser(IInventory iinventory, IInventory iinventory1) {
		this.items = iinventory1;
		// CraftBukkit start - Save player
		// TODO: Should we check to make sure it really is an InventoryPlayer?
		this.player = (PlayerInventory) iinventory;
		// CraftBukkit end

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				this.a(new Slot(iinventory1, j + i * 3, 62 + j * 18, 17 + i * 18));
			}

			for (int j = 0; j < 9; ++j) {
				this.a(new Slot(iinventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
				this.a(new Slot(iinventory, j, 8 + j * 18, 142));
			}
		}
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		if (!this.checkReachable) {
			return true; // CraftBukkit
		}
		return this.items.a(entityhuman);
	}

	@Override
	public ItemStack b(EntityHuman entityhuman, int i) {
		ItemStack itemstack = null;
		Slot slot = this.c.get(i);

		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();

			itemstack = itemstack1.cloneItemStack();
			if (i < 9) {
				if (!this.a(itemstack1, 9, 45, true)) {
					return null;
				}
			} else if (!this.a(itemstack1, 0, 9, false)) {
				return null;
			}

			if (itemstack1.count == 0) {
				slot.set(null);
			} else {
				slot.f();
			}

			if (itemstack1.count == itemstack.count) {
				return null;
			}

			slot.a(entityhuman, itemstack1);
		}

		return itemstack;
	}

	// CraftBukkit start
	@Override
	public CraftInventoryView getBukkitView() {
		if (bukkitEntity != null) {
			return bukkitEntity;
		}

		CraftInventory inventory = new CraftInventory(this.items);
		bukkitEntity = new CraftInventoryView(this.player.player.getBukkitEntity(), inventory, this);
		return bukkitEntity;
	}
	// CraftBukkit end
}
