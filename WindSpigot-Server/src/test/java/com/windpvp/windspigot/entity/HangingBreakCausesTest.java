package com.windpvp.windspigot.entity;

import org.bukkit.event.hanging.HangingBreakEvent;
import org.junit.Assert;
import org.junit.Test;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.BlockPosition;
import net.minecraft.server.EntityHanging;
import net.minecraft.server.EnumDirection;
import net.minecraft.server.MathHelper;

/**
 * Regression for EntityHanging suffocation / obstruction cause (TODO #11).
 * <p>
 * The old check sampled only {@code BlockPosition(entity)} for non-AIR. Large paintings and
 * partial overlaps can fail {@code survives()} via {@code getCubes} while that origin cell is
 * still air — plugins then saw PHYSICS instead of OBSTRUCTION.
 */
public class HangingBreakCausesTest {

	@Test
	public void obstructedByBlocksMapsToObstruction() {
		Assert.assertEquals(HangingBreakEvent.RemoveCause.OBSTRUCTION,
				HangingBreakCauses.whenUnsurviving(true));
	}

	@Test
	public void supportOrOverlapFailureMapsToPhysics() {
		Assert.assertEquals(HangingBreakEvent.RemoveCause.PHYSICS,
				HangingBreakCauses.whenUnsurviving(false));
	}

	@Test
	public void largePaintingBoundingBoxSpansMultipleBlockCells() {
		// 4×4 block painting (64×64 px) facing south — origin cell alone cannot cover the BB.
		BlockPosition origin = new BlockPosition(10, 70, 20);
		AxisAlignedBB bb = EntityHanging.calculateBoundingBox(origin, EnumDirection.SOUTH, 64, 64);

		int minX = MathHelper.floor(bb.a);
		int maxX = MathHelper.floor(bb.d);
		int minY = MathHelper.floor(bb.b);
		int maxY = MathHelper.floor(bb.e);

		Assert.assertTrue("painting should span more than one X cell", maxX > minX);
		Assert.assertTrue("painting should span more than one Y cell", maxY > minY);
	}

	@Test
	public void singleOriginCellSampleMissesEdgeObstructionCells() {
		BlockPosition origin = new BlockPosition(10, 70, 20);
		AxisAlignedBB bb = EntityHanging.calculateBoundingBox(origin, EnumDirection.SOUTH, 64, 64);

		int originX = origin.getX();
		int originY = origin.getY();
		int minX = MathHelper.floor(bb.a);
		int maxX = MathHelper.floor(bb.d);
		int minY = MathHelper.floor(bb.b);
		int maxY = MathHelper.floor(bb.e);

		boolean hasCellOutsideOrigin = false;
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				if (x != originX || y != originY) {
					hasCellOutsideOrigin = true;
					break;
				}
			}
		}

		Assert.assertTrue(
				"edge cells exist outside BlockPosition(origin); a non-AIR sample there alone is not enough",
				hasCellOutsideOrigin);
		// A block placed only in an edge cell leaves origin air — old heuristic → PHYSICS;
		// getCubes / whenUnsurviving(true) → OBSTRUCTION.
		Assert.assertEquals(HangingBreakEvent.RemoveCause.OBSTRUCTION,
				HangingBreakCauses.whenUnsurviving(true));
	}

	@Test
	public void itemFrameSizedBoxStaysInOneHorizontalCell() {
		BlockPosition origin = new BlockPosition(5, 64, 5);
		// Item frames are 12×12 px (EntityItemFrame.l/m).
		AxisAlignedBB bb = EntityHanging.calculateBoundingBox(origin, EnumDirection.NORTH, 12, 12);

		Assert.assertEquals(origin.getX(), MathHelper.floor((bb.a + bb.d) / 2.0D));
		Assert.assertEquals(origin.getZ(), MathHelper.floor((bb.c + bb.f) / 2.0D));
		// Thin facing-axis extent must remain sub-block so valid wall placements still survive.
		Assert.assertTrue("frame depth should be thinner than one block", (bb.f - bb.c) < 1.0D);
		Assert.assertTrue("frame width should be thinner than one block", (bb.d - bb.a) < 1.0D);
	}
}
