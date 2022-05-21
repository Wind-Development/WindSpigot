package net.minecraft.server;

import java.util.Random;

import org.bukkit.event.block.BlockRedstoneEvent; // CraftBukkit

import ga.windpvp.windspigot.cache.Constants;

public class BlockRedstoneTorch extends BlockTorch {

	private final boolean isOn;

	private boolean a(World world, BlockPosition blockposition, boolean flag) {
		java.util.ArrayDeque<BlockRedstoneTorch.RedstoneUpdateInfo> list = world.redstoneUpdateInfos;
		if (list == null) {
			list = world.redstoneUpdateInfos = new java.util.ArrayDeque<>();
		}

		if (flag) {
			list.add(new BlockRedstoneTorch.RedstoneUpdateInfo(blockposition, world.getTime()));
		}

		int i = 0;

		for (java.util.Iterator<BlockRedstoneTorch.RedstoneUpdateInfo> iterator = list.iterator(); iterator
				.hasNext();) {
			BlockRedstoneTorch.RedstoneUpdateInfo blockredstonetorch_redstoneupdateinfo = iterator.next();

			if (blockredstonetorch_redstoneupdateinfo.a.equals(blockposition)) {
				++i;
				if (i >= 8) {
					return true;
				}
			}
		}

		return false;
	}

	protected BlockRedstoneTorch(boolean flag) {
		this.isOn = flag;
		this.a(true);
		this.a((CreativeModeTab) null);
	}

	@Override
	public int a(World world) {
		return 2;
	}

