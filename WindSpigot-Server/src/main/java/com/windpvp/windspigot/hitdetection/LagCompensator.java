package com.windpvp.windspigot.hitdetection;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.windpvp.windspigot.config.WindSpigotConfig;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

public class LagCompensator {

    /*
        Lag Compensator based on https://github.com/Islandscout/ServerSideHitDetection
        Adjusts hit detection to be more fair for players with worse ping.
     */

    // WindSpigot - GamingOP69 - registerMovement is called from Netty IO threads while
    // getHistoryLocation/clearCache are called from the main server thread.
    // ArrayListMultimap is not thread-safe; wrap it with synchronizedListMultimap.
    private final ListMultimap<UUID, Pair<Location, Long>> locationTimes =
            Multimaps.synchronizedListMultimap(ArrayListMultimap.create());
    private final int historySize = 40;
    private final int pingOffset = 92;
    private final int timeResolution = 30;

    // Gets an estimate location of the player at "rewindMillisecs" ago
    public Location getHistoryLocation(Player player, int rewindMillisecs) {
        // synchronizedListMultimap requires explicit sync during iteration
        synchronized (locationTimes) {
            if (!locationTimes.containsKey(player.getUniqueId()))
                return player.getLocation();

            List<Pair<Location, Long>> previousLocations = locationTimes.get(player.getUniqueId());
            long currentTime = System.currentTimeMillis();

            int rewindTime = rewindMillisecs + pingOffset;
            int timesSize = previousLocations.size() - 1;

            for (int i = timesSize; i >= 0; i--) {
                Pair<Location, Long> locationPair = previousLocations.get(i);
                int elapsedTime = (int) (currentTime - locationPair.getValue());

                if (elapsedTime >= rewindTime) {
                    if (i == timesSize)
                        return locationPair.getKey();

                    int maxRewindMilli = rewindMillisecs + pingOffset;
                    int millisSinceLoc = (int) (currentTime - locationPair.getValue());

                    double movementRelAge = millisSinceLoc - maxRewindMilli;
                    double millisSinceLastLoc = currentTime - previousLocations.get(i + 1).getValue();

                    double nextMoveWeight = movementRelAge / (millisSinceLoc - millisSinceLastLoc);
                    Location before = locationPair.getKey().clone();
                    Location after = previousLocations.get(i + 1).getKey();
                    Vector interpolate = after.toVector().subtract(before.toVector());

                    interpolate.multiply(nextMoveWeight);
                    before.add(interpolate);

                    return before;
                }
            }
        }

        return player.getLocation();
    }

    private void processPosition(Location loc, Player p) {
        if (!WindSpigotConfig.improvedHitDetection) return;

        // synchronizedListMultimap requires explicit sync during iteration/compound ops
        synchronized (locationTimes) {
            int timesSize = locationTimes.get(p.getUniqueId()).size();
            long currTime = System.currentTimeMillis();

            if (timesSize > 0 && currTime - locationTimes.get(p.getUniqueId()).get(timesSize - 1).getValue() < timeResolution)
                return;

            locationTimes.put(p.getUniqueId(), Pair.of(loc, currTime));

            if (timesSize > historySize)
                locationTimes.get(p.getUniqueId()).remove(0);
        }
    }


    public void registerMovement(Player player, Location to) {
        processPosition(to, player);
    }

    public void clearCache(Player player) {
        locationTimes.removeAll(player.getUniqueId());
    }


}