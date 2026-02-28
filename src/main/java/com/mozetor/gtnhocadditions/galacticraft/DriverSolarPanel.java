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
import micdoodle8.mods.galacticraft.core.tile.TileEntitySolar;

public class DriverSolarPanel extends DriverSidedTileEntity {

    private static final String Name = "solar_panel";

    @Override
    public Class<?> getTileEntityClass() {
        return TileEntitySolar.class;
    }

    public static class InternalEnvironment extends li.cil.oc.api.prefab.ManagedEnvironment implements NamedBlock {

        private final TileEntitySolar tileEntity;

        public InternalEnvironment(TileEntitySolar tile) {
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

        @Callback(doc = "function(): bool --- Returns whether energy production is enabled.")
        public Object[] isEnabled(final Context context, final Arguments args) {
            return new Object[] { !tileEntity.getDisabled(0) };
        }

        @Callback(doc = "function(bool: enable) --- Enable or disable energy production.")
        public Object[] setEnabled(final Context context, final Arguments args) {
            tileEntity.setDisabled(0, !args.checkBoolean(0));
            return new Object[] {};
        }

        @Callback(doc = "function(): number --- Get energy being produced per tick.")
        public Object[] getEnergyProduction(final Context context, final Arguments args) {
            return new Object[] { tileEntity.generateWatts };
        }

        @Callback(
            doc = "function(): number --- Get the solar boost multiplier as a percentage above baseline (e.g. 50 = 1.5x).")
        public Object[] getBoost(final Context context, final Arguments args) {
            return new Object[] { Math.round((tileEntity.getSolarBoost() - 1) * 1000) / 10.0F };
        }

        @Callback(
            doc = "function(): string --- Get current status of the solar panel: DISABLED, NIGHT_TIME, RAINING, BLOCKED_FULLY, BLOCKED_PARTIALLY, GENERATING, UNKNOWN.")
        public Object[] getStatus(final Context context, final Arguments args) {
            if (tileEntity.getDisabled(0)) {
                return new Object[] { "DISABLED" };
            } else if (!tileEntity.getWorldObj()
                .isDaytime()) {
                    return new Object[] { "NIGHT_TIME" };
                } else if (tileEntity.getWorldObj()
                    .isRaining()
                    || tileEntity.getWorldObj()
                        .isThundering()) {
                            return new Object[] { "RAINING" };
                        } else
                    if (tileEntity.solarStrength == 0) {
                        return new Object[] { "BLOCKED_FULLY" };
                    } else if (tileEntity.solarStrength < 9) {
                        return new Object[] { "BLOCKED_PARTIALLY" };
                    } else if (tileEntity.generateWatts > 0) {
                        return new Object[] { "GENERATING" };
                    } else {
                        return new Object[] { "UNKNOWN" };
                    }
        }
    }

    @Override
    public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
        TileEntitySolar t = (TileEntitySolar) world.getTileEntity(x, y, z);
        return new InternalEnvironment(t);
    }
}
