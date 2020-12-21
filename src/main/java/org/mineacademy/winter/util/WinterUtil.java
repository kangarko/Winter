package org.mineacademy.winter.util;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.mineacademy.fo.BlockUtil;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.SimpleLocalization;
import org.mineacademy.winter.settings.Localization;
import org.mineacademy.winter.settings.Settings;

public class WinterUtil {

	public static boolean canMelt(Location loc) {
		if (Settings.Terrain.SnowGeneration.IGNORE_SNOWY) {
			final World w = loc.getWorld();
			final int x = loc.getBlockX(), z = loc.getBlockZ();
			final String biome = w.getBiome(x, z).toString();

			/* for details refer to https://minecraft.gamepedia.com/Biome#Temperature */
			final boolean naturallySnowy = biome.contains("SNOW") || biome.contains("FROZEN") || w.getTemperature(x, z) < 0.15 || biome.contains("MOUNTAINS") && loc.getBlockY() > 90;

			return !naturallySnowy;
		}

		return true;
	}

	public static final boolean canPlace(Block ground) {
		final Material m = ground.getType();
		final boolean can = m.isSolid() && !m.isTransparent() && !Valid.isInListContains(m.toString(), Settings.Terrain.SnowGeneration.IGNORE_PLACE);

		if (can && CompMaterial.isLongGrass(ground.getType()))
			ground.setType(Material.AIR);

		return can;
	}

	public static final boolean checkPerm(CommandSender pl, String permission) {
		return checkPerm(pl, permission, true);
	}

	public static final boolean checkPerm(CommandSender pl, String permission, boolean notify) {
		permission = permission.replace("{plugin_name}", SimplePlugin.getNamed().toLowerCase());

		final boolean has = PlayerUtil.hasPerm(pl, permission);

		if (!has && notify)
			Common.tell(pl, SimpleLocalization.NO_PERMISSION.replace("{permission}", permission));

		return has;
	}

	public static final String formatTime(int seconds) {
		final int second = seconds % 60;
		int minute = seconds / 60;
		String hourMsg = "";

		if (minute >= 60) {
			final int hour = seconds / 60 / 60;
			minute %= 60;

			hourMsg = Localization.Cases.HOUR.formatWithCount(hour) + " ";
		}

		return hourMsg + (minute > 0 ? Localization.Cases.MINUTE.formatWithCount(minute) + " " : "") + Localization.Cases.SECOND.formatWithCount(second);
	}

	/**
	 * Return a random location within the given chunk that is not covered by snow
	 *
	 *
	 * @param chunk
	 * @return
	 */
	public static Location nextLocationNoSnow(Chunk chunk) {
		final int x = RandomUtil.nextInt(16) + (chunk.getX() << 4)/* - 16*/;
		final int z = RandomUtil.nextInt(16) + (chunk.getZ() << 4)/* - 16*/;
		final double y = BlockUtil.findHighestBlockNoSnow(chunk.getWorld(), x, z);

		return y == -1 ? null : new Location(chunk.getWorld(), x, y, z);
	}
}
