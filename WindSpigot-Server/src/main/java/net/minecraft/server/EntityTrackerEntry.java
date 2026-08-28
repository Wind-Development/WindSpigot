package net.minecraft.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
// CraftBukkit start
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerVelocityEvent;
// CraftBukkit end

import com.windpvp.windspigot.config.WindSpigotConfig;

import it.unimi.dsi.fastutil.objects.Reference2BooleanOpenHashMap;

// WindSpigot - sendPacket methods have been replaced with the queuePacket method
public class EntityTrackerEntry {

	private static final Logger p = LogManager.getLogger();
	public Entity tracker;

	public Entity getTracker() {
		return tracker;
	}

	public int b;

	public int maxTrackingDistance() {
		return b;
	}

	public int c;

	public int updateInterval() {
		return c;
	}

	public int xLoc;
	public int yLoc;
	public int zLoc;
	public int yRot;
	public int xRot;
	public int lastHeadYaw;
	public double motionX;
	public double motionY;
	public double motionZ;
	public int tickCount;
	private double posX;
	private double posY;
	private double posZ;
	private boolean isMoving;
	private boolean u;

	public boolean sendVelocityUpdates() {
		return u;
	}

	private int ticksSinceLastForcedTeleport;
	private Entity lastRecoredRider;
	private boolean ridingEntity;
	private boolean lastOnGround;
	public boolean n;

	public boolean playerEntitiesUpdated() {
		return n;
	}

	// PaperSpigot start
	// Replace trackedPlayers Set with a Map. The value is true until the player
	// receives
	// their first update (which is forced to have absolute coordinates), false
	// afterward.
	public java.util.Map<EntityPlayer, Boolean> trackedPlayerMap = new Reference2BooleanOpenHashMap<>();
	// IonSpigot
	// -
	// HashMap
	// ->
	// Reference2BooleanMap
	
	public Set<EntityPlayer> trackedPlayers = trackedPlayerMap.keySet();
	// PaperSpigot end

	private List<EntityPlayer> toRemove = new ArrayList<>();
	private EntityTracker entityTracker;
	private int addRemoveRate;
	private int addRemoveCooldown;
	private boolean withinNoTrack = false;
	private int trackerThread; // FalchusSpigot

	// Constructor is used internally (Incompatible with NMS plugins)
	public EntityTrackerEntry(EntityTracker entityTracker, Entity entity, int b, int c, boolean flag) {
		this.entityTracker = entityTracker;
		this.tracker = entity;
		this.b = b;
		this.c = c;
		this.u = flag;
		this.xLoc = MathHelper.floor(entity.locX * 32.0D);
		this.yLoc = MathHelper.floor(entity.locY * 32.0D);
		this.zLoc = MathHelper.floor(entity.locZ * 32.0D);
		this.yRot = MathHelper.d(entity.yaw * 256.0F / 360.0F);
		this.xRot = MathHelper.d(entity.pitch * 256.0F / 360.0F);
		this.lastHeadYaw = MathHelper.d(entity.getHeadRotation() * 256.0F / 360.0F);
		this.lastOnGround = entity.onGround;

		if (WindSpigotConfig.disableTracking) {
			this.addRemoveRate = 100;
		} else if (this.tracker instanceof EntityArrow || this.tracker instanceof EntityProjectile) {
			this.addRemoveRate = 5; // projectile things
		} else if (this.tracker instanceof EntityPlayer) {
			this.addRemoveRate = 5; // players
		} else {
			this.addRemoveRate = 10; // default
		}
		this.addRemoveCooldown = this.tracker.getId() % addRemoveRate;
	}

