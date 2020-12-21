package org.mineacademy.winter.commands;

import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.winter.model.data.PlayerData;
import org.mineacademy.winter.settings.Localization;

public final class SnowCommand extends SimpleSubCommand {

	public SnowCommand() {
		super("snow|s");

		setDescription("Toggle snow particles for yourself.");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		final PlayerData data = PlayerData.$();
		final boolean has = data.hasSnowEnabled(getPlayer());

		data.setSnowEnabled(getPlayer(), !has);

		tell(has ? Localization.Commands.SNOW_OFF : Localization.Commands.SNOW_ON);
	}
}