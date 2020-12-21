package org.mineacademy.winter.model.chest;

import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Valid;
import org.mineacademy.winter.model.data.ChestData;

public interface WinterChest extends ChestMatcher {

	default boolean isOwner(Player player) {
		Valid.checkNotNull(player, "player = null");

		return ChestData.$().getOwner(getSign()).equals(player);
	}

	Sign getSign();

	Chest getChest();
}