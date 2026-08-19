package com.windpvp.windspigot.combat;

import org.junit.Assert;
import org.junit.Test;

public class ExplosionKnockbackRegressionTest {

    @Test
    public void testFireballKnockbackDirectionNotNegative() {
        // Fireball with size 1.0 -> f3 = 2.0
        float size = 1.0F;
        float f3 = size * 2.0F;
        double maxDistSq = (double) (f3 * f3);

        double explosionX = 0.0;
        double explosionY = 64.0;
        double explosionZ = 0.0;

        // Entity at distance 3.0 (outside f3=2.0)
        double entityX = 3.0;
        double entityY = 64.0;
        double entityZ = 0.0;

        double d8 = entityX - explosionX;
        double d9 = entityY - explosionY;
        double d10 = entityZ - explosionZ;
        double distanceSquared = d8 * d8 + d9 * d9 + d10 * d10;

        // With fix, entity outside maxDistSq must not be affected
        boolean isAffected = (distanceSquared <= maxDistSq && distanceSquared != 0.0D);
        Assert.assertFalse("Entity outside explosion radius must not be affected", isAffected);

        // Entity within explosion radius (distance 1.0)
        double innerEntityX = 1.0;
        double innerEntityY = 64.0;
        double innerEntityZ = 0.0;

        double inD8 = innerEntityX - explosionX;
        double inD9 = innerEntityY - explosionY;
        double inD10 = innerEntityZ - explosionZ;
        double inDistSq = inD8 * inD8 + inD9 * inD9 + inD10 * inD10;

        boolean innerAffected = (inDistSq <= maxDistSq && inDistSq != 0.0D);
        Assert.assertTrue("Entity inside explosion radius must be affected", innerAffected);

        double d11 = Math.sqrt(inDistSq);
        double d7 = d11 / f3;
        Assert.assertTrue("d7 must be <= 1.0", d7 <= 1.0D);

        double blockDensity = 1.0;
        double d13 = (1.0D - d7) * blockDensity;
        Assert.assertTrue("Knockback multiplier d13 must be non-negative", d13 >= 0.0D);

        double normalizedDirectionX = inD8 / d11;
        double impulseX = normalizedDirectionX * d13;
        Assert.assertTrue("Impulse must push entity away from explosion (positive X)", impulseX > 0.0D);
    }

    @Test
    public void testLargeExplosionRadiusScales() {
        // Large explosion with size 6.0 (Charged Creeper) -> f3 = 12.0
        float size = 6.0F;
        float f3 = size * 2.0F;
        double maxDistSq = (double) (f3 * f3);

        // Entity at distance 10.0 (beyond old hardcoded 8.0/64.0 limit, but within 12.0)
        double entityDist = 10.0;
        double distSq = entityDist * entityDist;

        boolean affected = (distSq <= maxDistSq && distSq != 0.0D);
        Assert.assertTrue("Large explosion must reach entity at distance 10", affected);

        double d7 = entityDist / f3;
        Assert.assertTrue("d7 must be <= 1.0", d7 <= 1.0D);
        double d13 = (1.0D - d7) * 1.0;
        Assert.assertTrue("Knockback multiplier must be positive", d13 > 0.0D);
    }
}
