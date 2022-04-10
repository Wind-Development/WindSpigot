package ga.windpvp.windspigot.async.entity;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.Lists;

import net.minecraft.server.Entity;

@ThreadSafe // Multiple worlds can group entities at the same time

// This is a utility class to group entities for ticking on separate threads.
public class EntityGrouper {

	// Gets lists of entities to tick on separate threads
	public static CompletableFuture<List<List<Entity>>> getGroupedEntities(List<Entity> entities) {
		
		return CompletableFuture.supplyAsync(() -> {
			
			List<List<Entity>> finalList = Lists.newArrayList();
	
			int count = 0;
	
			for (Entity entity : entities) {
				if (count == 0) {
					List<Entity> newList = Lists.newArrayList();
	
					newList.add(entity);
					finalList.add(newList);
	
					count++;
					continue;
				}
	
				boolean shouldBreak = false;
	
				// Check if entity is close to another entity, then add it to that list if it is
				for (List<Entity> entityList : finalList) {
	
					// Loop through each entity in the list
					for (Entity entityInList : entityList) {
	
						if (entity.getBukkitEntity().getLocation()
								.distanceSquared(entityInList.getBukkitEntity().getLocation()) <= 160) { // TODO: tweak based on set view distance, not default 
							// Checks if entity is within the default view distance
	
							// Group the entity in the same list if within the view distance
							entityList.add(entity);
	
							shouldBreak = true;
	
							break;
						} else {
							
							// Put in new list if entity can be safely ticked separately
							List<Entity> newList = Lists.newArrayList();
	
							newList.add(entity);
							finalList.add(newList);
	
							shouldBreak = true;
	
							break;
						}
					}
					if (shouldBreak)
						break;
				}
	
				count++;
			}
	
			System.out.println(finalList.size()); // Debug purposes
	
			return finalList;
		});
	}

}