	@Override
	public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
		if (this.isOn) {
			// PaperSpigot start - Fix cannons
			if (world.paperSpigotConfig.fixCannons) {
				world.applyPhysics(blockposition.shift(EnumDirection.DOWN), this);
				world.applyPhysics(blockposition.shift(EnumDirection.UP), this);
				world.applyPhysics(blockposition.shift(EnumDirection.WEST), this);
				world.applyPhysics(blockposition.shift(EnumDirection.EAST), this);
				world.applyPhysics(blockposition.shift(EnumDirection.SOUTH), this);
				world.applyPhysics(blockposition.shift(EnumDirection.NORTH), this);
				return;
			}
			// PaperSpigot end
			EnumDirection[] aenumdirection = EnumDirection.values();
			int i = aenumdirection.length;

			for (int j = 0; j < i; ++j) {
				EnumDirection enumdirection = aenumdirection[j];

				world.applyPhysics(blockposition.shift(enumdirection), this);
			}
		}

	}

	@Override
	public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
		if (this.isOn) {
			// PaperSpigot start - Fix cannons
			if (world.paperSpigotConfig.fixCannons) {
				world.applyPhysics(blockposition.shift(EnumDirection.DOWN), this);
				world.applyPhysics(blockposition.shift(EnumDirection.UP), this);
				world.applyPhysics(blockposition.shift(EnumDirection.WEST), this);
				world.applyPhysics(blockposition.shift(EnumDirection.EAST), this);
				world.applyPhysics(blockposition.shift(EnumDirection.SOUTH), this);
				world.applyPhysics(blockposition.shift(EnumDirection.NORTH), this);
				return;
			}
			// PaperSpigot end
			EnumDirection[] aenumdirection = EnumDirection.values();
			int i = aenumdirection.length;

			for (int j = 0; j < i; ++j) {
				EnumDirection enumdirection = aenumdirection[j];

				world.applyPhysics(blockposition.shift(enumdirection), this);
			}
		}

	}

	@Override
	public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata,
			EnumDirection enumdirection) {
		return this.isOn && iblockdata.get(BlockTorch.FACING) != enumdirection ? 15 : 0;
	}

	private boolean g(World world, BlockPosition blockposition, IBlockData iblockdata) {
		EnumDirection enumdirection = iblockdata.get(BlockTorch.FACING).opposite();

		return world.isBlockFacePowered(blockposition.shift(enumdirection), enumdirection);
	}

	@Override
	public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
	}

	@Override
	public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
		boolean flag = this.g(world, blockposition, iblockdata);
		// Paper start
		java.util.ArrayDeque<BlockRedstoneTorch.RedstoneUpdateInfo> redstoneUpdateInfos = world.redstoneUpdateInfos;
		if (redstoneUpdateInfos != null) {
			BlockRedstoneTorch.RedstoneUpdateInfo curr;
			while ((curr = redstoneUpdateInfos.peek()) != null && world.getTime() - curr.getTime() > 60L) {
				redstoneUpdateInfos.poll();
			}
		}
		// Paper end

		// CraftBukkit start
		org.bukkit.plugin.PluginManager manager = world.getServer().getPluginManager();
		org.bukkit.block.Block block = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(),
				blockposition.getZ());
		int oldCurrent = this.isOn ? 15 : 0;

		BlockRedstoneEvent event = new BlockRedstoneEvent(block, oldCurrent, oldCurrent);
		// CraftBukkit end

		if (this.isOn) {
			if (flag) {
				// CraftBukkit start
				if (oldCurrent != 0) {
					event.setNewCurrent(0);
					manager.callEvent(event);
					if (event.getNewCurrent() != 0) {
						return;
					}
				}
				// CraftBukkit end
				world.setTypeAndData(blockposition, Blocks.UNLIT_REDSTONE_TORCH.getBlockData().set(BlockTorch.FACING,
						iblockdata.get(BlockTorch.FACING)), 3);
				if (this.a(world, blockposition, true)) {
					world.makeSound(blockposition.getX() + 0.5F, blockposition.getY() + 0.5F,
							blockposition.getZ() + 0.5F, "random.fizz", 0.5F,
							2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

					for (int i = 0; i < 5; ++i) {
						double d0 = blockposition.getX() + random.nextDouble() * 0.6D + 0.2D;
						double d1 = blockposition.getY() + random.nextDouble() * 0.6D + 0.2D;
						double d2 = blockposition.getZ() + random.nextDouble() * 0.6D + 0.2D;

						world.addParticle(EnumParticle.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D,
								Constants.EMPTY_ARRAY);
					}

					world.a(blockposition, world.getType(blockposition).getBlock(), 160);
				}
			}
		} else if (!flag && !this.a(world, blockposition, false)) {
			// CraftBukkit start
			if (oldCurrent != 15) {
				event.setNewCurrent(15);
				manager.callEvent(event);
				if (event.getNewCurrent() != 15) {
					return;
				}
			}
			// CraftBukkit end
			world.setTypeAndData(blockposition,
					Blocks.REDSTONE_TORCH.getBlockData().set(BlockTorch.FACING, iblockdata.get(BlockTorch.FACING)), 3);
		}

	}

	@Override
	public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
		if (!this.e(world, blockposition, iblockdata)) {
			if (this.isOn == this.g(world, blockposition, iblockdata)) {
				world.a(blockposition, this, this.a(world));
			}

		}
	}

	@Override
	public int b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata,
			EnumDirection enumdirection) {
		return enumdirection == EnumDirection.DOWN ? this.a(iblockaccess, blockposition, iblockdata, enumdirection) : 0;
	}

	@Override
	public Item getDropType(IBlockData iblockdata, Random random, int i) {
		return Item.getItemOf(Blocks.REDSTONE_TORCH);
	}

	@Override
	public boolean isPowerSource() {
		return true;
	}

	@Override
	public boolean b(Block block) {
		return block == Blocks.UNLIT_REDSTONE_TORCH || block == Blocks.REDSTONE_TORCH;
	}

	static class RedstoneUpdateInfo {

		BlockPosition a;
		long b;

		final long getTime() {
			return this.b;
		} // Paper - OBFHELPER

		public RedstoneUpdateInfo(BlockPosition blockposition, long i) {
			this.a = blockposition;
			this.b = i;
		}
	}
}
