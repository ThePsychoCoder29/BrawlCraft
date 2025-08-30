
package net.mrmisc.brawlcraft.networking.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.mrmisc.brawlcraft.util.ui.switcher.ClientBrawlerData;

import java.util.HashSet;
import java.util.Set;

public class BrawlerUnlockPacket {

    private final Set<String> unlockedbrawlers;

    public BrawlerUnlockPacket(Set<String> unlockedbrawlers) {
        this.unlockedbrawlers = unlockedbrawlers;
    }

    public static void encode(BrawlerUnlockPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.unlockedbrawlers.size());
        for (String brawler : packet.unlockedbrawlers) {
            buf.writeUtf(brawler);
        }
    }

    public static BrawlerUnlockPacket decode(FriendlyByteBuf buf) {
        int size = buf.readInt();
        Set<String> brawlers = new HashSet<>();
        for (int i = 0; i < size; i++) {
            brawlers.add(buf.readUtf());
        }
        return new BrawlerUnlockPacket(brawlers);
    }

    public static void handle(BrawlerUnlockPacket packet, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            ClientBrawlerData.UNLOCKED_BRAWLERS.clear();
            ClientBrawlerData.UNLOCKED_BRAWLERS.addAll(packet.unlockedbrawlers);
            System.out.println("[CLIENT] Brawlers synced via packet: " + packet.unlockedbrawlers);
            ClientBrawlerData.print();
        });
        context.setPacketHandled(true);
    }
}
