package com.mozetor.gtnhocadditions.galacticraft;

import java.lang.reflect.Field;

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
import micdoodle8.mods.galacticraft.core.tile.TileEntityFuelLoader;

public class DriverFuelLoader extends DriverSidedTileEntity {

    private static final String Name = "fuel_loader";

    @Override
    public Class<?> getTileEntityClass() {
        return TileEntityFuelLoader.class;
    }

    public static class InternalEnvironment extends li.cil.oc.api.prefab.ManagedEnvironment implements NamedBlock {

        private final TileEntityFuelLoader tileEntity;

        public InternalEnvironment(TileEntityFuelLoader tile) {
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

        @Callback(doc = "function(): bool --- Returns whether fuel is being loaded right now.")
        public Object[] isLoading(final Context context, final Arguments args) throws Exception {
            try {
                Field f = TileEntityFuelLoader.class.getDeclaredField("loadedFuelLastTick");
                f.setAccessible(true);
                return new Object[] { (boolean) f.get(tileEntity) };
            } catch (Exception e) {
                throw new Exception("failed to get fueler status");
            }
        }

        @Callback(doc = "function(): table --- Get information about the loader's fuel tank.")
        public Object[] getFuelTank(final Context context, final Arguments args) {
            return new Object[] { tileEntity.fuelTank.getInfo() };
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
    }

    @Override
    public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
        TileEntityFuelLoader t = (TileEntityFuelLoader) world.getTileEntity(x, y, z);
        return new InternalEnvironment(t);
    }
}
