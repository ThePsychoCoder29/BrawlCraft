
package net.mrmisc.brawlcraft.event.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrmisc.brawlcraft.BrawlCraftMod;
import net.mrmisc.brawlcraft.networking.ModPacketHandler;
import net.mrmisc.brawlcraft.networking.c2s.BrawlerIndexProvider;
import net.mrmisc.brawlcraft.networking.s2c.BrawlerUnlockPacket;
import net.mrmisc.brawlcraft.networking.s2c.BrawlerUnlockProvider;


@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event){
        if(event.getObject() instanceof Player){
            event.addCapability(ResourceLocation.fromNamespaceAndPath(BrawlCraftMod.MOD_ID, "properties"), new BrawlerIndexProvider());
            event.addCapability(ResourceLocation.fromNamespaceAndPath(BrawlCraftMod.MOD_ID, "unlockedbrawlers"), new BrawlerUnlockProvider());            }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        copyData(event.getOriginal(), event.getEntity());
    }

    private static void copyData(Player oldPlayer, Player newPlayer) {
        if (newPlayer.level().isClientSide()) return;
        oldPlayer.getCapability(BrawlerUnlockProvider.BRAWLER_UNLOCK).ifPresent(oldStore ->
                newPlayer.getCapability(BrawlerUnlockProvider.BRAWLER_UNLOCK).ifPresent(newStore -> {
                    newStore.copy(oldStore.getAll());
                    BrawlerUnlockPacket packet = new BrawlerUnlockPacket(newStore.getAll());
                    ModPacketHandler.sendToClient(packet, (ServerPlayer) newPlayer);
                }));
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(BrawlerUnlockProvider.BRAWLER_UNLOCK).ifPresent(data -> {
                System.out.println("[SERVER] Sending full unlock sync to player on login: " + data.getAll());
                ModPacketHandler.sendToClient(new BrawlerUnlockPacket(data.getAll()), serverPlayer);
            });
        }
    }
}
