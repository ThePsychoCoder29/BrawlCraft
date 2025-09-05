package net.mrmisc.brawlcraft.event.quests.bo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrmisc.brawlcraft.BrawlCraftMod;
import net.mrmisc.brawlcraft.util.helpers.Constants;
import net.mrmisc.brawlcraft.util.helpers.HelperMethods;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.mrmisc.brawlcraft.util.helpers.Constants.BO;

@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BoQuestEvent {
    static Map<UUID, Integer> arrowKillCounts =  new HashMap<>();

    @SubscribeEvent
    public static void boQuest(LivingDeathEvent event){
        Entity entity = event.getSource().getEntity();
        Entity arrow = event.getSource().getDirectEntity();
        if(arrow instanceof Arrow item){
            if(entity instanceof ServerPlayer player){
                if(fullyChargedBow(item)) {
                    if (HelperMethods.manageQuestThresholdCounter(player, arrowKillCounts, 20, "Entities killed using bow and arrow ", BO)) {
                        HelperMethods.completeQuest(BO, player);
                    }
                }
            }
        }
    }

    public static boolean fullyChargedBow(Arrow arrow) {
        ItemStack weapon = arrow.getWeaponItem();
        if (weapon.getItem() instanceof BowItem) {
            // Fully charged bow arrows fly at ~3.0 blocks/tick
            double velocity = arrow.getDeltaMovement().length();
            return velocity >= 2.9;
        }
        return false;
    }
}
