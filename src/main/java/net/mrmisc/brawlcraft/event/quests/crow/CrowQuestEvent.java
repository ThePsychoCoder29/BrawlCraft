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
import net.mrmisc.brawlcraft.util.HelperMethods;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CrowQuestEvent {
    private static final String POISON_ID_KEY = "QuestPoisonID";
    private static final String POISON_KILL_COUNT_KEY = "QuestPoisonKillCount";
    private static final int THRESHOLD = 5;

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
        player.sendSystemMessage(Component.literal("Zombies Killed" + ":" + kills + "/" + THRESHOLD));
        data.putInt(POISON_KILL_COUNT_KEY, kills);

        // Quest completion check
        if (kills >= THRESHOLD) {
            // Trigger quest completion logic
            HelperMethods.completeQuest("Crow", player);
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
