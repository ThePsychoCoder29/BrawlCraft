package net.mrmisc.brawlcraft.event.quests.doug;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingUseTotemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrmisc.brawlcraft.BrawlCraftMod;
import net.mrmisc.brawlcraft.networking.ModPacketHandler;
import net.mrmisc.brawlcraft.networking.s2c.BrawlerAddPacket;

@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DougQuestEvent {
    @SubscribeEvent
    public static void unlockDoug(LivingUseTotemEvent event){
        if(!event.getEntity().level().isClientSide()){
            if(event.getEntity() instanceof ServerPlayer player) {
                ModPacketHandler.sendToClient(new BrawlerAddPacket("Doug"), player);
            }
        }
    }
}
