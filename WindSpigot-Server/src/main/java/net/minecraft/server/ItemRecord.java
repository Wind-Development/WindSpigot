package net.minecraft.server;

import java.util.Map;

import com.google.common.collect.Maps;

public class ItemRecord extends Item {

	private static final Map<String, ItemRecord> b = Maps.newHashMap();
	public final String a;

	protected ItemRecord(String s) {
		this.a = s;
		this.maxStackSize = 1;
		this.a(CreativeModeTab.f);
		ItemRecord.b.put("records." + s, this);
	}

	@Override
	public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, BlockPosition blockposition,
			EnumDirection enumdirection, float f, float f1, float f2) {
		IBlockData iblockdata = world.getType(blockposition);

		if (iblockdata.getBlock() == Blocks.JUKEBOX && !iblockdata.get(BlockJukeBox.HAS_RECORD).booleanValue()) {
			if (world.isClientSide) {
				return true;
			} else {
				// CraftBukkit Start
				/*
				 * ((BlockJukeBox) Blocks.JUKEBOX).a(world, blockposition, iblockdata,
				 * itemstack); world.a(null, 1005, blockposition,
				 * Item.getId(this)); --itemstack.count; entityhuman.b(StatisticList.X);
				 */
				// CraftBukkit End
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public EnumItemRarity g(ItemStack itemstack) {
		return EnumItemRarity.RARE;
	}
}
