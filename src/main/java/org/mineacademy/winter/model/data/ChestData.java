package org.mineacademy.winter.model.data;

import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.mineacademy.fo.SerializeUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.StrictMap;
import org.mineacademy.fo.constants.FoConstants;
import org.mineacademy.fo.exception.InvalidWorldException;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.YamlSectionConfig;

public class ChestData extends YamlSectionConfig {

	// -----------------------------------------------------------------------------
	private final static ChestData instance = new ChestData();

	public static final ChestData $() {
		return instance;
	}
	// -----------------------------------------------------------------------------

	// Location, UUID
	private final StrictMap<String, String> signs = new StrictMap<>();

	protected ChestData() {
		super(null);

		loadConfiguration(NO_DEFAULT, FoConstants.File.DATA);
	}

	@Override
	protected void onLoadFinish() {
		signs.clear();

		for (final Entry<String, Object> e : getMap("Signs").asMap().entrySet())
			try {
				final Location loc = SerializeUtil.deserializeLocation(e.getKey());

				if (!CompMaterial.isWallSign(loc.getBlock().getType()))
					continue;

				signs.put(e.getKey(), e.getValue().toString());
			} catch (final InvalidWorldException ex) {
				continue;
			}
	}

	public final void addSign(Player owner, Sign sign) {
		signs.put(SerializeUtil.serializeLoc(sign.getLocation()), owner.getUniqueId().toString());

		save("Signs", signs);
		onLoadFinish();
	}

	public final void removeSign(Sign sign) {
		signs.remove(SerializeUtil.serializeLoc(sign.getLocation()));

		save("Signs", signs);
		onLoadFinish();
	}

	public final OfflinePlayer getOwner(Sign sign) {
		final String loc = SerializeUtil.serializeLoc(sign.getLocation());
		Valid.checkBoolean(signs.contains(loc), "No sign registered at [" + loc + "]");

		final OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(signs.get(loc)));
		Valid.checkNotNull(owner, "Could not find owner from " + signs.get(loc));

		return owner;
	}
}
