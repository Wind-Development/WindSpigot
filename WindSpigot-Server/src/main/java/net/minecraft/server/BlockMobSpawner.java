package net.minecraft.server;

import java.util.Random;

public class BlockMobSpawner extends BlockContainer {

	protected BlockMobSpawner() {
		super(Material.STONE);
	}

	@Override
	public TileEntity a(World world, int i) {
		return new TileEntityMobSpawner();
	}

	@Override
	public Item getDropType(IBlockData iblockdata, Random random, int i) {
		return null;
	}

	@Override
	public int a(Random random) {
		return 0;
	}

	@Override
	public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
		super.dropNaturally(world, blockposition, iblockdata, f, i);
		/*
		 * CraftBukkit start - Delegate to getExpDrop int j = 15 +
		 * world.random.nextInt(15) + world.random.nextInt(15);
		 * 
		 * this.dropExperience(world, blockposition, j);
		 */
	}

	@Override
	public int getExpDrop(World world, IBlockData iblockdata, int enchantmentLevel) {
		int j = 15 + world.random.nextInt(15) + world.random.nextInt(15);

		return j;
		// CraftBukkit end
	}

	@Override
	public boolean c() {
		return false;
	}

	@Override
	public int b() {
		return 3;
	}
}
