package net.mrmisc.brawlcraft.event.quests.jacky;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrmisc.brawlcraft.BrawlCraftMod;
import net.mrmisc.brawlcraft.item.ModItems;
import net.mrmisc.brawlcraft.util.helpers.HelperMethods;

import java.util.*;

@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class JackyQuestEvent {

    private static final Map<UUID, JackyQuestEvent.Tracker> brokenPickTracker = new HashMap<>();
    private static final int QUEST_THRESHOLD = 10;


    @SubscribeEvent
    public static void onPickaxeBreak(PlayerDestroyItemEvent event){
        Player player = event.getEntity();
        if(!player.getPersistentData().contains("Jacky")) {
            if (!player.level().isClientSide()) {
                ItemStack stack = event.getOriginal();
                ServerPlayer serverPlayer = (ServerPlayer) player;
                if (stack.is(Items.IRON_PICKAXE)) {
                    handlePickBreak(serverPlayer);
                }
            }
        }
    }

    public static void handlePickBreak(ServerPlayer player){
        UUID uuid = player.getUUID();
        Tracker tracker = brokenPickTracker.get(uuid);
        if(tracker == null){
            tracker = new Tracker();
            tracker.brokenPicks = 1;
            brokenPickTracker.put(uuid, tracker);
            player.displayClientMessage(Component.literal("Jacky's Drill-hammer quest started. Break 5 Iron Pickaxes to unlock Jacky"), true);
        }
        else {
            tracker.brokenPicks++;
            player.displayClientMessage(
                    Component.literal("Iron Pickaxes Broken: " + tracker.brokenPicks + "/" + QUEST_THRESHOLD),
                    true
            );
            if(tracker.brokenPicks >= QUEST_THRESHOLD){
                HelperMethods.completeQuest("Jacky", player);
                player.getInventory().add(new ItemStack(ModItems.JACKY_DRILL.get()));
            }
        }
    }

    private static class Tracker {
        public int brokenPicks = 0;
    }
}
