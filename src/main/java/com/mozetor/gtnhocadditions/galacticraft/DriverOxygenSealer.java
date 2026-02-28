package com.mozetor.gtnhocadditions.galacticraft;

import net.minecraft.block.Block;
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
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenSealer;

public class DriverOxygenSealer extends DriverSidedTileEntity {

    private static final String Name = "oxygen_sealer";

    @Override
    public Class<?> getTileEntityClass() {
        return TileEntityOxygenSealer.class;
    }

    public static class InternalEnvironment extends li.cil.oc.api.prefab.ManagedEnvironment implements NamedBlock {

        private final TileEntityOxygenSealer tileEntity;

        public InternalEnvironment(TileEntityOxygenSealer tile) {
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

        @Callback(
            doc = "function(): bool --- Returns whether the sealer is currently providing oxygen to the surrounding area.")
        public Object[] isSealed(final Context context, final Arguments args) {
            return new Object[] { tileEntity.sealed };
        }

        @Callback(doc = "function(): bool --- Returns whether thermal control is enabled on this sealer.")
        public Object[] isThermalControlEnabled(final Context context, final Arguments args) {
            return new Object[] { tileEntity.thermalControlEnabled() };
        }

        @Callback(
            doc = "function(): bool --- Returns whether thermal control is currently working (breatheable air with meta=1 above the sealer).")
        public Object[] isThermalControlWorking(final Context context, final Arguments args) {
            int bx = tileEntity.xCoord;
            int by = tileEntity.yCoord + 1;
            int bz = tileEntity.zCoord;
            Block blockAbove = tileEntity.getWorldObj()
                .getBlock(bx, by, bz);
            int meta = tileEntity.getWorldObj()
                .getBlockMetadata(bx, by, bz);
            return new Object[] {
                (blockAbove == GCBlocks.breatheableAir || blockAbove == GCBlocks.brightBreatheableAir) && meta == 1 };
        }
    }

    @Override
    public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
        TileEntityOxygenSealer t = (TileEntityOxygenSealer) world.getTileEntity(x, y, z);
        return new InternalEnvironment(t);
    }
}
