package com.windpvp.windspigot.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import io.netty.buffer.Unpooled;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Items;
import net.minecraft.server.NBTBase;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.PacketDataSerializer;

/**
 * Validates MC|BEdit / MC|BSign book custom-payload NBT for 1.8 clients.
 * <p>
 * Bounds match CraftMetaBook / FlamePaper limits (pages, page length, title,
 * author) plus PandaSpigot-style total UTF-8 byte budgeting, and rejects
 * unexpected top-level NBT keys that are not part of a legitimate book tag.
 */
public final class BookNbtValidator {

	/** Align with {@code CraftMetaBook.MAX_PAGES}. */
	public static final int MAX_PAGES = 50;
	/** Align with {@code CraftMetaBook.MAX_PAGE_LENGTH}. */
	public static final int MAX_PAGE_LENGTH = 340;
	/** Align with {@code CraftMetaBook.MAX_TITLE_LENGTH} / vanilla written-book title cap. */
	public static final int MAX_TITLE_LENGTH = 32;
	/** Align with {@code CraftMetaBook.MAX_AUTHOR_LENGTH}. */
	public static final int MAX_AUTHOR_LENGTH = 16;
	/** PandaSpigot default max bytes contributed per page before multiplier. */
	public static final int MAX_BOOK_PAGE_BYTES = 2560;
	/** PandaSpigot default total-size multiplier (clamped 0.3–1.0 at use site). */
	public static final double MAX_BOOK_TOTAL_SIZE_MULTIPLIER = 0.98D;
	/** Book payloads should never nest compounds/lists beyond compound→list→value. */
	public static final int MAX_BOOK_NBT_DEPTH = 3;
	/** AntiCrash readable-bytes ceiling for custom payloads; keep in sync. */
	public static final int MAX_PAYLOAD_BYTES = 25780;

	private static final Set<String> ALLOWED_BOOK_KEYS = new HashSet<String>(
			Arrays.asList("pages", "title", "author", "generation", "resolved"));

	private BookNbtValidator() {
	}

	/**
	 * Peek a book custom-payload buffer and validate item type + NBT.
	 * Does not consume the caller's buffer; does not run CraftBukkit item-meta
	 * sanitization (validates raw NBT).
	 *
	 * @param payload raw custom-payload body (not retained)
	 * @param signing {@code true} for MC|BSign, {@code false} for MC|BEdit
	 * @throws IOException when the payload is malformed or exceeds book bounds
	 */
	public static void validateBookPayload(PacketDataSerializer payload, boolean signing) throws IOException {
		if (payload == null) {
			throw new IOException("Missing book payload");
		}
		if (payload.readableBytes() > MAX_PAYLOAD_BYTES) {
			throw new IOException("Book payload too large");
		}

		PacketDataSerializer peek = new PacketDataSerializer(Unpooled.copiedBuffer(payload));
		try {
			short itemId = peek.readShort();
			if (itemId < 0) {
				throw new IOException("Invalid book item id");
			}
			peek.readByte(); // count
			peek.readShort(); // damage/data

			Item item = Item.getById(itemId);
			if (signing) {
				if (item != Items.WRITTEN_BOOK) {
					throw new IOException("MC|BSign must carry a written book");
				}
			} else if (item != Items.WRITABLE_BOOK) {
				throw new IOException("MC|BEdit must carry a writable book");
			}

			NBTTagCompound tag = peek.h();
			validateBookTag(tag, signing);
		} finally {
			peek.release();
		}
	}

	/**
	 * Validate an already-decoded book {@link ItemStack}.
	 *
	 * @return {@code true} if the book is within size/structure bounds
	 */
	public static boolean isValidBookItem(ItemStack stack, boolean signing) {
		if (stack == null) {
			return false;
		}
		try {
			validateBookTag(stack.getTag(), signing);
			return true;
		} catch (IOException ex) {
			return false;
		}
	}

