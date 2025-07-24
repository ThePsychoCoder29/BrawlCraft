package net.mrmisc.brawlcraft.event.brawlers.max;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrmisc.brawlcraft.BrawlCraftMod;
import net.mrmisc.brawlcraft.networking.c2s.BrawlerIndexProvider;

import java.util.Random;

@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MaxGiveSpeedEffectEvent {

    @SubscribeEvent
    public static void giveSpeedBoost(TickEvent.PlayerTickEvent event){
        Random random = new Random();
        int probability = random.nextInt(2);
        Player player = event.player;
        player.getCapability(BrawlerIndexProvider.BRAWLER_INDEX).ifPresent(brawlerIndex -> {
            int index = brawlerIndex.getBrawlerIndex();
            if(index == 3) {
                if (player.isCrouching()) {
                    if (probability == 1) {
                        if (!(player.hasEffect(MobEffects.MOVEMENT_SPEED))) {
                            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 3600, 2, false, false));
                        }
                    }
                }
            }
        });
    }
}
