package org.mineacademy.winter.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.MinecraftVersion;
import org.mineacademy.fo.MinecraftVersion.V;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.model.OfflineRegionScanner;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.remain.Remain;
import org.mineacademy.winter.WinterRegionCleaner;
import org.mineacademy.winter.util.Permissions;
import org.mineacademy.winter.util.WinterUtil;

public class PopulateCommand extends SimpleSubCommand {

	private boolean warned = false;

	public PopulateCommand() {
		super("populate|p");

		setDescription("Add or remove snow to your worlds.");
		setUsage("<add|remove> <world>");
		setMinArguments(2);
	}

	@Override
	protected final void onCommand() {
		checkBoolean(!(sender instanceof Player), "&cThis command requires execution from the console.");
		checkBoolean(MinecraftVersion.newerThan(V.v1_7), "Only MC 1.8.8 and up are supported (for safety). Please notify the developer to test out if the new MC version is safe to use.");

		final String param = args[0].toLowerCase();
		final String worldRaw = args[1];

		final World world = Bukkit.getWorld(worldRaw);
		checkNotNull(world, "World " + worldRaw + " does not exist. Available: " + StringUtils.join(getWorlds(), ", "));

		if (!warned) {
			warned = true;

			returnTell(
					Common.consoleLine(),
					" WARNING ABOUT REGION MANIPULATION",
					Common.consoleLine(),
					" ",
					"You are about to access and change your regions saved",
					"on the disk. *Every* such operation is potentially",
					"dangerous and requires special attention.",
					" ",
					"PLEASE MAKE SURE THAT,",
					"1) You have manually backed up your world file:",
					"  (if unsure, just clone the world folder to a different name)",
					"  " + world.getWorldFolder().getAbsolutePath(),
					" ",
					"2) No other region-related operation is running,",
					"   such as WorldEdit, VoxelSniper or related.",
					" ",
					"We will respect 'Do_Not_Place_On' and 'Ignore_Biomes'",
					"options in 'Snow_Generation' in your settings.yml.",
					" ",
					"All players will be kicked for safety. The operation may",
					"take *MINUTES TO HOURS* depending on your world size",
					"and hardware.",
					" ",
					"Each file is saved immediatelly after processing, so",
					"if server stops forcefully, data loss *should* not occur.",
					" ",
					"** If Spigot will complain about server not responding **",
					"** you can safely ignore this (we must run this on the main thread). **",
					" ",
					SimplePlugin.getNamed().toUpperCase() + " TAKES NO RESPONSIBILITY FOR YOUR DATA",
					"AND PROVIDES THIS FUNCTION WITHOUT ANY WARRANTY.",
					" ",
					Common.consoleLine(),
					" > Run the command again to proceed...",
					" > Estimated duration: " + WinterUtil.formatTime(OfflineRegionScanner.getEstimatedWaitTimeSec(world)),
					Common.consoleLine());
		}

		if ("add".equals(param))
			launch(false, world);

		else if ("remove".equals(param))
			launch(true, world);

		else
			tell("Unknown parameter '" + param + "'. Use 'add' to place snow or 'remove' to melt existing snow.");
	}

	private static long now = 0;

	private final void launch(boolean melt, World w) {
		Common.log("1/3 Kicking all players & enabling whitelist ...");

		for (final Player player : Remain.getOnlinePlayers())
			player.kickPlayer("Kicked due to server maintenance");

		Bukkit.setWhitelist(true);

		final WinterRegionCleaner cleaner = new WinterRegionCleaner();

		Common.log("2/3 Running region scan ...");
		now = System.currentTimeMillis();
		cleaner.launch(melt, w);
	}

	public static final void finish(World w) {
		Common.log("3/3 Disabling whitelist ...");
		Bukkit.setWhitelist(false);

		Common.log(Common.consoleLine());
		Common.log("Operation finished in " + WinterUtil.formatTime((int) (System.currentTimeMillis() - now) / 1000));
		Common.log(Common.consoleLine());

		now = 0;
	}

	@Override
	public final List<String> tabComplete() {
		if (!PlayerUtil.hasPerm(sender, Permissions.Commands.POPULATE))
			return null;

		if (args.length == 1)
			return completeLastWord("add", "remove");

		else if (args.length == 2)
			return completeLastWord(getWorlds());

		return new ArrayList<>();
	}

	private final List<String> getWorlds() {
		return Common.convert(Bukkit.getWorlds(), World::getName);
	}
}