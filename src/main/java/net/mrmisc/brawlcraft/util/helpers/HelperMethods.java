
package net.mrmisc.brawlcraft.util.helpers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.mrmisc.brawlcraft.networking.ModPacketHandler;
import net.mrmisc.brawlcraft.networking.s2c.BrawlerUnlockPacket;
import net.mrmisc.brawlcraft.networking.s2c.BrawlerUnlockProvider;

import java.util.Map;
import java.util.UUID;

public class HelperMethods {
    public static void completeQuest(String brawlerName, ServerPlayer player) {
        CompoundTag tag = player.getPersistentData();

        if (tag.contains(brawlerName) && tag.getBoolean(brawlerName)) {
            player.sendSystemMessage(Component.literal(brawlerName + " is already unlocked."), true);
            return;
        }

        tag.putBoolean(brawlerName, true);

        player.getCapability(BrawlerUnlockProvider.BRAWLER_UNLOCK).ifPresent(data -> {
            data.addBrawlerUnlock(brawlerName);

            ModPacketHandler.sendToClient(new BrawlerUnlockPacket(data.getAll()), player);
        });

        player.sendSystemMessage(Component.literal("You unlocked " + brawlerName + "!"), true);
    }
    public static void removeBrawler(String brawlerName, ServerPlayer player) {
        CompoundTag tag = player.getPersistentData();

        if (tag.contains(brawlerName) && tag.getBoolean(brawlerName)) {
            player.getCapability(BrawlerUnlockProvider.BRAWLER_UNLOCK).ifPresent(data -> {
                data.removeBrawlerUnlock(brawlerName);
                ModPacketHandler.sendToClient(new BrawlerUnlockPacket(data.getAll()), player);
            });

            tag.remove(brawlerName);
            player.sendSystemMessage(Component.literal("Locked " + brawlerName), true);
            return;
        }

        player.sendSystemMessage(Component.literal(brawlerName + " is already locked."), true);
    }
    public static void clearBrawlers(ServerPlayer player) {
        player.getCapability(BrawlerUnlockProvider.BRAWLER_UNLOCK).ifPresent(data -> {
            data.clearBrawlerUnlock();
            ModPacketHandler.sendToClient(new BrawlerUnlockPacket(data.getAll()), player);
        });
    }


    public static boolean manageQuestThresholdCounter(ServerPlayer player, Map<UUID, Integer> map, int threshold, String quest, String brawlerName){
        if(!player.getPersistentData().getBoolean(brawlerName) && !player.getPersistentData().contains(brawlerName)) {
            UUID uuid = player.getUUID();
            int count = map.getOrDefault(uuid, 0) + 1;
            player.sendSystemMessage(Component.literal(quest + ":" + count + "/" + threshold), true);
            map.put(uuid, count);
            if(count >= threshold){
                map.remove(uuid);
                return true;
            }
        }
        return false;
    }

}