	// Constructor is used by plugins via NMS
	// WindSpigot - re add removed method used by Citizens
	public EntityTrackerEntry(Entity entity, int b, int c, boolean flag) {

		// WindSpigot Start - get the entity's tracker from it's dimension
		CraftServer server = (CraftServer) Bukkit.getServer();
		WorldServer worldServer = server.getServer().getWorldServer(entity.dimension);
		EntityTracker entityTracker = worldServer.getTracker();
		// WindSpigot End

		// Code from above constructor

		this.entityTracker = entityTracker;
		this.tracker = entity;
		this.b = b;
		this.c = c;
		this.u = flag;
		this.xLoc = MathHelper.floor(entity.locX * 32.0D);
		this.yLoc = MathHelper.floor(entity.locY * 32.0D);
		this.zLoc = MathHelper.floor(entity.locZ * 32.0D);
		this.yRot = MathHelper.d(entity.yaw * 256.0F / 360.0F);
		this.xRot = MathHelper.d(entity.pitch * 256.0F / 360.0F);
		this.lastHeadYaw = MathHelper.d(entity.getHeadRotation() * 256.0F / 360.0F);
		this.lastOnGround = entity.onGround;
		
		// WindSpigot - async entity tracking
		if (WindSpigotConfig.disableTracking) {
			this.addRemoveRate = 100;
		} else if (this.tracker instanceof EntityArrow || this.tracker instanceof EntityProjectile) {
			this.addRemoveRate = 5; // projectile things
		} else if (this.tracker instanceof EntityPlayer) {
			this.addRemoveRate = 5; // players
		} else {
			this.addRemoveRate = 10; // default
		}
		this.addRemoveCooldown = this.tracker.getId() % addRemoveRate;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof EntityTrackerEntry) {
			return ((EntityTrackerEntry) object).tracker.getId() == this.tracker.getId();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.tracker.getId();
	}
	
	public void update() {
	// FalchusSpigot start
		update(0);
	}

	public void update(int trackerThread) {
		this.trackerThread = trackerThread;
	// FalchusSpigot end
		this.withinNoTrack = this.withinNoTrack();
		if (--this.addRemoveCooldown <= 0) {
			this.removeFarPlayers();
			this.addNearPlayers(trackerThread, false);
			this.addRemoveCooldown = this.addRemoveRate;
		}

		this.track(null);
	}

	private void removeFarPlayers() {
		if (this.withinNoTrack) {
			toRemove.addAll(this.trackedPlayers);
			processToRemove();
			return;
		}

		for (EntityPlayer entityplayer : trackedPlayers) {
			double d0 = entityplayer.locX - this.tracker.locX;
			double d1 = entityplayer.locZ - this.tracker.locZ;
			int range = this.getRange();

			if (!(d0 >= (-range) && d0 <= range && d1 >= (-range) && d1 <= range) || withinNoTrack()) {
				toRemove.add(entityplayer);
			}
		}

		this.processToRemove();
	}

	public void processToRemove() {
		for (EntityPlayer entityPlayer : toRemove) {
			entityPlayer.d(this.tracker);
			this.trackedPlayers.remove(entityPlayer);
		}

		toRemove.clear();
	}

	public void addNearPlayers() {
	// FalchusSpigot start
		addNearPlayers(0, true);
	}

	private void addNearPlayers(int trackerThread, boolean immediate) {
		addNearPlayers(trackerThread, immediate, false);
	}

	private void addNearPlayers(int trackerThread, boolean immediate, boolean updateCooldown) {
	// FalchusSpigot end
		if (this.withinNoTrack) {
			return;
		}
		if (updateCooldown) {
			this.addRemoveCooldown = addRemoveRate;
		}
		// FalchusSpigot start
		this.addNearPlayersTrackerThread = trackerThread;
		this.addNearPlayersImmediate = immediate;
		// FalchusSpigot end
		this.tracker.world.playerMap.forEachNearby(this.tracker.locX, this.tracker.locY, this.tracker.locZ,
				this.getRange(), false, addNearPlayersConsumer);
	}

	private int addNearPlayersTrackerThread;
	private boolean addNearPlayersImmediate;

	private boolean withinNoTrack() {
		return this.withinNoTrack(this.tracker);
	}

	private boolean withinNoTrack(Entity entity) {
		if (!(entity instanceof EntityPlayer)) {
			return false; // ensure all non-players are always tracked
		}
		double xDistSqrd = entity.locX * entity.locX;
		double zDistSqrd = entity.locZ * entity.locZ;

		int noTrackDistanceSqrd = entityTracker.getNoTrackDistance() * entityTracker.getNoTrackDistance();
		return noTrackDistanceSqrd != 0 && xDistSqrd <= noTrackDistanceSqrd && zDistSqrd <= noTrackDistanceSqrd;
	}

	private final Consumer<EntityPlayer> addNearPlayersConsumer = new Consumer<EntityPlayer>() {

		@Override
		public void accept(EntityPlayer entityPlayer) {
			updatePlayer(entityPlayer, addNearPlayersTrackerThread, addNearPlayersImmediate);
		}
	};

