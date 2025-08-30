package net.mrmisc.brawlcraft.util.ui.switcher;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.Minecraft;
import net.mrmisc.brawlcraft.BrawlCraftMod;
import net.mrmisc.brawlcraft.event.brawlers.doug.DougRevivePlayerEvent;

@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID, value = Dist.CLIENT)
public class ClientTickHandler {
    protected static int F3_KEY = 292;
    protected static int tick;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft minecraft = Minecraft.getInstance();
            if (BrawlKeybinds.OPEN_BRAWLER_SWITCHER.isDown() &&
                    InputConstants.isKeyDown(minecraft.getWindow().getWindow(), F3_KEY)) {
                minecraft.setScreen(new BrawlerSwitcherScreen());
            }
        }
    }

    public static void startTickEvent(){
        tick = 0;
    }

    @SubscribeEvent
    public static void dougClientDelay(TickEvent.ClientTickEvent event) {
        if(tick != 60){
            tick++;
        }
        if(tick == 60){
            DougRevivePlayerEvent.setDoDeath(true);
        }
    }
}
