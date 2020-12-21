package org.mineacademy.winter.hook;

import org.bukkit.block.Biome;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.remain.CompBiome;
import org.mineacademy.winter.settings.Settings;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

public final class ProtocolLibBiomeHook {

	private static final int BIOME_ARRAY_LENGTH = 256;

	public ProtocolLibBiomeHook() {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(SimplePlugin.getInstance(), PacketType.Play.Server.MAP_CHUNK) {

			@Override
			public void onPacketSending(final PacketEvent e) {
				final PacketContainer packet = e.getPacket();

				if (Settings.ALLOWED_WORLDS.contains(e.getPlayer().getWorld().getName())) {
					final byte[] data = packet.getByteArrays().read(0);
					final boolean hasContinous = Common.getOrDefault(packet.getBooleans().readSafely(0), true);

					if (data != null && hasContinous)
						translateBiomeData(data);
				}
			}
		});
	}

	private void translateBiomeData(final byte[] data) {
		final int biomeStart = data.length - BIOME_ARRAY_LENGTH;

		for (int i = biomeStart; i < data.length; i++) {
			final Biome biome = CompBiome.getBiomeByID(data[i]);

			if (biome != null)
				data[i] = Settings.Terrain.Biomes.BIOME_ID;

			else
				data[i] = 0;
		}
	}
}