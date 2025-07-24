package net.mrmisc.brawlcraft.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.Minecraft;
import net.mrmisc.brawlcraft.BrawlCraftMod;

@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID, value = Dist.CLIENT)
public class ClientTickHandler {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (BrawlKeybinds.OPEN_BRAWLER_SWITCHER.isDown() &&
                    InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292)) {
                Minecraft.getInstance().setScreen(new BrawlerSwitcherScreen());
            }
        }
    }

}
