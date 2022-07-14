package org.bukkit.configuration.file;

import org.apache.commons.lang.Validate;

import java.util.List;

/**
 * Various settings for controlling the input and output of a
 * {@link YamlConfiguration}
 */
public class YamlConfigurationOptions extends FileConfigurationOptions {
	private int indent = 2;
	private int width = 80;

	protected YamlConfigurationOptions(YamlConfiguration configuration) {
		super(configuration);
	}

	@Override
	public YamlConfiguration configuration() {
		return (YamlConfiguration) super.configuration();
	}

	@Override
	public YamlConfigurationOptions copyDefaults(boolean value) {
		super.copyDefaults(value);
		return this;
	}

	@Override
	public YamlConfigurationOptions pathSeparator(char value) {
		super.pathSeparator(value);
		return this;
	}

	@Override
	public YamlConfigurationOptions setHeader(List<String> value) {
		super.setHeader(value);
		return this;
	}

	@Override
	@Deprecated
	public YamlConfigurationOptions header(String value) {
		super.header(value);
		return this;
	}

	@Override
	public YamlConfigurationOptions setFooter(List<String> value) {
		super.setFooter(value);
		return this;
	}

	@Override
	public YamlConfigurationOptions parseComments(boolean value) {
		super.parseComments(value);
		return this;
	}

	@Override
	@Deprecated
	public YamlConfigurationOptions copyHeader(boolean value) {
		super.copyHeader(value);
		return this;
	}

	/**
	 * Gets how much spaces should be used to indent each line.
	 * <p>
	 * The minimum value this may be is 2, and the maximum is 9.
	 *
	 * @return How much to indent by
	 */
	public int indent() {
		return indent;
	}

	/**
	 * Sets how much spaces should be used to indent each line.
	 * <p>
	 * The minimum value this may be is 2, and the maximum is 9.
	 *
	 * @param value New indent
	 * @return This object, for chaining
	 */
	public YamlConfigurationOptions indent(int value) {
		Validate.isTrue(value >= 2, "Indent must be at least 2 characters");
		Validate.isTrue(value <= 9, "Indent cannot be greater than 9 characters");

		this.indent = value;
		return this;
	}

	/**
	 * Gets how long a line can be, before it gets split.
	 *
	 * @return How the max line width
	 */
	public int width() {
		return width;
	}

	/**
	 * Sets how long a line can be, before it gets split.
	 *
	 * @param value New width
	 * @return This object, for chaining
	 */
	public YamlConfigurationOptions width(int value) {
		this.width = value;
		return this;
	}
}
