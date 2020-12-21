package org.mineacademy.winter.commands;

import org.mineacademy.fo.command.ReloadCommand;
import org.mineacademy.fo.command.SimpleCommandGroup;

public final class WinterCommandGroup extends SimpleCommandGroup {

	@Override
	protected void registerSubcommands() {
		registerSubcommand(new SnowCommand());
		registerSubcommand(new PsychoCommand());
		registerSubcommand(new PopulateCommand());
		registerSubcommand(new ReloadCommand());
	}
}
