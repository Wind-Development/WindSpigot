package com.windpvp.windspigot.security;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import net.minecraft.server.ItemBookAndQuill;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;

/**
 * Unit tests for book custom-payload NBT validation (MC|BEdit / MC|BSign).
 */
public class BookNbtValidatorTest {

	private static NBTTagCompound editableBook(String... pages) {
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for (String page : pages) {
			list.add(new NBTTagString(page));
		}
		tag.set("pages", list);
		return tag;
	}

	private static NBTTagCompound signedBook(String title, String author, String... pages) {
		NBTTagCompound tag = editableBook(pages);
		tag.setString("title", title);
		if (author != null) {
			tag.setString("author", author);
		}
		return tag;
	}

	@Test
	public void acceptsLegitimateEditableBook() throws IOException {
		BookNbtValidator.validateBookTag(editableBook("Hello", "World"), false);
	}

	@Test
	public void acceptsLegitimateSignedBook() throws IOException {
		BookNbtValidator.validateBookTag(signedBook("My Title", "Steve", "Page one"), true);
	}

	@Test(expected = IOException.class)
	public void rejectsNullTag() throws IOException {
		BookNbtValidator.validateBookTag(null, false);
	}

	@Test(expected = IOException.class)
	public void rejectsMissingPages() throws IOException {
		BookNbtValidator.validateBookTag(new NBTTagCompound(), false);
	}

	@Test(expected = IOException.class)
	public void rejectsTooManyPages() throws IOException {
		String[] pages = new String[BookNbtValidator.MAX_PAGES + 1];
		for (int i = 0; i < pages.length; i++) {
			pages[i] = "p" + i;
		}
		BookNbtValidator.validateBookTag(editableBook(pages), false);
	}

	@Test(expected = IOException.class)
	public void rejectsOversizedPage() throws IOException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < BookNbtValidator.MAX_PAGE_LENGTH + 1; i++) {
			sb.append('a');
		}
		BookNbtValidator.validateBookTag(editableBook(sb.toString()), false);
	}

	@Test(expected = IOException.class)
	public void rejectsDisallowedNbtKeys() throws IOException {
		NBTTagCompound tag = editableBook("ok");
		tag.set("ench", new NBTTagList()); // classic Any-NBT-book vector
		BookNbtValidator.validateBookTag(tag, false);
	}

	@Test(expected = IOException.class)
	public void rejectsSignedBookWithoutTitle() throws IOException {
		BookNbtValidator.validateBookTag(editableBook("page"), true);
	}

	@Test(expected = IOException.class)
	public void rejectsOversizedTitle() throws IOException {
		StringBuilder title = new StringBuilder();
		for (int i = 0; i < BookNbtValidator.MAX_TITLE_LENGTH + 1; i++) {
			title.append('T');
		}
		BookNbtValidator.validateBookTag(signedBook(title.toString(), "Steve", "page"), true);
	}

	@Test(expected = IOException.class)
	public void rejectsOversizedAuthor() throws IOException {
		StringBuilder author = new StringBuilder();
		for (int i = 0; i < BookNbtValidator.MAX_AUTHOR_LENGTH + 1; i++) {
			author.append('A');
		}
		BookNbtValidator.validateBookTag(signedBook("Title", author.toString(), "page"), true);
	}

	@Test(expected = IOException.class)
	public void rejectsNestedCompoundDepth() throws IOException {
		NBTTagCompound tag = editableBook("ok");
		NBTTagCompound nested = new NBTTagCompound();
		NBTTagCompound deeper = new NBTTagCompound();
		NBTTagCompound deepest = new NBTTagCompound();
		deepest.setString("x", "y");
		deeper.set("z", deepest);
		nested.set("y", deeper);
		// generation is allowed as a key but must be an int in vanilla; stuffing a
		// compound here exercises depth rejection via allowed-key walk.
		tag.set("generation", nested);
		BookNbtValidator.validateBookTag(tag, false);
	}

	@Test
	public void itemBookAndQuillRejectsOversizedPage() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < BookNbtValidator.MAX_PAGE_LENGTH + 1; i++) {
			sb.append('x');
		}
		Assert.assertFalse(ItemBookAndQuill.b(editableBook(sb.toString())));
	}

	@Test
	public void itemBookAndQuillRejectsTooManyPages() {
		String[] pages = new String[BookNbtValidator.MAX_PAGES + 1];
		for (int i = 0; i < pages.length; i++) {
			pages[i] = "p";
		}
		Assert.assertFalse(ItemBookAndQuill.b(editableBook(pages)));
	}

	@Test
	public void itemBookAndQuillAcceptsNormalBook() {
		Assert.assertTrue(ItemBookAndQuill.b(editableBook("one", "two")));
	}

	@Test
	public void isValidBookItemRejectsNullStack() {
		Assert.assertFalse(BookNbtValidator.isValidBookItem(null, false));
		Assert.assertFalse(BookNbtValidator.isValidBookItem(null, true));
	}

	@Test
	public void validateBookTagRejectsEnchantInjection() {
		NBTTagCompound bad = editableBook("hi");
		bad.setByteArray("ench", new byte[] { 1, 2, 3 });
		try {
			BookNbtValidator.validateBookTag(bad, false);
			Assert.fail("Expected IOException for ench injection");
		} catch (IOException expected) {
			Assert.assertTrue(expected.getMessage().contains("Disallowed"));
		}
	}
}
