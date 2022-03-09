package net.minecraft.server;

import java.util.Random;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class BlockCrops extends BlockPlant implements IBlockFragilePlantElement {

	public static final BlockStateInteger AGE = BlockStateInteger.of("age", 0, 7);

	protected BlockCrops() {
		this.j(this.blockStateList.getBlockData().set(BlockCrops.AGE, Integer.valueOf(0)));
		this.a(true);
		float f = 0.5F;

		this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
		this.a((CreativeModeTab) null);
		this.c(0.0F);
		this.a(Block.h);
		this.K();
	}

	@Override
	protected boolean c(Block block) {
		return block == Blocks.FARMLAND;
	}

	@Override
	public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
		super.b(world, blockposition, iblockdata, random);
		if (world.isLightLevel(blockposition.up(), 9)) { // Paper
			int i = iblockdata.get(BlockCrops.AGE).intValue();

			if (i < 7) {
				float f = a(this, world, blockposition);

				if (random
						.nextInt((int) (world.growthOdds / world.spigotConfig.wheatModifier * (25.0F / f)) + 1) == 0) { // Spigot
																														// //
																														// CraftBukkit
																														// start
					IBlockData data = iblockdata.set(AGE, Integer.valueOf(i + 1));
					CraftEventFactory.handleBlockGrowEvent(world, blockposition.getX(), blockposition.getY(),
							blockposition.getZ(), this, toLegacyData(data));
					// CraftBukkit end
				}
			}
		}

	}

	public void g(World world, BlockPosition blockposition, IBlockData iblockdata) {
		int i = iblockdata.get(BlockCrops.AGE).intValue() + MathHelper.nextInt(world.random, 2, 5);

		if (i > 7) {
			i = 7;
		}

		// CraftBukkit start
		IBlockData data = iblockdata.set(AGE, Integer.valueOf(i));
		CraftEventFactory.handleBlockGrowEvent(world, blockposition.getX(), blockposition.getY(), blockposition.getZ(),
				this, toLegacyData(data));
		// CraftBukkit end
	}

	protected static float a(Block block, World world, BlockPosition blockposition) {
		float f = 1.0F;
		BlockPosition blockposition1 = blockposition.down();

		for (int i = -1; i <= 1; ++i) {
			for (int j = -1; j <= 1; ++j) {
				float f1 = 0.0F;
				IBlockData iblockdata = world.getType(blockposition1.a(i, 0, j));

				if (iblockdata.getBlock() == Blocks.FARMLAND) {
					f1 = 1.0F;
					if (iblockdata.get(BlockSoil.MOISTURE).intValue() > 0) {
						f1 = 3.0F;
					}
				}

				if (i != 0 || j != 0) {
					f1 /= 4.0F;
				}

				f += f1;
			}
		}

		BlockPosition blockposition2 = blockposition.north();
		BlockPosition blockposition3 = blockposition.south();
		BlockPosition blockposition4 = blockposition.west();
		BlockPosition blockposition5 = blockposition.east();
		boolean flag = block == world.getType(blockposition4).getBlock()
				|| block == world.getType(blockposition5).getBlock();
		boolean flag1 = block == world.getType(blockposition2).getBlock()
				|| block == world.getType(blockposition3).getBlock();

		if (flag && flag1) {
			f /= 2.0F;
		} else {
			boolean flag2 = block == world.getType(blockposition4.north()).getBlock()
					|| block == world.getType(blockposition5.north()).getBlock()
					|| block == world.getType(blockposition5.south()).getBlock()
					|| block == world.getType(blockposition4.south()).getBlock();

			if (flag2) {
				f /= 2.0F;
			}
		}

		return f;
	}

	@Override
	public boolean f(World world, BlockPosition blockposition, IBlockData iblockdata) {
		return (world.k(blockposition) >= 8 || world.i(blockposition))
				&& this.c(world.getType(blockposition.down()).getBlock());
	}

	protected Item l() {
		return Items.WHEAT_SEEDS;
	}

	protected Item n() {
		return Items.WHEAT;
	}

	@Override
	public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
		super.dropNaturally(world, blockposition, iblockdata, f, 0);
		if (!world.isClientSide) {
			int j = iblockdata.get(BlockCrops.AGE).intValue();

			if (j >= 7) {
				int k = 3 + i;

				for (int l = 0; l < k; ++l) {
					if (world.random.nextInt(15) <= j) {
						a(world, blockposition, new ItemStack(this.l(), 1, 0));
					}
				}
			}

		}
	}

	@Override
	public Item getDropType(IBlockData iblockdata, Random random, int i) {
		return iblockdata.get(BlockCrops.AGE).intValue() == 7 ? this.n() : this.l();
	}

	@Override
	public boolean a(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
		return iblockdata.get(BlockCrops.AGE).intValue() < 7;
	}

	@Override
	public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
		return true;
	}

	@Override
	public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
		this.g(world, blockposition, iblockdata);
	}

	@Override
	public IBlockData fromLegacyData(int i) {
		return this.getBlockData().set(BlockCrops.AGE, Integer.valueOf(i));
	}

	@Override
	public int toLegacyData(IBlockData iblockdata) {
		return iblockdata.get(BlockCrops.AGE).intValue();
	}

	@Override
	protected BlockStateList getStateList() {
		return new BlockStateList(this, new IBlockState[] { BlockCrops.AGE });
	}
}
