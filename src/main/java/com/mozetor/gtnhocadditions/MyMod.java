package com.mozetor.gtnhocadditions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mozetor.gtnhocadditions.galacticraft.DriverAirLockController;
import com.mozetor.gtnhocadditions.galacticraft.DriverBubbleDistributor;
import com.mozetor.gtnhocadditions.galacticraft.DriverCargoLaunchController;
import com.mozetor.gtnhocadditions.galacticraft.DriverCargoLoader;
import com.mozetor.gtnhocadditions.galacticraft.DriverCargoUnloader;
import com.mozetor.gtnhocadditions.galacticraft.DriverEnergyStorage;
import com.mozetor.gtnhocadditions.galacticraft.DriverFuelLoader;
import com.mozetor.gtnhocadditions.galacticraft.DriverOxygenCollector;
import com.mozetor.gtnhocadditions.galacticraft.DriverOxygenSealer;
import com.mozetor.gtnhocadditions.galacticraft.DriverSolarPanel;
import com.mozetor.gtnhocadditions.galacticraft.DriverTelemetryUnit;
import com.mozetor.gtnhocadditions.nuclearcontrol.DriverAdvInfoPanel;
import com.mozetor.gtnhocadditions.nuclearcontrol.DriverInfoPanel;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import li.cil.oc.api.Driver;

@Mod(modid = MyMod.MODID, version = Tags.VERSION, name = "gtnh-oc-additions", acceptedMinecraftVersions = "[1.7.10]")
public class MyMod {

    public static final String MODID = "gtnhocadditions";
    public static final Logger LOG = LogManager.getLogger(MODID);

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Driver.add(new DriverCargoUnloader());
        Driver.add(new DriverCargoLoader());
        Driver.add(new DriverCargoLaunchController());
        Driver.add(new DriverAirLockController());
        Driver.add(new DriverOxygenCollector());
        Driver.add(new DriverFuelLoader());
        Driver.add(new DriverBubbleDistributor());
        Driver.add(new DriverOxygenSealer());
        Driver.add(new DriverSolarPanel());
        Driver.add(new DriverEnergyStorage());
        Driver.add(new DriverTelemetryUnit());
        Driver.add(new DriverInfoPanel());
        Driver.add(new DriverAdvInfoPanel());
        LOG.info("Loaded all additional drivers.");
    }

}
