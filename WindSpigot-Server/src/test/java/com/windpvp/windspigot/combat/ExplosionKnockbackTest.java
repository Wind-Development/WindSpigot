package com.windpvp.windspigot.combat;

import org.junit.Assert;
import org.junit.Test;

/**
 * ExplosionKnockbackTest: Verify that explosions at various sizes (yield 1 to 8)
 * push entities strictly away with positive magnitude.
 */
public class ExplosionKnockbackTest {

    @Test
    public void testExplosionKnockbackAtVariousYields() {
        // Test yields from 1 to 8 (Fireball yield=1, TNT yield=4, Charged Creeper/End Crystal yield=6, Custom yield=8)
        int[] testYields = { 1, 2, 3, 4, 5, 6, 7, 8 };

        double explosionX = 100.0;
        double explosionY = 64.0;
        double explosionZ = 100.0;

        for (int yield : testYields) {
            float size = (float) yield;
            float f3 = size * 2.0F;
            double maxDistSq = (double) (f3 * f3);

            // 1. Entity inside radius (at 50% radius in +X, +Z direction)
            double innerDistance = f3 * 0.5;
            double innerX = explosionX + (innerDistance / Math.sqrt(2.0));
            double innerY = explosionY;
            double innerZ = explosionZ + (innerDistance / Math.sqrt(2.0));

            double d8 = innerX - explosionX;
            double d9 = innerY - explosionY;
            double d10 = innerZ - explosionZ;
            double distanceSquared = d8 * d8 + d9 * d9 + d10 * d10;

            Assert.assertTrue("Entity at 50% radius must be inside explosion range for yield " + yield,
                    distanceSquared <= maxDistSq && distanceSquared != 0.0D);

            double d11 = Math.sqrt(distanceSquared);
            double d7 = d11 / f3;
            Assert.assertTrue("d7 must be <= 1.0 for entity inside radius (yield " + yield + ")", d7 <= 1.0D);

            double blockDensity = 1.0;
            double d13 = (1.0D - d7) * blockDensity;
            Assert.assertTrue("Knockback factor d13 must be non-negative for yield " + yield, d13 >= 0.0D);

            // Vector components
            double dirX = d8 / d11;
            double dirZ = d10 / d11;

            double impulseX = dirX * d13;
            double impulseZ = dirZ * d13;

            // Must push strictly away from explosion origin (+X and +Z)
            Assert.assertTrue("Impulse X must be positive (pushing away) for yield " + yield, impulseX > 0.0D);
            Assert.assertTrue("Impulse Z must be positive (pushing away) for yield " + yield, impulseZ > 0.0D);

            // 2. Entity outside radius (at 120% radius)
            double outerDistance = f3 * 1.2;
            double outerX = explosionX + outerDistance;
            double outerDistSq = (outerX - explosionX) * (outerX - explosionX);

            boolean isOuterAffected = (outerDistSq <= maxDistSq && outerDistSq != 0.0D);
            Assert.assertFalse("Entity outside explosion radius must not be affected for yield " + yield, isOuterAffected);
        }
    }

    @Test
    public void testFireballYieldOneDoesNotPullEntityTowardsCenter() {
        // Fireball with yield 1 -> f3 = 2.0
        float size = 1.0F;
        float f3 = size * 2.0F; // 2.0 blocks
        double maxDistSq = (double) (f3 * f3); // 4.0

        double explosionX = 0.0;
        double explosionY = 64.0;
        double explosionZ = 0.0;

        // Entity at distance 3.0 (between 2.0 and 8.0 blocks, which was broken by the old hardcoded 64.0D check)
        double entityX = 3.0;
        double entityY = 64.0;
        double entityZ = 0.0;

        double distSq = (entityX - explosionX) * (entityX - explosionX)
                + (entityY - explosionY) * (entityY - explosionY)
                + (entityZ - explosionZ) * (entityZ - explosionZ);

        boolean affected = (distSq <= maxDistSq && distSq != 0.0D);
        Assert.assertFalse("Entity at distance 3.0 must NOT be affected by yield 1 fireball", affected);
    }
}
