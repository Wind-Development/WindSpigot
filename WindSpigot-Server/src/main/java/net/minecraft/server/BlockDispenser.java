package net.minecraft.server;

import ga.windpvp.windspigot.random.FastRandom;

import java.util.Random;

public class BlockDispenser extends BlockContainer {

	public static final BlockStateDirection FACING = BlockStateDirection.of("facing");
	public static final BlockStateBoolean TRIGGERED = BlockStateBoolean.of("triggered");
	public static final RegistryDefault<Item, IDispenseBehavior> REGISTRY = new RegistryDefault(
			new DispenseBehaviorItem());
	protected Random O = new FastRandom();
	public static boolean eventFired = false; // CraftBukkit

	protected BlockDispenser() {
		super(Material.STONE);
		this.j(this.blockStateList.getBlockData().set(BlockDispenser.FACING, EnumDirection.NORTH)
				.set(BlockDispenser.TRIGGERED, false));
		this.a(CreativeModeTab.d);
	}

	@Override
	public int a(World world) {
		return 4;
	}

	@Override
	public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
		super.onPlace(world, blockposition, iblockdata);
		this.e(world, blockposition, iblockdata);
	}

	private void e(World world, BlockPosition blockposition, IBlockData iblockdata) {
		if (!world.isClientSide) {
			EnumDirection enumdirection = iblockdata.get(BlockDispenser.FACING);
			boolean flag = world.getType(blockposition.north()).getBlock().o();
			boolean flag1 = world.getType(blockposition.south()).getBlock().o();

			if (enumdirection == EnumDirection.NORTH && flag && !flag1) {
				enumdirection = EnumDirection.SOUTH;
			} else if (enumdirection == EnumDirection.SOUTH && flag1 && !flag) {
				enumdirection = EnumDirection.NORTH;
			} else {
				boolean flag2 = world.getType(blockposition.west()).getBlock().o();
				boolean flag3 = world.getType(blockposition.east()).getBlock().o();

				if (enumdirection == EnumDirection.WEST && flag2 && !flag3) {
					enumdirection = EnumDirection.EAST;
				} else if (enumdirection == EnumDirection.EAST && flag3 && !flag2) {
					enumdirection = EnumDirection.WEST;
				}
			}

			world.setTypeAndData(blockposition, iblockdata.set(BlockDispenser.FACING, enumdirection)
					.set(BlockDispenser.TRIGGERED, false), 2);
		}
	}

	@Override
	public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman,
			EnumDirection enumdirection, float f, float f1, float f2) {
		if (world.isClientSide) {
			return true;
		} else {
			TileEntity tileentity = world.getTileEntity(blockposition);

			if (tileentity instanceof TileEntityDispenser) {
				entityhuman.openContainer((TileEntityDispenser) tileentity);
				if (tileentity instanceof TileEntityDropper) {
					entityhuman.b(StatisticList.O);
				} else {
					entityhuman.b(StatisticList.Q);
				}
			}

			return true;
		}
	}

	public void dispense(World world, BlockPosition blockposition) {
		SourceBlock sourceblock = new SourceBlock(world, blockposition);
		TileEntityDispenser tileentitydispenser = (TileEntityDispenser) sourceblock.getTileEntity();

		if (tileentitydispenser != null) {
			int i = tileentitydispenser.m();

			if (i < 0) {
				world.triggerEffect(1001, blockposition, 0);
			} else {
				ItemStack itemstack = tileentitydispenser.getItem(i);
				IDispenseBehavior idispensebehavior = this.a(itemstack);

				if (idispensebehavior != IDispenseBehavior.NONE) {
					ItemStack itemstack1 = idispensebehavior.a(sourceblock, itemstack);
					eventFired = false; // CraftBukkit - reset event status

					tileentitydispenser.setItem(i, itemstack1.count <= 0 ? null : itemstack1);
				}

			}
		}
	}

	protected IDispenseBehavior a(ItemStack itemstack) {
		return BlockDispenser.REGISTRY.get(itemstack == null ? null : itemstack.getItem());
	}

	@Override
	public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
		boolean flag = world.isBlockIndirectlyPowered(blockposition)
				|| world.isBlockIndirectlyPowered(blockposition.up());
		boolean flag1 = iblockdata.get(BlockDispenser.TRIGGERED).booleanValue();

		if (flag && !flag1) {
			world.a(blockposition, this, this.a(world));
			world.setTypeAndData(blockposition, iblockdata.set(BlockDispenser.TRIGGERED, true), 4);
		} else if (!flag && flag1) {
			world.setTypeAndData(blockposition, iblockdata.set(BlockDispenser.TRIGGERED, false), 4);
		}

	}

	@Override
	public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
		if (!world.isClientSide) {
			this.dispense(world, blockposition);
		}

	}

	@Override
	public TileEntity a(World world, int i) {
		return new TileEntityDispenser();
	}

	@Override
	public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f,
			float f1, float f2, int i, EntityLiving entityliving) {
		return this.getBlockData().set(BlockDispenser.FACING, BlockPiston.a(world, blockposition, entityliving))
				.set(BlockDispenser.TRIGGERED, false);
	}

	@Override
	public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving,
			ItemStack itemstack) {
		world.setTypeAndData(blockposition,
				iblockdata.set(BlockDispenser.FACING, BlockPiston.a(world, blockposition, entityliving)), 2);
		if (itemstack.hasName()) {
			TileEntity tileentity = world.getTileEntity(blockposition);

			if (tileentity instanceof TileEntityDispenser) {
				((TileEntityDispenser) tileentity).a(itemstack.getName());
			}
		}

	}

	@Override
	public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
		TileEntity tileentity = world.getTileEntity(blockposition);

		if (tileentity instanceof TileEntityDispenser) {
			InventoryUtils.dropInventory(world, blockposition, (TileEntityDispenser) tileentity);
			world.updateAdjacentComparators(blockposition, this);
		}

		super.remove(world, blockposition, iblockdata);
	}

	public static IPosition a(ISourceBlock isourceblock) {
		EnumDirection enumdirection = b(isourceblock.f());
		double d0 = isourceblock.getX() + 0.7D * enumdirection.getAdjacentX();
		double d1 = isourceblock.getY() + 0.7D * enumdirection.getAdjacentY();
		double d2 = isourceblock.getZ() + 0.7D * enumdirection.getAdjacentZ();

		return new Position(d0, d1, d2);
	}

	public static EnumDirection b(int i) {
		return EnumDirection.fromType1(i & 7);
	}

	@Override
	public boolean isComplexRedstone() {
		return true;
	}

	@Override
	public int l(World world, BlockPosition blockposition) {
		return Container.a(world.getTileEntity(blockposition));
	}

	@Override
	public int b() {
		return 3;
	}

	@Override
	public IBlockData fromLegacyData(int i) {
		return this.getBlockData().set(BlockDispenser.FACING, b(i)).set(BlockDispenser.TRIGGERED,
				(i & 8) > 0);
	}

	@Override
	public int toLegacyData(IBlockData iblockdata) {
		byte b0 = 0;
		int i = b0 | iblockdata.get(BlockDispenser.FACING).a();

		if (iblockdata.get(BlockDispenser.TRIGGERED)) {
			i |= 8;
		}

		return i;
	}

	@Override
	protected BlockStateList getStateList() {
		return new BlockStateList(this, BlockDispenser.FACING, BlockDispenser.TRIGGERED);
	}
}
