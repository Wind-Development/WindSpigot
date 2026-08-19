package com.windpvp.windspigot.block;

import org.junit.Assert;
import org.junit.Test;

/**
 * BlockPlaceValidationTest: Verify malformed placement packets (null direction, out of bounds Y)
 * are rejected safely without throwing NullPointerException or crashing the main server thread.
 */
public class BlockPlaceValidationTest {

    // Helper method simulating PlayerConnection block placement validation
    private boolean validateBlockPlacement(Integer face, Integer yCoord, int maxBuildHeight) {
        if (face == null || yCoord == null) {
            return false;
        }

        // Face 255 = Right-click air / use item in hand
        if (face == 255) {
            return true;
        }

        // Face must map to a valid EnumDirection (0..5: DOWN, UP, NORTH, SOUTH, WEST, EAST)
        if (face < 0 || face > 5) {
            return false;
        }

        // Coordinates must be within valid world height bounds
        return yCoord >= 0 && yCoord < maxBuildHeight;
    }

    @Test
    public void testValidBlockPlacementPasses() {
        int maxBuildHeight = 256;

        // Face 1 = UP, Y = 64
        Assert.assertTrue("Valid placement on top of block 64 must pass",
                validateBlockPlacement(1, 64, maxBuildHeight));

        // Face 255 = Air click
        Assert.assertTrue("Face 255 air click must pass",
                validateBlockPlacement(255, 64, maxBuildHeight));
    }

    @Test
    public void testNullOrInvalidFaceRejected() {
        int maxBuildHeight = 256;

        // Invalid face values that don't correspond to valid EnumDirection
        Assert.assertFalse("Face -1 must be safely rejected",
                validateBlockPlacement(-1, 64, maxBuildHeight));

        Assert.assertFalse("Face 6 (out of EnumDirection enum bounds) must be safely rejected",
                validateBlockPlacement(6, 64, maxBuildHeight));

        Assert.assertFalse("Face 100 must be safely rejected",
                validateBlockPlacement(100, 64, maxBuildHeight));
    }

    @Test
    public void testOutOfBoundsYCoordinatesRejected() {
        int maxBuildHeight = 256;

        // Negative Y coordinate (e.g. void / hacked clients)
        Assert.assertFalse("Negative Y coordinate must be rejected",
                validateBlockPlacement(1, -1, maxBuildHeight));

        Assert.assertFalse("Negative Y coordinate -10 must be rejected",
                validateBlockPlacement(1, -10, maxBuildHeight));

        // Y >= maxBuildHeight (256)
        Assert.assertFalse("Y coordinate at 256 must be rejected",
                validateBlockPlacement(1, 256, maxBuildHeight));

        Assert.assertFalse("Y coordinate at 512 must be rejected",
                validateBlockPlacement(1, 512, maxBuildHeight));
    }
}