	/**
	 * sends velocity, Location, rotation, and riding info.
	 */
	public void track(List<EntityHuman> list) {
		// FalchusSpigot start
		Packet<?> vehicleUpdate = null;
		Packet<?> movementPacketUpdate = null;
		Packet<?> velocityPacketUpdate = null;
		Packet<?> headYawUpdate = null;
		// FalchusSpigot end
		this.n = false;
		if (!this.isMoving || this.tracker.distanceSqured(this.posX, this.posY, this.posZ) > 16.0D) {
			this.posX = this.tracker.locX;
			this.posY = this.tracker.locY;
			this.posZ = this.tracker.locZ;
			this.isMoving = true;
			this.n = true;
			// this.scanPlayers(list);
		}

		if (this.lastRecoredRider != this.tracker.vehicle || this.tracker.vehicle != null && this.tickCount % 60 == 0) {
			this.lastRecoredRider = this.tracker.vehicle;
			vehicleUpdate = new PacketPlayOutAttachEntity(0, this.tracker, this.tracker.vehicle);
		}

		if (this.tracker instanceof EntityItemFrame && this.tickCount % 20 == 0) { // Paper
			EntityItemFrame entityitemframe = (EntityItemFrame) this.tracker;
			ItemStack itemstack = entityitemframe.getItem();

			if (itemstack != null && itemstack.getItem() instanceof ItemWorldMap) { // Paper - moved back up
				WorldMap worldmap = Items.FILLED_MAP.getSavedMap(itemstack, this.tracker.world);
				Iterator iterator = this.trackedPlayers.iterator(); // CraftBukkit

				while (iterator.hasNext()) {
					EntityHuman entityhuman = (EntityHuman) iterator.next();
					EntityPlayer entityplayer = (EntityPlayer) entityhuman;

					worldmap.a(entityplayer, itemstack);
					Packet packet = Items.FILLED_MAP.c(itemstack, this.tracker.world, entityplayer);

					if (packet != null) {
						entityplayer.playerConnection.queuePacket(packet, trackerThread);
					}
				}
			}

			this.b();
		}

		if (this.tickCount % this.c == 0 || this.tracker.ai || this.tracker.getDataWatcher().a()) {
			int i;
			int j;

			if (this.tracker.vehicle == null) {
				++this.ticksSinceLastForcedTeleport;
				i = MathHelper.floor(this.tracker.locX * 32.0D);
				j = MathHelper.floor(this.tracker.locY * 32.0D);
				int k = MathHelper.floor(this.tracker.locZ * 32.0D);
				int l = MathHelper.d(this.tracker.yaw * 256.0F / 360.0F);
				int i1 = MathHelper.d(this.tracker.pitch * 256.0F / 360.0F);
				int j1 = i - this.xLoc;
				int k1 = j - this.yLoc;
				int l1 = k - this.zLoc;
				Object object = null;
				boolean flag = Math.abs(j1) >= 4 || Math.abs(k1) >= 4 || Math.abs(l1) >= 4 || this.tickCount % 60 == 0;
				boolean flag1 = Math.abs(l - this.yRot) >= 4 || Math.abs(i1 - this.xRot) >= 4;

				if (this.tickCount > 0 || this.tracker instanceof EntityArrow) { // PaperSpigot - Moved up
																					// CraftBukkit start - Code moved
																					// from below
					if (flag) {
						this.xLoc = i;
						this.yLoc = j;
						this.zLoc = k;
					}

					if (flag1) {
						this.yRot = l;
						this.xRot = i1;
					}
					// CraftBukkit end

					if (j1 >= -128 && j1 < 128 && k1 >= -128 && k1 < 128 && l1 >= -128 && l1 < 128
							&& this.ticksSinceLastForcedTeleport <= 400 && !this.ridingEntity
							&& this.lastOnGround == this.tracker.onGround) {
						if ((!flag || !flag1) && !(this.tracker instanceof EntityArrow)) {
							if (flag) {
								object = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(this.tracker.getId(),
										(byte) j1, (byte) k1, (byte) l1, this.tracker.onGround);
							} else if (flag1) {
								object = new PacketPlayOutEntity.PacketPlayOutEntityLook(this.tracker.getId(), (byte) l,
										(byte) i1, this.tracker.onGround);
							}
						} else {
							object = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(this.tracker.getId(),
									(byte) j1, (byte) k1, (byte) l1, (byte) l, (byte) i1, this.tracker.onGround);
						}
					} else {
						this.lastOnGround = this.tracker.onGround;
						this.ticksSinceLastForcedTeleport = 0;
						// CraftBukkit start - Refresh list of who can see a player before sending
						// teleport packet
						/*
						 * if (this.tracker instanceof EntityPlayer) { // SportPaper - Fix invisibility
						 * on teleport this.scanPlayers(new ArrayList<>(this.tracker.world.players)); }
						 */
						// CraftBukkit end
						object = new PacketPlayOutEntityTeleport(this.tracker.getId(), i, j, k, (byte) l, (byte) i1,
								this.tracker.onGround);
					}
				}

				if (this.u) {
					double d0 = this.tracker.motX - this.motionX;
					double d1 = this.tracker.motY - this.motionY;
					double d2 = this.tracker.motZ - this.motionZ;
					double d3 = 0.02D;
					double d4 = d0 * d0 + d1 * d1 + d2 * d2;

					if (d4 > d3 * d3 || d4 > 0.0D && this.tracker.motX == 0.0D && this.tracker.motY == 0.0D
							&& this.tracker.motZ == 0.0D) {
						this.motionX = this.tracker.motX;
						this.motionY = this.tracker.motY;
						this.motionZ = this.tracker.motZ;
						velocityPacketUpdate = new PacketPlayOutEntityVelocity(this.tracker.getId(), this.motionX, this.motionY,
								this.motionZ);
					}
				}

				if (object != null) {
					// PaperSpigot start - ensure fresh viewers get an absolute position on their
					// first update,
					// since we can't be certain what position they received in the spawn packet.
					if (object instanceof PacketPlayOutEntityTeleport) {
						movementPacketUpdate = (Packet) object;
					} else {
						PacketPlayOutEntityTeleport teleportPacket = null;

						for (java.util.Map.Entry<EntityPlayer, Boolean> viewer : trackedPlayerMap.entrySet()) {
							if (viewer.getValue()) {
								viewer.setValue(false);
								if (teleportPacket == null) {
									teleportPacket = new PacketPlayOutEntityTeleport(this.tracker.getId(), i, j, k,
											(byte) l, (byte) i1, this.tracker.onGround);
								}
								viewer.getKey().playerConnection.queuePacket(teleportPacket, trackerThread);
							} else {
								viewer.getKey().playerConnection.queuePacket((Packet) object, trackerThread);
							}
						}
					}
					// PaperSpigot end
				}

				this.b();
				/*
				 * CraftBukkit start - Code moved up if (flag) { this.xLoc = i; this.yLoc = j;
				 * this.zLoc = k; }
				 * 
				 * if (flag1) { this.yRot = l; this.xRot = i1; } // CraftBukkit end
				 */

				this.ridingEntity = false;
			} else {
				i = MathHelper.d(this.tracker.yaw * 256.0F / 360.0F);
				j = MathHelper.d(this.tracker.pitch * 256.0F / 360.0F);
				boolean flag2 = Math.abs(i - this.yRot) >= 4 || Math.abs(j - this.xRot) >= 4;

				if (flag2) {
					movementPacketUpdate = new PacketPlayOutEntity.PacketPlayOutEntityLook(this.tracker.getId(), (byte) i,
							(byte) j, this.tracker.onGround);
					this.yRot = i;
					this.xRot = j;
				}

				this.xLoc = MathHelper.floor(this.tracker.locX * 32.0D);
				this.yLoc = MathHelper.floor(this.tracker.locY * 32.0D);
				this.zLoc = MathHelper.floor(this.tracker.locZ * 32.0D);
				this.b();
				this.ridingEntity = true;
			}

			i = MathHelper.d(this.tracker.getHeadRotation() * 256.0F / 360.0F);
			if (Math.abs(i - this.lastHeadYaw) >= 4) {
				headYawUpdate = new PacketPlayOutEntityHeadRotation(this.tracker, (byte) i);
				this.lastHeadYaw = i;
			}

			this.tracker.ai = false;
		}

		++this.tickCount;
		if (this.tracker.velocityChanged) {
			// CraftBukkit start - Create PlayerVelocity event
			boolean cancelled = false;

			if (this.tracker instanceof EntityPlayer) {
				Player player = (Player) this.tracker.getBukkitEntity();
				org.bukkit.util.Vector velocity = player.getVelocity();

				PlayerVelocityEvent event = new PlayerVelocityEvent(player, velocity.clone());
				this.tracker.world.getServer().getPluginManager().callEvent(event);

				if (event.isCancelled()) {
					cancelled = true;
				} else if (!velocity.equals(event.getVelocity())) {
					player.setVelocity(event.getVelocity());
				}
			}

			if (!cancelled) {
				this.broadcastIncludingSelfInternal(new PacketPlayOutEntityVelocity(this.tracker));
			}
			// CraftBukkit end
			this.tracker.velocityChanged = false;
		}

		// FalchusSpigot start
		if (vehicleUpdate != null || movementPacketUpdate != null || velocityPacketUpdate != null || headYawUpdate != null) {
			queueToWatchers(trackerThread, vehicleUpdate, movementPacketUpdate, velocityPacketUpdate, headYawUpdate);
		}
		// FalchusSpigot end
	}

