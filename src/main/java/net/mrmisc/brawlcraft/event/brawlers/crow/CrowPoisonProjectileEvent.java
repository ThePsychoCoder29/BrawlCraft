package net.mrmisc.brawlcraft.event.brawlers.crow;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrmisc.brawlcraft.BrawlCraftMod;
import net.mrmisc.brawlcraft.networking.c2s.BrawlerIndexProvider;

@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CrowPoisonProjectileEvent {

    @SubscribeEvent
    public static void crowPoisonProjectileEvent(ProjectileImpactEvent event){
        if(!(event.getProjectile() instanceof Projectile projectile)) return;

        Entity owner = projectile.getOwner();

        if(!(owner instanceof Player player)) return;

        player.getCapability(BrawlerIndexProvider.BRAWLER_INDEX).ifPresent(brawlerIndex -> {
            int index = brawlerIndex.getBrawlerIndex();
            if(index == 6){
                if(event.getRayTraceResult() instanceof EntityHitResult result){
                    if(result.getEntity() instanceof LivingEntity entity){
                        if(player.getRandom().nextFloat() <= 0.33f) {
                            entity.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 2));
                        }
                    }
                }
            }
        });
    }
}
