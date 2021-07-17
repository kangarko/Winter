package org.mineacademy.winter.psycho;

import org.bukkit.Bukkit;
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
import org.mineacademy.fo.exception.FoException;
import org.mineacademy.winter.settings.Settings;

public class PsychoMob implements Listener {

	public static final boolean IS_COMPATIBLE;
	public static final String COMPATIBLE = "1.16, 1.17";

	static {
		IS_COMPATIBLE = (MinecraftVersion.equals(V.v1_16) || MinecraftVersion.equals(V.v1_17)) && !Bukkit.getName().contains("Cauldron");
	}

	public static final void spawn(Location loc) {
		if (MinecraftVersion.equals(V.v1_17))
			new PsychoMob1_17(loc);

		else if (MinecraftVersion.equals(V.v1_16))
			new PsychoMob1_16(loc);

		else
			throw new FoException("Psycho mob is unsupported for MC version " + MinecraftVersion.getServerVersion());
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
	public void onChunkLoad(ChunkLoadEvent event) {
		if (!Settings.Snowman.Psycho.CONVERT_EXISTING)
			return;

		for (final Entity en : event.getChunk().getEntities())
			if (en.getType() == EntityType.SNOWMAN) {
				if (en.hasMetadata("DeadlySnowman"))
					continue;

				final Location oldLoc = en.getLocation().clone();
				en.remove();

				spawn(oldLoc);
			}
	}
}
