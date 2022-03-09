package net.minecraft.server;

import java.util.Random;

public class BlockSnow extends Block {

	public static final BlockStateInteger LAYERS = BlockStateInteger.of("layers", 1, 8);

	protected BlockSnow() {
		super(Material.PACKED_ICE);
		this.j(this.blockStateList.getBlockData().set(BlockSnow.LAYERS, Integer.valueOf(1)));
		this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
		this.a(true);
		this.a(CreativeModeTab.c);
		this.j();
	}

	@Override
	public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
		return iblockaccess.getType(blockposition).get(BlockSnow.LAYERS).intValue() < 5;
	}

	@Override
	public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
		int i = iblockdata.get(BlockSnow.LAYERS).intValue() - 1;
		float f = 0.125F;

		return new AxisAlignedBB(blockposition.getX() + this.minX, blockposition.getY() + this.minY,
				blockposition.getZ() + this.minZ, blockposition.getX() + this.maxX, blockposition.getY() + i * f,
				blockposition.getZ() + this.maxZ);
	}

	@Override
	public boolean c() {
		return false;
	}

	@Override
	public boolean d() {
		return false;
	}

	@Override
	public void j() {
		this.b(0);
	}

	@Override
	public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
		IBlockData iblockdata = iblockaccess.getType(blockposition);

		this.b(iblockdata.get(BlockSnow.LAYERS).intValue());
	}

	protected void b(int i) {
		this.a(0.0F, 0.0F, 0.0F, 1.0F, i / 8.0F, 1.0F);
	}

	@Override
	public boolean canPlace(World world, BlockPosition blockposition) {
		IBlockData iblockdata = world.getType(blockposition.down());
		Block block = iblockdata.getBlock();

		return block != Blocks.ICE && block != Blocks.PACKED_ICE ? (block.getMaterial() == Material.LEAVES ? true
				: (block == this && iblockdata.get(BlockSnow.LAYERS).intValue() >= 7 ? true
						: block.c() && block.material.isSolid()))
				: false;
	}

	@Override
	public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
		this.e(world, blockposition, iblockdata);
	}

	private boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
		if (!this.canPlace(world, blockposition)) {
			this.b(world, blockposition, iblockdata, 0);
			world.setAir(blockposition);
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata,
			TileEntity tileentity) {
		a(world, blockposition, new ItemStack(Items.SNOWBALL, iblockdata.get(BlockSnow.LAYERS).intValue() + 1, 0));
		world.setAir(blockposition);
		entityhuman.b(StatisticList.MINE_BLOCK_COUNT[Block.getId(this)]);
	}

	@Override
	public Item getDropType(IBlockData iblockdata, Random random, int i) {
		return Items.SNOWBALL;
	}

	@Override
	public int a(Random random) {
		return 0;
	}

	@Override
	public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
		if (world.b(EnumSkyBlock.BLOCK, blockposition) > 11) {
			// CraftBukkit start
			if (org.bukkit.craftbukkit.event.CraftEventFactory.callBlockFadeEvent(
					world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()),
					Blocks.AIR).isCancelled()) {
				return;
			}
			// CraftBukkit end
			this.b(world, blockposition, world.getType(blockposition), 0);
			world.setAir(blockposition);
		}

	}

	@Override
	public IBlockData fromLegacyData(int i) {
		return this.getBlockData().set(BlockSnow.LAYERS, Integer.valueOf((i & 7) + 1));
	}

	@Override
	public boolean a(World world, BlockPosition blockposition) {
		return world.getType(blockposition).get(BlockSnow.LAYERS).intValue() == 1;
	}

	@Override
	public int toLegacyData(IBlockData iblockdata) {
		return iblockdata.get(BlockSnow.LAYERS).intValue() - 1;
	}

	@Override
	protected BlockStateList getStateList() {
		return new BlockStateList(this, new IBlockState[] { BlockSnow.LAYERS });
	}
}
