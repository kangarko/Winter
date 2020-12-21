package org.mineacademy.winter.model.chest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.exception.FoException;
import org.mineacademy.fo.menu.model.InventoryDrawer;
import org.mineacademy.fo.remain.CompSound;
import org.mineacademy.winter.model.data.PlayerData;
import org.mineacademy.winter.model.data.PlayerData.ChestDataCache;
import org.mineacademy.winter.settings.Localization;
import org.mineacademy.winter.settings.Settings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class InfiniteChest implements WinterChest {

	public static final String TITLE = Common.colorize("&0Gift Chest");
	public static final String TITLE_PREVIEW = Common.colorize("&0Gift Chest Preview");

	private final Chest chest;
	private final Sign sign;

	public abstract boolean canOpen(Player player);

	public abstract String getNoUseMessage(Player player);

	public final boolean onTryOpen(Player player) {
		if (!canOpen(player)) {
			Common.tell(player, getNoUseMessage(player));

			return false;
		}

		final ChestDataCache data = PlayerData.$().onInteract(player, getChest());
		final InventoryDrawer inv = InventoryDrawer.of(chest.getInventory().getSize(), data.isPreview(this) ? TITLE_PREVIEW : TITLE);

		inv.setContent(chest.getInventory().getContents());
		inv.display(player);

		if (data.isPreview(this)) {
			Localization.Boxed.CHEST_PREVIEW.tell(player);
			CompSound.ENDERDRAGON_WINGS.play(player, 1, 1);

		} else {
			Localization.Boxed.CHEST_OPEN.tell(player);
			CompSound.LEVEL_UP.play(player, 1, 1);
		}

		return true;
	}

	protected static long parseDate(String string) {
		final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM");
		final Calendar c = new GregorianCalendar();

		try {
			c.setTime(formatter.parse(string));
		} catch (final ParseException e) {
			throw new FoException(e);
		}

		c.set(Calendar.YEAR, Settings.DatedChest.DEFAULT_YEAR);

		return c.getTimeInMillis();
	}

	protected static long parseHour(String string) {
		final String[] parts = string.split("\\:");
		final long hoursInMins = Long.parseLong(parts[0]) * 60;
		final long minutes = Long.parseLong(parts[1]);

		return hoursInMins + minutes;
	}
}