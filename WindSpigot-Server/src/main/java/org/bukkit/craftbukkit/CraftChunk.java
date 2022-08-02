package org.bukkit.craftbukkit;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;

import net.minecraft.server.BiomeBase;
import net.minecraft.server.BlockPosition;
import net.minecraft.server.ChunkSection;
import net.minecraft.server.EmptyChunk;
import net.minecraft.server.IBlockData;
import net.minecraft.server.WorldChunkManager;
import net.minecraft.server.WorldServer;

public class CraftChunk implements Chunk {
	private WeakReference<net.minecraft.server.Chunk> weakChunk;
	private final WorldServer worldServer;
	private final int x;
	private final int z;
	private static final byte[] emptyData = new byte[2048];
	private static final short[] emptyBlockIDs = new short[4096];
	private static final byte[] emptySkyLight = new byte[2048];

	public CraftChunk(net.minecraft.server.Chunk chunk) {
		if (!(chunk instanceof EmptyChunk)) {
			this.weakChunk = new WeakReference<net.minecraft.server.Chunk>(chunk);
		}

		worldServer = (WorldServer) getHandle().world;
		x = getHandle().locX;
		z = getHandle().locZ;
	}

	@Override
	public World getWorld() {
		return worldServer.getWorld();
	}

	public CraftWorld getCraftWorld() {
		return (CraftWorld) getWorld();
	}

	public net.minecraft.server.Chunk getHandle() {
		net.minecraft.server.Chunk c = weakChunk.get();

		if (c == null) {
			c = worldServer.getChunkAt(x, z);

			if (!(c instanceof EmptyChunk)) {
				weakChunk = new WeakReference<net.minecraft.server.Chunk>(c);
			}
		}

		return c;
	}

	void breakLink() {
		weakChunk.clear();
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getZ() {
		return z;
	}

	@Override
	public String toString() {
		return "CraftChunk{" + "x=" + getX() + "z=" + getZ() + '}';
	}

	@Override
	public Block getBlock(int x, int y, int z) {
		return new CraftBlock(this, (getX() << 4) | (x & 0xF), y, (getZ() << 4) | (z & 0xF));
	}

	@Override
	public Entity[] getEntities() {
		int count = 0, index = 0;
		net.minecraft.server.Chunk chunk = getHandle();

		for (int i = 0; i < 16; i++) {
			count += chunk.entitySlices[i].size();
		}

		Entity[] entities = new Entity[count];

		for (int i = 0; i < 16; i++) {
			// Paper start - speed up (was with chunk.entitySlices[i].toArray() and cast
			// checks which costs a lot of performance if called often)
			for (net.minecraft.server.Entity entity : chunk.entitySlices[i]) {
				if (entity == null) {
					continue;
				}
				entities[index++] = entity.getBukkitEntity();
			}
			// Paper end
		}

		return entities;
	}

	@Override
	public BlockState[] getTileEntities() {
		int index = 0;
		net.minecraft.server.Chunk chunk = getHandle();

		BlockState[] entities = new BlockState[chunk.tileEntities.size()];

		for (Object obj : chunk.tileEntities.keySet().toArray()) {
			if (!(obj instanceof BlockPosition)) {
				continue;
			}

			BlockPosition position = (BlockPosition) obj;
			entities[index++] = worldServer.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ())
					.getState();
		}

		return entities;
	}

	@Override
	public boolean isLoaded() {
		return getWorld().isChunkLoaded(this);
	}

	@Override
	public boolean load() {
		return getWorld().loadChunk(getX(), getZ(), true);
	}

	@Override
	public boolean load(boolean generate) {
		return getWorld().loadChunk(getX(), getZ(), generate);
	}

	@Override
	public boolean unload() {
		return getWorld().unloadChunk(getX(), getZ());
	}

