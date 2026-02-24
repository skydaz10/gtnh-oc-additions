package com.mozetor.gtnhocadditions.nuclearcontrol;

import static com.mozetor.gtnhocadditions.nuclearcontrol.DriverAdvInfoPanel.InternalEnvironment.processCard;

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
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityInfoPanel;

public class DriverInfoPanel extends DriverSidedTileEntity {

    private static final String Name = "info_panel";

    @Override
    public Class<?> getTileEntityClass() {
        return TileEntityInfoPanel.class;
    }

    public static class InternalEnvironment extends li.cil.oc.api.prefab.ManagedEnvironment implements NamedBlock {

        private final TileEntityInfoPanel tileEntity;

        public InternalEnvironment(TileEntityInfoPanel tile) {
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

        @Callback(doc = "function(int: card index): string --- Gets the card data or null if invalid.")
        public Object[] getCardData(final Context context, final Arguments args) {
            return processCard(args, tileEntity);
        }
    }

    @Override
    public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
        TileEntityInfoPanel t = (TileEntityInfoPanel) world.getTileEntity(x, y, z);
        return new InternalEnvironment(t);
    }
}
