package net.mrmisc.brawlcraft.networking.s2c;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class BrawlerAddPacket {
    private final String brawlerName;

    public BrawlerAddPacket(String brawlerName) {
        this.brawlerName = brawlerName;
    }

    public BrawlerAddPacket(FriendlyByteBuf buf) {
        this.brawlerName = buf.readUtf(32767);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.brawlerName);
    }

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player != null) {
                player.getCapability(BrawlerUnlockProvider.BRAWLER_UNLOCK).ifPresent(data -> {
                    data.addBrawlerUnlock(brawlerName);
                });
            }
        });
        context.setPacketHandled(true);
    }
}
