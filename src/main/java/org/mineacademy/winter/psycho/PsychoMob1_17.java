package org.mineacademy.winter.psycho;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.metadata.FixedMetadataValue;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.winter.settings.Settings;

import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntitySnowman;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntitySnowball;

public class PsychoMob1_17 extends EntitySnowman {

	@SuppressWarnings("rawtypes")
	PsychoMob1_17(Location loc) {
		super(EntityTypes.aF, ((CraftWorld) loc.getWorld()).getHandle());

		this.setPosition(loc.getX(), loc.getY(), loc.getZ());
		this.bP.a(1, new PathfinderGoalMeleeAttack(this, 1.0, true));
		this.bP.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));

		if (!Settings.Snowman.Psycho.PUMPKIN)
			setHasPumpkin(false);

		getBukkitEntity().setMetadata("DeadlySnowman", new FixedMetadataValue(SimplePlugin.getInstance(), true));

		// Persistent
		persist = !Settings.Snowman.Psycho.DESPAWN;

		((CraftWorld) loc.getWorld()).getHandle().addEntity(this, SpawnReason.CUSTOM);
	}

	@Override
	public void a(final EntityLiving entityliving, final float f) {
		final EntitySnowball entitysnowball = new EntitySnowball(this.t /*world*/, this);
		final double d0 = entityliving.locY() + entityliving.getHeadHeight() - 1.100000023841858;
		final double d2 = entityliving.locX() - this.locX();
		final double d3 = d0 - entitysnowball.locY();
		final double d4 = entityliving.locZ() - this.locZ();
		final float f2 = (float) Math.sqrt(d2 * d2 + d4 * d4) * 0.2f;

		entitysnowball.shoot(d2, d3 + f2, d4, 1.6f, 12.0f);

		this.playSound(SoundEffects.qE /*shulker shoot*/, 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
		this.t.addEntity(entitysnowball); // t = world
	}

	@Override
	protected SoundEffect getSoundFall(int i) {
		return SoundEffects.uO; //ENTITY_WITHER_SKELETON_STEP;
	}

	@Override
	protected SoundEffect getSoundAmbient() {
		return SoundEffects.uA; //ENTITY_WITCH_AMBIENT;
	}

	@Override
	protected SoundEffect getSoundDeath() {
		return SoundEffects.uM; //ENTITY_WITHER_SKELETON_DEATH;
	}
}