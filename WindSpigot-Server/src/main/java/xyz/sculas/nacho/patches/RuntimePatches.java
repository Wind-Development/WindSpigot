package xyz.sculas.nacho.patches;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.Plugin;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;

public class RuntimePatches {

	private static final Logger logger = Bukkit.getLogger();

	public static CompletableFuture<Boolean> applyProtocolLibPatch(Plugin plugin) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				logger.info("Patching ProtocolLib, please wait.");

				try {
					String[] tmp = plugin.getDescription().getVersion().split("\\.");
					if (Integer.parseInt(tmp[0]) <= 4 && Integer.parseInt(tmp[1]) <= 6) {
						logger.warning("Please update to ProtocolLib version 4.7.0 or higher!\n"
								+ "In version 4.6.0 and lower, we have to do a nasty fix to make it work.\n"
								+ "So.. once again, please update!\n" + "You can update with this link: "
								+ "https://github.com/dmulloy2/ProtocolLib/releases/latest\n"
								+ "Sleeping for 10s so this message can be read.");
						Thread.sleep(10000);
					} else {
						logger.info(
								"It seems that you are using ProtocolLib version 4.7 or higher, which is supported!");
						logger.info("No need to patch ProtocolLib, skipping.");
						return true;
					}
				} catch (Exception ignored) {
				}

				ClassPool pool = ClassPool.getDefault();
				pool.insertClassPath(new LoaderClassPath(plugin.getClass().getClassLoader()));

				CtClass defaultProtocolInjector = pool.get("com.comphenix.protocol.injector.netty.ProtocolInjector$1");
				if (defaultProtocolInjector.isFrozen()) {
					defaultProtocolInjector.defrost();
				}

				CtClass clazz = pool
						.makeClass(CraftServer.class.getClassLoader().getResourceAsStream("protpatch.class"));
				clazz.replaceClassName(clazz.getName(), "com.comphenix.protocol.injector.netty.ProtocolInjector$1");
				clazz.toClass(plugin.getClass().getClassLoader(), plugin.getClass().getProtectionDomain());

				logger.info("Successfully patched ProtocolLib!");
				return true;
			} catch (Exception e) {
				logger.warning("Could not patch ProtocolLib.");
				e.printStackTrace();
			}
			return false;
		});
	}

	public static CompletableFuture<Boolean> applyCitizensPatch(Plugin plugin) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				logger.info("Patching Citizens, please wait.");

				ClassPool pool = ClassPool.getDefault();
				pool.insertClassPath(new LoaderClassPath(plugin.getClass().getClassLoader()));
				pool.importPackage("io.netty.channel.ChannelMetadata");

				CtClass emptyChannel = pool.get("net.citizensnpcs.nms.v1_8_R3.network.EmptyChannel");
				if (emptyChannel.isFrozen()) {
					emptyChannel.defrost();
				}

				CtMethod metaData = emptyChannel.getDeclaredMethods("metadata")[0];
				metaData.setBody("{ return new ChannelMetadata(true); }");

				emptyChannel.toClass(plugin.getClass().getClassLoader(), plugin.getClass().getProtectionDomain());

				logger.info("Successfully patched Citizens!");
				return true;
			} catch (Exception e) {
				logger.warning("Could not patch Citizens.");
				e.printStackTrace();
			}
			return false;
		});
	}

}
