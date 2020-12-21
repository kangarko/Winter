package org.mineacademy.winter.model.chest;

import java.util.List;

import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Valid;
import org.mineacademy.winter.settings.Localization;
import org.mineacademy.winter.settings.Settings;
import org.mineacademy.winter.util.Permissions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class GiftChest implements WinterChest {

	private final Chest chest;
	private final Sign sign;

	private final boolean publicChest;
	private final List<String> receivers;

	public boolean canAccess(Player player) {
		Valid.checkBoolean(!publicChest, "Chest must not be generic!");
		final String name = player.getName().toLowerCase();

		for (final String line : receivers)
			if (line.toLowerCase().equals(name))
				return true;

		return false;
	}

	@Override
	public boolean isValidFormat(String[] lines) {
		final boolean empty = Common.getOrEmpty(lines[1]).isEmpty() && Common.getOrEmpty(lines[2]).isEmpty() && Common.getOrEmpty(lines[3]).isEmpty();

		if (empty && Settings.GiftChest.Public.ALLOW)
			return true;

		return !empty;
	}

	@Override
	public String getValidFormatExample() {
		return Localization.Chest.FORMAT_GIFT;
	}

	@Override
	public String getPermission() {
		return Permissions.Chest.GIFT;
	}
}