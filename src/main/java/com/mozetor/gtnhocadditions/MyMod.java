package com.mozetor.gtnhocadditions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mozetor.gtnhocadditions.galacticraft.DriverCargoLaunchController;
import com.mozetor.gtnhocadditions.galacticraft.DriverCargoLoader;
import com.mozetor.gtnhocadditions.galacticraft.DriverCargoUnloader;
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
        Driver.add(new DriverInfoPanel());
        Driver.add(new DriverAdvInfoPanel());
        LOG.info("Loaded all additional drivers.");
    }

}