	@Override
	public List<Block> getBlocks(org.bukkit.Material material) {
		List<Block> blocks = new ArrayList<>();

		net.minecraft.server.Block nmsBlock = CraftMagicNumbers.getBlock(material);
		net.minecraft.server.Chunk chunk = getHandle();

		for (ChunkSection section : chunk.getSections()) {
			if (section == null || section.a()) {
				continue; // ChunkSection.a() -> true if section is empty
			}

			char[] blockIds = section.getIdArray();
			for (int i = 0; i < blockIds.length; i++) {
				// This does a lookup in the block registry, but does not create any objects, so
				// should be pretty efficient
				IBlockData blockData = net.minecraft.server.Block.d.a(blockIds[i]);
				if (blockData.getBlock() == nmsBlock) {
					blocks.add(getBlock(i & 0xf, section.getYPosition() | (i >> 8), (i >> 4) & 0xf));
				}
			}
		}
		return blocks;
	}

	@Override
	public boolean unload(boolean save) {
		return getWorld().unloadChunk(getX(), getZ(), save);
	}

	@Override
	public boolean unload(boolean save, boolean safe) {
		return getWorld().unloadChunk(getX(), getZ(), save, safe);
	}

	@Override
	public ChunkSnapshot getChunkSnapshot() {
		return getChunkSnapshot(true, false, false);
	}

	@Override
	public ChunkSnapshot getChunkSnapshot(boolean includeMaxBlockY, boolean includeBiome,
			boolean includeBiomeTempRain) {
		net.minecraft.server.Chunk chunk = getHandle();

		ChunkSection[] cs = chunk.getSections();
		short[][] sectionBlockIDs = new short[cs.length][];
		byte[][] sectionBlockData = new byte[cs.length][];
		byte[][] sectionSkyLights = new byte[cs.length][];
		byte[][] sectionEmitLights = new byte[cs.length][];
		boolean[] sectionEmpty = new boolean[cs.length];

		for (int i = 0; i < cs.length; i++) {
			if (cs[i] == null) { // Section is empty?
				sectionBlockIDs[i] = emptyBlockIDs;
				sectionBlockData[i] = emptyData;
				sectionSkyLights[i] = emptySkyLight;
				sectionEmitLights[i] = emptyData;
				sectionEmpty[i] = true;
			} else { // Not empty
				short[] blockids = new short[4096];
				char[] baseids = cs[i].getIdArray();
				byte[] dataValues = sectionBlockData[i] = new byte[2048];

				// Copy base IDs
				for (int j = 0; j < 4096; j++) {
					if (baseids[j] == 0) {
						continue;
					}
					IBlockData blockData = net.minecraft.server.Block.d.a(baseids[j]);
					if (blockData == null) {
						continue;
					}
					blockids[j] = (short) net.minecraft.server.Block.getId(blockData.getBlock());
					int data = blockData.getBlock().toLegacyData(blockData);
					int jj = j >> 1;
					if ((j & 1) == 0) {
						dataValues[jj] = (byte) ((dataValues[jj] & 0xF0) | (data & 0xF));
					} else {
						dataValues[jj] = (byte) ((dataValues[jj] & 0xF) | ((data & 0xF) << 4));
					}
				}

				sectionBlockIDs[i] = blockids;

				if (cs[i].getSkyLightArray() == null) {
					sectionSkyLights[i] = emptyData;
				} else {
					sectionSkyLights[i] = new byte[2048];
					System.arraycopy(cs[i].getSkyLightArray().a(), 0, sectionSkyLights[i], 0, 2048);
				}
				sectionEmitLights[i] = new byte[2048];
				System.arraycopy(cs[i].getEmittedLightArray().a(), 0, sectionEmitLights[i], 0, 2048);
			}
		}

		int[] hmap = null;

		if (includeMaxBlockY) {
			hmap = new int[256]; // Get copy of height map
			System.arraycopy(chunk.heightMap, 0, hmap, 0, 256);
		}

		BiomeBase[] biome = null;
		double[] biomeTemp = null;
		double[] biomeRain = null;

		if (includeBiome || includeBiomeTempRain) {
			WorldChunkManager wcm = chunk.world.getWorldChunkManager();

			if (includeBiome) {
				biome = new BiomeBase[256];
				for (int i = 0; i < 256; i++) {
					biome[i] = chunk.getBiome(new BlockPosition(i & 0xF, 0, i >> 4), wcm);
				}
			}

			if (includeBiomeTempRain) {
				biomeTemp = new double[256];
				biomeRain = new double[256];
				float[] dat = getTemperatures(wcm, getX() << 4, getZ() << 4);

				for (int i = 0; i < 256; i++) {
					biomeTemp[i] = dat[i];
				}

				dat = wcm.getWetness(null, getX() << 4, getZ() << 4, 16, 16);

				for (int i = 0; i < 256; i++) {
					biomeRain[i] = dat[i];
				}
			}
		}

		World world = getWorld();
		return new CraftChunkSnapshot(getX(), getZ(), world.getName(), world.getFullTime(), sectionBlockIDs,
				sectionBlockData, sectionSkyLights, sectionEmitLights, sectionEmpty, hmap, biome, biomeTemp, biomeRain);
	}

