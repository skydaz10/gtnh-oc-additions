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
import micdoodle8.mods.galacticraft.core.tile.TileEntityAirLockController;

public class DriverAirLockController extends DriverSidedTileEntity {

    private static final String Name = "airlock_controller";

    @Override
    public Class<?> getTileEntityClass() {
        return TileEntityAirLockController.class;
    }

    public static class InternalEnvironment extends li.cil.oc.api.prefab.ManagedEnvironment implements NamedBlock {

        private final TileEntityAirLockController tileEntity;

        public InternalEnvironment(TileEntityAirLockController tile) {
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

        @Callback(doc = "function(): bool --- Returns whether the air lock is open (not active).")
        public Object[] isOpen(final Context context, final Arguments args) {
            return new Object[] { !tileEntity.active };
        }
    }

    @Override
    public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
        TileEntityAirLockController t = (TileEntityAirLockController) world.getTileEntity(x, y, z);
        return new InternalEnvironment(t);
    }
}
