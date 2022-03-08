package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class BlockCactus extends Block {

	public static final BlockStateInteger AGE = BlockStateInteger.of("age", 0, 15);

	protected BlockCactus() {
		super(Material.CACTUS);
		this.j(this.blockStateList.getBlockData().set(BlockCactus.AGE, Integer.valueOf(0)));
		this.a(true);
		this.a(CreativeModeTab.c);
	}

	@Override
	public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
		BlockPosition blockposition1 = blockposition.up();

		if (world.isEmpty(blockposition1)) {
			int i;

			for (i = 1; world.getType(blockposition.down(i)).getBlock() == this; ++i) {
				;
			}

			if (i < world.paperSpigotConfig.cactusMaxHeight) { // PaperSpigot - Configurable max growth height for
																// cactus blocks) {
				int j = iblockdata.get(BlockCactus.AGE).intValue();

				if (j >= (byte) range(3, (world.growthOdds / world.spigotConfig.cactusModifier * 15) + 0.5F, 15)) { // Spigot
																													// world.setTypeUpdate(blockposition1,
																													// this.getBlockData());
																													// //
																													// CraftBukkit
					IBlockData iblockdata1 = iblockdata.set(BlockCactus.AGE, Integer.valueOf(0));

					CraftEventFactory.handleBlockGrowEvent(world, blockposition1.getX(), blockposition1.getY(),
							blockposition1.getZ(), this, 0); // CraftBukkit
					world.setTypeAndData(blockposition, iblockdata1, 4);
					this.doPhysics(world, blockposition1, iblockdata1, this);
				} else {
					world.setTypeAndData(blockposition, iblockdata.set(BlockCactus.AGE, Integer.valueOf(j + 1)), 4);
				}

			}
		}
	}

	@Override
	public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
		float f = 0.0625F;

		return new AxisAlignedBB(blockposition.getX() + f, blockposition.getY(), blockposition.getZ() + f,
				blockposition.getX() + 1 - f, blockposition.getY() + 1 - f, blockposition.getZ() + 1 - f);
	}

	@Override
	public boolean d() {
		return false;
	}

	@Override
	public boolean c() {
		return false;
	}

	@Override
	public boolean canPlace(World world, BlockPosition blockposition) {
		return super.canPlace(world, blockposition) ? this.e(world, blockposition) : false;
	}

	@Override
	public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
		if (!this.e(world, blockposition)) {
			world.setAir(blockposition, true);
		}

	}

	public boolean e(World world, BlockPosition blockposition) {
		Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

		while (iterator.hasNext()) {
			EnumDirection enumdirection = (EnumDirection) iterator.next();

			if (world.getType(blockposition.shift(enumdirection)).getBlock().getMaterial().isBuildable()) {
				return false;
			}
		}

		Block block = world.getType(blockposition.down()).getBlock();

		return block == Blocks.CACTUS || block == Blocks.SAND;
	}

	@Override
	public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {
		CraftEventFactory.blockDamage = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(),
				blockposition.getZ()); // CraftBukkit
		entity.damageEntity(DamageSource.CACTUS, 1.0F);
		CraftEventFactory.blockDamage = null; // CraftBukkit
	}

	@Override
	public IBlockData fromLegacyData(int i) {
		return this.getBlockData().set(BlockCactus.AGE, Integer.valueOf(i));
	}

	@Override
	public int toLegacyData(IBlockData iblockdata) {
		return iblockdata.get(BlockCactus.AGE).intValue();
	}

	@Override
	protected BlockStateList getStateList() {
		return new BlockStateList(this, new IBlockState[] { BlockCactus.AGE });
	}
}
