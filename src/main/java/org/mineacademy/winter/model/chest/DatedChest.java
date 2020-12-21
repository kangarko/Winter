package org.mineacademy.winter.model.chest;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.RangedValue;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.winter.model.data.PlayerData;
import org.mineacademy.winter.settings.Localization;
import org.mineacademy.winter.util.Permissions;

import lombok.val;

public final class DatedChest extends InfiniteChest {

	public static final ChestMatcher MATCHER = new DatedChest(null, null, null, null);

	private final RangedValue day;
	private final RangedValue hour;

	private DatedChest(Sign sign, Chest chest, RangedValue day, RangedValue hour) {
		super(chest, sign);

		this.day = day;
		this.hour = hour;
	}

	@Override
	public boolean canOpen(Player player) {
		val data = PlayerData.$().getWeak(player, getChest());

		if (data != null && data.getUsedCount() >= 2)
			return false;

		final Date now = new Date();

		if (!day.isWithin(now.getTime()))
			return false;

		return hour == null || hour.isWithin(now.getHours() * 60 + now.getMinutes());
	}

	@Override
	public String getNoUseMessage(Player player) {
		val data = PlayerData.$().getWeak(player, getChest());

		if (data != null && data.getUsedCount() >= 2)
			return Localization.Chest.DATED_LIMIT;

		final Date maxD = new Date(day.getMaxLong());
		final Date minD = new Date(day.getMinLong());

		final String min = minD.getDate() + "." + (minD.getMonth() + 1);
		final String max = maxD.getDate() + "." + (maxD.getMonth() + 1);

		final long hMinL = hour != null ? hour.getMinLong() : 0;
		final long hMaxL = hour != null ? hour.getMaxLong() : 0;

		final String hMin = hMinL / 60 + ":" + hMinL % 60;
		final String hMax = hMaxL / 60 + ":" + hMaxL % 60;

		final boolean sameDay = minD.getYear() == maxD.getYear() && minD.getMonth() == maxD.getMonth() && minD.getDay() == maxD.getDay();

		return Localization.Chest.DATED_NOT_READY.replace("{accessibility}",
				((sameDay ? "{on} " + min : "{between} " + min + " {and} " + max) + (hour != null ? hour.isStatic() ? " {between} " + hMin + " {and} " + hMax : " {at} " + hMin : ""))
						.replace("{on}", Localization.Parts.ON)
						.replace("{between}", Localization.Parts.BETWEEN)
						.replace("{and}", Localization.Parts.AND)
						.replace("{at}", Localization.Parts.AT));
	}

	@Override
	public boolean isValidFormat(String[] lines) {
		lines = Common.replaceNullWithEmpty(lines);

		final String delay = lines[1];

		if (delay.isEmpty() || !delay.contains("."))
			return false;

		if (!lines[2].isEmpty() && !lines[2].contains(":"))
			return false;

		return true;
	}

	@Override
	public String getValidFormatExample() {
		return Localization.Chest.FORMAT_DATED;
	}

	@Override
	public String getPermission() {
		return Permissions.Chest.DATED;
	}

	public static InfiniteChest make(Chest chest, Sign sign) {
		try {
			final String date = ChatColor.stripColor(sign.getLine(1)).replaceAll("\\s", "");

			RangedValue day;

			if (date.contains("-")) {
				final String[] parts = date.split("\\-");

				final long first = parseDate(parts[0]);
				long second = parseDate(parts[1]);

				if (first > second)
					second += TimeUnit.DAYS.toMillis(365);

				day = new RangedValue(first, second);

			} else {
				final long start = parseDate(date);
				day = new RangedValue(start, start + TimeUnit.DAYS.toMillis(1) - 1);
			}

			final String hours = ChatColor.stripColor(sign.getLine(2)).replaceAll("\\s", "");
			RangedValue hour = null;

			if (!hours.isEmpty())
				if (hours.contains("-")) {
					final String[] parts = hours.split("\\-");

					hour = new RangedValue(parseHour(parts[0]), parseHour(parts[1]));
				} else {
					final long start = parseHour(hours);
					hour = new RangedValue(start, start + 59);
				}

			return new DatedChest(sign, chest, day, hour);
		} catch (final NumberFormatException e) {
			SimplePlugin.getInstance().getLogger().warning("Failed to parse date and time for dated chest at " + chest.getLocation());
		}
		return null;
	}
}