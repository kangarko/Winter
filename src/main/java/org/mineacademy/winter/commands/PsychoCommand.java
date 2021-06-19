package org.mineacademy.winter.commands;

import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.winter.psycho.PsychoMob;

public final class PsychoCommand extends SimpleSubCommand {

	public PsychoCommand() {
		super("psycho|ps");

		setDescription("Spawn a Psycho (Warning: He kills players).");
	}

	@Override
	protected void onCommand() {
		checkConsole();
		checkBoolean(PsychoMob.IS_COMPATIBLE, "Psycho is not available for your Minecraft version. Currently requires MC " + PsychoMob.COMPATIBLE);

		try {
			PsychoMob.spawn(getPlayer().getLocation());
			tell("&cPsycho &7has been summoned at your location!");

		} catch (final Throwable t) {
			tell("&cError spawning Psycho, please check the console and report!");
		}
	}
}