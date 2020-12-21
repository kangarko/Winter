package org.mineacademy.winter.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.winter.settings.Settings;

public class MeltingListener implements Listener {

	@EventHandler
	public void onBlockFade(BlockFadeEvent e) {
		if (!Settings.ALLOWED_WORLDS.contains(e.getBlock().getWorld().getName()))
			return;

		if (Settings.Terrain.PREVENT_MELTING.contains(CompMaterial.fromBlock(e.getBlock())))
			e.setCancelled(true);
	}
}
