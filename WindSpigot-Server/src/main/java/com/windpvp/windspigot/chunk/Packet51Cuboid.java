package com.windpvp.windspigot.chunk;

/**
 * Packet51 (pre-Anvil / early CraftBukkit) cuboid map-chunk payload helpers.
 * <p>
 * Bukkit {@code Player#sendChunkChange} documents a buffer of length
 * {@code (sx * sy * sz * 5) / 2} in Packet51 format:
 * <ul>
 * <li>{@code size} bytes of block IDs</li>
 * <li>{@code size / 2} bytes of metadata nibbles</li>
 * <li>{@code size / 2} bytes of block-light nibbles</li>
 * <li>{@code size / 2} bytes of sky-light nibbles</li>
 * </ul>
 * Block storage order matches beta Minecraft region updates: for each local
 * {@code (dx, dz)} column, {@code dy} varies fastest —
 * {@code index = dy + sy * (dz + sz * dx)}.
 * <p>
 * 1.8 no longer has a cuboid Packet51; callers convert block ID + metadata into
 * {@code PacketPlayOutBlockChange} (or equivalent) for the client.
 */
public final class Packet51Cuboid {

	/** Anvil / 1.8 world height (original CraftBukkit stub used 128). */
	public static final int MAX_WORLD_HEIGHT = 256;

	private Packet51Cuboid() {
	}

	/**
	 * Expected Packet51 cuboid buffer length for the given dimensions.
	 * Requires {@code sx * sy * sz} even so nibble sections pack cleanly
	 * (Bukkit: at least one dimension must be even).
	 */
	public static int expectedDataLength(int sx, int sy, int sz) {
		return (sx * sy * sz * 5) / 2;
	}

	/**
	 * Validates location, cuboid size, single-chunk constraint, height, and
	 * Packet51 buffer length. Returns {@code false} when the change must not be
	 * sent (same contract as the historical CraftBukkit stub).
	 */
	public static boolean isValid(int blockX, int blockY, int blockZ, int sx, int sy, int sz, byte[] data) {
		if (data == null) {
			return false;
		}
		if (sx <= 0 || sy <= 0 || sz <= 0) {
			return false;
		}

		int size = sx * sy * sz;
		if ((size & 1) != 0) {
			// Odd volume cannot pack nibble arrays to an integer byte length of 2.5*size
			return false;
		}

		int cx = blockX >> 4;
		int cz = blockZ >> 4;
		if ((blockX + sx - 1) >> 4 != cx || (blockZ + sz - 1) >> 4 != cz) {
			return false;
		}
		if (blockY < 0 || blockY + sy > MAX_WORLD_HEIGHT) {
			return false;
		}
		return data.length == expectedDataLength(sx, sy, sz);
	}

	/** Packet51 column-major index: Y fastest, then Z, then X. */
	public static int blockIndex(int dx, int dy, int dz, int sy, int sz) {
		return dy + sy * (dz + sz * dx);
	}

	/**
	 * Reads the metadata nibble for block index {@code index} (0 .. size-1).
	 * Metadata section starts immediately after the block-ID section.
	 */
	public static int metadata(byte[] data, int size, int index) {
		return nibble(data, size, index);
	}

	/**
	 * Reads a nibble from a Packet51 nibble section starting at
	 * {@code sectionOffset}. Even indices use the low nibble; odd use the high
	 * (same packing as {@code NibbleArray}).
	 */
	public static int nibble(byte[] data, int sectionOffset, int index) {
		int byteIndex = sectionOffset + (index >> 1);
		int shift = (index & 1) << 2;
		return (data[byteIndex] >> shift) & 15;
	}

	/**
	 * Unsigned block ID at the given Packet51 block index.
	 */
	public static int blockId(byte[] data, int index) {
		return data[index] & 0xFF;
	}

	/**
	 * Iterates every block in the cuboid in Packet51 storage order.
	 */
	public static void forEachBlock(int originX, int originY, int originZ, int sx, int sy, int sz, byte[] data,
			BlockVisitor visitor) {
		int size = sx * sy * sz;
		for (int dx = 0; dx < sx; dx++) {
			for (int dz = 0; dz < sz; dz++) {
				for (int dy = 0; dy < sy; dy++) {
					int index = blockIndex(dx, dy, dz, sy, sz);
					visitor.accept(originX + dx, originY + dy, originZ + dz, blockId(data, index),
							metadata(data, size, index));
				}
			}
		}
	}

	@FunctionalInterface
	public interface BlockVisitor {
		void accept(int x, int y, int z, int blockId, int metadata);
	}
}
