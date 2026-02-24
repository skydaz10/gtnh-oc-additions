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
import micdoodle8.mods.galacticraft.core.tile.TileEntityCargoUnloader;

public class DriverCargoUnloader extends DriverSidedTileEntity {

    private static final String Name = "cargo_unloader";

    @Override
    public Class<?> getTileEntityClass() {
        return TileEntityCargoUnloader.class;
    }

    public static class InternalEnvironment extends li.cil.oc.api.prefab.ManagedEnvironment implements NamedBlock {

        private final TileEntityCargoUnloader tileEntity;

        public InternalEnvironment(TileEntityCargoUnloader tile) {
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

        @Callback(doc = "function(): bool --- Gets the current state of the machine.")
        public Object[] isEnabled(final Context context, final Arguments args) {
            return new Object[] { !tileEntity.disabled };
        }

        @Callback(doc = "function(bool: enable): bool --- Sets on/off and returns whether its enabled.")
        public Object[] setEnabled(final Context context, final Arguments args) {
            tileEntity.disabled = !args.checkBoolean(0);
            return new Object[] { !tileEntity.disabled };
        }

        @Callback(doc = "function(): bool --- Toggles the loader on/off and returns whether its enabled.")
        public Object[] toggleEnabled(final Context context, final Arguments args) {
            tileEntity.disabled = !tileEntity.disabled;
            return new Object[] { !tileEntity.disabled };
        }
    }

    @Override
    public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
        TileEntityCargoUnloader t = (TileEntityCargoUnloader) world.getTileEntity(x, y, z);
        return new InternalEnvironment(t);
    }
}
