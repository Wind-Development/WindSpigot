package com.windpvp.windspigot.entity;

import org.bukkit.event.hanging.HangingBreakEvent;

/**
 * Classifies why a hanging entity (item frame / painting) failed {@code survives()}.
 * <p>
 * Obstruction must be decided from solid collision geometry intersecting the hanging
 * bounding box — not from a single {@code BlockPosition} material sample at the entity
 * origin, which misses large paintings and partial block overlaps.
 */
public final class HangingBreakCauses {

	private HangingBreakCauses() {
	}

	/**
	 * @param obstructedByBlocks {@code true} when {@code world.getCubes(hanging, bb)} is
	 *                           non-empty (same criterion as the first {@code survives()} check)
	 * @return {@link HangingBreakEvent.RemoveCause#OBSTRUCTION} when clipped by a block,
	 *         otherwise {@link HangingBreakEvent.RemoveCause#PHYSICS} (support gone, overlap, etc.)
	 */
	public static HangingBreakEvent.RemoveCause whenUnsurviving(boolean obstructedByBlocks) {
		return obstructedByBlocks ? HangingBreakEvent.RemoveCause.OBSTRUCTION
				: HangingBreakEvent.RemoveCause.PHYSICS;
	}
}
