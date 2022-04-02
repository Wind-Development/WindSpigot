package net.minecraft.server;

import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.Iterator;

public class BlockLever extends Block {

	public static final BlockStateEnum<BlockLever.EnumLeverPosition> FACING = BlockStateEnum.of("facing",
			BlockLever.EnumLeverPosition.class);
	public static final BlockStateBoolean POWERED = BlockStateBoolean.of("powered");

	protected BlockLever() {
		super(Material.ORIENTABLE);
		this.j(this.blockStateList.getBlockData().set(BlockLever.FACING, BlockLever.EnumLeverPosition.NORTH)
				.set(BlockLever.POWERED, Boolean.valueOf(false)));
		this.a(CreativeModeTab.d);
	}

	@Override
	public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
		return null;
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
	public boolean canPlace(World world, BlockPosition blockposition, EnumDirection enumdirection) {
		return a(world, blockposition, enumdirection.opposite());
	}

	@Override
	public boolean canPlace(World world, BlockPosition blockposition) {
		EnumDirection[] aenumdirection = EnumDirection.values();
		int i = aenumdirection.length;

		for (int j = 0; j < i; ++j) {
			EnumDirection enumdirection = aenumdirection[j];

			if (a(world, blockposition, enumdirection)) {
				return true;
			}
		}

		return false;
	}

	protected static boolean a(World world, BlockPosition blockposition, EnumDirection enumdirection) {
		return BlockButtonAbstract.a(world, blockposition, enumdirection);
	}