	private void b() {
		DataWatcher datawatcher = this.tracker.getDataWatcher();

		if (datawatcher.a()) {
			this.broadcastIncludingSelfInternal(new PacketPlayOutEntityMetadata(this.tracker.getId(), datawatcher, false));
		}

		if (this.tracker instanceof EntityLiving) {
			AttributeMapServer attributemapserver = (AttributeMapServer) ((EntityLiving) this.tracker)
					.getAttributeMap();
			Set set = attributemapserver.getAttributes();

			if (!set.isEmpty()) {
				// CraftBukkit start - Send scaled max health
				if (this.tracker instanceof EntityPlayer) {
					((EntityPlayer) this.tracker).getBukkitEntity().injectScaledMaxHealth(set, false);
				}
				// CraftBukkit end
				this.broadcastIncludingSelfInternal(new PacketPlayOutUpdateAttributes(this.tracker.getId(), set));
			}

			set.clear();
		}

	}

	public void broadcast(Packet packet) {
		Iterator iterator = this.trackedPlayers.iterator();

		while (iterator.hasNext()) {
			EntityPlayer entityplayer = (EntityPlayer) iterator.next();

			entityplayer.playerConnection.sendPacket(packet);
		}

	}
	
	// FalchusSpigot start
	private void queueToWatchers(int trackerThread, Packet<?>... packets) {
		for (EntityPlayer entityPlayer : trackedPlayers) {
			for (Packet<?> packet : packets) {
				if (packet != null) {
					entityPlayer.playerConnection.queuePacket(packet, trackerThread);
				}
			}
		}
	}
	
