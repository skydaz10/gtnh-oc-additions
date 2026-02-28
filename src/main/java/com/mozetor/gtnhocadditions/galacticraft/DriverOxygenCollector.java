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
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenCollector;

public class DriverOxygenCollector extends DriverSidedTileEntity {

    private static final String Name = "oxygen_collector";

    @Override
    public Class<?> getTileEntityClass() {
        return TileEntityOxygenCollector.class;
    }

    public static class InternalEnvironment extends li.cil.oc.api.prefab.ManagedEnvironment implements NamedBlock {

        private final TileEntityOxygenCollector tileEntity;

        public InternalEnvironment(TileEntityOxygenCollector tile) {
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

        @Callback(doc = "function(): number --- Get oxygen being collected per second.")
        public Object[] getCollectionSpeed(final Context context, final Arguments args) {
            return new Object[] { tileEntity.lastOxygenCollected * 20 };
        }
    }

    @Override
    public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
        TileEntityOxygenCollector t = (TileEntityOxygenCollector) world.getTileEntity(x, y, z);
        return new InternalEnvironment(t);
    }
}
