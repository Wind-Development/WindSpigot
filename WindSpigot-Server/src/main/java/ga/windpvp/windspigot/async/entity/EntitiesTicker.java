package ga.windpvp.windspigot.async.entity;

import java.util.List;

import net.minecraft.server.CrashReport;
import net.minecraft.server.CrashReportSystemDetails;
import net.minecraft.server.Entity;
import net.minecraft.server.ReportedException;
import net.minecraft.server.World;

public class EntitiesTicker {
	
	public void tick(List<Entity> entities, World world) {
		
		CrashReport crashreport;
		CrashReportSystemDetails crashreportsystemdetails;
		
		Entity entity;
		
		int i;
				
		for (i = 0; i < world.k.size(); ++i) {
			entity = world.k.get(i);
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
				world.k.remove(i--);
			}
			world.latch.decrement();
		}
		
	}

}
