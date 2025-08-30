package net.mrmisc.brawlcraft.event.quests.dynamike;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
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

import static net.mrmisc.brawlcraft.util.Constants.DYNAMIKE;

@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DynamikeQuestEvent {
    static Map<UUID, Integer> tntKillCounts =  new HashMap<>();
    static String id = "entityDamagedByTNT";
    static String name = "Dynamike";
    protected static int THRESHOLD = 5;

    @SubscribeEvent
    public static void tntExplosion(ExplosionEvent.Detonate event){
        Explosion explosion = event.getExplosion();
        if (explosion.getDirectSourceEntity() instanceof PrimedTnt tnt) {
            LivingEntity entity = tnt.getOwner();
            if(entity instanceof Player player) {
                if (!player.getPersistentData().contains(name)) {
                    UUID playerUUID = player.getUUID();
                    for (Entity e : event.getAffectedEntities()) {
                        if (e instanceof LivingEntity living && !(living instanceof Player)) {
                            living.getPersistentData().putUUID(id, playerUUID);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void killedEntityCounter(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();
        if (entity.getPersistentData().hasUUID(id)) {
            UUID playerUUID = entity.getPersistentData().getUUID(id);
            if (!level.isClientSide()) {
                ServerPlayer player = (ServerPlayer) level.getPlayerByUUID(playerUUID);
                if (player != null) {
                    if (HelperMethods.manageQuestThresholdCounter(player, tntKillCounts, THRESHOLD, "Entities killed using the TNT", DYNAMIKE)) {
                        player.getInventory().add(new ItemStack(ModItems.DYNAMITE.get()));
                        HelperMethods.completeQuest(name, player);
                    }
                }
            }
        }
    }
}
