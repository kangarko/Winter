package org.mineacademy.winter.util;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.mineacademy.fo.model.IsInList;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class FreezeIgnore {

	private final IsInList<String> neighbors;
	private final IsInList<String> crops;

	public boolean canFreeze(Block block) {
		for (final BlockFace f : Arrays.asList(BlockFace.SOUTH, BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST)) {
			final Block neighbor = block.getRelative(f);
			final Material t = neighbor.getType();

			if (neighbors.contains(t.toString())) {
				final Material up = neighbor.getRelative(BlockFace.UP).getType();

				if (crops.contains(up.toString()))
					return false;
			}
		}

		return true;
	}
}
