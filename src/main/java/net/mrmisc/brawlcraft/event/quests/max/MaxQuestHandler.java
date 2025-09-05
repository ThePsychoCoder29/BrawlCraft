package net.mrmisc.brawlcraft.event.quests.max;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrmisc.brawlcraft.BrawlCraftMod;
import net.mrmisc.brawlcraft.util.helpers.HelperMethods;

import static net.mrmisc.brawlcraft.util.helpers.Constants.MAX;

@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MaxQuestHandler {
    private static final int QUEST_THRESHOLD = 5000;

    @SubscribeEvent
    public static void maxRunner(TickEvent.PlayerTickEvent event){
        Player player = event.player;
        CompoundTag tag = player.getPersistentData();
        if(!tag.contains(MAX) && !tag.getBoolean(MAX)){
            if(player.isSprinting()){
                if(player instanceof ServerPlayer serverPlayer){
                    int value = serverPlayer.getStats().getValue(Stats.CUSTOM.get(Stats.SPRINT_ONE_CM)) / 100;
                    int lastProgress = tag.getInt(MAX + "_progress");

                    if (value - lastProgress >= QUEST_THRESHOLD) {
                        HelperMethods.completeQuest(MAX, serverPlayer);
                        tag.putInt(MAX + "_progress", value);
                    }
                }
            }
        }
    }
}