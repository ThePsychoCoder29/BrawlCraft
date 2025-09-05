package net.mrmisc.brawlcraft.event.util;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;
import net.mrmisc.brawlcraft.BrawlCraftMod;
import net.mrmisc.brawlcraft.commands.BrawlerCommand;
import net.mrmisc.brawlcraft.commands.BrawlerItemCommand;

@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID)
public class CommandRegisterEvent {
    
    @SubscribeEvent
    public static void onCmdRegister(RegisterCommandsEvent event){
        new BrawlerCommand(event.getDispatcher());
        new BrawlerItemCommand(event.getDispatcher());

        ConfigCommand.register(event.getDispatcher());
    }
}