	protected void broadcastInternal(Packet<?> packet) {
		queueToWatchers(trackerThread, packet);
	}

	public void broadcastIncludingSelf(Packet packet) {
		this.broadcast(packet);
		if (this.tracker instanceof EntityPlayer) {
			((EntityPlayer) this.tracker).playerConnection.sendPacket(packet);
		}

	}

	protected void broadcastIncludingSelfInternal(Packet<?> packet) {
		this.broadcastInternal(packet);
		if (this.tracker instanceof EntityPlayer) {
			((EntityPlayer) this.tracker).playerConnection.queuePacket(packet, trackerThread);
		}
	}
	// FalchusSpigot end
	
	public void a() {
		Iterator iterator = this.trackedPlayers.iterator();

		while (iterator.hasNext()) {
			EntityPlayer entityplayer = (EntityPlayer) iterator.next();

			entityplayer.d(this.tracker);
		}

	}

	public void a(EntityPlayer entityplayer) {
		if (this.trackedPlayers.contains(entityplayer)) {
			entityplayer.d(this.tracker);
			this.trackedPlayers.remove(entityplayer);
		}

	}

	// FalchusSpigot start
	public void updatePlayer(EntityPlayer entityplayer) {
		if (entityplayer != tracker) {
			updatePlayer(entityplayer, 0, true);
		}
	}

