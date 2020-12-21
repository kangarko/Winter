package org.mineacademy.winter.model.chest;

import java.util.Arrays;

import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.TimeUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.exception.FoException;
import org.mineacademy.winter.model.data.PlayerData;
import org.mineacademy.winter.model.data.PlayerData.ChestDataCache;
import org.mineacademy.winter.settings.Localization;
import org.mineacademy.winter.util.Permissions;
import org.mineacademy.winter.util.WinterUtil;

public final class TimedChest extends InfiniteChest {

	public static final ChestMatcher MATCHER = new TimedChest(null, null, 0, 0);

	private final long delaySeconds;
	private final int tries;

	private TimedChest(Sign sign, Chest chest, long delaySeconds, int tries) {
		super(chest, sign);

		this.delaySeconds = delaySeconds;
		this.tries = tries;
	}

	@Override
	public boolean canOpen(Player player) {
		final ChestDataCache chestData = PlayerData.$().getWeak(player, getChest());

		if (chestData == null)
			return true;

		if (tries != -1 && chestData.getUsedCount() >= tries)
			return false;

		return TimeUtil.currentTimeSeconds() - chestData.getLastUsed() > delaySeconds;
	}

	@Override
	public String getNoUseMessage(Player player) {
		final ChestDataCache chestData = PlayerData.$().getWeak(player, getChest());
		Valid.checkNotNull(chestData);

		if (tries != -1 && chestData.getUsedCount() >= tries)
			return Localization.Chest.TIMED_LIMIT.replace("{limit}", tries + "");

		return Localization.Chest.TIMED_NOT_READY.replace("{time}", WinterUtil.formatTime((int) (delaySeconds - (TimeUtil.currentTimeSeconds() - chestData.getLastUsed())) + 1));
	}

	@Override
	public boolean isValidFormat(String[] lines) {
		lines = Common.replaceNullWithEmpty(lines);

		final String delay = lines[1];

		if (delay.isEmpty() || !Valid.isInListContains(delay, Arrays.asList("d", "h", "m", "s")))
			return false;

		if (!lines[2].isEmpty())
			try {
				Integer.parseInt(lines[2]);
			} catch (final NumberFormatException ex) {
				return false;
			}

		return true;
	}

	@Override
	public String getValidFormatExample() {
		return Localization.Chest.FORMAT_TIMED;
	}

	@Override
	public String getPermission() {
		return Permissions.Chest.TIMED;
	}

	public static InfiniteChest make(Chest chest, Sign sign) {
		long delay;

		{
			final String delayRaw = Common.getOrEmpty(sign.getLine(1)).trim();

			if (delayRaw.contains("d"))
				delay = Integer.parseInt(delayRaw.replace("d", "")) * 24 * 60 * 60;
			else if (delayRaw.contains("h"))
				delay = Integer.parseInt(delayRaw.replace("h", "")) * 60 * 60;
			else if (delayRaw.contains("m"))
				delay = Integer.parseInt(delayRaw.replace("m", "")) * 60;
			else if (delayRaw.contains("s"))
				delay = Integer.parseInt(delayRaw.replace("s", ""));
			else
				throw new FoException("Invalid timed chest delay: " + delayRaw);
		}

		int tries = -1;

		{
			final String triesRaw = Common.getOrEmpty(sign.getLine(2)).trim();

			if (!triesRaw.isEmpty())
				tries = Integer.parseInt(triesRaw);
		}

		return new TimedChest(sign, chest, delay, tries);
	}
}