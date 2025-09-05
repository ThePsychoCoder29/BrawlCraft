package net.mrmisc.brawlcraft.event.brawlers.bea;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrmisc.brawlcraft.BrawlCraftMod;
import net.mrmisc.brawlcraft.networking.c2s.BrawlerIndexProvider;
import net.mrmisc.brawlcraft.util.helpers.PlayerTimerManager;

@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID)
public class BeaSpawnBeeEvent {

    @SubscribeEvent
    public static void spawnBeeEvent(LivingHurtEvent event){
        if(event.getEntity() instanceof ServerPlayer player){
            player.getCapability(BrawlerIndexProvider.BRAWLER_INDEX).ifPresent(brawlerUnlock -> {
                int index = brawlerUnlock.getBrawlerIndex();
                if(index == 4){
                    if(player.getRandom().nextFloat() <= 0.33f){
                        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                            Bee bee = EntityType.BEE.create(player.level());
                            if (bee != null) {
                                PlayerTimerManager.scheduleDelay(player, 10, bee::kill);
                                bee.moveTo(player.getX(), player.getY(), player.getZ(), 0, 0);
                                bee.setTarget(attacker); // force attack target
                                bee.setPersistentAngerTarget(attacker.getUUID());
                                bee.setRemainingPersistentAngerTime(200);
                                bee.setPersistenceRequired(); // prevent despawn
                                player.level().addFreshEntity(bee);

                                bee.getPersistentData().putBoolean("BeaBee", true);
                                bee.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 0));
                            }
                        }                    }
                }
            });
        }
        if (event.getSource().getEntity() instanceof Bee bee) {
            if (bee.getPersistentData().getBoolean("BeaBee")) {
                LivingEntity target = event.getEntity();
                if (target != null) {
                    // Apply effects for 5 seconds (100 ticks)
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 2));
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
                    target.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1));
                }
            }
        }
    }
}
