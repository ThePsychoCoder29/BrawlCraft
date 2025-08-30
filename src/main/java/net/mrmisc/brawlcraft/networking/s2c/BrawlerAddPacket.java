
package net.mrmisc.brawlcraft.networking.s2c;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.mrmisc.brawlcraft.networking.c2s.BrawlerIndex;
import net.mrmisc.brawlcraft.networking.c2s.BrawlerIndexProvider;
import net.mrmisc.brawlcraft.util.ui.switcher.ClientBrawlerData;

public class BrawlerAddPacket {
    private final int brawlerName;

    public BrawlerAddPacket(int brawlerName) {
        this.brawlerName = brawlerName;
    }

    public BrawlerAddPacket(FriendlyByteBuf buf) {
        this.brawlerName = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.brawlerName);
    }

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            // Existing capability update
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player != null) {
                player.getCapability(BrawlerIndexProvider.BRAWLER_INDEX)
                        .ifPresent(data -> BrawlerIndex.addUnlockedBrawlers(brawlerName));


            // Also update static client set
            player.getCapability(BrawlerUnlockProvider.BRAWLER_UNLOCK)
                    .ifPresent(data -> ClientBrawlerData.UNLOCKED_BRAWLERS.addAll(data.getBrawlerUnlock()));
            mc.player
                    .sendSystemMessage(Component.literal("Unlocked brawler: " + brawlerName));
        }});
        context.setPacketHandled(true);
    }
}
