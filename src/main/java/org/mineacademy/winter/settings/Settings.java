package org.mineacademy.winter.settings;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.MinecraftVersion;
import org.mineacademy.fo.MinecraftVersion.V;
import org.mineacademy.fo.ReflectionUtil;
import org.mineacademy.fo.collection.StrictList;
import org.mineacademy.fo.collection.StrictSet;
import org.mineacademy.fo.model.IsInList;
import org.mineacademy.fo.remain.CompBiome;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.SimpleSettings;
import org.mineacademy.winter.psycho.PsychoMob;
import org.mineacademy.winter.util.FreezeIgnore;

@SuppressWarnings("unused")
public final class Settings extends SimpleSettings {

	@Override
	protected int getConfigVersion() {
		return 1;
	}

	/**
	 * @see org.mineacademy.fo.settings.YamlStaticConfig#saveComments()
	 */
	@Override
	protected boolean saveComments() {
		return true;
	}

	@Override
	protected List<String> getUncommentedSections() {
		return Arrays.asList("Terrain.Snow_Generation.Freeze_Ignore");
	}

	public static final class GiftChest {

		public static Boolean ENABLED;
		public static String TITLE;

		private static void init() {
			pathPrefix("Gift_Chest");

			ENABLED = getBoolean("Enabled");
			TITLE = getString("Title");
		}

		public static final class Public {

			public static Boolean ALLOW;
			private static String[] FORMAT;

			private static void init() {
				pathPrefix("Gift_Chest.Public");

				ALLOW = getBoolean("Allow");
				FORMAT = getStringArray("Format");
			}

			public static String[] getFormat() {
				return Arrays.copyOf(FORMAT, FORMAT.length);
			}
		}

		public static final class Private {

			private static String[] FORMAT;

			private static void init() {
				pathPrefix("Gift_Chest.Private");

				FORMAT = getStringArray("Format");
			}

			public static String[] getFormat() {
				return Arrays.copyOf(FORMAT, FORMAT.length);
			}
		}
	}

	public static final class DatedChest {

		public static Boolean PREVIEW;
		public static Integer DEFAULT_YEAR;

		private static void init() {
			pathPrefix("Dated_Chest");

			PREVIEW = getBoolean("Preview");
			DEFAULT_YEAR = getInteger("Default_Year");

			if (DEFAULT_YEAR != new GregorianCalendar().get(Calendar.YEAR))
				Common.logFramed(false,
						"Warning: Your Dated_Chest.Default_Year is " + DEFAULT_YEAR + ".",
						"You do not need to update it if your chests are overlapping",
						"from for example 2020 to 2021 (then leave at 2020).");
		}
	}

	public static final class Weather {

		public static Boolean DISABLE;
		public static Boolean SNOW_STORM;

		private static void init() {
			pathPrefix("Weather");

			DISABLE = getBoolean("Disable");
			SNOW_STORM = getBoolean("Snow_Storm");
		}
	}

	public static final class Terrain {

		public static StrictList<CompMaterial> PREVENT_MELTING;

		private static void init() {
			pathPrefix("Terrain");

			PREVENT_MELTING = getMaterialList("Prevent_Melting");
		}

		public static final class SnowGeneration {

			public static Boolean ENABLED;
			public static Boolean MELT, IGNORE_SNOWY;
			public static Integer PERIOD;
			public static Integer RADIUS;
			public static Boolean MULTI_LAYER;
			public static Integer NEIGHBOR_MIN;
			public static Boolean FREEZE_WATER;
			public static Boolean DESTROY_CROPS;
			public static StrictList<FreezeIgnore> IGNORE_FREEZE;
			public static StrictList<String> IGNORE_PLACE;
			public static StrictSet<Biome> IGNORE_BIOMES;

			private static void init() {
				pathPrefix("Terrain.Snow_Generation");

				ENABLED = getBoolean("Enabled");
				MELT = getBoolean("Melt");
				IGNORE_SNOWY = getBoolean("Only_Melt_Unnatural_Snow");
				PERIOD = getInteger("Period_Ticks");
				RADIUS = getInteger("Radius");
				MULTI_LAYER = getBoolean("Multi_Layer");
				NEIGHBOR_MIN = getInteger("Required_Neighbors_To_Grow");
				FREEZE_WATER = getBoolean("Freeze_Water");
				DESTROY_CROPS = getBoolean("Destroy_Crops");
				IGNORE_FREEZE = loadFreezeIgnore("Freeze_Ignore");
				IGNORE_PLACE = new StrictList<>(getStringList("Do_Not_Place_On"));
				IGNORE_BIOMES = new StrictSet<>(getCompatibleEnumList("Ignore_Biomes", Biome.class));

			}

