package net.minecraft.server;

import java.util.Random;

public class BlockCake extends Block {

	public static final BlockStateInteger BITES = BlockStateInteger.of("bites", 0, 6);

	protected BlockCake() {
		super(Material.CAKE);
		this.j(this.blockStateList.getBlockData().set(BlockCake.BITES, Integer.valueOf(0)));
		this.a(true);
	}

	@Override
	public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
		float f = 0.0625F;
		float f1 = (1 + iblockaccess.getType(blockposition).get(BlockCake.BITES).intValue() * 2) / 16.0F;
		float f2 = 0.5F;

		this.a(f1, 0.0F, f, 1.0F - f, f2, 1.0F - f);
	}

	@Override
	public void j() {
		float f = 0.0625F;
		float f1 = 0.5F;

		this.a(f, 0.0F, f, 1.0F - f, f1, 1.0F - f);
	}

	@Override
	public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
		float f = 0.0625F;
		float f1 = (1 + iblockdata.get(BlockCake.BITES).intValue() * 2) / 16.0F;
		float f2 = 0.5F;

		return new AxisAlignedBB(blockposition.getX() + f1, blockposition.getY(), blockposition.getZ() + f,
				blockposition.getX() + 1 - f, blockposition.getY() + f2, blockposition.getZ() + 1 - f);
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
	public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman,
			EnumDirection enumdirection, float f, float f1, float f2) {
		this.b(world, blockposition, iblockdata, entityhuman);
		return true;
	}

	@Override
	public void attack(World world, BlockPosition blockposition, EntityHuman entityhuman) {
		this.b(world, blockposition, world.getType(blockposition), entityhuman);
	}

	private void b(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
		if (entityhuman.j(false)) {
			entityhuman.b(StatisticList.H);
			// CraftBukkit start
			// entityhuman.getFoodData().eat(2, 0.1F);
			int oldFoodLevel = entityhuman.getFoodData().foodLevel;

			org.bukkit.event.entity.FoodLevelChangeEvent event = org.bukkit.craftbukkit.event.CraftEventFactory
					.callFoodLevelChangeEvent(entityhuman, 2 + oldFoodLevel);

			if (!event.isCancelled()) {
				entityhuman.getFoodData().eat(event.getFoodLevel() - oldFoodLevel, 0.1F);
			}

			((EntityPlayer) entityhuman).playerConnection.sendPacket(
					new PacketPlayOutUpdateHealth(((EntityPlayer) entityhuman).getBukkitEntity().getScaledHealth(),
							entityhuman.getFoodData().foodLevel, entityhuman.getFoodData().saturationLevel));
			// CraftBukkit end
			int i = iblockdata.get(BlockCake.BITES).intValue();

			if (i < 6) {
				world.setTypeAndData(blockposition, iblockdata.set(BlockCake.BITES, Integer.valueOf(i + 1)), 3);
			} else {
				world.setAir(blockposition);
			}

		}
	}

	@Override
	public boolean canPlace(World world, BlockPosition blockposition) {
		return super.canPlace(world, blockposition) ? this.e(world, blockposition) : false;
	}

	@Override
	public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
		if (!this.e(world, blockposition)) {
			world.setAir(blockposition);
		}

	}

	private boolean e(World world, BlockPosition blockposition) {
		return world.getType(blockposition.down()).getBlock().getMaterial().isBuildable();
	}

	@Override
	public int a(Random random) {
		return 0;
	}

	@Override
	public Item getDropType(IBlockData iblockdata, Random random, int i) {
		return null;
	}

	@Override
	public IBlockData fromLegacyData(int i) {
		return this.getBlockData().set(BlockCake.BITES, Integer.valueOf(i));
	}

	@Override
	public int toLegacyData(IBlockData iblockdata) {
		return iblockdata.get(BlockCake.BITES).intValue();
	}

	@Override
	protected BlockStateList getStateList() {
		return new BlockStateList(this, new IBlockState[] { BlockCake.BITES });
	}

	@Override
	public int l(World world, BlockPosition blockposition) {
		return (7 - world.getType(blockposition).get(BlockCake.BITES).intValue()) * 2;
	}

	@Override
	public boolean isComplexRedstone() {
		return true;
	}
}
