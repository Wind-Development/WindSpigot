package net.minecraft.server;

public class TileEntityCommand extends TileEntity {

	private final CommandBlockListenerAbstract a = new CommandBlockListenerAbstract() {
		{
			sender = new org.bukkit.craftbukkit.command.CraftBlockCommandSender(this); // CraftBukkit - add sender
		}

		@Override
		public BlockPosition getChunkCoordinates() {
			return TileEntityCommand.this.position;
		}

		@Override
		public Vec3D d() {
			return new Vec3D(TileEntityCommand.this.position.getX() + 0.5D,
					TileEntityCommand.this.position.getY() + 0.5D, TileEntityCommand.this.position.getZ() + 0.5D);
		}

		@Override
		public World getWorld() {
			return TileEntityCommand.this.getWorld();
		}

		@Override
		public void setCommand(String s) {
			super.setCommand(s);
			TileEntityCommand.this.update();
		}

		@Override
		public void h() {
			TileEntityCommand.this.getWorld().notify(TileEntityCommand.this.position);
		}

		@Override
		public Entity f() {
			return null;
		}
	};

	public TileEntityCommand() {
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		this.a.a(nbttagcompound);
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		this.a.b(nbttagcompound);
	}

	@Override
	public Packet getUpdatePacket() {
		NBTTagCompound nbttagcompound = new NBTTagCompound();

		this.b(nbttagcompound);
		return new PacketPlayOutTileEntityData(this.position, 2, nbttagcompound);
	}

	@Override
	public boolean F() {
		return true;
	}

	public CommandBlockListenerAbstract getCommandBlock() {
		return this.a;
	}

	public CommandObjectiveExecutor c() {
		return this.a.n();
	}
}
