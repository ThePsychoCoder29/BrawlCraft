package net.mrmisc.brawlcraft.event.quests.dynamike;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrmisc.brawlcraft.BrawlCraftMod;
import net.mrmisc.brawlcraft.item.ModItems;
import net.mrmisc.brawlcraft.util.HelperMethods;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DynamikeQuestEvent {
    static Map<UUID, Integer> tntKillCounts =  new HashMap<>();

    @SubscribeEvent
    public static void tntExplosion(ExplosionEvent.Detonate event){
        Explosion explosion = event.getExplosion();
        if (explosion.getDirectSourceEntity() instanceof PrimedTnt tnt) {
            LivingEntity entity = tnt.getOwner();
            if(entity instanceof Player player) {
                if (!player.getPersistentData().contains("Dynamike")) {
                    UUID playerUUID = player.getUUID();
                    for (Entity e : event.getAffectedEntities()) {
                        if (e instanceof LivingEntity living && !(living instanceof Player)) {
                            living.getPersistentData().putUUID("entityDamagedByTNT", playerUUID);
                        }
                    }
                }
            }
        }
    }




    @SubscribeEvent
    public static void killedEntityCounter(LivingDeathEvent event){
        LivingEntity entity = event.getEntity();
        if (entity.getPersistentData().hasUUID("entityDamagedByTNT")) {
            UUID playerUUID = entity.getPersistentData().getUUID("entityDamagedByTNT");
            int newCount = tntKillCounts.getOrDefault(playerUUID, 0) + 1;
            tntKillCounts.put(playerUUID, newCount);
            if (entity.level() instanceof ServerLevel serverLevel) {
                if (newCount >= 5) {
                    ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(playerUUID);
                    if (player != null) {
                        player.getInventory().add(new ItemStack(ModItems.DYNAMITE.get()));
                        tntKillCounts.remove(playerUUID);
                        HelperMethods.completeQuest("Dynamike", player);
                    }
                }
            }
        }
    }
}
