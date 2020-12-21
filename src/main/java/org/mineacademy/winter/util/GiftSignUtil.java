package org.mineacademy.winter.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.StrictList;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.winter.model.chest.DatedChest;
import org.mineacademy.winter.model.chest.GiftChest;
import org.mineacademy.winter.model.chest.TimedChest;
import org.mineacademy.winter.model.chest.WinterChest;
import org.mineacademy.winter.model.data.ChestData;
import org.mineacademy.winter.settings.Settings;

public class GiftSignUtil {

	public static final void registerSign(Player owner, Sign sign, String[] format) {
		updateSign(sign, format);

		ChestData.$().addSign(owner, sign);
	}

	public static final void updateSign(Sign sign, String[] lines) {
		Common.runLater(2, () -> {
			for (int i = 0; i < sign.getLines().length && i < lines.length; i++)
				sign.setLine(i, Common.colorize(lines[i]));

			sign.update(true, false);
		});
	}

	public static final WinterChest findChestFull(Chest chest) {
		WinterChest sign = findChestStrict(chest);

		if (sign == null)
			// If this is a double chest, try to find the sign on the other chest
			for (final BlockState side : getSideBlocks(chest))
				if (side instanceof Chest) {
					sign = findChestStrict((Chest) side);

					if (sign != null)
						break;
				}

		return sign;
	}

	public static final WinterChest findChestStrict(Chest chest) {
		for (final BlockState side : getSideBlocks(chest))
			if (CompMaterial.isWallSign(side.getType()) && side instanceof Sign) {
				final WinterChest sign = constructChest(chest, (Sign) side);

				if (sign != null)
					return sign;
			}

		return null;
	}

	public static final Chest findChest(Sign sign) {
		for (final BlockState side : getSideBlocks(sign))
			if (side.getType() != Material.AIR && side instanceof Chest)
				return (Chest) side;

		return null;
	}

	private static final StrictList<BlockState> getSideBlocks(BlockState state) {
		final StrictList<BlockState> found = new StrictList<>();
		final Block b = state.getBlock();

		Arrays
				.asList(BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH)
				.forEach(face -> found.add(b.getRelative(face).getState()));

		return found;
	}

	public static final WinterChest constructChest(Sign sign) {
		final Chest chest = findChest(sign);

		return chest == null ? null : constructChest(chest, sign);
	}

	public static final WinterChest constructChest(Chest chest, Sign sign) {
		Valid.checkNotNull(chest, "chest == null");
		Valid.checkNotNull(sign, "sign == null");

		final List<String> lines = new ArrayList<>();

		for (int i = 0; i < sign.getLines().length; i++)
			lines.add(Common.getOrEmpty(sign.getLine(i)));

		final String title = lines.isEmpty() ? "" : lines.remove(0).toLowerCase();
		final String[] linesArray = lines.toArray(new String[lines.size()]);

		// Our sign is used
		if (Valid.colorlessEquals(title, Settings.GiftChest.TITLE)) {
			final boolean generic = Valid.colorlessEquals(linesArray, Settings.GiftChest.Public.getFormat());

			return new GiftChest(chest, sign, generic, lines /* = receivers now */);
		} else if ("[dated]".equals(title))
			return DatedChest.make(chest, sign);

		else if ("[timed]".equals(title))
			return TimedChest.make(chest, sign);

		return null;
	}

}
