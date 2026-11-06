package com.destroystokyo.paper.profile;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a player profile for the game, such as UUID, name, and textures.
 * <p>
 * Used by {@link com.destroystokyo.paper.event.server.PaperServerListPingEvent}
 * for the mutable player sample list.
 * </p>
 */
public interface PlayerProfile {

	/**
	 * @return The player's name, if set
	 */
	String getName();

	/**
	 * Sets this profile's name.
	 *
	 * @param name The new name
	 * @return The previous name
	 */
	String setName(String name);

	/**
	 * @return The player's unique identifier, if set
	 */
	UUID getId();

	/**
	 * Sets this profile's UUID.
	 *
	 * @param uuid The new UUID
	 * @return The previous UUID
	 */
	UUID setId(UUID uuid);

	/**
	 * @return A mutable set of this player's properties, such as textures
	 */
	Set<ProfileProperty> getProperties();

	/**
	 * Check if the profile has the specified property.
	 *
	 * @param property Property name to check
	 * @return If the property is set
	 */
	boolean hasProperty(String property);

	/**
	 * Sets a property. If the property already exists, the previous one will be
	 * replaced.
	 *
	 * @param property Property to set
	 */
	void setProperty(ProfileProperty property);

	/**
	 * Sets multiple properties. If any of the set properties already exist, they
	 * will be replaced.
	 *
	 * @param properties The properties to set
	 */
	void setProperties(Collection<ProfileProperty> properties);

	/**
	 * Removes a specific property from this profile.
	 *
	 * @param property The property name to remove
	 * @return If a property was removed
	 */
	boolean removeProperty(String property);

	/**
	 * Clears all properties on this profile.
	 */
	void clearProperties();

	/**
	 * @return If the profile is complete (has UUID and name)
	 */
	boolean isComplete();

	/**
	 * Attempt to complete this profile from cache only (no network).
	 *
	 * @return If the profile is now complete
	 */
	boolean completeFromCache();

	/**
	 * Attempt to complete this profile, optionally filling textures. May block.
	 *
	 * @param textures whether to fill texture properties
	 * @return If the profile is now complete
	 */
	boolean complete(boolean textures);
}