			private static StrictList<FreezeIgnore> loadFreezeIgnore(String path) {
				final StrictList<FreezeIgnore> list = new StrictList<>();

				for (final Entry<String, String> e : getMap(path, String.class, String.class).entrySet()) {
					final IsInList<String> neighbors = findMaterials(e.getKey());
					final IsInList<String> crops = findMaterials(e.getValue());

					list.add(new FreezeIgnore(neighbors, crops));
				}

				return list;
			}

			private static IsInList<String> findMaterials(String rawList) {
				final StrictList<String> list = new StrictList<>();

				for (final String raw : rawList.split(", ")) {
					if (raw.equals("*")) {
						list.add(raw);
						continue;
					}

					final CompMaterial mat = CompMaterial.fromStringCompat(raw);

					if (mat != null && !list.contains(mat.toString()))
						list.add(mat.toString());
				}

				return new IsInList<>(list.getSource());
			}
		}

		public static final class Biomes {

			public static Boolean ENABLED;
			public static Byte BIOME_ID;

			private static void init() {
				pathPrefix("Terrain.Disguise_Biomes");

				ENABLED = getBoolean("Enabled");
				BIOME_ID = CompBiome.getBiomeID(get("Biome", Biome.class));

				if (ENABLED && MinecraftVersion.atLeast(V.v1_13)) {
					Common.logFramed(false, "The option Terrain.Disguise_Biomes crashes", "clients on 1.13+ and has been disabled for safety.");

					set("Enabled", false);
					ENABLED = false;
				}
			}
		}
	}

	public static final class Snow {

		public static Boolean ENABLED;
		public static Integer PERIOD;
		public static Integer AMOUNT;
		public static Float CHAOS;
		public static Boolean REALISTIC;
		public static Boolean REQUIRE_SNOW_BIOMES;
		public static Integer RANGE_H, RANGE_V;
		public static Boolean IGNORE_VANISHED;

		private static void init() {
			pathPrefix("Snow");

			ENABLED = getBoolean("Enabled");
			PERIOD = getInteger("Period_Ticks");
			AMOUNT = getInteger("Amount");
			CHAOS = (float) getDoubleSafe("Chaos");
			REALISTIC = getBoolean("Realistic");
			REQUIRE_SNOW_BIOMES = getBoolean("Require_Snow_Biomes");
			IGNORE_VANISHED = getBoolean("Ignore_Vanished");

			pathPrefix("Snow.Range");
			RANGE_H = getInteger("Horizontal");
			RANGE_V = getInteger("Vertical");
		}
	}

	public static final class Snowman {

		public static Boolean DISABLE_MELT_DAMAGE;
		public static Boolean PREVENT_TARGET;

		private static void init() {
			pathPrefix("Snowman");

			DISABLE_MELT_DAMAGE = getBoolean("Disable_Melt_Damage");
			PREVENT_TARGET = getBoolean("Prevent_Target");
		}

		public static final class Psycho {

			public static Boolean CONVERT_NEW, CONVERT_EXISTING, PUMPKIN, DESPAWN;

			private static void init() {
				pathPrefix("Snowman.Psycho");

				CONVERT_NEW = getBoolean("Convert_New");
				CONVERT_EXISTING = getBoolean("Convert_Existing");
				PUMPKIN = getBoolean("Pumpkin_Head");
				DESPAWN = getBoolean("Despawn");

				if ((CONVERT_EXISTING || CONVERT_NEW) && !PsychoMob.IS_COMPATIBLE)
					Common.logFramed(false, "Notice: Psycho is not compatible with your MC version. Currently requires: " + PsychoMob.COMP_VERSION);
			}
		}

		public static final class Damage {

			public static Double SNOWBALL;

			private static void init() {
				pathPrefix("Snowman.Damage");

				SNOWBALL = getDoubleSafe("Snowball");
			}
		}

		public static final class Transform {

			public static Boolean ENABLED;
			public static Integer CHANCE;
			public static StrictList<EntityType> ALLOWED;

			private static void init() {
				pathPrefix("Snowman.Transform");

				ENABLED = getBoolean("Enabled");
				CHANCE = getInteger("Chance_Percent");
				ALLOWED = new StrictList<>();

				for (final String entity : getStringList("Applicable")) {
					final EntityType en = ReflectionUtil.lookupEnum(EntityType.class, entity);

					ALLOWED.add(en);
				}
			}
		}
	}

	public static IsInList<String> ALLOWED_WORLDS;

	private static void init() {
		pathPrefix(null);

		ALLOWED_WORLDS = new IsInList<>(getStringList("Worlds"));
	}
}
