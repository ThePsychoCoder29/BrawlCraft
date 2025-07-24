package net.mrmisc.brawlcraft.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.mrmisc.brawlcraft.networking.ModPacketHandler;
import net.mrmisc.brawlcraft.networking.s2c.BrawlerAddPacket;

public class HelperMethods {
    public static void completeQuest(String name, ServerPlayer player){
        ModPacketHandler.sendToClient(new BrawlerAddPacket(name), player);
        CompoundTag tag = player.getPersistentData();
        tag.putBoolean(name, true);
        if (!tag.contains(name)){
            tag.putBoolean(name, true);
            player.displayClientMessage(
                    Component.literal("Quest complete! You unlocked" + name + "!"),
                    true
            );
        }
        else {
            player.displayClientMessage(
                    Component.literal(name + " is already unlocked."),
                    true
            );
        }
    }
}
