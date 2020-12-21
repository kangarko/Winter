package org.mineacademy.winter.psycho;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.MinecraftVersion;
import org.mineacademy.fo.MinecraftVersion.V;
import org.mineacademy.winter.settings.Settings;

public class PsychoMob implements Listener {

	public static final V COMP_VERSION = V.v1_16;
	public static final boolean IS_COMPATIBLE;

	static {
		IS_COMPATIBLE = MinecraftVersion.equals(COMP_VERSION) && MinecraftVersion.getServerVersion().endsWith("R3");
	}

	public static final void spawn(Location loc) {
		new PsychoMob1_16(loc);
	}

	@EventHandler
	public void onSpawn(CreatureSpawnEvent e) {
		if (e.getEntityType() == EntityType.SNOWMAN && Settings.Snowman.Psycho.CONVERT_NEW) {
			if (e.getEntity().hasMetadata("DeadlySnowman"))
				return;

			Common.runLater(1, () -> {
				e.getEntity().remove();

				spawn(e.getLocation());
			});
		}
	}

	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e) {
		if (!Settings.Snowman.Psycho.CONVERT_EXISTING)
			return;

		for (final Entity en : e.getChunk().getEntities())
			if (en.getType() == EntityType.SNOWMAN) {
				if (en.hasMetadata("DeadlySnowman"))
					continue;

				final Location oldLoc = en.getLocation().clone();
				en.remove();

				spawn(oldLoc);
			}
	}
}
