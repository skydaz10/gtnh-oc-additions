package com.mozetor.gtnhocadditions.galacticraft;

import java.lang.reflect.Method;

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
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.energy.tile.EnergyStorageTile;

public class DriverEnergyStorage extends DriverSidedTileEntity {

    private static final String Name = "gc_energy_device";

    @Override
    public Class<?> getTileEntityClass() {
        return EnergyStorageTile.class;
    }

    public static class InternalEnvironment extends li.cil.oc.api.prefab.ManagedEnvironment implements NamedBlock {

        private final EnergyStorageTile tileEntity;

        public InternalEnvironment(EnergyStorageTile tile) {
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
            // Lower priority so specific drivers take precedence over this generic one
            return -1;
        }

        @Callback(doc = "function(): number --- (GC) Get amount of energy currently stored.")
        public Object[] getStoredEnergy(final Context context, final Arguments args) {
            return new Object[] { tileEntity.getEnergyStoredGC() };
        }

        @Callback(doc = "function(): number --- (GC) Get energy capacity of the machine.")
        public Object[] getMaxEnergy(final Context context, final Arguments args) {
            return new Object[] { tileEntity.getMaxEnergyStoredGC() };
        }
    }

    @Override
    public boolean worksWith(World world, int x, int y, int z, ForgeDirection side) {
        if (super.worksWith(world, x, y, z, side)) {
            EnergyStorageTile tile = (EnergyStorageTile) world.getTileEntity(x, y, z);
            return tile.getMaxEnergyStoredGC() != 0;
        }
        return false;
    }

    @Override
    public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
        EnergyStorageTile tile = (EnergyStorageTile) world.getTileEntity(x, y, z);
        if (GalacticraftCore.isPlanetsLoaded) {
            try {
                final Class<?> fakepad = Class
                    .forName("micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityTelepadFake");
                if (fakepad.isAssignableFrom(tile.getClass())) {
                    final Method getBaseTelepad = fakepad.getDeclaredMethod("getBaseTelepad");
                    getBaseTelepad.setAccessible(true);
                    tile = (EnergyStorageTile) getBaseTelepad.invoke(tile);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return (tile != null) ? new InternalEnvironment(tile) : null;
    }
}
