package org.mineacademy.winter.model.task;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.mineacademy.fo.collection.StrictList;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.Remain;
import org.mineacademy.winter.settings.Settings;
import org.mineacademy.winter.util.FreezeIgnore;
import org.mineacademy.winter.util.WinterUtil;

public class TaskTerrain implements Runnable {

	private final StrictList<Chunk> activeChunks = new StrictList<>();

	@Override
	public final void run() {
		loadChunks();

		final boolean melt = Settings.Terrain.SnowGeneration.MELT;

		for (final Chunk chunk : activeChunks) {
			final Location loc = WinterUtil.nextLocationNoSnow(chunk);

			if (loc == null)
				continue;

			final Block block = loc.getBlock();

			if (Settings.Terrain.SnowGeneration.IGNORE_BIOMES.contains(block.getBiome()))
				continue;

			final Block ground = block.getRelative(BlockFace.DOWN);
			final boolean canMelt = WinterUtil.canMelt(loc);

			if (Settings.Terrain.SnowGeneration.DESTROY_CROPS) {
				final Block ground2 = ground.getRelative(BlockFace.DOWN);

				if (ground2.getType() == CompMaterial.FARMLAND.getMaterial()) {
					ground.setType(Material.SNOW);
					ground2.setType(Material.DIRT);

					continue;
				}
			}

			if (block.getType() == Material.SNOW) {

				if (melt && canMelt && block.getData() == 0) {
					block.setType(Material.AIR);

					continue;
				}

				final int near = getNeighboorsSameLevel(block);
				boolean manipulate = melt ? block.getData() > 0 : block.getData() < 8;

				if (!melt && manipulate)
					manipulate = near >= Settings.Terrain.SnowGeneration.NEIGHBOR_MIN && Settings.Terrain.SnowGeneration.MULTI_LAYER;

				if (manipulate) {
					Remain.setData(block, block.getData() + (melt ? -1 : 1));

					continue;
				}
			}

			if (!melt && WinterUtil.canPlace(ground)) {
				block.setType(Material.SNOW);

				continue;
			}

			if (Settings.Terrain.SnowGeneration.FREEZE_WATER)
				if (melt) {
					if (canMelt && ground.getType() == Material.ICE)
						ground.setType(Material.WATER);

				} else if (canFreeze(ground) && (ground.getType() == Material.WATER || ground.getType().toString().equals("STATIONARY_WATER")))
					ground.setType(Material.ICE);
		}
	}

	private final boolean canFreeze(Block block) {
		for (final FreezeIgnore freeze : Settings.Terrain.SnowGeneration.IGNORE_FREEZE)
			if (!freeze.canFreeze(block))
				return false;

		return true;
	}

	private final int getNeighboorsSameLevel(Block block) {
		int count = 0;

		for (final BlockFace face : Arrays.asList(BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH)) {
			final Block rel = block.getRelative(face);

			if (rel.getType() == Material.SNOW && rel.getData() == block.getData())
				count++;
		}

		return count;
	}

	private final void loadChunks() {
		activeChunks.clear();

		final int radius = Settings.Terrain.SnowGeneration.RADIUS;
		int chunkX, chunkZ;

		for (final World world : Bukkit.getWorlds()) {
			if (!Settings.ALLOWED_WORLDS.contains(world.getName()))
				continue;

			for (final Player worldPlayers : world.getPlayers()) {
				chunkX = worldPlayers.getLocation().getBlockX() >> 4;
				chunkZ = worldPlayers.getLocation().getBlockZ() >> 4;

				for (int x = chunkX - radius; x <= chunkX + radius; x++)
					for (int z = chunkZ - radius; z <= chunkZ + radius; z++) {
						final Chunk ch = world.getChunkAt(x, z);

						if (!activeChunks.contains(ch))
							activeChunks.add(ch);
					}
			}
		}
	}
}