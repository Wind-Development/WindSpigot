package me.rastrian.dev;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MathHelper;
import net.minecraft.server.Packet;

public class PlayerMap {

	private static final int CHUNK_BITS = 5;
	private final Long2ObjectMap<List<EntityPlayer>> map = new Long2ObjectArrayMap<>();

	private static long xzToKey(long x, long z) {
		return (x << 32) + z - Integer.MIN_VALUE;
	}

	public void add(EntityPlayer player) {
		int x = MathHelper.floor(player.locX) >> CHUNK_BITS;
		int z = MathHelper.floor(player.locZ) >> CHUNK_BITS;
		long key = xzToKey(x, z);
		List<EntityPlayer> list = map.get(key);
		if (list == null) {
			list = new ArrayList<>();
			map.put(key, list);
		}
		list.add(player);
		player.playerMapX = x;
		player.playerMapZ = z;
	}

	public void move(EntityPlayer player) {
		int x = MathHelper.floor(player.locX) >> CHUNK_BITS;
		int z = MathHelper.floor(player.locZ) >> CHUNK_BITS;

		// did we move?
		if (x == player.playerMapX && z == player.playerMapZ) {
			return;
		}

		// do remove
		long key = xzToKey(player.playerMapX, player.playerMapZ);
		List<EntityPlayer> list = map.get(key);
		list.remove(player);
		if (list.isEmpty()) {
			map.remove(key);
		}

		// do add
		key = xzToKey(x, z);
		list = map.get(key);
		if (list == null) {
			list = new ArrayList<>();
			map.put(key, list);
		}
		list.add(player);
		player.playerMapX = x;
		player.playerMapZ = z;
	}

	public void remove(EntityPlayer player) {
		long key = xzToKey(player.playerMapX, player.playerMapZ);
		List<EntityPlayer> list = map.get(key);
		if (list == null) {
			// player has not yet been added to this playermap, this happens when
			// teleporting to another world during PlayerJoinEvent
			return;
		}
		list.remove(player);
		if (list.isEmpty()) {
			map.remove(key);
		}
	}

	public void forEachNearby(double x, double y, double z, double distance, boolean useRadius, Consumer<EntityPlayer> function) {
		
		int chunkXMax = MathHelper.floor(x + distance) >> CHUNK_BITS;
		int chunkZMax = MathHelper.floor(z + distance) >> CHUNK_BITS;
		
		for (int chunkX = MathHelper.floor(x - distance) >> CHUNK_BITS; chunkX <= chunkXMax; chunkX++) {
			for (int chunkZ = MathHelper.floor(z - distance) >> CHUNK_BITS; chunkZ <= chunkZMax; chunkZ++) {
				List<EntityPlayer> players = map.get(xzToKey(chunkX, chunkZ));
				if (players != null) {
					for (EntityPlayer player : players) {
						if (!useRadius || player.e(x, y, z) < distance * distance) {
							function.accept(player);
						}
					}
				}
			}
		}
	}

	public EntityPlayer getNearestPlayer(double x, double y, double z, double distance) {
		double bestDistanceSqrd = -1.0;
		EntityPlayer bestPlayer = null;
		
		int chunkXMax = MathHelper.floor(x + distance) >> CHUNK_BITS;
		int chunkZMax = MathHelper.floor(z + distance) >> CHUNK_BITS;
		
		for (int chunkX = MathHelper.floor(x - distance) >> CHUNK_BITS; chunkX <= chunkXMax; chunkX++) {
			for (int chunkZ = MathHelper.floor(z - distance) >> CHUNK_BITS; chunkZ <= chunkZMax; chunkZ++) {
				List<EntityPlayer> players = map.get(xzToKey(chunkX, chunkZ));
				if (players != null) {
					for (EntityPlayer player : players) {
						double playerDistSqrd = player.e(x, y, z);
						if (playerDistSqrd < distance * distance
								&& (bestDistanceSqrd == -1.0 || playerDistSqrd < bestDistanceSqrd)) {
							bestDistanceSqrd = playerDistSqrd;
							bestPlayer = player;
						}
					}
				}
			}
		}
		return bestPlayer;
	}

	public boolean isPlayerNearby(double x, double y, double z, double distance, boolean respectSpawningApi) {
		
		int chunkXMax = MathHelper.floor(x + distance) >> CHUNK_BITS;
		int chunkZMax = MathHelper.floor(z + distance) >> CHUNK_BITS;
		
		for (int chunkX = MathHelper.floor(x - distance) >> CHUNK_BITS; chunkX <= chunkXMax; chunkX++) {
			for (int chunkZ = MathHelper.floor(z - distance) >> CHUNK_BITS; chunkZ <= chunkZMax; chunkZ++) {
				List<EntityPlayer> players = map.get(xzToKey(chunkX, chunkZ));
				if (players != null) {
					for (EntityPlayer player : players) {
						if (player != null && !player.dead && (!respectSpawningApi || player.affectsSpawning)) {
							double playerDistSqrd = player.e(x, y, z);
							if (playerDistSqrd < distance * distance) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public EntityPlayer getNearbyPlayer(double x, double y, double z, double distance, boolean respectSpawningApi) {
		double bestDistanceSqrd = -1.0;
		EntityPlayer bestPlayer = null;

		int chunkXMax = MathHelper.floor(x + distance) >> CHUNK_BITS;
		int chunkZMax = MathHelper.floor(z + distance) >> CHUNK_BITS;
							
		for (int chunkX = MathHelper.floor(x - distance) >> CHUNK_BITS; chunkX <= chunkXMax; chunkX++) {
			for (int chunkZ = MathHelper.floor(z - distance) >> CHUNK_BITS; chunkZ <= chunkZMax; chunkZ++) {
				List<EntityPlayer> players = map.get(xzToKey(chunkX, chunkZ));
				if (players != null) {
					for (EntityPlayer player : players) {
						if (player != null && !player.dead && (!respectSpawningApi || player.affectsSpawning)) {
							double playerDistSqrd = player.e(x, y, z);
							if (playerDistSqrd < distance * distance
									&& (bestDistanceSqrd == -1.0 || playerDistSqrd < bestDistanceSqrd)) {
								bestDistanceSqrd = playerDistSqrd;
								bestPlayer = player;
							}
						}
					}
				}
			}
		}

		return bestPlayer;
	}

	public void sendPacketNearby(EntityPlayer source, double x, double y, double z, double distance, Packet<?> packet, boolean self) {
		
		double distanceSqrd = distance * distance;
		int chunkXMax = MathHelper.floor(x + distance) >> CHUNK_BITS;
		int chunkZMax = MathHelper.floor(z + distance) >> CHUNK_BITS;	
									
		for (int chunkX = MathHelper.floor(x - distance) >> CHUNK_BITS; chunkX <= chunkXMax; chunkX++) {
			for (int chunkZ = MathHelper.floor(z - distance) >> CHUNK_BITS; chunkZ <= chunkZMax; chunkZ++) {
				List<EntityPlayer> players = map.get(xzToKey(chunkX, chunkZ));
				if (players != null) {
					for (EntityPlayer player : players) {
						// don't send self
						if (!self) {
							if (player == source) {
								continue;
							}
						}

						// bukkit visibility api
						if (source != null && !player.getBukkitEntity().canSee(source.getBukkitEntity())) {
							continue;
						}

						double playerDistSqrd = player.e(x, y, z);
						if (playerDistSqrd < distanceSqrd) {
							player.playerConnection.sendPacket(packet);
						}
					}
				}
			}
		}
	}
}