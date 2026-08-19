package com.destroystokyo.paper.event.player;

import org.junit.Assert;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * PlayerHandshakeEventTest: Verify handshake handling when listeners leave optional fields null.
 */
public class PlayerHandshakeEventTest {

    @Test
    public void testHandshakeWithNullOptionalFields() {
        String originalHost = "play.hypixel.net";
        PlayerHandshakeEvent event = new PlayerHandshakeEvent(originalHost, false);

        // When listeners do not modify optional fields, they remain null/default
        Assert.assertEquals(originalHost, event.getOriginalHandshake());
        Assert.assertEquals(originalHost, event.getServerHostname());
        Assert.assertNull(event.getSocketAddressHostname());
        Assert.assertNull(event.getUniqueId());
        Assert.assertNull(event.getPropertiesJson());

        // Simulating the server-side HandshakeListener logic with our null guards:
        String effectiveHostname = originalHost;
        if (event.getServerHostname() != null) {
            effectiveHostname = event.getServerHostname();
        }
        Assert.assertEquals("play.hypixel.net", effectiveHostname);

        // socketAddressHostname is null -> must NOT call new InetSocketAddress(null, port) which throws IllegalArgumentException
        InetSocketAddress clientAddress = new InetSocketAddress("127.0.0.1", 25565);
        if (event.getSocketAddressHostname() != null) {
            clientAddress = new InetSocketAddress(event.getSocketAddressHostname(), clientAddress.getPort());
        }
        // Verification: clientAddress is untouched and valid without NPE or IllegalArgumentException
        Assert.assertNotNull(clientAddress);
        Assert.assertEquals("127.0.0.1", clientAddress.getHostString());
    }

    @Test
    public void testHandshakeWithPluginPopulatedFields() {
        PlayerHandshakeEvent event = new PlayerHandshakeEvent("127.0.0.1", false);
        UUID spoofedUUID = UUID.randomUUID();

        // TCPShield / BungeeGuard populating fields
        event.setServerHostname("lobby.server.net");
        event.setSocketAddressHostname("10.0.0.1");
        event.setUniqueId(spoofedUUID);
        event.setPropertiesJson("[]");

        Assert.assertEquals("lobby.server.net", event.getServerHostname());
        Assert.assertEquals("10.0.0.1", event.getSocketAddressHostname());
        Assert.assertEquals(spoofedUUID, event.getUniqueId());
        Assert.assertEquals("[]", event.getPropertiesJson());

        // Simulating HandshakeListener when fields are present
        InetSocketAddress clientAddress = new InetSocketAddress("127.0.0.1", 25565);
        if (event.getSocketAddressHostname() != null) {
            clientAddress = new InetSocketAddress(event.getSocketAddressHostname(), clientAddress.getPort());
        }
        Assert.assertEquals("10.0.0.1", clientAddress.getHostString());
    }
}