	public static ChunkSnapshot getEmptyChunkSnapshot(int x, int z, CraftWorld world, boolean includeBiome,
			boolean includeBiomeTempRain) {
		BiomeBase[] biome = null;
		double[] biomeTemp = null;
		double[] biomeRain = null;

		if (includeBiome || includeBiomeTempRain) {
			WorldChunkManager wcm = world.getHandle().getWorldChunkManager();

			if (includeBiome) {
				biome = new BiomeBase[256];
				for (int i = 0; i < 256; i++) {
					biome[i] = world.getHandle()
							.getBiome(new BlockPosition((x << 4) + (i & 0xF), 0, (z << 4) + (i >> 4)));
				}
			}

			if (includeBiomeTempRain) {
				biomeTemp = new double[256];
				biomeRain = new double[256];
				float[] dat = getTemperatures(wcm, x << 4, z << 4);

				for (int i = 0; i < 256; i++) {
					biomeTemp[i] = dat[i];
				}

				dat = wcm.getWetness(null, x << 4, z << 4, 16, 16);

				for (int i = 0; i < 256; i++) {
					biomeRain[i] = dat[i];
				}
			}
		}

		/* Fill with empty data */
		int hSection = world.getMaxHeight() >> 4;
		short[][] blockIDs = new short[hSection][];
		byte[][] skyLight = new byte[hSection][];
		byte[][] emitLight = new byte[hSection][];
		byte[][] blockData = new byte[hSection][];
		boolean[] empty = new boolean[hSection];

		for (int i = 0; i < hSection; i++) {
			blockIDs[i] = emptyBlockIDs;
			skyLight[i] = emptySkyLight;
			emitLight[i] = emptyData;
			blockData[i] = emptyData;
			empty[i] = true;
		}

		return new CraftChunkSnapshot(x, z, world.getName(), world.getFullTime(), blockIDs, blockData, skyLight,
				emitLight, empty, new int[256], biome, biomeTemp, biomeRain);
	}

	private static float[] getTemperatures(WorldChunkManager chunkmanager, int chunkX, int chunkZ) {
		BiomeBase[] biomes = chunkmanager.getBiomes(null, chunkX, chunkZ, 16, 16);
		float[] temps = new float[biomes.length];

		for (int i = 0; i < biomes.length; i++) {
			float temp = biomes[i].temperature; // Vanilla of olde: ((int) biomes[i].temperature * 65536.0F) / 65536.0F

			if (temp > 1F) {
				temp = 1F;
			}

			temps[i] = temp;
		}

		return temps;
	}

	static {
		Arrays.fill(emptySkyLight, (byte) 0xFF);
	}
}
