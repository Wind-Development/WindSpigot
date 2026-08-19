package com.destroystokyo.paper.event.player;

import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class PlayerHandshakeEventRegressionTest {

    @Test
    public void testPlayerHandshakeEventDefaults() {
        String originalHandshake = "play.example.com";
        PlayerHandshakeEvent event = new PlayerHandshakeEvent(originalHandshake, false);

        Assert.assertEquals(originalHandshake, event.getOriginalHandshake());
        Assert.assertEquals(originalHandshake, event.getServerHostname());
        Assert.assertNull(event.getSocketAddressHostname());
        Assert.assertNull(event.getUniqueId());
        Assert.assertNull(event.getPropertiesJson());
        Assert.assertFalse(event.isCancelled());
        Assert.assertFalse(event.isFailed());
    }

    @Test
    public void testPlayerHandshakeEventCustomProperties() {
        PlayerHandshakeEvent event = new PlayerHandshakeEvent("127.0.0.1", false);
        UUID uuid = UUID.randomUUID();

        event.setServerHostname("mc.example.com");
        event.setSocketAddressHostname("192.168.1.100");
        event.setUniqueId(uuid);
        event.setPropertiesJson("[{\"name\":\"textures\",\"value\":\"xyz\"}]");

        Assert.assertEquals("mc.example.com", event.getServerHostname());
        Assert.assertEquals("192.168.1.100", event.getSocketAddressHostname());
        Assert.assertEquals(uuid, event.getUniqueId());
        Assert.assertEquals("[{\"name\":\"textures\",\"value\":\"xyz\"}]", event.getPropertiesJson());
    }
}
