package com.destroystokyo.paper.profile;

import java.util.Objects;

import org.apache.commons.lang.Validate;

/**
 * Represents a property on a {@link PlayerProfile}.
 */
public final class ProfileProperty {

	private final String name;
	private final String value;
	private final String signature;

	public ProfileProperty(String name, String value) {
		this(name, value, null);
	}

	public ProfileProperty(String name, String value, String signature) {
		Validate.notNull(name, "Profile property name cannot be null");
		Validate.notNull(value, "Profile property value cannot be null");
		this.name = name;
		this.value = value;
		this.signature = signature;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getSignature() {
		return signature;
	}

	public boolean isSigned() {
		return signature != null;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ProfileProperty)) {
			return false;
		}
		ProfileProperty other = (ProfileProperty) obj;
		return Objects.equals(name, other.name) && Objects.equals(value, other.value)
				&& Objects.equals(signature, other.signature);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, value, signature);
	}
}
