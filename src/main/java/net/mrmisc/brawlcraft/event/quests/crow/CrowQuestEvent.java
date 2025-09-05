package net.mrmisc.brawlcraft.event.quests.crow;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrmisc.brawlcraft.BrawlCraftMod;
import net.mrmisc.brawlcraft.util.helpers.HelperMethods;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.mrmisc.brawlcraft.util.helpers.Constants.CROW;

@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CrowQuestEvent {
    private static final String POISON_ID_KEY = "QuestPoisonID";
    private static final String POISON_KILL_COUNT_KEY = "QuestPoisonKillCount";
    private static final int THRESHOLD = 20;

    private static final Map<UUID, Integer> crowMap = new HashMap<>();

    // When player gets a poison effect
    @SubscribeEvent
    public static void onPoisonApplied(MobEffectEvent.Added event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getEffectInstance().getEffect() != MobEffects.POISON) return;

        // New poison instance
        String poisonId = UUID.randomUUID().toString();
        CompoundTag data = player.getPersistentData();
        data.putString(POISON_ID_KEY, poisonId);
        data.putInt(POISON_KILL_COUNT_KEY, 0);
    }

    // When player kills something
    @SubscribeEvent
    public static void onZombieKill(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        if (!(event.getEntity() instanceof Zombie)) return;

        CompoundTag data = player.getPersistentData();
        if (!data.contains(POISON_ID_KEY)) return;

        // Check if still poisoned with the same effect
        if (!player.hasEffect(MobEffects.POISON)) return;

        // Count kill
        int kills = data.getInt(POISON_KILL_COUNT_KEY) + 1;
        data.putInt(POISON_KILL_COUNT_KEY, kills);
        player.sendSystemMessage(Component.literal("Zombies Killed" + ":" + kills + "/" + THRESHOLD));

        // Quest completion check
        if (HelperMethods.manageQuestThresholdCounter(player, crowMap, THRESHOLD, "Zombies Killed ", CROW)) {
            // Trigger quest completion logic
            HelperMethods.completeQuest(CROW, player);
        }
    }

    // When poison wears off
    @SubscribeEvent
    public static void onPoisonRemoved(MobEffectEvent.Remove event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getEffect() != MobEffects.POISON) return;

        CompoundTag data = player.getPersistentData();
        data.remove(POISON_ID_KEY);
        data.remove(POISON_KILL_COUNT_KEY);
    }
}
