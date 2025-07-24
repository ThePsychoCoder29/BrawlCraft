package net.mrmisc.brawlcraft.event.quests.max;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrmisc.brawlcraft.BrawlCraftMod;
import net.mrmisc.brawlcraft.networking.ModPacketHandler;
import net.mrmisc.brawlcraft.networking.s2c.BrawlerAddPacket;
import net.mrmisc.brawlcraft.util.HelperMethods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MaxQuestHandler {

    private static final List<Holder<Potion>> potions = List.of(
            Potions.SWIFTNESS,
            Potions.LONG_SWIFTNESS,
            Potions.STRONG_SWIFTNESS
    );

    private static final Map<UUID, Tracker> speedPotionTrackers = new HashMap<>();

    private static final int QUEST_THRESHOLD = 10;
    private static final long TIME_LIMIT_MS = 2 * 60 * 1000; // 2 minutes

    @SubscribeEvent
    public static void onPotionFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ItemStack item = event.getItem();
            if (item.getItem() instanceof PotionItem) {
                PotionContents potionContents = item.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
                if (potions.stream().anyMatch(potionContents::is)) {
                    if(!player.getPersistentData().contains("Max")) {
                        handleSpeedPotionDrink(player);
                    }
                }
            }
        }
    }

    private static void handleSpeedPotionDrink(ServerPlayer player) {
        UUID uuid = player.getUUID();
        long now = System.currentTimeMillis();

        Tracker tracker = speedPotionTrackers.get(uuid);

        if (tracker == null) {
            tracker = new Tracker();
            tracker.startTime = now;
            tracker.count = 1;
            speedPotionTrackers.put(uuid, tracker);

            player.displayClientMessage(Component.literal("Speed Quest started! Drink 10 Speed Potions in 2 minutes."), true);
        } else {
            long elapsed = now - tracker.startTime;

            if (elapsed <= TIME_LIMIT_MS) {
                tracker.count++;
                player.displayClientMessage(
                        Component.literal("Speed Potions drunk: " + tracker.count + "/" + QUEST_THRESHOLD),
                        true
                );

                if (tracker.count >= QUEST_THRESHOLD) {
                    HelperMethods.completeQuest("Max", player);
                    speedPotionTrackers.remove(uuid);
                }
            } else {
                // Time expired → reset the quest
                tracker.startTime = now;
                tracker.count = 1;
                player.displayClientMessage(
                        Component.literal("Speed Quest timer reset! Drink fast!"),
                        true
                );
            }
        }
    }

    private static class Tracker {
        public int count = 0;
        public long startTime = 0;
    }

}
