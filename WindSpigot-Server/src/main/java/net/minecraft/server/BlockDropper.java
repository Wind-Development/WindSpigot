package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
// CraftBukkit end

public class BlockDropper extends BlockDispenser {

	private final IDispenseBehavior P = new DispenseBehaviorItem();

	public BlockDropper() {
	}

	@Override
	protected IDispenseBehavior a(ItemStack itemstack) {
		return this.P;
	}

	@Override
	public TileEntity a(World world, int i) {
		return new TileEntityDropper();
	}

	@Override
	public void dispense(World world, BlockPosition blockposition) {
		SourceBlock sourceblock = new SourceBlock(world, blockposition);
		TileEntityDispenser tileentitydispenser = (TileEntityDispenser) sourceblock.getTileEntity();

		if (tileentitydispenser != null) {
			int i = tileentitydispenser.m();

			if (i < 0) {
				world.triggerEffect(1001, blockposition, 0);
			} else {
				ItemStack itemstack = tileentitydispenser.getItem(i);

				if (itemstack != null) {
					EnumDirection enumdirection = world.getType(blockposition).get(BlockDispenser.FACING);
					BlockPosition blockposition1 = blockposition.shift(enumdirection);
					IInventory iinventory = TileEntityHopper.b(world, blockposition1.getX(), blockposition1.getY(),
							blockposition1.getZ());
					ItemStack itemstack1;

					if (iinventory == null) {
						itemstack1 = this.P.a(sourceblock, itemstack);
						if (itemstack1 != null && itemstack1.count <= 0) {
							itemstack1 = null;
						}
					} else {
						// CraftBukkit start - Fire event when pushing items into other inventories
						CraftItemStack oitemstack = CraftItemStack
								.asCraftMirror(itemstack.cloneItemStack().cloneAndSubtract(1));

						org.bukkit.inventory.Inventory destinationInventory;
						// Have to special case large chests as they work oddly
						if (iinventory instanceof InventoryLargeChest) {
							destinationInventory = new org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest(
									(InventoryLargeChest) iinventory);
						} else {
							destinationInventory = iinventory.getOwner().getInventory();
						}

						InventoryMoveItemEvent event = new InventoryMoveItemEvent(
								tileentitydispenser.getOwner().getInventory(), oitemstack.clone(), destinationInventory,
								true);
						world.getServer().getPluginManager().callEvent(event);
						if (event.isCancelled()) {
							return;
						}
						itemstack1 = TileEntityHopper.addItem(iinventory, CraftItemStack.asNMSCopy(event.getItem()),
								enumdirection.opposite());
						if (event.getItem().equals(oitemstack) && itemstack1 == null) {
							// CraftBukkit end
							itemstack1 = itemstack.cloneItemStack();
							if (--itemstack1.count <= 0) {
								itemstack1 = null;
							}
						} else {
							itemstack1 = itemstack.cloneItemStack();
						}
					}

					tileentitydispenser.setItem(i, itemstack1);
				}
			}
		}
	}
}
