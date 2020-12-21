package org.mineacademy.winter.listener;

import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.winter.settings.Settings;

public class SnowmanTransformListener implements Listener {

	@EventHandler
	public void onMobSpawn(CreatureSpawnEvent e) {
		if (!Settings.ALLOWED_WORLDS.contains(e.getLocation().getWorld().getName()))
			return;

		if (Settings.Snowman.Transform.ALLOWED.contains(e.getEntityType()) && RandomUtil.chance(Settings.Snowman.Transform.CHANCE)) {
			e.setCancelled(true);

			e.getLocation().getWorld().spawn(e.getLocation(), Snowman.class);
		}
	}
}
