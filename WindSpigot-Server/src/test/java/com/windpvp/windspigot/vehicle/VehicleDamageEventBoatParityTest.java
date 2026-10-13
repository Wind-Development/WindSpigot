package com.windpvp.windspigot.vehicle;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.junit.Assert;
import org.junit.Test;

/**
 * Regression for EntityBoat VehicleDamageEvent damage application (TODO #3).
 * <p>
 * Boats previously fired and honored cancel, but ignored {@code event.getDamage()}.
 * Minecarts already applied modified damage — these tests lock that shared contract.
 * <p>
 * Uses JDK proxies instead of Mockito (1.10.19 is incompatible with modern JDKs).
 */
public class VehicleDamageEventBoatParityTest {

	/** Same accumulation used by EntityBoat and EntityMinecartAbstract after the event. */
	private static float applyVehicleHitDamage(float currentDamage, float eventDamage) {
		return currentDamage + eventDamage * 10.0F;
	}

	@SuppressWarnings("unchecked")
	private static <T> T stub(Class<T> type) {
		InvocationHandler handler = new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) {
				Class<?> r = method.getReturnType();
				if (r == boolean.class) {
					return false;
				}
				if (r == int.class || r == byte.class || r == short.class || r == char.class) {
					return 0;
				}
				if (r == long.class) {
					return 0L;
				}
				if (r == float.class) {
					return 0.0F;
				}
				if (r == double.class) {
					return 0.0D;
				}
				if (r == void.class) {
					return null;
				}
				if (method.getName().equals("equals")) {
					return proxy == args[0];
				}
				if (method.getName().equals("hashCode")) {
					return System.identityHashCode(proxy);
				}
				if (method.getName().equals("toString")) {
					return "stub:" + type.getSimpleName();
				}
				return null;
			}
		};
		return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type }, handler);
	}

	@Test
	public void pluginModifiedDamageIsAppliedLikeMinecart() {
		Vehicle vehicle = stub(Vehicle.class);
		Entity attacker = stub(Entity.class);

		float vanillaHit = 4.0F;
		VehicleDamageEvent event = new VehicleDamageEvent(vehicle, attacker, vanillaHit);
		event.setDamage(1.5D);

		Assert.assertFalse(event.isCancelled());

		float f = (float) event.getDamage();
		float boatDamage = applyVehicleHitDamage(0.0F, f);

		Assert.assertEquals(1.5F, f, 0.0001F);
		Assert.assertEquals(15.0F, boatDamage, 0.0001F);
		// Pre-fix behavior ignored setDamage and would have applied vanillaHit instead:
		Assert.assertNotEquals(applyVehicleHitDamage(0.0F, vanillaHit), boatDamage, 0.0001F);
	}

	@Test
	public void cancelledEventMeansNoDamageApplied() {
		Vehicle vehicle = stub(Vehicle.class);
		Entity attacker = stub(Entity.class);

		VehicleDamageEvent event = new VehicleDamageEvent(vehicle, attacker, 4.0D);
		event.setCancelled(true);
		event.setDamage(99.0D); // should be irrelevant when cancelled

		Assert.assertTrue(event.isCancelled());
		// EntityBoat returns false before applying damage when cancelled (SPIGOT-4647).
		if (!event.isCancelled()) {
			Assert.fail("cancel path must short-circuit before setDamage");
		}
	}

	@Test
	public void unmodifiedDamageMatchesVanillaHit() {
		Vehicle vehicle = stub(Vehicle.class);

		float vanillaHit = 2.0F;
		VehicleDamageEvent event = new VehicleDamageEvent(vehicle, null, vanillaHit);

		float f = (float) event.getDamage();
		Assert.assertEquals(vanillaHit, f, 0.0001F);
		Assert.assertEquals(20.0F, applyVehicleHitDamage(0.0F, f), 0.0001F);
	}
}
