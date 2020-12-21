package org.mineacademy.winter;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.mineacademy.fo.BlockUtil;
import org.mineacademy.fo.model.OfflineRegionScanner;
import org.mineacademy.winter.settings.Settings;
import org.mineacademy.winter.util.WinterUtil;

public class WinterRegionCleaner extends OfflineRegionScanner {

	private boolean melt;

	public final void launch(boolean melt, World w) {
		this.melt = melt;

		scan(w);
	}

	@Override
	protected void onChunkScan(Chunk ch) {

		for (final Block block : getBlocks(ch)) {
			if (Settings.Terrain.SnowGeneration.IGNORE_BIOMES.contains(block.getBiome()))
				continue;

			if (melt && !WinterUtil.canMelt(block.getLocation()))
				continue;

			final Block ground = block.getRelative(BlockFace.DOWN);

			if (block.getType() == Material.SNOW) {
				if (melt) {
					block.setType(Material.AIR);

					continue;
				}

			} else if (!melt && WinterUtil.canPlace(ground))
				block.setType(Material.SNOW);
			else if (Settings.Terrain.SnowGeneration.FREEZE_WATER)
				if (melt) {
					if (ground.getType() == Material.ICE)
						ground.setType(Material.WATER);
				} else if (ground.getType() == Material.WATER || ground.getType().toString().equals("STATIONARY_WATER"))
					ground.setType(Material.ICE);
		}
	}

	private final List<Block> getBlocks(Chunk chunk) {
		final List<Block> found = new ArrayList<>();

		final int cx = chunk.getX() << 4;
		final int cz = chunk.getZ() << 4;

		for (int x = cx; x < cx + 16; x++)
			for (int z = cz; z < cz + 16; z++) {
				final double y = BlockUtil.findHighestBlockNoSnow(chunk.getWorld(), x, z);

				if (y == -1)
					continue;

				final Location loc = new Location(chunk.getWorld(), x, y, z);

				found.add(loc.getBlock());
			}

		return found;
	}
}
