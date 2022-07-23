package org.bukkit.configuration;

import java.util.Collections;
import java.util.List;

final class SectionPathData {

    private Object data;
    private List<String> comments;
    private List<String> inlineComments;

    public SectionPathData(Object data) {
        this.data = data;
        comments = Collections.emptyList();
        inlineComments = Collections.emptyList();
    }

    public Object getData() {
        return data;
    }

    public void setData(final Object data) {
        this.data = data;
    }

    /**
     * If no comments exist, an empty list will be returned. A null entry in the
     * list represents an empty line and an empty String represents an empty
     * comment line.
     *
     * @return A unmodifiable list of the requested comments, every entry
     * represents one line.
     */
    public List<String> getComments() {
        return comments;
    }

    /**
     * Represents the comments on a {@link ConfigurationSection} entry.
     *
     * A null entry in the List is an empty line and an empty String entry is an
     * empty comment line. Any existing comments will be replaced, regardless of
     * what the new comments are.
     *
     * @param comments New comments to set every entry represents one line.
     */
    public void setComments(final List<String> comments) {
        this.comments = (comments == null) ? Collections.emptyList() : Collections.unmodifiableList(comments);
    }

    /**
     * If no comments exist, an empty list will be returned. A null entry in the
     * list represents an empty line and an empty String represents an empty
     * comment line.
     *
     * @return A unmodifiable list of the requested comments, every entry
     * represents one line.
     */
    public List<String> getInlineComments() {
        return inlineComments;
    }

    /**
     * Represents the comments on a {@link ConfigurationSection} entry.
     *
     * A null entry in the List is an empty line and an empty String entry is an
     * empty comment line. Any existing comments will be replaced, regardless of
     * what the new comments are.
     *
     * @param inlineComments New comments to set every entry represents one
     * line.
     */
    public void setInlineComments(final List<String> inlineComments) {
        this.inlineComments = (inlineComments == null) ? Collections.emptyList() : Collections.unmodifiableList(inlineComments);
    }
}