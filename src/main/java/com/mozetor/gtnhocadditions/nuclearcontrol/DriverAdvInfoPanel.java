package com.mozetor.gtnhocadditions.nuclearcontrol;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
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
import shedar.mods.ic2.nuclearcontrol.api.CardState;
import shedar.mods.ic2.nuclearcontrol.api.DisplaySettingHelper;
import shedar.mods.ic2.nuclearcontrol.api.PanelString;
import shedar.mods.ic2.nuclearcontrol.panel.CardWrapperImpl;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityAdvancedInfoPanel;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityInfoPanel;
import shedar.mods.ic2.nuclearcontrol.utils.StringUtils;

public class DriverAdvInfoPanel extends DriverSidedTileEntity {

    private static final String Name = "advanced_info_panel";

    @Override
    public Class<?> getTileEntityClass() {
        return TileEntityAdvancedInfoPanel.class;
    }

    public static class InternalEnvironment extends li.cil.oc.api.prefab.ManagedEnvironment implements NamedBlock {

        private final TileEntityAdvancedInfoPanel tileEntity;

        public InternalEnvironment(TileEntityAdvancedInfoPanel tile) {
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

        @Nonnull
        static Object[] processCard(Arguments args, TileEntityInfoPanel tileEntity) {
            List<ItemStack> cards = tileEntity.getCards();
            int idx = args.count() > 0 ? args.checkInteger(0) : 0;
            if (cards.isEmpty() || idx >= cards.size()) {
                return new Object[] { null };
            }
            ItemStack card = cards.get(idx);
            if (card == null) {
                return new Object[] { null };
            }
            CardWrapperImpl helper = new CardWrapperImpl(card, -1);
            CardState state = helper.getState();

            DisplaySettingHelper displaySettingHelper = tileEntity.getNewDisplaySettingsByCard(card);

            List<PanelString> data;
            if (state != CardState.OK && state != CardState.CUSTOM_ERROR) {
                data = StringUtils.getStateMessage(state);
            } else {
                data = tileEntity.getCardData(displaySettingHelper, card, helper);
            }
            String result = data.stream()
                .map(PanelString::toString)
                .collect(Collectors.joining("\n"));
            return new Object[] { result };
        }
    }

    @Override
    public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
        TileEntityAdvancedInfoPanel t = (TileEntityAdvancedInfoPanel) world.getTileEntity(x, y, z);
        return new InternalEnvironment(t);
    }
}
