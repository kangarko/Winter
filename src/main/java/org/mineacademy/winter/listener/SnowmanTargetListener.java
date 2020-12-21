package org.mineacademy.winter.listener;

import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.mineacademy.winter.settings.Settings;

public class SnowmanTargetListener implements Listener {

	@EventHandler
	public void onTarget(EntityTargetLivingEntityEvent e) {
		if (!Settings.ALLOWED_WORLDS.contains(e.getEntity().getWorld().getName()))
			return;

		if (e.getTarget() instanceof Snowman)
			e.setCancelled(true);
	}
}
