package com.mozetor.gtnhocadditions.galacticraft;

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
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenDistributor;

public class DriverBubbleDistributor extends DriverSidedTileEntity {

    private static final String Name = "bubble_distributor";

    @Override
    public Class<?> getTileEntityClass() {
        return TileEntityOxygenDistributor.class;
    }

    public static class InternalEnvironment extends li.cil.oc.api.prefab.ManagedEnvironment implements NamedBlock {

        private final TileEntityOxygenDistributor tileEntity;

        public InternalEnvironment(TileEntityOxygenDistributor tile) {
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

        @Callback(doc = "function(): bool --- Returns whether the oxygen bubble is visible.")
        public Object[] isBubbleVisible(final Context context, final Arguments args) {
            return new Object[] { tileEntity.getBubbleVisible() };
        }

        @Callback(doc = "function(bool: visibility) --- Set bubble visibility.")
        public Object[] setBubbleVisible(final Context context, final Arguments args) {
            tileEntity.setBubbleVisible(args.checkBoolean(0));
            return new Object[] {};
        }

        @Callback(doc = "function(): number --- Get the current bubble radius.")
        public Object[] getBubbleSize(final Context context, final Arguments args) {
            return new Object[] { tileEntity.getBubbleSize() };
        }
    }

    @Override
    public boolean worksWith(World world, int x, int y, int z, ForgeDirection side) {
        if (super.worksWith(world, x, y, z, side)) {
            TileEntityOxygenDistributor tile = (TileEntityOxygenDistributor) world.getTileEntity(x, y, z);
            return tile.getMaxEnergyStoredGC() != 0;
        }
        return false;
    }

    @Override
    public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
        TileEntityOxygenDistributor t = (TileEntityOxygenDistributor) world.getTileEntity(x, y, z);
        return new InternalEnvironment(t);
    }
}
