package net.mrmisc.brawlcraft.util;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.mrmisc.brawlcraft.BrawlCraftMod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BrawlKeybinds {

    public static KeyMapping OPEN_BRAWLER_SWITCHER;

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        OPEN_BRAWLER_SWITCHER = new KeyMapping(
                "key.brawlcraft.open_brawler_switcher",
                GLFW.GLFW_KEY_O, // just O
                "key.categories.misc"
        );
        event.register(OPEN_BRAWLER_SWITCHER);
    }
}
