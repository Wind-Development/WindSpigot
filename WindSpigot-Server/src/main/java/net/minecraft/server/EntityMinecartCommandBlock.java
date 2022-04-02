package net.minecraft.server;

public class EntityMinecartCommandBlock extends EntityMinecartAbstract {

	private final CommandBlockListenerAbstract a = new CommandBlockListenerAbstract() {
		{
			this.sender = EntityMinecartCommandBlock.this.getBukkitEntity(); // CraftBukkit - Set the sender
		}

		@Override
		public void h() {
			EntityMinecartCommandBlock.this.getDataWatcher().watch(23, this.getCommand());
			EntityMinecartCommandBlock.this.getDataWatcher().watch(24, IChatBaseComponent.ChatSerializer.a(this.k()));
		}

		@Override
		public BlockPosition getChunkCoordinates() {
			return new BlockPosition(EntityMinecartCommandBlock.this.locX, EntityMinecartCommandBlock.this.locY + 0.5D,
					EntityMinecartCommandBlock.this.locZ);
		}

		@Override
		public Vec3D d() {
			return new Vec3D(EntityMinecartCommandBlock.this.locX, EntityMinecartCommandBlock.this.locY,
					EntityMinecartCommandBlock.this.locZ);
		}

		@Override
		public World getWorld() {
			return EntityMinecartCommandBlock.this.world;
		}

		@Override
		public Entity f() {
			return EntityMinecartCommandBlock.this;
		}
	};
	private int b = 0;

	public EntityMinecartCommandBlock(World world) {
		super(world);
	}

	public EntityMinecartCommandBlock(World world, double d0, double d1, double d2) {
		super(world, d0, d1, d2);
	}

	@Override
	protected void h() {
		super.h();
		this.getDataWatcher().a(23, "");
		this.getDataWatcher().a(24, "");
	}

	@Override
	protected void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		this.a.b(nbttagcompound);
		this.getDataWatcher().watch(23, this.getCommandBlock().getCommand());
		this.getDataWatcher().watch(24, IChatBaseComponent.ChatSerializer.a(this.getCommandBlock().k()));
	}

	@Override
	protected void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		this.a.a(nbttagcompound);
	}

	@Override
	public EntityMinecartAbstract.EnumMinecartType s() {
		return EntityMinecartAbstract.EnumMinecartType.COMMAND_BLOCK;
	}

	@Override
	public IBlockData u() {
		return Blocks.COMMAND_BLOCK.getBlockData();
	}

	public CommandBlockListenerAbstract getCommandBlock() {
		return this.a;
	}

	@Override
	public void a(int i, int j, int k, boolean flag) {
		if (flag && this.ticksLived - this.b >= 4) {
			this.getCommandBlock().a(this.world);
			this.b = this.ticksLived;
		}

	}

	@Override
	public boolean e(EntityHuman entityhuman) {
		this.a.a(entityhuman);
		return false;
	}

	@Override
	public void i(int i) {
		super.i(i);
		if (i == 24) {
			try {
				this.a.b(IChatBaseComponent.ChatSerializer.a(this.getDataWatcher().getString(24)));
			} catch (Throwable ignored) {
            }
		} else if (i == 23) {
			this.a.setCommand(this.getDataWatcher().getString(23));
		}

	}
}
