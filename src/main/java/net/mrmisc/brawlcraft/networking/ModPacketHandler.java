package net.mrmisc.brawlcraft.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import net.mrmisc.brawlcraft.BrawlCraftMod;
import net.mrmisc.brawlcraft.networking.c2s.BrawlerDataPacket;
import net.mrmisc.brawlcraft.networking.s2c.BrawlerAddPacket;
import net.mrmisc.brawlcraft.networking.s2c.BrawlerUnlockPacket;

@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModPacketHandler {
    public static final SimpleChannel INSTANCE = ChannelBuilder.named(
            ResourceLocation.fromNamespaceAndPath(BrawlCraftMod.MOD_ID, "main"))
            .serverAcceptedVersions((status, version) -> true)
            .clientAcceptedVersions((status, version) -> true)
            .networkProtocolVersion(1)
            .simpleChannel();

    public static void register(){
        INSTANCE.messageBuilder(BrawlerDataPacket.class, NetworkDirection.PLAY_TO_SERVER)
                .encoder(BrawlerDataPacket::toBytes)
                .decoder(BrawlerDataPacket::new)
                .consumerMainThread(BrawlerDataPacket::handle)
                .add();
        INSTANCE.messageBuilder(BrawlerUnlockPacket.class, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(BrawlerUnlockPacket::encode)
                .decoder(BrawlerUnlockPacket::decode)
                .consumerMainThread(BrawlerUnlockPacket::handle)
                .add();
        INSTANCE.messageBuilder(BrawlerAddPacket.class, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(BrawlerAddPacket::toBytes)
                .decoder(BrawlerAddPacket::new)
                .consumerMainThread(BrawlerAddPacket::handle)
                .add();
    }

    public static void sendToServer(Object msg) {
        INSTANCE.send(msg, PacketDistributor.SERVER.noArg());
    }

    public static void sendToClient(Object msg, ServerPlayer player) {
        INSTANCE.send(msg, PacketDistributor.PLAYER.with(player));
    }
}
