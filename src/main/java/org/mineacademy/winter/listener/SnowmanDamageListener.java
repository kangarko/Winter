package org.mineacademy.winter.listener;

import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.mineacademy.winter.settings.Settings;

public class SnowmanDamageListener implements Listener {

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (!Settings.ALLOWED_WORLDS.contains(e.getEntity().getWorld().getName()))
			return;

		if (e.getEntity() instanceof Snowman && e.getCause() == DamageCause.MELTING)
			e.setCancelled(true);
	}
}
