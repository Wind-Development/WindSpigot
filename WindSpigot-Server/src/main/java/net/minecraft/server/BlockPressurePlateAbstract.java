package net.minecraft.server;

import java.util.Random;

import org.bukkit.event.block.BlockRedstoneEvent; // CraftBukkit

public abstract class BlockPressurePlateAbstract extends Block {

	protected BlockPressurePlateAbstract(Material material) {
		this(material, material.r());
	}

	protected BlockPressurePlateAbstract(Material material, MaterialMapColor materialmapcolor) {
		super(material, materialmapcolor);
		this.a(CreativeModeTab.d);
		this.a(true);
	}

	@Override
	public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
		this.d(iblockaccess.getType(blockposition));
	}

	protected void d(IBlockData iblockdata) {
		boolean flag = this.e(iblockdata) > 0;
		float f = 0.0625F;

		if (flag) {
			this.a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.03125F, 0.9375F);
		} else {
			this.a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.0625F, 0.9375F);
		}

	}

	@Override
	public int a(World world) {
		return 20;
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
	public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
		return true;
	}

	@Override
	public boolean g() {
		return true;
	}

	@Override
	public boolean canPlace(World world, BlockPosition blockposition) {
		return this.m(world, blockposition.down());
	}

	@Override
	public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
		if (!this.m(world, blockposition.down())) {
			this.b(world, blockposition, iblockdata, 0);
			world.setAir(blockposition);
		}

	}

	private boolean m(World world, BlockPosition blockposition) {
		return World.a(world, blockposition) || world.getType(blockposition).getBlock() instanceof BlockFence;
	}

	@Override
	public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
	}

	@Override
	public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
		if (!world.isClientSide) {
			int i = this.e(iblockdata);

			if (i > 0) {
				this.a(world, blockposition, iblockdata, i);
			}

		}
	}

	@Override
	public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {
		if (!world.isClientSide) {
			int i = this.e(iblockdata);

			if (i == 0) {
				this.a(world, blockposition, iblockdata, i);
			}

		}
	}

	protected void a(World world, BlockPosition blockposition, IBlockData iblockdata, int i) {
		int j = this.f(world, blockposition);
		boolean flag = i > 0;
		boolean flag1 = j > 0;

		// CraftBukkit start - Interact Pressure Plate
		org.bukkit.World bworld = world.getWorld();
		org.bukkit.plugin.PluginManager manager = world.getServer().getPluginManager();

		if (flag != flag1) {
			BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(
					bworld.getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()), i, j);
			manager.callEvent(eventRedstone);

			flag1 = eventRedstone.getNewCurrent() > 0;
			j = eventRedstone.getNewCurrent();
		}
		// CraftBukkit end

		if (i != j) {
			iblockdata = this.a(iblockdata, j);
			world.setTypeAndData(blockposition, iblockdata, 2);
			this.e(world, blockposition);
			world.b(blockposition, blockposition);
		}

		if (!flag1 && flag) {
			world.makeSound(blockposition.getX() + 0.5D, blockposition.getY() + 0.1D, blockposition.getZ() + 0.5D,
					"random.click", 0.3F, 0.5F);
		} else if (flag1 && !flag) {
			world.makeSound(blockposition.getX() + 0.5D, blockposition.getY() + 0.1D, blockposition.getZ() + 0.5D,
					"random.click", 0.3F, 0.6F);
		}

		if (flag1) {
			world.a(blockposition, this, this.a(world));
		}

	}

	protected AxisAlignedBB getBoundingBox(BlockPosition blockposition) {
		float f = 0.125F;

		return new AxisAlignedBB(blockposition.getX() + 0.125F, blockposition.getY(), blockposition.getZ() + 0.125F,
				blockposition.getX() + 1 - 0.125F, blockposition.getY() + 0.25D, blockposition.getZ() + 1 - 0.125F);
	}

	@Override
	public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
		if (this.e(iblockdata) > 0) {
			this.e(world, blockposition);
		}

		super.remove(world, blockposition, iblockdata);
	}

	protected void e(World world, BlockPosition blockposition) {
		world.applyPhysics(blockposition, this);
		world.applyPhysics(blockposition.down(), this);
	}

	@Override
	public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata,
			EnumDirection enumdirection) {
		return this.e(iblockdata);
	}

	@Override
	public int b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata,
			EnumDirection enumdirection) {
		return enumdirection == EnumDirection.UP ? this.e(iblockdata) : 0;
	}

	@Override
	public boolean isPowerSource() {
		return true;
	}

	@Override
	public void j() {
		float f = 0.5F;
		float f1 = 0.125F;
		float f2 = 0.5F;

		this.a(0.0F, 0.375F, 0.0F, 1.0F, 0.625F, 1.0F);
	}

	@Override
	public int k() {
		return 1;
	}

	protected abstract int f(World world, BlockPosition blockposition);

	protected abstract int e(IBlockData iblockdata);

	protected abstract IBlockData a(IBlockData iblockdata, int i);
}
