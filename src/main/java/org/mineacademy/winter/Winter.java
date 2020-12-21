package org.mineacademy.winter;

import java.util.Arrays;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.MinecraftVersion;
import org.mineacademy.fo.MinecraftVersion.V;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.model.SpigotUpdater;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.settings.YamlStaticConfig;
import org.mineacademy.winter.commands.WinterCommandGroup;
import org.mineacademy.winter.hook.ProtocolLibBiomeHook;
import org.mineacademy.winter.listener.ChestListener;
import org.mineacademy.winter.listener.MeltingListener;
import org.mineacademy.winter.listener.SnowmanDamageListener;
import org.mineacademy.winter.listener.SnowmanDealDamageListener;
import org.mineacademy.winter.listener.SnowmanTargetListener;
import org.mineacademy.winter.listener.SnowmanTransformListener;
import org.mineacademy.winter.listener.WinterListener;
import org.mineacademy.winter.model.data.ChestData;
import org.mineacademy.winter.model.data.PlayerData;
import org.mineacademy.winter.model.task.TaskParticleSnow;
import org.mineacademy.winter.model.task.TaskTerrain;
import org.mineacademy.winter.model.task.TaskWeather;
import org.mineacademy.winter.psycho.PsychoMob;
import org.mineacademy.winter.settings.Localization;
import org.mineacademy.winter.settings.Settings;
import org.mineacademy.winter.util.SnowStorm;

import lombok.Getter;

public class Winter extends SimplePlugin {

	@Getter
	private final WinterCommandGroup mainCommand = new WinterCommandGroup();

	@Override
	public V getMinimumVersion() {
		return V.v1_7;
	}

	@Override
	protected String[] getStartupLogo() {
		return new String[] {
				"&f_ _ _ _ _  _ ___ ____ ____ ",
				"&f| | | | |\\ |  |  |___ |__/ ",
				"&7|_|_| | | \\|  |  |___ |  \\ ",
				" ",
		};
	}

	@Override
	protected void onPluginStart() {
		Common.runLater(ChestData::$);

		registerEvents(new WinterListener());
		registerEvents(new ChestListener());
		registerEventsIf(new PsychoMob(), PsychoMob.IS_COMPATIBLE);

		Common.log(
				"&fGet Support:",
				"&6https://github.com/kangarko/Winter/issues",
				Common.consoleLineSmooth());
	}

	@Override
	protected void onPluginReload() {
		PlayerData.$();
		ChestData.$();
	}

	@Override
	protected void onReloadablesStart() {

		// Events
		if (Settings.Snowman.DISABLE_MELT_DAMAGE)
			registerEvents(new SnowmanDamageListener());

		if (Settings.Snowman.PREVENT_TARGET)
			registerEvents(new SnowmanTargetListener());

		if (Settings.Snowman.Transform.ENABLED)
			registerEvents(new SnowmanTransformListener());

		if (Settings.Snowman.Damage.SNOWBALL > 0)
			registerEvents(new SnowmanDealDamageListener());

		if (!Settings.Terrain.PREVENT_MELTING.isEmpty())
			registerEvents(new MeltingListener());

		if (!Settings.Weather.SNOW_STORM)
			SnowStorm.cleanAll();

		// Packets
		if (Settings.Terrain.Biomes.ENABLED && MinecraftVersion.atLeast(V.v1_11))
			if (HookManager.isProtocolLibLoaded())
				new ProtocolLibBiomeHook();
			else
				Common.log("&cCannot enable disguising biomes because the plugin ProtocolLib is missing ...");

		// Tasks
		if (Settings.Snow.ENABLED)
			Common.runTimer(20, Settings.Snow.PERIOD, new TaskParticleSnow());

		if (Settings.Terrain.SnowGeneration.ENABLED)
			Common.runTimer(20, Settings.Terrain.SnowGeneration.PERIOD, new TaskTerrain());

		if (Settings.Weather.DISABLE || Settings.Weather.SNOW_STORM)
			Common.runTimer(20, 20 * 10, new TaskWeather());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onWeatherChange(WeatherChangeEvent event) {
		if (!event.toWeatherState())
			return;

		if (Settings.Weather.DISABLE && Settings.ALLOWED_WORLDS.contains(event.getWorld().getName())) {
			event.setCancelled(true);

			event.getWorld().setWeatherDuration(0);
			event.getWorld().setThundering(false);
		}
	}

	@Override
	public SpigotUpdater getUpdateCheck() {
		return new SpigotUpdater(49646);
	}

	@Override
	public final List<Class<? extends YamlStaticConfig>> getSettings() {
		return Arrays.asList(Settings.class, Localization.class);
	}

	@Override
	public final int getFoundedYear() {
		return 2017; // 15.11.2017 - 16.11 released beast!
	}
}
