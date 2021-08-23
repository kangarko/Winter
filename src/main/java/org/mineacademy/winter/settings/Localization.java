package org.mineacademy.winter.settings;

import org.bukkit.ChatColor;
import org.mineacademy.fo.model.BoxedMessage;
import org.mineacademy.fo.settings.SimpleLocalization;
import org.mineacademy.fo.settings.YamlConfig.CasusHelper;

@SuppressWarnings("unused")
public class Localization extends SimpleLocalization {

	@Override
	protected final int getConfigVersion() {
		return 1;
	}

	/**
	 * @see org.mineacademy.fo.settings.YamlConfig#saveComments()
	 */
	@Override
	protected boolean saveComments() {
		return false;
	}

	// --------------------------------------------------------------------------------------------------------
	// The actual implementation
	// --------------------------------------------------------------------------------------------------------

	public static class Chest {
		public static String NO_CHEST, NO_PLAYER;
		public static String INVALID_FORMAT;
		public static String CREATE_SUCCESS;
		public static String OPEN_OWN, OPEN_PUBLIC, OPEN_PRIVATE, OPEN_ADMIN;
		public static String EXPAND_OWN;
		public static String BREAK_OWN, BREAK_ADMIN, BREAK_OWN_SIGN, BREAK_ADMIN_SIGN;
		public static String FORMAT_GIFT, FORMAT_DATED, FORMAT_TIMED;

		public static String TIMED_LIMIT, TIMED_NOT_READY;
		public static String DATED_LIMIT, DATED_NOT_READY;

		public static String ILLEGAL_PLACE, ILLEGAL_BREAK, ILLEGAL_BREAK_SIGN, ILLEGAL_EXPAND, ILLEGAL_ACCESS, ILLEGAL_INVENTORY_CLICK;

		private static void init() {
			pathPrefix("Chest");

			NO_CHEST = getString("Lacks_Chest");
			NO_PLAYER = getString("Lacks_Players");
			INVALID_FORMAT = getString("Invalid_Format");
			CREATE_SUCCESS = getString("Create_Success");

			OPEN_OWN = getString("Open.Own");
			OPEN_PUBLIC = getString("Open.Public");
			OPEN_PRIVATE = getString("Open.Private");
			OPEN_ADMIN = getString("Open.Admin");

			EXPAND_OWN = getString("Expand.Own");

			BREAK_OWN = getString("Break.Own");
			BREAK_ADMIN = getString("Break.Admin");
			BREAK_OWN_SIGN = getString("Break.Own_Sign");
			BREAK_ADMIN_SIGN = getString("Break.Admin_Sign");

			FORMAT_GIFT = getString("Format.Gift");
			FORMAT_TIMED = getString("Format.Timed");
			FORMAT_DATED = getString("Format.Dated");

			TIMED_LIMIT = getString("Timed.Exceeded_Limit");
			TIMED_NOT_READY = getString("Timed.Not_Ready");
			DATED_LIMIT = getString("Dated.Exceeded_Limit");
			DATED_NOT_READY = getString("Dated.Not_Ready");

			ILLEGAL_PLACE = getString("Illegal.Place");
			ILLEGAL_BREAK = getString("Illegal.Break");
			ILLEGAL_BREAK_SIGN = getString("Illegal.Break_Sign");
			ILLEGAL_EXPAND = getString("Illegal.Expand");
			ILLEGAL_ACCESS = getString("Illegal.Access");
			ILLEGAL_INVENTORY_CLICK = getString("Illegal.Inventory_Click");
		}
	}

	public static class Boxed {
		public static BoxedMessage CHEST_OPEN, CHEST_PREVIEW;

		private static void init() {
			pathPrefix("Boxed");

			BoxedMessage.LINE_COLOR = get("Border_Color", ChatColor.class);
			CHEST_OPEN = getBoxedMessage("Chest_Open");
			CHEST_PREVIEW = getBoxedMessage("Chest_Preview");
		}
	}

	public static class Commands {
		public static String SNOW_ON, SNOW_OFF;

		private static void init() {
			pathPrefix("Commands");

			SNOW_ON = getString("Snow_Enabled");
			SNOW_OFF = getString("Snow_Disabled");
		}
	}

	public static class Cases {
		public static CasusHelper PLAYER;
		public static CasusHelper LEVEL;
		public static CasusHelper HOUR;
		public static CasusHelper MINUTE;
		public static CasusHelper SECOND;
		public static CasusHelper LIFE;

		private static void init() {
			pathPrefix("Cases");

			PLAYER = getCasus("Player");
			LEVEL = getCasus("Level");
			HOUR = getCasus("Hour");
			MINUTE = getCasus("Minute");
			SECOND = getCasus("Second");
			LIFE = getCasus("Life");
		}
	}

	public static class Parts {
		public static String ON, BETWEEN, AND, AT;

		private static void init() {
			pathPrefix("Parts");

			ON = getString("On");
			BETWEEN = getString("Between");
			AND = getString("And");
			AT = getString("At");
		}
	}
}
