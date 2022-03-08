package net.minecraft.server;

public class BlockAnvil extends BlockFalling {

	public static final BlockStateDirection FACING = BlockStateDirection.of("facing",
			EnumDirection.EnumDirectionLimit.HORIZONTAL);
	public static final BlockStateInteger DAMAGE = BlockStateInteger.of("damage", 0, 2);

	protected BlockAnvil() {
		super(Material.HEAVY);
		this.j(this.blockStateList.getBlockData().set(BlockAnvil.FACING, EnumDirection.NORTH).set(BlockAnvil.DAMAGE,
				Integer.valueOf(0)));
		this.e(0);
		this.a(CreativeModeTab.c);
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
	public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f,
			float f1, float f2, int i, EntityLiving entityliving) {
		EnumDirection enumdirection1 = entityliving.getDirection().e();

		return super.getPlacedState(world, blockposition, enumdirection, f, f1, f2, i, entityliving)
				.set(BlockAnvil.FACING, enumdirection1).set(BlockAnvil.DAMAGE, Integer.valueOf(i >> 2));
	}

	@Override
	public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman,
			EnumDirection enumdirection, float f, float f1, float f2) {
		if (!world.isClientSide) {
			entityhuman.openTileEntity(new BlockAnvil.TileEntityContainerAnvil(world, blockposition));
		}

		return true;
	}

	@Override
	public int getDropData(IBlockData iblockdata) {
		return iblockdata.get(BlockAnvil.DAMAGE).intValue();
	}

	@Override
	public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
		EnumDirection enumdirection = iblockaccess.getType(blockposition).get(BlockAnvil.FACING);

		if (enumdirection.k() == EnumDirection.EnumAxis.X) {
			this.a(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F);
		} else {
			this.a(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
		}

	}

	@Override
	protected void a(EntityFallingBlock entityfallingblock) {
		entityfallingblock.a(true);
	}

	@Override
	public void a_(World world, BlockPosition blockposition) {
		world.triggerEffect(1022, blockposition, 0);
	}

	@Override
	public IBlockData fromLegacyData(int i) {
		return this.getBlockData().set(BlockAnvil.FACING, EnumDirection.fromType2(i & 3)).set(BlockAnvil.DAMAGE,
				Integer.valueOf((i & 15) >> 2));
	}

	@Override
	public int toLegacyData(IBlockData iblockdata) {
		byte b0 = 0;
		int i = b0 | iblockdata.get(BlockAnvil.FACING).b();

		i |= iblockdata.get(BlockAnvil.DAMAGE).intValue() << 2;
		return i;
	}

	@Override
	protected BlockStateList getStateList() {
		return new BlockStateList(this, new IBlockState[] { BlockAnvil.FACING, BlockAnvil.DAMAGE });
	}

	public static class TileEntityContainerAnvil implements ITileEntityContainer {

		private final World a;
		private final BlockPosition b;

		public TileEntityContainerAnvil(World world, BlockPosition blockposition) {
			this.a = world;
			this.b = blockposition;
		}

		@Override
		public String getName() {
			return "anvil";
		}

		@Override
		public boolean hasCustomName() {
			return false;
		}

		@Override
		public IChatBaseComponent getScoreboardDisplayName() {
			return new ChatMessage(Blocks.ANVIL.a() + ".name", new Object[0]);
		}

		@Override
		public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
			return new ContainerAnvil(playerinventory, this.a, this.b, entityhuman);
		}

		@Override
		public String getContainerName() {
			return "minecraft:anvil";
		}
	}
}
