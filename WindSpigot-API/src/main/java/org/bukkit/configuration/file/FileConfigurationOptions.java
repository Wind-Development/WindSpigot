package org.bukkit.configuration.file;

import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.MemoryConfigurationOptions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Various settings for controlling the input and output of a
 * {@link FileConfiguration}
 */
public class FileConfigurationOptions extends MemoryConfigurationOptions {
    private List<String> header = Collections.emptyList();
    private List<String> footer = Collections.emptyList();
    private boolean parseComments = true;

    protected FileConfigurationOptions(MemoryConfiguration configuration) {
        super(configuration);
    }

    @Override
    public FileConfiguration configuration() {
        return (FileConfiguration) super.configuration();
    }

    @Override
    public FileConfigurationOptions copyDefaults(boolean value) {
        super.copyDefaults(value);
        return this;
    }

    @Override
    public FileConfigurationOptions pathSeparator(char value) {
        super.pathSeparator(value);
        return this;
    }

    /**
     * Gets the header that will be applied to the top of the saved output.
     * <p>
     * This header will be commented out and applied directly at the top of the
     * generated output of the {@link FileConfiguration}. It is not required to
     * include a newline at the end of the header as it will automatically be
     * applied, but you may include one if you wish for extra spacing.
     * <p>
     * If no comments exist, an empty list will be returned. A null entry
     * represents an empty line and an empty String represents an empty comment
     * line.
     *
     * @return Unmodifiable header, every entry represents one line.
     */
    public List<String> getHeader() {
        return header;
    }

    /**
     * @return The string header.
     * @deprecated use getHeader() instead.
     */
    @Deprecated
    public String header() {
        StringBuilder stringHeader = new StringBuilder();
        for (String line : header) {
            stringHeader.append(line == null ? "\n" : line + "\n");
        }
        return stringHeader.toString();
    }

    /**
     * Sets the header that will be applied to the top of the saved output.
     * <p>
     * This header will be commented out and applied directly at the top of the
     * generated output of the {@link FileConfiguration}. It is not required to
     * include a newline at the end of the header as it will automatically be
     * applied, but you may include one if you wish for extra spacing.
     * <p>
     * If no comments exist, an empty list will be returned. A null entry
     * represents an empty line and an empty String represents an empty comment
     * line.
     *
     * @param value New header, every entry represents one line.
     * @return This object, for chaining
     */
    public FileConfigurationOptions setHeader(List<String> value) {
        this.header = (value == null) ? Collections.emptyList() : Collections.unmodifiableList(value);
        return this;
    }

    /**
     * @param value The string header.
     * @return This object, for chaining.
     * @deprecated use setHeader() instead
     */
    @Deprecated
    public FileConfigurationOptions header(String value) {
        this.header = (value == null) ? Collections.emptyList() : Collections.unmodifiableList(Arrays.asList(value.split("\\n")));
        return this;
    }

    /**
     * Gets the footer that will be applied to the bottom of the saved output.
     * <p>
     * This footer will be commented out and applied directly at the bottom of
     * the generated output of the {@link FileConfiguration}. It is not required
     * to include a newline at the beginning of the footer as it will
     * automatically be applied, but you may include one if you wish for extra
     * spacing.
     * <p>
     * If no comments exist, an empty list will be returned. A null entry
     * represents an empty line and an empty String represents an empty comment
     * line.
     *
     * @return Unmodifiable footer, every entry represents one line.
     */
    public List<String> getFooter() {
        return footer;
    }

    /**
     * Sets the footer that will be applied to the bottom of the saved output.
     * <p>
     * This footer will be commented out and applied directly at the bottom of
     * the generated output of the {@link FileConfiguration}. It is not required
     * to include a newline at the beginning of the footer as it will
     * automatically be applied, but you may include one if you wish for extra
     * spacing.
     * <p>
     * If no comments exist, an empty list will be returned. A null entry
     * represents an empty line and an empty String represents an empty comment
     * line.
     *
     * @param value New footer, every entry represents one line.
     * @return This object, for chaining
     */
    public FileConfigurationOptions setFooter(List<String> value) {
        this.footer = (value == null) ? Collections.emptyList() : Collections.unmodifiableList(value);
        return this;
    }

    /**
     * Gets whether or not comments should be loaded and saved.
     * <p>
     * Defaults to true.
     *
     * @return Whether comments are parsed.
     */
    public boolean parseComments() {
        return parseComments;
    }

    /**
     * Sets whether or not comments should be loaded and saved.
     * <p>
     * Defaults to true.
     *
     * @param value Whether or not comments are parsed.
     * @return This object, for chaining
     */
    public FileConfigurationOptions parseComments(boolean value) {
        parseComments = value;
        return this;
    }

	/**
	 * @return Whether comments are parsed.
	 *
	 * @deprecated Call {@link #parseComments()} instead.
	 */
	@Deprecated
	public boolean copyHeader() {
		return parseComments;
	}

	/**
	 * @param value Should comments be parsed.
	 * @return This object, for chaining
	 *
	 * @deprecated Call {@link #parseComments(boolean)} instead.
	 */
	@Deprecated
	public FileConfigurationOptions copyHeader(boolean value) {
		parseComments = value;
		return this;
	}
}
