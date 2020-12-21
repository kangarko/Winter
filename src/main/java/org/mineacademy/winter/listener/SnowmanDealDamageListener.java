package org.mineacademy.winter.listener;

import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.collection.StrictList;
import org.mineacademy.fo.remain.CompSound;
import org.mineacademy.winter.settings.Settings;

public class SnowmanDealDamageListener implements Listener {

	private final StrictList<UUID> storedSnowballs = new StrictList<>();

	@EventHandler
	public void onShoot(ProjectileLaunchEvent e) {
		final Projectile projectile = e.getEntity();

		if (projectile == null || !(projectile instanceof Snowball) || !Settings.ALLOWED_WORLDS.contains(projectile.getWorld().getName()))
			return;

		if (projectile.getShooter() instanceof Snowman)
			storedSnowballs.add(projectile.getUniqueId());
	}

	@EventHandler
	public void onHit(ProjectileHitEvent e) {
		if (e.getEntity() == null)
			return;

		final UUID entityId = e.getEntity().getUniqueId();

		if (storedSnowballs.contains(entityId)) {
			storedSnowballs.remove(entityId);

			final LivingEntity hitEntity = getHitEntity(e);

			if (hitEntity != null && !isInvulnerable(hitEntity)) {

				if (hitEntity instanceof Player) {
					final float pitch = RandomUtil.nextInt(20) / 100;

					CompSound.HURT_FLESH.play((Player) hitEntity, 1F, 1F + (RandomUtil.nextBoolean() ? pitch : -pitch));
				}

				hitEntity.setHealth(MathUtil.range(hitEntity.getHealth() - Settings.Snowman.Damage.SNOWBALL, 0, hitEntity.getMaxHealth()));
			}
		}
	}

	private final boolean isInvulnerable(LivingEntity en) {
		try {
			return en.isInvulnerable();

		} catch (final NoSuchMethodError err) {
			return false; // MC 1.8
		}
	}

	private final LivingEntity getHitEntity(ProjectileHitEvent e) {
		try {
			if (e.getHitEntity() instanceof LivingEntity)
				return (LivingEntity) e.getHitEntity();

		} catch (final Throwable t) {
			final double r = 0.01;

			for (final Entity en : e.getEntity().getNearbyEntities(r, r, r))
				if (en instanceof LivingEntity)
					return (LivingEntity) en;
		}

		return null;
	}
}
