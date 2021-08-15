package org.mineacademy.winter.model.task;

import static org.mineacademy.winter.settings.Settings.ALLOWED_WORLDS;
import static org.mineacademy.winter.settings.Settings.Snow.CHAOS;
import static org.mineacademy.winter.settings.Settings.Snow.RANGE_H;
import static org.mineacademy.winter.settings.Settings.Snow.RANGE_V;
import static org.mineacademy.winter.settings.Settings.Snow.REALISTIC;
import static org.mineacademy.winter.settings.Settings.Snow.REQUIRE_SNOW_BIOMES;

import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompParticle;
import org.mineacademy.fo.remain.Remain;
import org.mineacademy.winter.model.data.PlayerData;
import org.mineacademy.winter.settings.Settings;
import org.mineacademy.winter.util.SnowStorm;

import lombok.SneakyThrows;

/**
 * The task that sends snow particles around players
 */
public final class TaskParticleSnow extends BukkitRunnable {

	/**
	 * Player cache for all players, used for /winter snow to toggle snow per-player
	 */
	private final PlayerData playerData = PlayerData.$();

	/**
	 * Monitors server's TPS and limits particles
	 */
	private final ParticleLimiter limiter = new ParticleLimiter();

	/**
	 * Heartbeat to send particles
	 */
	@Override
	public void run() {
		try {
			for (final Player pl : Remain.getOnlinePlayers()) {
				if (!checkPlayer(pl))
					continue;

				for (int i = 0; i < limiter.getAmountOfParticles(); ++i) {
					final Location loc = randomizeLocation(pl.getLocation());

					if (checkLocation(loc))
						sendParticle(pl, loc);
				}
			}
		} catch (final Throwable t) {
			Common.error(t, "Error showing snow particles!", "Stopping task for safety.", "%error");

			cancel();
		}
	}

	private boolean checkPlayer(Player player) {
		if (!ALLOWED_WORLDS.contains(player.getWorld().getName()))
			return false;

		if (!playerData.hasSnowEnabled(player))
			return false;

		if (Settings.Snow.IGNORE_VANISHED && (PlayerUtil.isVanished(player) || player.getGameMode().toString().equals("SPECTATOR"))) // Compatibility
			return false;

		return true;
	}

	private boolean checkLocation(Location l) {

		final Biome biome = l.getBlock().getBiome();

		// Snow biomes
		if (REQUIRE_SNOW_BIOMES) {
			final String b = biome.toString();

			// using Arrays.asList is not good for time-sensitive checks - about 80% faster using this method
			if (!(l.getY() > 120 || b.contains("COLD") || b.contains("ICE") || b.contains("FROZEN") || l.getY() > 85 && b.contains("HILLS")))
				return false;
		}

		if (Settings.Terrain.SnowGeneration.IGNORE_BIOMES.contains(biome))
			return false;

		// Under the roof
		if (REALISTIC && findHighestBlock(l.getWorld(), l.getBlockX(), l.getBlockZ()) > l.getY())
			return false;

		// On the ground
		{
			final Block b = l.getBlock();
			final Block d = b.getRelative(BlockFace.DOWN);

			if (!b.isEmpty() || !d.isEmpty())
				return false;
		}

		return true;
	}

	private int findHighestBlock(World w, int x, int z) {
		for (int y = w.getMaxHeight() - 1; y > 0; --y) {
			final Material type = w.getBlockAt(x, y, z).getType();

			if (!CompMaterial.isAir(type) && !type.isTransparent())
				return y + 1;
		}

		return -1;
	}

	private Location randomizeLocation(Location loc) {
		return loc.clone().add(randomLoc(RANGE_H, RANGE_H), randomLoc(RANGE_V, RANGE_V), randomLoc(RANGE_H, RANGE_H));
	}

	private double randomLoc(int min, int max) {
		return -min + (max + min) * Math.random();
	}

	private void sendParticle(Player player, Location location) {
		final float speed = SnowStorm.has(location.getWorld()) ? 1F : CHAOS;

		CompParticle.FIREWORKS_SPARK.spawn(player, location, speed);
	}

	/**
	 * Limit maximum amount of particles when server's TPS is low.
	 */
	private class ParticleLimiter {

		/**
		 * Check server's TPS every now and then and limit maximum amount of particles when it drops.
		 */
		private final int CHECK_PERIOD_SECONDS = 20;

		/**
		 * The minimum TPS for maximum particles allowed
		 */
		private final int MINIMUM_TPS = 17;

		/**
		 * How many particles spawn when server is lagging?
		 */
		private final int LAG_PARTICLE_AMOUNT = MathUtil.range(8, Settings.Snow.AMOUNT, 8);

		private long lastTimeCheckedTPS = 0;
		private int lastTPS = -1;

		public final int getAmountOfParticles() {
			return shouldLimitParticles() ? LAG_PARTICLE_AMOUNT : Settings.Snow.AMOUNT;
		}

		private boolean shouldLimitParticles() {
			return getTps() < MINIMUM_TPS;
		}

		private int getTps() {
			if (lastTimeCheckedTPS != 0 && System.currentTimeMillis() - lastTimeCheckedTPS < CHECK_PERIOD_SECONDS * 1000)
				return lastTPS;

			lastTPS = TPSHelper.getTPS();
			lastTimeCheckedTPS = System.currentTimeMillis();

			return lastTPS;
		}
	}
}

// Only works on Paperspigot
class TPSHelper {
	private static final Method getTPS;

	static {
		Method method = null;

		try {
			method = Bukkit.class.getDeclaredMethod("getTPS", double[].class);
		} catch (final ReflectiveOperationException ex) {
		}

		getTPS = method;
	}

	@SneakyThrows
	protected static int getTPS() {
		return (int) MathUtil.floor(getTPS == null ? 20 : ((double[]) getTPS.invoke(null))[0]);
	}
}
