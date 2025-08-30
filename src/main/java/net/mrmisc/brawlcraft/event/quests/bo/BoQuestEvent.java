package net.mrmisc.brawlcraft.event.quests.bo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrmisc.brawlcraft.BrawlCraftMod;
import net.mrmisc.brawlcraft.util.Constants;
import net.mrmisc.brawlcraft.util.HelperMethods;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BoQuestEvent {
    static Map<UUID, Integer> arrowKillCounts =  new HashMap<>();

    @SubscribeEvent
    public static void boQuest(LivingDeathEvent event){
        Entity entity = event.getSource().getEntity();
        Entity arrow = event.getSource().getDirectEntity();
        if(arrow instanceof Arrow){
            if(entity instanceof ServerPlayer player){
                if(HelperMethods.manageQuestThresholdCounter(player, arrowKillCounts, 5, "Entities killed using bow and arrow", Constants.BO)){
                    HelperMethods.completeQuest("Bo", player);
                }
            }
        }
    }
}