	public void updatePlayer(EntityPlayer entityplayer, int trackerThread, boolean immediate) {
	// FalchusSpigot end
		// org.spigotmc.AsyncCatcher.catchOp( "player tracker update"); // Spigot
		if (entityplayer != this.tracker) {
			boolean isPlayerEntityTracked = this.trackedPlayers.contains(entityplayer);
			if (this.c(entityplayer)) {
				if (!isPlayerEntityTracked && (this.e(entityplayer) || this.tracker.attachedToPlayer)) {
					if (this.tracker instanceof EntityPlayer && withinNoTrack()) {
						return;
					}
					// CraftBukkit start - respect vanish API
					if (!entityplayer.getBukkitEntity().canSee(this.tracker.getBukkitEntity())) {
						return;
					}

					// entityplayer.removeQueue.remove(Integer.valueOf(this.tracker.getId()));
					// CraftBukkit end
					List<Packet<?>> queue = immediate ? new ArrayList<>(10) : null; // FalchusSpigot
					Packet packet = this.c();
					if (packet == null) return; // FalchusSpigot

					this.trackedPlayerMap.put(entityplayer, true); // PaperBukkit // FalchusSpigot - after null check

					queuePacket(entityplayer, packet, trackerThread, immediate, queue);
					if (!this.tracker.getDataWatcher().d()) {
						queuePacket(entityplayer, new PacketPlayOutEntityMetadata(this.tracker.getId(),
								this.tracker.getDataWatcher(), true), trackerThread, immediate, queue);
					}

					NBTTagCompound nbttagcompound = this.tracker.getNBTTag();

					if (nbttagcompound != null) {
						queuePacket(entityplayer, new PacketPlayOutUpdateEntityNBT(this.tracker.getId(), nbttagcompound), trackerThread, immediate, queue);
					}

					if (this.tracker instanceof EntityLiving) {
						AttributeMapServer attributemapserver = (AttributeMapServer) ((EntityLiving) this.tracker)
								.getAttributeMap();
						Collection<AttributeInstance> collection = attributemapserver.c();

						// CraftBukkit start - If sending own attributes send scaled health instead of
						// current maximum health
						if (this.tracker.getId() == entityplayer.getId()) {
							((EntityPlayer) this.tracker).getBukkitEntity().injectScaledMaxHealth(collection, false);
						}
						// CraftBukkit end

						if (!collection.isEmpty()) {
							queuePacket(entityplayer, new PacketPlayOutUpdateAttributes(this.tracker.getId(), collection), trackerThread, immediate, queue);
						}
					}

					this.motionX = this.tracker.motX;
					this.motionY = this.tracker.motY;
					this.motionZ = this.tracker.motZ;

					if (this.u && !(packet instanceof PacketPlayOutSpawnEntityLiving)) {
						queuePacket(entityplayer, new PacketPlayOutEntityVelocity(this.tracker.getId(),
								this.tracker.motX, this.tracker.motY, this.tracker.motZ), trackerThread, immediate, queue);
					}

					if (this.tracker.vehicle != null) {
						queuePacket(entityplayer, new PacketPlayOutAttachEntity(0, this.tracker, this.tracker.vehicle), trackerThread, immediate, queue);
					}

					if (this.tracker instanceof EntityInsentient
							&& ((EntityInsentient) this.tracker).getLeashHolder() != null) {
						queuePacket(entityplayer, new PacketPlayOutAttachEntity(1, this.tracker,
								((EntityInsentient) this.tracker).getLeashHolder()), trackerThread, immediate, queue);
					}

					if (this.tracker instanceof EntityLiving) {
						for (int i = 0; i < 5; ++i) {
							ItemStack itemstack = ((EntityLiving) this.tracker).getEquipment(i);
							if (itemstack != null) {
								queuePacket(entityplayer,
										new PacketPlayOutEntityEquipment(this.tracker.getId(), i, itemstack), trackerThread, immediate, queue);
							}
						}
					}

					if (this.tracker instanceof EntityHuman) {
						EntityHuman entityhuman = (EntityHuman) this.tracker;
						if (entityhuman.isSleeping()) {
							queuePacket(entityplayer, new PacketPlayOutBed(entityhuman, new BlockPosition(this.tracker)), trackerThread, immediate, queue);
						}
					}

					// CraftBukkit start - Fix for nonsensical head yaw
					if (this.tracker instanceof EntityLiving) { // SportPaper - avoid processing entities that can't
																// change head rotation
						this.lastHeadYaw = MathHelper.d(this.tracker.getHeadRotation() * 256.0F / 360.0F);
						// SportPaper start
						// This was originally introduced by CraftBukkit, though the implementation is
						// wrong since it's broadcasting
						// the packet again in a method that is already called for each player. This
						// would create a very serious performance issue
						// with high player and entity counts (each sendPacket call involves waking up
						// the event loop and flushing the network stream).
						// this.broadcast(new PacketPlayOutEntityHeadRotation(this.tracker, (byte)
						// lastHeadYaw));
						queuePacket(entityplayer, new PacketPlayOutEntityHeadRotation(this.tracker, (byte) lastHeadYaw), trackerThread, immediate, queue);
						// SportPaper end
					}
					// CraftBukkit end

					if (this.tracker instanceof EntityLiving) {
						EntityLiving entityliving = (EntityLiving) this.tracker;
						for (MobEffect mobeffect : entityliving.getEffects()) {
							queuePacket(entityplayer, new PacketPlayOutEntityEffect(this.tracker.getId(), mobeffect), trackerThread, immediate, queue);
						}
					}

					// FalchusSpigot start
					if (immediate && queue != null && !queue.isEmpty()) {
						entityplayer.playerConnection.sendPackets(queue, trackerThread);
					}
					// FalchusSpigot end
				}
			} else if (isPlayerEntityTracked) {
				this.trackedPlayers.remove(entityplayer);
				entityplayer.d(this.tracker);
			}

		}
	}

