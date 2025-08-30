package net.mrmisc.brawlcraft.event.brawlers.max;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrmisc.brawlcraft.BrawlCraftMod;
import net.mrmisc.brawlcraft.networking.c2s.BrawlerIndexProvider;
import net.mrmisc.brawlcraft.util.Constants.*;
import net.mrmisc.brawlcraft.util.PlayerTimerManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.mrmisc.brawlcraft.util.Constants.MAX;

@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MaxGiveSpeedEffectEvent {
    private static final Map<UUID, Runnable> activeRewinds = new HashMap<>();
    private static final List<Holder<Potion>> potions = List.of(
            Potions.SWIFTNESS,
            Potions.LONG_SWIFTNESS,
            Potions.STRONG_SWIFTNESS
    );

    @SubscribeEvent
    public static void onPotionFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ItemStack item = event.getItem();
            if (item.getItem() instanceof PotionItem) {
                PotionContents potionContents = item.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
                if (potions.stream().anyMatch(potionContents::is)) {
                    player.getCapability(BrawlerIndexProvider.BRAWLER_INDEX).ifPresent(brawlerIndex -> {
                        int index = brawlerIndex.getBrawlerIndex();
                        if(index == Brawlers.fromName(MAX).getId()) {
                            BlockPos pos = player.getOnPos();
                            float health = player.getHealth();
                            scheduleRewind(player, 10, pos, health);
                        }
                    });
                }
            }
        }
    }


    public static void scheduleRewind(ServerPlayer player, int delaySeconds, BlockPos pos, float health) {
        UUID id = player.getUUID();

        // Cancel any existing rewind
        cancelRewind(id);

        Runnable task = () -> {
            // remove when executed
            activeRewinds.remove(id);

            // check if still alive
            if (player.isDeadOrDying()) return;

            // restore pos + health
            player.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            if (player.getHealth() < health) {
                player.setHealth(health);
            }
        };

        // store for cancellation
        activeRewinds.put(id, task);

        // schedule with your delay system
        PlayerTimerManager.scheduleDelay(player, delaySeconds, task);
    }

    public static void cancelRewind(UUID id) {
        activeRewinds.remove(id);
    }

    public static boolean hasRewind(UUID id) {
        return activeRewinds.containsKey(id);
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            UUID id = player.getUUID();

            // cancel scheduled rewind
            if (hasRewind(id)) {
                cancelRewind(id);
            }
        }
    }
}
