package org.mineacademy.winter.model.data;

import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.SerializeUtil;
import org.mineacademy.fo.TimeUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.collection.StrictMap;
import org.mineacademy.fo.collection.StrictSet;
import org.mineacademy.fo.constants.FoConstants;
import org.mineacademy.fo.exception.InvalidWorldException;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.settings.YamlSectionConfig;
import org.mineacademy.winter.model.chest.DatedChest;
import org.mineacademy.winter.model.chest.WinterChest;
import org.mineacademy.winter.settings.Settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

public class PlayerData extends YamlSectionConfig {

	// -----------------------------------------------------------------------------
	private final static PlayerData instance = new PlayerData();

	public static final PlayerData $() {
		return instance;
	}

	// -----------------------------------------------------------------------------

	public static final class PlayerDataFile implements ConfigSerializable {

		private final StrictMap<Location, ChestDataCache> openedChests = new StrictMap<>();

		public ChestDataCache get(Chest chest) {
			return openedChests.get(chest.getLocation());
		}

		public ChestDataCache onInteract(Chest chest) {
			final ChestDataCache cache = openedChests.getOrPut(chest.getLocation(), new ChestDataCache(0, 0));

			cache.onNewInteract();

			return cache;
		}

		@Override
		public SerializedMap serialize() {
			final SerializedMap map = new SerializedMap();

			map.put("chests", Common.convertStrict(openedChests.getSource(), new Common.MapToMapConverter<Location, ChestDataCache, String, Object>() {
				@Override
				public String convertKey(Location key) {
					return SerializeUtil.serializeLoc(key);
				}

				@Override
				public Object convertValue(ChestDataCache value) {
					return value.serialize();
				}
			}));

			return map;
		}

		public static PlayerDataFile deserialize(SerializedMap map) {
			val data = new PlayerDataFile();

			if (map.containsKey("chests"))
				for (final Entry<String, Object> e : map.getMap("chests").entrySet())
					try {
						final Location loc = SerializeUtil.deserializeLocation(e.getKey());
						final ChestDataCache cache = ChestDataCache.deserialize(SerializedMap.of(e.getValue()));

						data.openedChests.put(loc, cache);
					} catch (final InvalidWorldException ex) {
						continue;
					}

			return data;
		}
	}

	@Getter
	@AllArgsConstructor
	public static final class ChestDataCache implements ConfigSerializable {
		private long lastUsed;
		private int usedCount;

		private void onNewInteract() {
			lastUsed = TimeUtil.currentTimeSeconds();
			usedCount++;
		}

		public boolean isPreview(WinterChest chest) {
			return chest instanceof DatedChest && Settings.DatedChest.PREVIEW && usedCount == 1;
		}

		@Override
		public SerializedMap serialize() {
			final SerializedMap map = new SerializedMap();

			map.put("lastUsed", lastUsed);
			map.put("usedCount", usedCount);

			return map;
		}

		public static ChestDataCache deserialize(SerializedMap map) {
			final long lastUsed = map.getLong("lastUsed", new Long(0));
			final int count = map.getInteger("usedCount", 0);

			return new ChestDataCache(lastUsed, count);
		}
	}

	// -----------------------------------------------------------------------------

	private final StrictMap<UUID, PlayerDataFile> data = new StrictMap<>();
	private final StrictSet<UUID> snowDisabled = new StrictSet<>();

	protected PlayerData() {
		super(null);

		loadConfiguration(NO_DEFAULT, FoConstants.File.DATA);
	}

	@Override
	protected void onLoadFinish() {
		{ // Load players
			data.clear();

			for (final Entry<String, Object> e : getMap("Players").asMap().entrySet())
				data.put(UUID.fromString(e.getKey()), PlayerDataFile.deserialize(SerializedMap.of(e.getValue())));
		}

		{ // Load snow
			snowDisabled.clear();

			for (final String e : getStringList("Snow_Disabled"))
				snowDisabled.add(UUID.fromString(e));
		}
	}

	public final void onBreak(Chest chest) {
		boolean found = false;

		for (final PlayerDataFile f : data.values())
			inner:
			for (final Location l : f.openedChests.keySet())
				if (Valid.locationEquals(l, chest.getLocation())) {
					f.openedChests.remove(l);

					found = true;
					break inner;
				}

		if (found)
			saveChestData();
	}

	public final ChestDataCache onInteract(Player owner, Chest chest) {
		val cache = data.getOrPut(owner.getUniqueId(), new PlayerDataFile());
		val chestData = cache.onInteract(chest);

		saveChestData();
		return chestData;
	}

	private void saveChestData() {
		save("Players", Common.convertStrict(data.getSource(), new Common.MapToMapConverter<UUID, PlayerDataFile, String, Object>() {

			@Override
			public String convertKey(UUID key) {
				return key.toString();
			}

			@Override
			public Object convertValue(PlayerDataFile value) {
				return value.serialize();
			}
		}));

		onLoadFinish();
	}

	public final ChestDataCache getWeak(Player owner, Chest chest) {
		return data.contains(owner.getUniqueId()) ? data.get(owner.getUniqueId()).get(chest) : null;
	}

	public final boolean hasSnowEnabled(Player player) {
		return !snowDisabled.contains(player.getUniqueId());
	}

	public final void setSnowEnabled(Player player, boolean enabled) {
		if (enabled)
			snowDisabled.remove(player.getUniqueId());
		else
			snowDisabled.add(player.getUniqueId());

		saveSnowData();
	}

	private void saveSnowData() {
		save("Snow_Disabled", Common.convert(snowDisabled.getSource(), UUID::toString));

		onLoadFinish();
	}
}