	@Override
	public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f,
			float f1, float f2, int i, EntityLiving entityliving) {
		IBlockData iblockdata = this.getBlockData().set(BlockLever.POWERED, Boolean.valueOf(false));

		if (a(world, blockposition, enumdirection.opposite())) {
			return iblockdata.set(BlockLever.FACING,
					BlockLever.EnumLeverPosition.a(enumdirection, entityliving.getDirection()));
		} else {
			Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

			EnumDirection enumdirection1;

			do {
				if (!iterator.hasNext()) {
					if (World.a(world, blockposition.down())) {
						return iblockdata.set(BlockLever.FACING,
								BlockLever.EnumLeverPosition.a(EnumDirection.UP, entityliving.getDirection()));
					}

					return iblockdata;
				}

				enumdirection1 = (EnumDirection) iterator.next();
			} while (enumdirection1 == enumdirection || !a(world, blockposition, enumdirection1.opposite()));

			return iblockdata.set(BlockLever.FACING,
					BlockLever.EnumLeverPosition.a(enumdirection1, entityliving.getDirection()));
		}
	}

	public static int a(EnumDirection enumdirection) {
		switch (BlockLever.SyntheticClass_1.a[enumdirection.ordinal()]) {
		case 1:
			return 0;

		case 2:
			return 5;

		case 3:
			return 4;

		case 4:
			return 3;

		case 5:
			return 2;

		case 6:
			return 1;

		default:
			return -1;
		}
	}

	@Override
	public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
		if (this.e(world, blockposition, iblockdata)
				&& !a(world, blockposition, iblockdata.get(BlockLever.FACING).c().opposite())) {
			this.b(world, blockposition, iblockdata, 0);
			world.setAir(blockposition);
		}

	}

	private boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
		if (this.canPlace(world, blockposition)) {
			return true;
		} else {
			this.b(world, blockposition, iblockdata, 0);
			world.setAir(blockposition);
			return false;
		}
	}

	@Override
	public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
		float f = 0.1875F;

		switch (BlockLever.SyntheticClass_1.b[iblockaccess.getType(blockposition).get(BlockLever.FACING).ordinal()]) {
		case 1:
			this.a(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
			break;

		case 2:
			this.a(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
			break;

		case 3:
			this.a(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
			break;

		case 4:
			this.a(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
			break;

		case 5:
		case 6:
			f = 0.25F;
			this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.6F, 0.5F + f);
			break;

		case 7:
		case 8:
			f = 0.25F;
			this.a(0.5F - f, 0.4F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f);
		}

	}

	@Override
	public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman,
			EnumDirection enumdirection, float f, float f1, float f2) {
		if (world.isClientSide) {
			return true;
		} else {
			// CraftBukkit start - Interact Lever
			boolean powered = iblockdata.get(POWERED);
			org.bukkit.block.Block block = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(),
					blockposition.getZ());
			int old = (powered) ? 15 : 0;
			int current = (!powered) ? 15 : 0;

			BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, old, current);
			world.getServer().getPluginManager().callEvent(eventRedstone);

			if ((eventRedstone.getNewCurrent() > 0) == (powered)) {
				return true;
			}
			// CraftBukkit end

			iblockdata = iblockdata.a(BlockLever.POWERED);
			world.setTypeAndData(blockposition, iblockdata, 3);
			world.makeSound(blockposition.getX() + 0.5D, blockposition.getY() + 0.5D, blockposition.getZ() + 0.5D,
					"random.click", 0.3F, iblockdata.get(BlockLever.POWERED).booleanValue() ? 0.6F : 0.5F);
			world.applyPhysics(blockposition, this);
			EnumDirection enumdirection1 = iblockdata.get(BlockLever.FACING).c();

			world.applyPhysics(blockposition.shift(enumdirection1.opposite()), this);
			return true;
		}
	}

	@Override
	public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
		if (iblockdata.get(BlockLever.POWERED).booleanValue()) {
			world.applyPhysics(blockposition, this);
			EnumDirection enumdirection = iblockdata.get(BlockLever.FACING).c();

			world.applyPhysics(blockposition.shift(enumdirection.opposite()), this);
		}

		super.remove(world, blockposition, iblockdata);
	}

	@Override
	public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata,
			EnumDirection enumdirection) {
		return iblockdata.get(BlockLever.POWERED).booleanValue() ? 15 : 0;
	}

	@Override
	public int b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata,
			EnumDirection enumdirection) {
		return !iblockdata.get(BlockLever.POWERED).booleanValue() ? 0
				: (iblockdata.get(BlockLever.FACING).c() == enumdirection ? 15 : 0);
	}

	@Override
	public boolean isPowerSource() {
		return true;
	}

	@Override
	public IBlockData fromLegacyData(int i) {
		return this.getBlockData().set(BlockLever.FACING, BlockLever.EnumLeverPosition.a(i & 7)).set(BlockLever.POWERED,
				Boolean.valueOf((i & 8) > 0));
	}

	@Override
	public int toLegacyData(IBlockData iblockdata) {
		byte b0 = 0;
		int i = b0 | iblockdata.get(BlockLever.FACING).a();

		if (iblockdata.get(BlockLever.POWERED).booleanValue()) {
			i |= 8;
		}

		return i;
	}

	@Override
	protected BlockStateList getStateList() {
		return new BlockStateList(this, new IBlockState[] { BlockLever.FACING, BlockLever.POWERED });
	}

	static class SyntheticClass_1 {

		static final int[] a;
		static final int[] b;
		static final int[] c = new int[EnumDirection.EnumAxis.values().length];

		static {
			try {
				BlockLever.SyntheticClass_1.c[EnumDirection.EnumAxis.X.ordinal()] = 1;
			} catch (NoSuchFieldError ignored) {
			}

			try {
				BlockLever.SyntheticClass_1.c[EnumDirection.EnumAxis.Z.ordinal()] = 2;
			} catch (NoSuchFieldError ignored) {
			}

			b = new int[BlockLever.EnumLeverPosition.values().length];

			try {
				BlockLever.SyntheticClass_1.b[BlockLever.EnumLeverPosition.EAST.ordinal()] = 1;
			} catch (NoSuchFieldError ignored) {
			}

			try {
				BlockLever.SyntheticClass_1.b[BlockLever.EnumLeverPosition.WEST.ordinal()] = 2;
			} catch (NoSuchFieldError ignored) {
			}

			try {
				BlockLever.SyntheticClass_1.b[BlockLever.EnumLeverPosition.SOUTH.ordinal()] = 3;
			} catch (NoSuchFieldError ignored) {
			}

			try {
				BlockLever.SyntheticClass_1.b[BlockLever.EnumLeverPosition.NORTH.ordinal()] = 4;
			} catch (NoSuchFieldError ignored) {
			}

			try {
				BlockLever.SyntheticClass_1.b[BlockLever.EnumLeverPosition.UP_Z.ordinal()] = 5;
			} catch (NoSuchFieldError ignored) {
			}

			try {
				BlockLever.SyntheticClass_1.b[BlockLever.EnumLeverPosition.UP_X.ordinal()] = 6;
			} catch (NoSuchFieldError ignored) {
			}

			try {
				BlockLever.SyntheticClass_1.b[BlockLever.EnumLeverPosition.DOWN_X.ordinal()] = 7;
			} catch (NoSuchFieldError ignored) {
			}

			try {
				BlockLever.SyntheticClass_1.b[BlockLever.EnumLeverPosition.DOWN_Z.ordinal()] = 8;
			} catch (NoSuchFieldError ignored) {
			}

			a = new int[EnumDirection.values().length];

			try {
				BlockLever.SyntheticClass_1.a[EnumDirection.DOWN.ordinal()] = 1;
			} catch (NoSuchFieldError ignored) {
			}

			try {
				BlockLever.SyntheticClass_1.a[EnumDirection.UP.ordinal()] = 2;
			} catch (NoSuchFieldError ignored) {
			}

			try {
				BlockLever.SyntheticClass_1.a[EnumDirection.NORTH.ordinal()] = 3;
			} catch (NoSuchFieldError ignored) {
			}

			try {
				BlockLever.SyntheticClass_1.a[EnumDirection.SOUTH.ordinal()] = 4;
			} catch (NoSuchFieldError ignored) {
			}

			try {
				BlockLever.SyntheticClass_1.a[EnumDirection.WEST.ordinal()] = 5;
			} catch (NoSuchFieldError ignored) {
			}

			try {
				BlockLever.SyntheticClass_1.a[EnumDirection.EAST.ordinal()] = 6;
			} catch (NoSuchFieldError ignored) {
			}

		}
	}

	public enum EnumLeverPosition implements INamable {

		DOWN_X(0, "down_x", EnumDirection.DOWN), EAST(1, "east", EnumDirection.EAST),
		WEST(2, "west", EnumDirection.WEST), SOUTH(3, "south", EnumDirection.SOUTH),
		NORTH(4, "north", EnumDirection.NORTH), UP_Z(5, "up_z", EnumDirection.UP), UP_X(6, "up_x", EnumDirection.UP),
		DOWN_Z(7, "down_z", EnumDirection.DOWN);

		private static final BlockLever.EnumLeverPosition[] i = new BlockLever.EnumLeverPosition[values().length];
		private final int j;
		private final String k;
		private final EnumDirection l;

		EnumLeverPosition(int i, String s, EnumDirection enumdirection) {
			this.j = i;
			this.k = s;
			this.l = enumdirection;
		}

		public int a() {
			return this.j;
		}

		public EnumDirection c() {
			return this.l;
		}

		@Override
		public String toString() {
			return this.k;
		}

		public static BlockLever.EnumLeverPosition a(int i) {
			if (i < 0 || i >= BlockLever.EnumLeverPosition.i.length) {
				i = 0;
			}

			return BlockLever.EnumLeverPosition.i[i];
		}

		public static BlockLever.EnumLeverPosition a(EnumDirection enumdirection, EnumDirection enumdirection1) {
			switch (BlockLever.SyntheticClass_1.a[enumdirection.ordinal()]) {
			case 1:
				switch (BlockLever.SyntheticClass_1.c[enumdirection1.k().ordinal()]) {
				case 1:
					return BlockLever.EnumLeverPosition.DOWN_X;

				case 2:
					return BlockLever.EnumLeverPosition.DOWN_Z;

				default:
					throw new IllegalArgumentException(
							"Invalid entityFacing " + enumdirection1 + " for facing " + enumdirection);
				}

			case 2:
				switch (BlockLever.SyntheticClass_1.c[enumdirection1.k().ordinal()]) {
				case 1:
					return BlockLever.EnumLeverPosition.UP_X;

				case 2:
					return BlockLever.EnumLeverPosition.UP_Z;

				default:
					throw new IllegalArgumentException(
							"Invalid entityFacing " + enumdirection1 + " for facing " + enumdirection);
				}

			case 3:
				return BlockLever.EnumLeverPosition.NORTH;

			case 4:
				return BlockLever.EnumLeverPosition.SOUTH;

			case 5:
				return BlockLever.EnumLeverPosition.WEST;

			case 6:
				return BlockLever.EnumLeverPosition.EAST;

			default:
				throw new IllegalArgumentException("Invalid facing: " + enumdirection);
			}
		}

		@Override
		public String getName() {
			return this.k;
		}

		static {
			BlockLever.EnumLeverPosition[] ablocklever_enumleverposition = values();
			int i = ablocklever_enumleverposition.length;

			for (int j = 0; j < i; ++j) {
				BlockLever.EnumLeverPosition blocklever_enumleverposition = ablocklever_enumleverposition[j];

				BlockLever.EnumLeverPosition.i[blocklever_enumleverposition.a()] = blocklever_enumleverposition;
			}

		}
	}
}
