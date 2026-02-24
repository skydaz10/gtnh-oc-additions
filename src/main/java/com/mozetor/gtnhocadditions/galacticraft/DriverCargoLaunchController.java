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
import micdoodle8.mods.galacticraft.api.entity.ICargoEntity;
import micdoodle8.mods.galacticraft.core.tile.TileEntityLandingPad;
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityLaunchController;

public class DriverCargoLaunchController extends DriverSidedTileEntity {

    private static final String Name = "cargo_launch_controller";

    @Override
    public Class<?> getTileEntityClass() {
        return TileEntityLaunchController.class;
    }

    public static class InternalEnvironment extends li.cil.oc.api.prefab.ManagedEnvironment implements NamedBlock {

        private final TileEntityLaunchController tileEntity;

        public InternalEnvironment(TileEntityLaunchController tile) {
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

        @Callback(doc = "function(): bool --- Toggles the controller on/off and returns whether its enabled.")
        public Object[] toggleEnabled(final Context context, final Arguments args) {
            tileEntity.disabled = !tileEntity.disabled;
            return new Object[] { !tileEntity.disabled };
        }

        @Callback(doc = "function(int: frequency): void --- Sets a new frequency.")
        public Object[] setFrequency(final Context context, final Arguments args) {
            tileEntity.setFrequency(args.checkInteger(0));
            return new Object[] {};
        }

        @Callback(doc = "function(): int --- Gets the current frequency.")
        public Object[] getFrequency(final Context context, final Arguments args) {
            return new Object[] { tileEntity.frequency };
        }

        @Callback(doc = "function(): bool --- Gets whether the current frequency is valid.")
        public Object[] isValidFrequency(final Context context, final Arguments args) {
            return new Object[] { tileEntity.frequencyValid };
        }

        @Callback(doc = "function(int: frequency): void --- Sets a new destination frequency.")
        public Object[] setDstFrequency(final Context context, final Arguments args) {
            tileEntity.setDestinationFrequency(args.checkInteger(0));
            return new Object[] {};
        }

        @Callback(doc = "function(): int --- Gets the current destination frequency.")
        public Object[] getDstFrequency(final Context context, final Arguments args) {
            return new Object[] { tileEntity.destFrequency };
        }

        @Callback(doc = "function(): bool --- Gets whether the current destination frequency is valid.")
        public Object[] isValidDstFrequency(final Context context, final Arguments args) {
            return new Object[] { tileEntity.destFrequencyValid };
        }

        @Callback(doc = "function(): bool --- Gets whether any rocket is docked on the connected landing pad.")
        public Object[] isRocketDocked(final Context context, final Arguments args) {
            TileEntityLandingPad pad = tileEntity.attachedDock instanceof TileEntityLandingPad
                ? (TileEntityLandingPad) tileEntity.attachedDock
                : null;
            if (pad == null) return new Object[] { false };
            ICargoEntity entity = pad.getDockedEntity();
            return new Object[] { entity != null };
        }
    }

    @Override
    public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
        TileEntityLaunchController t = (TileEntityLaunchController) world.getTileEntity(x, y, z);
        return new InternalEnvironment(t);
    }
}
