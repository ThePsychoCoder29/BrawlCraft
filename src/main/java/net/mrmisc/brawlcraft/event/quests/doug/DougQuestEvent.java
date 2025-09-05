package net.mrmisc.brawlcraft.event.quests.doug;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingUseTotemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrmisc.brawlcraft.BrawlCraftMod;
import net.mrmisc.brawlcraft.util.helpers.Constants;
import net.mrmisc.brawlcraft.util.helpers.HelperMethods;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DougQuestEvent {
    static Map<UUID, Integer> totemCount = new HashMap<>();

    @SubscribeEvent
    public static void unlockDoug(LivingUseTotemEvent event){
        if(!event.getEntity().level().isClientSide()){
            if(event.getEntity() instanceof ServerPlayer player) {
                if (HelperMethods.manageQuestThresholdCounter(player, totemCount, 10, "Totems popped", Constants.DOUG)) {
                    HelperMethods.completeQuest("Doug", player);
                }
            }
        }
    }
}
