package ga.windpvp.windspigot.async.entity;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import dev.cobblesword.nachospigot.commons.MCUtils;
import net.minecraft.server.CrashReport;
import net.minecraft.server.CrashReportSystemDetails;
import net.minecraft.server.Entity;
import net.minecraft.server.ReportedException;
import net.minecraft.server.World;

public class EntitiesTicker {
	
	private static EntitiesTicker INSTANCE;
	
	public EntitiesTicker() {
		INSTANCE = this;
	}
	
	public void tick(List<Entity> entities, World world) {
		
		CrashReport crashreport;
		CrashReportSystemDetails crashreportsystemdetails;
		
		Entity entity;
		
		int i;
		
		List<Entity> removeQueue = null;
				
		for (i = 0; i < entities.size(); ++i) {
			entity = entities.get(i);
			// CraftBukkit start - Fixed an NPE
			if (entity == null) {
				continue;
			}
			// CraftBukkit end

			try {
				++entity.ticksLived;
				entity.t_();
			} catch (Throwable throwable) {
				crashreport = CrashReport.a(throwable, "Ticking entity");
				crashreportsystemdetails = crashreport.a("Entity being ticked");
				if (entity == null) {
					crashreportsystemdetails.a("Entity", "~~NULL~~");
				} else {
					entity.appendEntityCrashDetails(crashreportsystemdetails);
				}

				throw new ReportedException(crashreport);
			}

			if (entity.dead) {
				if (removeQueue == null) {
					removeQueue = Lists.newArrayList();
				}
				removeQueue.add(entity);
				//world.k.remove(i--);
			}
		}
		
		if (removeQueue != null) {
			List<Entity> copy = ImmutableList.copyOf(removeQueue);
			MCUtils.ensureMain(() -> {
				for (Entity entityToRemove : copy) {
					world.k.remove(entityToRemove);
				}
			});
		}
		
		world.latch.countDown();
		
	}
	
	private ConcurrentMap<World, Runnable> runnableCache = Maps.newConcurrentMap();
	
	public Runnable getRunnable(final World world) {
		if (runnableCache.get(world) == null) {
			Runnable runnable = () -> {
				tick(world.k, world);
			};
			runnableCache.put(world, runnable); 
		}
		return runnableCache.get(world);
	}

	public static EntitiesTicker getInstance() {
		return INSTANCE;
	}
	
}
