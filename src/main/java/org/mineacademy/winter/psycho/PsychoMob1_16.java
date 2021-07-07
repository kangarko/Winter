package org.mineacademy.winter.psycho;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.metadata.FixedMetadataValue;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.winter.settings.Settings;

import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.EntitySnowball;
import net.minecraft.server.v1_16_R3.EntitySnowman;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.MathHelper;
import net.minecraft.server.v1_16_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_16_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_16_R3.SoundEffect;
import net.minecraft.server.v1_16_R3.SoundEffects;

public class PsychoMob1_16 extends EntitySnowman {

	@SuppressWarnings("rawtypes")
	PsychoMob1_16(Location loc) {
		super(EntityTypes.SNOW_GOLEM, ((CraftWorld) loc.getWorld()).getHandle());

		this.setPosition(loc.getX(), loc.getY(), loc.getZ());
		this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0, true));
		this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));

		if (!Settings.Snowman.Psycho.PUMPKIN)
			setHasPumpkin(false);

		getBukkitEntity().setMetadata("DeadlySnowman", new FixedMetadataValue(SimplePlugin.getInstance(), true));
		persistent = !Settings.Snowman.Psycho.DESPAWN;

		((CraftWorld) loc.getWorld()).getHandle().addEntity(this, SpawnReason.CUSTOM);
	}

	@Override
	public void a(final EntityLiving entityliving, final float f) {
		final EntitySnowball entitysnowball = new EntitySnowball(this.world, this);
		final double d0 = entityliving.locY() + entityliving.getHeadHeight() - 1.100000023841858;
		final double d2 = entityliving.locX() - this.locX();
		final double d3 = d0 - entitysnowball.locY();
		final double d4 = entityliving.locZ() - this.locZ();
		final float f2 = MathHelper.sqrt(d2 * d2 + d4 * d4) * 0.2f;

		entitysnowball.shoot(d2, d3 + f2, d4, 1.6f, 12.0f);

		this.playSound(SoundEffects.ENTITY_SHULKER_SHOOT, 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
		this.world.addEntity(entitysnowball);
	}

	@Override
	protected SoundEffect getSoundFall(int i) {
		return SoundEffects.ENTITY_WITHER_SKELETON_STEP;
	}

	@Override
	protected SoundEffect getSoundAmbient() {
		return SoundEffects.ENTITY_WITCH_AMBIENT;
	}

	@Override
	protected SoundEffect getSoundDeath() {
		return SoundEffects.ENTITY_WITHER_SKELETON_DEATH;
	}
}