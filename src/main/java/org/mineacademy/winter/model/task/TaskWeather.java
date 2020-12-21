package org.mineacademy.winter.model.task;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.winter.settings.Settings;
import org.mineacademy.winter.util.SnowStorm;

public final class TaskWeather extends BukkitRunnable {

	@Override
	public void run() {
		for (final World world : Bukkit.getWorlds()) {

			if (!Settings.ALLOWED_WORLDS.contains(world.getName()))
				continue;

			if (Settings.Weather.DISABLE) {
				world.setThundering(false);
				world.setThunderDuration(0);
				world.setWeatherDuration(0);

				continue;
			}

			if (Settings.Weather.SNOW_STORM)
				if (world.isThundering())
					SnowStorm.add(world);
				else
					SnowStorm.remove(world);
		}
	}
}