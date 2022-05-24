package net.minecraft.server;

public class WorldManager implements IWorldAccess {

	private MinecraftServer a;
	private WorldServer world;

	public WorldManager(MinecraftServer minecraftserver, WorldServer worldserver) {
		this.a = minecraftserver;
		this.world = worldserver;
	}

	@Override
	public void a(int i, boolean flag, double d0, double d1, double d2, double d3, double d4, double d5, int... aint) {
	}

	@Override
	public void a(Entity entity) {
		// WindSpigot - synchronized untracking of entities
		synchronized (this.world.getTracker().trackLock) {
			this.world.getTracker().track(entity);
		}
	}

	@Override
	public void b(Entity entity) {
		// WindSpigot - synchronized untracking of entities
		synchronized (this.world.getTracker().trackLock) {
			this.world.getTracker().untrackEntity(entity);
			this.world.getScoreboard().a(entity);
		}
	}

	@Override
	public void a(String s, double d0, double d1, double d2, float f, float f1) {
		// CraftBukkit - this.world.dimension
		// this.a.getPlayerList().sendPacketNearby(d0, d1, d2, f > 1.0F ? (double)
		// (16.0F * f) : 16.0D, this.world.dimension, new
		// PacketPlayOutNamedSoundEffect(s, d0, d1, d2, f, f1));
		this.world.playerMap.sendPacketNearby(null, d0, d1, d2, f > 1.0F ? (double) (16.0F * f) : 16.0D,
				new PacketPlayOutNamedSoundEffect(s, d0, d1, d2, f, f1), false);
	}

	@Override
	public void a(EntityHuman entityhuman, String s, double d0, double d1, double d2, float f, float f1) {
		if ("random.drink".equals(s) || s.contains("step") || s.contains("player") || "random.eat".equals(s)) {
			// this.a.getPlayerList().sendPacketNearby(entityhuman, d0, d1, d2, f > 1.0F ?
			// (double) (16.0F * f) : 16.0D, this.world.dimension, new
			// PacketPlayOutNamedSoundEffect(s, d0, d1, d2, f, f1));
			this.world.playerMap.sendPacketNearby((EntityPlayer) entityhuman, d0, d1, d2,
					f > 1.0F ? (double) (16.0F * f) : 16.0D, new PacketPlayOutNamedSoundEffect(s, d0, d1, d2, f, f1),
					false);
		} else {
			// this.a.getPlayerList().sendPacketNearbyIncludingSelf(entityhuman, d0, d1, d2,
			// f > 1.0F ? (double) (16.0F * f) : 16.0D, this.world.dimension, new
			// PacketPlayOutNamedSoundEffect(s, d0, d1, d2, f, f1));
			this.world.playerMap.sendPacketNearby((EntityPlayer) entityhuman, d0, d1, d2,
					f > 1.0F ? (double) (16.0F * f) : 16.0D, new PacketPlayOutNamedSoundEffect(s, d0, d1, d2, f, f1),
					true);
		}
	}

	@Override
	public void a(int i, int j, int k, int l, int i1, int j1) {
	}

	@Override
	public void a(BlockPosition blockposition) {
		this.world.getPlayerChunkMap().flagDirty(blockposition);
	}

	@Override
	public void b(BlockPosition blockposition) {
	}

	@Override
	public void a(String s, BlockPosition blockposition) {
	}

	@Override
	public void a(EntityHuman entityhuman, int i, BlockPosition blockposition, int j) {
		if (i == 2001) {
			this.world.playerMap.sendPacketNearby((EntityPlayer) entityhuman, blockposition.getX(),
					blockposition.getY(), blockposition.getZ(), 64.0D,
					new PacketPlayOutWorldEvent(i, blockposition, j, false), false);
		} else {
			this.world.playerMap.sendPacketNearby((EntityPlayer) entityhuman, blockposition.getX(),
					blockposition.getY(), blockposition.getZ(), 64.0D,
					new PacketPlayOutWorldEvent(i, blockposition, j, false), true);
		}
	}

	@Override
	public void sendPlayWorldPacket(EntityHuman entityhuman, int i, int blockPosition_x, int blockPosition_y,
			int blockPosition_z, int j) {
		// this.a.getPlayerList().sendPacketNearby(entityhuman, (double)
		// blockPosition_x, (double) blockPosition_y, (double) blockPosition_z, 64.0D,
		// this.world, new Int3PacketPlayOutWorldEvent(i, blockPosition_x,
		// blockPosition_y, blockPosition_z, j, false));
		this.world.playerMap.sendPacketNearby((EntityPlayer) entityhuman, (double) blockPosition_x,
				(double) blockPosition_y, (double) blockPosition_z, 64.0D,
				new Int3PacketPlayOutWorldEvent(i, blockPosition_x, blockPosition_y, blockPosition_z, j, false), false);
	}

	@Override
	public void a(int i, BlockPosition blockposition, int j) {
		this.a.getPlayerList().sendAll(new PacketPlayOutWorldEvent(i, blockposition, j, true));
	}

	@Override
	public void b(int i, BlockPosition blockposition, int j) {
		// CraftBukkit start
		EntityHuman entityhuman = null;
		Entity entity = world.a(i); // PAIL Rename getEntity
		if (entity instanceof EntityHuman) {
			entityhuman = (EntityHuman) entity;
			// CraftBukkit end
		}

		java.util.List<? extends EntityHuman> list = entity != null ? entity.world.players : this.a.getPlayerList().v();
		PacketPlayOutBlockBreakAnimation packet = null; // SportPaper - cache packet

		for (EntityHuman human : list) {
			if (!(human instanceof EntityPlayer)) {
				continue;
			}
			EntityPlayer entityplayer = (EntityPlayer) human;

			if (entityplayer.world == this.world && entityplayer.getId() != i) {
				// CraftBukkit start
				if (entityhuman instanceof EntityPlayer
						&& !entityplayer.getBukkitEntity().canSee(((EntityPlayer) entityhuman).getBukkitEntity())) {
					continue;
				}
				// CraftBukkit end

				if (packet == null) {
					packet = new PacketPlayOutBlockBreakAnimation(i, blockposition, j);
				}
				// entityplayer.playerConnection.sendPacket(packet);
				this.world.playerMap.sendPacketNearby(entityplayer, blockposition.getX(), blockposition.getY(),
						blockposition.getZ(), 32.0D, new PacketPlayOutBlockBreakAnimation(i, blockposition, j), false);
			}
		}

	}
}
