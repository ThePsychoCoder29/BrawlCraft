package net.mrmisc.brawlcraft.util.ui.text;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrmisc.brawlcraft.networking.c2s.BrawlerIndexProvider;

import static net.mrmisc.brawlcraft.util.Constants.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class CurrentBrawlerTextEvent {

    @SubscribeEvent
    public static void onRenderGUI(CustomizeGuiOverlayEvent event){
        GuiGraphics guiGraphics = event.getGuiGraphics();
        showCurrentBrawler(guiGraphics);
    }

    private static void showCurrentBrawler(GuiGraphics graphics){
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        Font font = minecraft.font;
        if (player != null) {
            player.getCapability(BrawlerIndexProvider.BRAWLER_INDEX).ifPresent(brawlerIndex -> {
                        int currentBrawlerIndex = brawlerIndex.getBrawlerIndex();
                        String currentBrawler = getBrawlerName(currentBrawlerIndex);
                        if (currentBrawler.isEmpty()) currentBrawler = NO_BRAWLER;
                        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
                        graphics.drawString(
                                font,
                                "Brawler: " + currentBrawler,
                                5,
                                screenHeight - 20,
                                0xFFFFFF);
                    }
            );
        }
    }

    public static String getBrawlerName(int index){
        return Brawlers.fromID(index).getName().equals(NORMAL) ? NO_BRAWLER : Brawlers.fromID(index).getName();
    }
}
