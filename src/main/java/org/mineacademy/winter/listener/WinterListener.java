package org.mineacademy.winter.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.mineacademy.fo.event.RegionScanCompleteEvent;
import org.mineacademy.winter.commands.PopulateCommand;

public class WinterListener implements Listener {

	@EventHandler
	public void onRegionScanComplete(RegionScanCompleteEvent e) {
		PopulateCommand.finish(e.getWorld());
	}

	/*private boolean handle = true;

	@EventHandler
	public void onMobSpawn(CreatureSpawnEvent e) {
		if (!handle) {
			if (e.getEntityType() == EntityType.SNOWMAN) {
				try {
					final EntityCreature en = ((CraftCreature)e.getEntity()).getHandle();
					final Field b = en.goalSelector.getClass().getDeclaredField("b");
					b.setAccessible(true);

					final Set<?> goals = (Set<?>) b.get(en.goalSelector);

					aa.println( Common.join(goals, ", ", (g) -> {
						try {
							final Field f = g.getClass().getField("a");
							f.setAccessible(true);

							return f.get(g).getClass().getSimpleName();
						} catch (final Throwable t) {
							t.printStackTrace();
						}

						return null;
					}));

				} catch (final Throwable t) {
					t.printStackTrace();
				}

				final Entity ent = e.getEntity();

				new BukkitRunnable() {

					@Override
					public void run() {
						if (ent.isDead() || !ent.isValid()) {
							cancel();
							return;
						}

						final Entity target = ((Creature)ent).getTarget();

						if (target != null && target.getType() == EntityType.PLAYER) {

							if (target.getLocation().distance(ent.getLocation()) < 1) {
								final EntityIronGolem golem = new EntityIronGolem( ((CraftWorld)ent.getWorld()).getHandle() );

								golem.B( ((CraftEntity) target).getHandle() );
								golem.die();

							}
						}
					}
				}.runTaskTimer(KaPlugin.getInstance(), 0, 1);
			}

			return;
		}

		if (e.getEntityType() == EntityType.SNOWMAN) {

			e.setCancelled(true);

			final NmsEntity en = new NmsEntity(e.getLocation(), Snowman.class);
			final EntityCreature nms = (EntityCreature) en.getNmsEntity();

			try {
				final Field goals = nms.goalSelector.getClass().getDeclaredField("b");
				goals.setAccessible(true);

				((Set<?>)goals.get(nms.goalSelector)).clear();

			} catch (final Throwable t) {
				t.printStackTrace();
			}

			nms.goalSelector.a(0, new PathfinderGoalFloat(nms));
			nms.goalSelector.a(2, new PathfinderGoalMeleeAttack(nms, 1.0D, false));
			nms.goalSelector.a(4, new PathfinderGoalMeleeAttack(nms, 1.0D, true));
			nms.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(nms, 1.0D));
			nms.goalSelector.a(6, new PathfinderGoalMoveThroughVillage(nms, 1.0D, false));
			nms.goalSelector.a(7, new PathfinderGoalRandomStroll(nms, 1.0D));
			nms.goalSelector.a(8, new PathfinderGoalLookAtPlayer(nms, EntityHuman.class, 8.0F));
			nms.goalSelector.a(8, new PathfinderGoalRandomLookaround(nms));
			nms.targetSelector.a(1, new PathfinderGoalHurtByTarget(nms, true));
			nms.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(nms, EntityHuman.class, true));
			nms.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(nms, EntityVillager.class, false));

			handle = false;
			en.addEntity(e.getSpawnReason());
			handle = true;
		}
	}*/
}