	// FalchusSpigot start
	protected void queuePacket(EntityPlayer entityplayer, Packet<?> packet, int trackerThread, boolean immediate, List<Packet<?>> queue) {
		if (packet == null) return;
		if (immediate) {
			queue.add(packet);
		} else {
			entityplayer.playerConnection.queuePacket(packet, trackerThread);
		}
	}
	// FalchusSpigot end

	public boolean c(EntityPlayer entityplayer) {
		// CraftBukkit start - this.*Loc / 30 -> this.tracker.loc*
		double d0 = entityplayer.locX - this.tracker.locX;
		double d1 = entityplayer.locZ - this.tracker.locZ;
		// CraftBukkit end

		return d0 >= (-this.b) && d0 <= this.b && d1 >= (-this.b) && d1 <= this.b && this.tracker.a(entityplayer);
	}

	protected boolean e(EntityPlayer entityplayer) { // IonSpigot - private -> protected
		return entityplayer.u().getPlayerChunkMap().a(entityplayer, this.tracker.ae, this.tracker.ag);
	}

	/*
	 * public void scanPlayers(List<EntityHuman> list) { for (EntityHuman
	 * entityHuman : list) { // Don't update if player cannot see entity if
	 * (!((EntityPlayer)
	 * entityHuman).getBukkitEntity().canSee((this.tracker).getBukkitEntity())) {
	 * return; } this.updatePlayer((EntityPlayer) entityHuman); }
	 * 
	 * }
	 */

