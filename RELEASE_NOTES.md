# Release Notes - WindSpigot v2.1.5

## This update fixes

* **Explosion & Fireball Knockback**: Fixed inverted fireball knockback
  and incorrect explosion radius calculations by dynamically scaling to
  `f3 * f3` instead of hardcoding `64.0D`.
* **Outbound Packet Flushing**: Fixed outbound velocity and knockback
  packets being delayed or dropped by ensuring `NetworkManager.flush()`
  submits to the Netty event loop from non-I/O threads.
* **World Ticking & Memory Leaks**: Fixed memory leaks and stale world
  retention when loading and unloading dynamic worlds in `WorldTickManager`.
* **Async Entity Tracking Deadlock**: Fixed potential server freezes by
  enclosing tracker latch countdowns in `try-finally` blocks.
* **Entity Visibility Lag**: Removed the 5-second tracking delay
  (`disableTracking=100`) and restored responsive add/remove rates.
* **Mob Spawning & Mob Caps**: Fixed inverted boolean condition for
  spectators and restored active chunk-based mob counting in `SpawnerCreature`.
* **Block Placement Validation**: Fixed server crashes and exploits from
  malformed block placement packets with null directions or negative Y.
* **TCPShield / Handshake Compatibility**: Fixed `NullPointerException`
  when proxy listeners leave optional fields null in `PlayerHandshakeEvent`.
* **Process Queue Exception Safety**: Fixed server tick loop halts in
  `MinecraftServer.processQueue` while preserving JVM `Error` propagation.
* **Async Pathfinding Recovery**: Fixed frozen mob AI when pathfinding tasks
  are rejected by ensuring `isSearching` is always reset in `SearchHandler`.
* **Async Combat Thread Safety**: Fixed `NullPointerException` races in
  `CombatThread` and eliminated cross-player packet mixing in `Spigot404Write`.
* **Lag Compensator Data Race**: Fixed multi-threaded race conditions
  between Netty I/O threads and the main server thread in `LagCompensator`.
* **Player Spatial Map Crash**: Fixed `NullPointerException` in
  `PlayerMap.move()` during player teleportation or early joins.
* **Java 9-25 Compatibility**: Fixed reflection crashes in
  `CustomTimingsHandler` when running on modern Java versions (11, 17, 21, 25).
* **Shutdown Safety**: Fixed premature event loop termination and NPEs
  during server shutdown in `ServerConnection`.

## This update adds

* **Spatial Hash PlayerMap**: Replaced linear `Long2ObjectArrayMap` with
  O(1) `Long2ObjectOpenHashMap` for fast spatial entity lookups.
* **Zero-Allocation Concurrent Map**: Replaced lambda allocations in
  `ConcurrentIntHashMap` with direct synchronized methods.
* **Thread-Safe Explosion Density Cache**: Synchronized fastutil map
  caching for concurrent multi-threaded TNT density raytracing.
* **Thread-Safe Lazy Initialization**: Added double-checked locking to
  `LazyInitVar` to prevent duplicate Netty event loop group allocations.
* **Comprehensive Test Suite**: Added 14 unit and concurrency regression test
  suites covering combat, spawning, networking, and spatial indexing.

Special thanks to **GamingOP69** for all the core audits and optimizations!
