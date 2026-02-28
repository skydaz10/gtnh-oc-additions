package com.mozetor.gtnhocadditions.galacticraft;

import java.lang.reflect.Field;
import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.tile.TileEntityTelemetry;

public class DriverTelemetryUnit extends DriverSidedTileEntity {

    private static final String Name = "telemetry_unit";

    @Override
    public Class<?> getTileEntityClass() {
        return TileEntityTelemetry.class;
    }

    public static class InternalEnvironment extends li.cil.oc.api.prefab.ManagedEnvironment implements NamedBlock {

        private final TileEntityTelemetry tileEntity;

        // Loaded lazily — comes from GC Planets, may not be present
        static protected Class<?> classAstroMiner = null;

        public InternalEnvironment(TileEntityTelemetry tile) {
            tileEntity = tile;
            setNode(
                Network.newNode(this, Visibility.Network)
                    .withComponent(Name, Visibility.Network)
                    .create());
        }

        @Override
        public String preferredName() {
            return Name;
        }

        @Override
        public int priority() {
            return 0;
        }

        @Callback(doc = "function(): bool --- Returns whether the telemetry unit is linked to an entity.")
        public Object[] isLinked(final Context context, final Arguments args) {
            return new Object[] { tileEntity.linkedEntity != null };
        }

        @Callback(
            doc = "function(): table --- Read telemetry data from the linked entity. Returns nil if not linked. Fields vary by entity type (PLAYER, ROCKET, ASTRO_MINER, MOB, GENERIC).")
        public Object[] readTelemetry(final Context context, final Arguments args) {
            final Entity e = tileEntity.linkedEntity;
            if (e == null) {
                return new Object[] { null };
            }

            // Lazy-load AstroMiner class from GC Planets
            if (classAstroMiner == null && GalacticraftCore.isPlanetsLoaded) {
                try {
                    classAstroMiner = Class
                        .forName("micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityAstroMiner");
                } catch (Exception ex) {
                    // GC Planets not present
                }
            }

            HashMap<String, Object> m = new HashMap<String, Object>();

            // Common fields for all entity types
            m.put("name", e.getCommandSenderName());

            {
                double xmotion = e.motionX;
                double ymotion = e instanceof EntityLivingBase ? e.motionY + 0.0784D : e.motionY;
                double zmotion = e.motionZ;
                double speed = Math.sqrt(xmotion * xmotion + ymotion * ymotion + zmotion * zmotion) * 20D;
                m.put("speed", speed);
            }

            m.put("x", e.posX);
            m.put("y", e.posY);
            m.put("z", e.posZ);

            if (e instanceof EntityPlayer) {
                m.put("type", "PLAYER");
                m.put(
                    "food",
                    ((EntityPlayer) e).getFoodStats()
                        .getFoodLevel() * 5);

                GCPlayerStats stats = GCPlayerStats.get((EntityPlayerMP) e);
                double oxygen = stats.airRemaining * 4096 + stats.airRemaining2;
                m.put("oxygenSecondsLeft", oxygen * 9 / 20);

            } else if (e instanceof EntitySpaceshipBase) {
                final EntitySpaceshipBase es = (EntitySpaceshipBase) e;
                m.put("type", "ROCKET");
                m.put("countdown", es.timeUntilLaunch / 20F);
                m.put("isIgnited", es.launchPhase != 0);
                m.put("fuelTank", es.fuelTank.getInfo());

            } else if (classAstroMiner != null && classAstroMiner.isInstance(e)) {
                m.put("type", "ASTRO_MINER");

                try {
                    m.put(
                        "storedEnergy",
                        classAstroMiner.getField("energyLevel")
                            .getInt(e));
                    m.put("maxEnergy", 12000);
                } catch (Exception ex) {
                    // Field not accessible
                }

                try {
                    int ai = classAstroMiner.getField("AIstate")
                        .getInt(e);
                    String status = "UNKNOWN";
                    if (ai == classAstroMiner.getField("AISTATE_STUCK")
                        .getInt(e)) status = "STUCK";
                    else if (ai == classAstroMiner.getField("AISTATE_ATBASE")
                        .getInt(e)) status = "DOCKED";
                    else if (ai == classAstroMiner.getField("AISTATE_TRAVELLING")
                        .getInt(e)) status = "TRAVELLING";
                    else if (ai == classAstroMiner.getField("AISTATE_MINING")
                        .getInt(e)) status = "MINING";
                    else if (ai == classAstroMiner.getField("AISTATE_RETURNING")
                        .getInt(e)) status = "RETURNING";
                    else if (ai == classAstroMiner.getField("AISTATE_DOCKING")
                        .getInt(e)) status = "DOCKING";
                    else if (ai == classAstroMiner.getField("AISTATE_OFFLINE")
                        .getInt(e)) status = "OFFLINE";
                    m.put("status", status);
                } catch (Exception ex) {
                    // Field not accessible
                }

            } else if (e instanceof EntityLivingBase) {
                m.put("type", "MOB");
            } else {
                m.put("type", "GENERIC");
            }

            // Living entity extras (includes players)
            if (e instanceof EntityLivingBase) {
                final EntityLivingBase el = (EntityLivingBase) e;
                m.put("health", el.getHealth());
                m.put("maxHealth", el.getMaxHealth());
                m.put("recentlyHurt", el.hurtTime > 0);

                try {
                    Field f = TileEntityTelemetry.class.getDeclaredField("pulseRate");
                    f.setAccessible(true);
                    m.put("pulseRate", ((int) f.get(tileEntity)) / 10);
                } catch (Exception ex) {
                    // Not critical
                }
            }

            return new Object[] { m };
        }
    }

    @Override
    public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
        TileEntityTelemetry t = (TileEntityTelemetry) world.getTileEntity(x, y, z);
        return new InternalEnvironment(t);
    }
}
