package ga.windpvp.windspigot.async.entity;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.server.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.World;

@ThreadSafe // Multiple worlds can group entities at the same time

// This is a utility class to group entities for ticking on separate threads.
public class EntityBatcher {
	
	// TODO: Simplify this a lot and try to use less looping
	
	private static EntityBatcher INSTANCE;
	
	public EntityBatcher() {
		INSTANCE = this;
	}
	
	private List<Entity> getBatch(List<List<Entity>> batch, Entity entity, int distance) {
		// Checks if the entity is close to other entities that are already batched
		for (List<Entity> entityBatch : batch) {
			for (Entity batchedEntity : entityBatch) {
				if (entity.getBukkitEntity().getLocation()
						.distanceSquared(batchedEntity.getBukkitEntity().getLocation()) <= distance) {

					return entityBatch;
				}
			}
		}
		return null;
	}

	// Gets lists of entities to tick on separate threads
	public List<List<Entity>> getGroupedEntities(List<Entity> entities, World world) {

		List<List<Entity>> finalList = Lists.newArrayList();
		List<Entity> entityListCopy = ImmutableList.copyOf(entities);

		// View distance squared in blocks
		int distance = (int) Math.pow(world.spigotConfig.viewDistance * 16, 2);

		// Group each entity
		for (Entity entity : entityListCopy) {

			List<Entity> entityBatch = null;
			
			// Put the first entity and its nearby entities in a new group
			if (finalList.size() == 0) {

				entityBatch = Lists.newArrayList();
				entityBatch.add(entity);

				// Scan for nearby entities
				for (Entity entity1 : entities) {
					if (entity != entity1 && entity.getBukkitEntity().getLocation()
							.distanceSquared(entity1.getBukkitEntity().getLocation()) <= distance) {
						entityBatch.add(entity1);
					}
				}

				finalList.add(entityBatch);

			} else {
				boolean shouldBreak = false;

				// Scan if the entity is close to already batched entities
				entityBatch = getBatch(finalList, entity, distance);
				
				// Batch the entity
				if (entityBatch != null) {
					entityBatch.add(entity);
				}
				
				shouldBreak = true;
				
				if (!shouldBreak) {
					
					entityBatch = null;

					// See if nearby entities are batchable, then batch both the nearby entity and the original entity
					for (Entity entity1 : entities) {
						if (entity != entity1 && entity.getBukkitEntity().getLocation()
								.distanceSquared(entity1.getBukkitEntity().getLocation()) <= distance) {
							
							// Put in same batch as already batched entities
							if (entityBatch != null) {
								entityBatch.add(entity1);
							}
							
							// Check if batchable
							entityBatch = getBatch(finalList, entity1, distance);
							if (entityBatch != null) {
								
								// Add to batch
								entityBatch.add(entity1);
								
								if (!entityBatch.contains(entity)) {
									entityBatch.add(entity);
								}
								shouldBreak = true;
							}
						}
					}
					
					// Create a new batch only if the entity and its nearby entities are not batchable
					if (!shouldBreak) {
						
						// New batch
						entityBatch = Lists.newArrayList();
						
						// Scan for nearby entities and add to batch
						for (Entity entity1 : entities) {
							
							// Add nearby entities to batch
							if (entity != entity1 && entity.getBukkitEntity().getLocation()
									.distanceSquared(entity1.getBukkitEntity().getLocation()) <= distance) {
								
								entityBatch.add(entity1);
								
								if (!entityBatch.contains(entity)) {
									entityBatch.add(entity);
								}
							}
						}
						
						finalList.add(entityBatch);
					}

				}

			}

		}
		
		List<List<Entity>> finalListCopy = ImmutableList.copyOf(finalList);
		
		boolean shouldBreak = false;

		// Correct any errors made by the initial batch (for example entity1 is close to
		// entity2 which is close to entity3 which is close to entity4, entity4 is
		// batched with entity3 and not entity 2 or entity1
		for (List<Entity> entityBatch : finalListCopy) {
			
			// For each entity scan if it is close to another entity in another batch
			for (Entity entity : entityBatch) {
				
				// Scan all other batches
				for (List<Entity> entityBatch1 : finalList) {
					
					// Make sure to not scan own batch
					if (entityBatch != entityBatch1) {
						
						// Scan every single entity in other batches
						for (Entity entity1 : entityBatch1) {
							
							// If it is close add all entities from the original batch into that batch
							if (entity.getBukkitEntity().getLocation()
									.distanceSquared(entity1.getBukkitEntity().getLocation()) <= distance) {
								
								entityBatch1.addAll(entityBatch);
								
								// Stop distance checking
								shouldBreak = true;
								break;
								
							}
						}
						// Stop distance checking
						if (shouldBreak) {
							shouldBreak = false;
							break;
						}
					}
				}
			}
		}
		
		// System.out.println(finalList.size()); // Debug purposes

		return finalList;
	}
	
	public ForkJoinTask<?> prepareTick() {
		return ForkJoinPool.commonPool().submit(() -> {
			for (World world : MinecraftServer.getServer().worlds) {
				MinecraftServer.getServer().entityTickLists.put(world, getGroupedEntities(world.k, world));
			}
		});
	}
	
	public static EntityBatcher getInstance() {
		return INSTANCE;
	}

}
