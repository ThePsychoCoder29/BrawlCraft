package net.mrmisc.brawlcraft.networking.s2c;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.HashSet;
import java.util.Set;

public class BrawlerUnlockPacket {

    private final Set<String> unlockedBrawlers;

    public BrawlerUnlockPacket(Set<String> unlockedBrawlers) {
        this.unlockedBrawlers = unlockedBrawlers;
    }

    public static void encode(BrawlerUnlockPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.unlockedBrawlers.size());
        for (String brawler : packet.unlockedBrawlers) {
            buf.writeUtf(brawler);
        }
    }

    public static BrawlerUnlockPacket decode(FriendlyByteBuf buf) {
        int size = buf.readInt();
        Set<String> brawlers = new HashSet<String>();
        for (int i = 0; i < size; i++) {
            brawlers.add(buf.readUtf());
        }
        return new BrawlerUnlockPacket(brawlers);
    }

    public static void handle(BrawlerUnlockPacket packet, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if(player != null) {
                player.getCapability(BrawlerUnlockProvider.BRAWLER_UNLOCK).ifPresent(cap -> {
                    cap.getBrawlerUnlock().clear();
                    cap.getBrawlerUnlock().addAll(packet.unlockedBrawlers);
                });
            }
        });
        context.setPacketHandled(true);
    }
}
