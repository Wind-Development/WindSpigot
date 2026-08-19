package com.windpvp.windspigot.world;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class WorldTickManagerLifecycleTest {

    static class MockWorld {
        final String name;
        final AtomicInteger tickCount = new AtomicInteger(0);

        MockWorld(String name) {
            this.name = name;
        }

        void tick() {
            tickCount.incrementAndGet();
        }
    }

    @Test
    public void testDynamicWorldUnloadDoesNotTickOldWorld() {
        List<MockWorld> loadedWorlds = new ArrayList<>();
        MockWorld worldOverworld = new MockWorld("world");
        MockWorld worldNether = new MockWorld("world_nether");

        loadedWorlds.add(worldOverworld);
        loadedWorlds.add(worldNether);

        // Tick cycle 1
        for (int i = 0; i < loadedWorlds.size(); i++) {
            loadedWorlds.get(i).tick();
        }

        Assert.assertEquals(1, worldOverworld.tickCount.get());
        Assert.assertEquals(1, worldNether.tickCount.get());

        // Unload Nether and load Arena (same size = 2)
        MockWorld worldArena = new MockWorld("world_arena");
        loadedWorlds.remove(worldNether);
        loadedWorlds.add(worldArena);

        // Tick cycle 2
        for (int i = 0; i < loadedWorlds.size(); i++) {
            loadedWorlds.get(i).tick();
        }

        Assert.assertEquals("Overworld ticked twice", 2, worldOverworld.tickCount.get());
        Assert.assertEquals("Unloaded Nether must NOT have ticked a second time", 1, worldNether.tickCount.get());
        Assert.assertEquals("Newly loaded Arena must have ticked once", 1, worldArena.tickCount.get());
    }
}
