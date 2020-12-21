package org.mineacademy.winter.util;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.World;

public class SnowStorm {

	private static final Set<String> worlds = new HashSet<>();

	public static final void add(World world) {
		worlds.add(world.getName());
	}

	public static final void remove(World world) {
		worlds.remove(world.getName());
	}

	public static final boolean has(World world) {
		return worlds.contains(world.getName());
	}

	public static final void cleanAll() {
		worlds.clear();
	}
}