	protected Packet c() {
		if (this.tracker.dead) {
			// CraftBukkit start - Remove useless error spam, just return
			// EntityTrackerEntry.p.warn("Fetching addPacket for removed entity");
			return null;
			// CraftBukkit end
		}

		if (this.tracker instanceof EntityItem) {
			return new PacketPlayOutSpawnEntity(this.tracker, 2, 1);
		} else if (this.tracker instanceof EntityPlayer) {
			return new PacketPlayOutNamedEntitySpawn((EntityHuman) this.tracker);
		} else if (this.tracker instanceof EntityMinecartAbstract) {
			EntityMinecartAbstract entityminecartabstract = (EntityMinecartAbstract) this.tracker;

			return new PacketPlayOutSpawnEntity(this.tracker, 10, entityminecartabstract.s().a());
		} else if (this.tracker instanceof EntityBoat) {
			return new PacketPlayOutSpawnEntity(this.tracker, 1);
		} else if (this.tracker instanceof IAnimal) {
			this.lastHeadYaw = MathHelper.d(this.tracker.getHeadRotation() * 256.0F / 360.0F);
			return new PacketPlayOutSpawnEntityLiving((EntityLiving) this.tracker);
		} else if (this.tracker instanceof EntityFishingHook) {
			EntityHuman entityhuman = ((EntityFishingHook) this.tracker).owner;

			return new PacketPlayOutSpawnEntity(this.tracker, 90,
					entityhuman != null ? entityhuman.getId() : this.tracker.getId());
		} else if (this.tracker instanceof EntityArrow) {
			Entity entity = ((EntityArrow) this.tracker).shooter;

			return new PacketPlayOutSpawnEntity(this.tracker, 60,
					entity != null ? entity.getId() : this.tracker.getId());
		} else if (this.tracker instanceof EntitySnowball) {
			return new PacketPlayOutSpawnEntity(this.tracker, 61);
		} else if (this.tracker instanceof EntityPotion) {
			return new PacketPlayOutSpawnEntity(this.tracker, 73, ((EntityPotion) this.tracker).getPotionValue());
		} else if (this.tracker instanceof EntityThrownExpBottle) {
			return new PacketPlayOutSpawnEntity(this.tracker, 75);
		} else if (this.tracker instanceof EntityEnderPearl) {
			return new PacketPlayOutSpawnEntity(this.tracker, 65);
		} else if (this.tracker instanceof EntityEnderSignal) {
			return new PacketPlayOutSpawnEntity(this.tracker, 72);
		} else if (this.tracker instanceof EntityFireworks) {
			return new PacketPlayOutSpawnEntity(this.tracker, 76);
		} else {
			PacketPlayOutSpawnEntity packetplayoutspawnentity;

			if (this.tracker instanceof EntityFireball) {
				EntityFireball entityfireball = (EntityFireball) this.tracker;

				packetplayoutspawnentity = null;
				byte b0 = 63;

				if (this.tracker instanceof EntitySmallFireball) {
					b0 = 64;
				} else if (this.tracker instanceof EntityWitherSkull) {
					b0 = 66;
				}

				if (entityfireball.shooter != null) {
					packetplayoutspawnentity = new PacketPlayOutSpawnEntity(this.tracker, b0,
							((EntityFireball) this.tracker).shooter.getId());
				} else {
					packetplayoutspawnentity = new PacketPlayOutSpawnEntity(this.tracker, b0, 0);
				}

				packetplayoutspawnentity.d((int) (entityfireball.dirX * 8000.0D));
				packetplayoutspawnentity.e((int) (entityfireball.dirY * 8000.0D));
				packetplayoutspawnentity.f((int) (entityfireball.dirZ * 8000.0D));
				return packetplayoutspawnentity;
			} else if (this.tracker instanceof EntityEgg) {
				return new PacketPlayOutSpawnEntity(this.tracker, 62);
			} else if (this.tracker instanceof EntityTNTPrimed) {
				return new PacketPlayOutSpawnEntity(this.tracker, 50);
			} else if (this.tracker instanceof EntityEnderCrystal) {
				return new PacketPlayOutSpawnEntity(this.tracker, 51);
			} else if (this.tracker instanceof EntityFallingBlock) {
				EntityFallingBlock entityfallingblock = (EntityFallingBlock) this.tracker;

				return new PacketPlayOutSpawnEntity(this.tracker, 70,
						Block.getCombinedId(entityfallingblock.getBlock()));
			} else if (this.tracker instanceof EntityArmorStand) {
				return new PacketPlayOutSpawnEntity(this.tracker, 78);
			} else if (this.tracker instanceof EntityPainting) {
				return new PacketPlayOutSpawnEntityPainting((EntityPainting) this.tracker);
			} else {
				BlockPosition blockposition;

				if (this.tracker instanceof EntityItemFrame) {
					EntityItemFrame entityitemframe = (EntityItemFrame) this.tracker;

					packetplayoutspawnentity = new PacketPlayOutSpawnEntity(this.tracker, 71,
							entityitemframe.direction.b());
					blockposition = entityitemframe.getBlockPosition();
					packetplayoutspawnentity.a(MathHelper.d(blockposition.getX() * 32));
					packetplayoutspawnentity.b(MathHelper.d(blockposition.getY() * 32));
					packetplayoutspawnentity.c(MathHelper.d(blockposition.getZ() * 32));
					return packetplayoutspawnentity;
				} else if (this.tracker instanceof EntityLeash) {
					EntityLeash entityleash = (EntityLeash) this.tracker;

					packetplayoutspawnentity = new PacketPlayOutSpawnEntity(this.tracker, 77);
					blockposition = entityleash.getBlockPosition();
					packetplayoutspawnentity.a(MathHelper.d(blockposition.getX() * 32));
					packetplayoutspawnentity.b(MathHelper.d(blockposition.getY() * 32));
					packetplayoutspawnentity.c(MathHelper.d(blockposition.getZ() * 32));
					return packetplayoutspawnentity;
				} else if (this.tracker instanceof EntityExperienceOrb) {
					return new PacketPlayOutSpawnEntityExperienceOrb((EntityExperienceOrb) this.tracker);
				} else {
					throw new IllegalArgumentException("Don\'t know how to add " + this.tracker.getClass() + "!");
				}
			}
		}
	}

	public int getRange() {
		if (this.tracker.passenger == null) {
			return this.b;
		}
		return Math.max(this.b, org.spigotmc.TrackingRange.getEntityTrackingRange(this.tracker.passenger, 0));
	}

	public void clear(EntityPlayer entityplayer) {
		org.spigotmc.AsyncCatcher.catchOp("player tracker clear"); // Spigot
		if (this.trackedPlayers.remove(entityplayer)) {
			entityplayer.d(this.tracker);
		}

	}
}