	/**
	 * Core NBT checks for book tags.
	 *
	 * @throws IOException when the tag is missing required fields or exceeds limits
	 */
	public static void validateBookTag(NBTTagCompound tag, boolean signing) throws IOException {
		if (tag == null) {
			throw new IOException("Book NBT missing");
		}

		validateAllowedKeys(tag);
		validateNbtDepth(tag, 0);

		NBTBase pagesBase = tag.get("pages");
		if (!(pagesBase instanceof NBTTagList)) {
			throw new IOException("Book pages missing");
		}

		NBTTagList pages = (NBTTagList) pagesBase;
		if (pages.size() > 0 && pages.f() != 8) {
			throw new IOException("Book pages must be strings");
		}
		if (pages.size() > MAX_PAGES) {
			throw new IOException("Too many book pages");
		}

		long byteTotal = 0L;
		double multiplier = Math.max(0.3D, Math.min(1.0D, MAX_BOOK_TOTAL_SIZE_MULTIPLIER));
		long byteAllowed = MAX_BOOK_PAGE_BYTES;

		for (int i = 0; i < pages.size(); ++i) {
			NBTBase pageTag = pages.g(i);
			if (pageTag == null || pageTag.getTypeId() != 8) {
				throw new IOException("Invalid book page type");
			}
			String page = pages.getString(i);
			if (page == null) {
				throw new IOException("Null book page");
			}
			if (page.length() > MAX_PAGE_LENGTH) {
				throw new IOException("Book page too long");
			}

			int byteLength = page.getBytes(StandardCharsets.UTF_8).length;
			byteTotal += byteLength;

			int length = page.length();
			int multibytes = 0;
			if (length != byteLength) {
				for (int c = 0; c < length; ++c) {
					if (page.charAt(c) > 127) {
						multibytes++;
					}
				}
			}

			byteAllowed += (long) (MAX_BOOK_PAGE_BYTES * Math.min(1.0D, Math.max(0.1D, (double) length / 255.0D))
					* multiplier);
			if (multibytes > 1) {
				byteAllowed -= multibytes;
			}
		}

		if (byteTotal > byteAllowed) {
			throw new IOException("Book content too large");
		}

		if (signing) {
			if (!tag.hasKeyOfType("title", 8)) {
				throw new IOException("Signed book missing title");
			}
			String title = tag.getString("title");
			if (title == null || title.length() > MAX_TITLE_LENGTH) {
				throw new IOException("Invalid book title");
			}
			if (tag.hasKeyOfType("author", 8)) {
				String author = tag.getString("author");
				if (author != null && author.length() > MAX_AUTHOR_LENGTH) {
					throw new IOException("Invalid book author");
				}
			}
		}
	}

	private static void validateAllowedKeys(NBTTagCompound tag) throws IOException {
		Set<String> keys = tag.c();
		if (keys.size() > ALLOWED_BOOK_KEYS.size()) {
			throw new IOException("Book NBT has too many keys");
		}
		for (String key : keys) {
			if (!ALLOWED_BOOK_KEYS.contains(key)) {
				throw new IOException("Disallowed book NBT key: " + key);
			}
		}
	}

	private static void validateNbtDepth(NBTBase node, int depth) throws IOException {
		if (node == null) {
			return;
		}
		if (depth > MAX_BOOK_NBT_DEPTH) {
			throw new IOException("Book NBT too deep");
		}
		byte type = node.getTypeId();
		if (type == 10) {
			NBTTagCompound compound = (NBTTagCompound) node;
			for (String key : compound.c()) {
				validateNbtDepth(compound.get(key), depth + 1);
			}
		} else if (type == 9) {
			NBTTagList list = (NBTTagList) node;
			int elementType = list.f();
			// Only recurse into nested compounds/lists; primitives (incl. strings) are leaves.
			if (elementType == 9 || elementType == 10) {
				for (int i = 0; i < list.size(); ++i) {
					validateNbtDepth(list.g(i), depth + 1);
				}
			} else if (list.size() > 0 && depth + 1 > MAX_BOOK_NBT_DEPTH) {
				throw new IOException("Book NBT too deep");
			}
		}
	}
}
