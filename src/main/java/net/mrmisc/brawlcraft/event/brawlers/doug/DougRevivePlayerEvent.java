package net.mrmisc.brawlcraft.event.brawlers.doug;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrmisc.brawlcraft.BrawlCraftMod;
import net.mrmisc.brawlcraft.networking.c2s.BrawlerIndexProvider;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID)
public class DougRevivePlayerEvent {
    @SubscribeEvent
    public static void onDougRevive(LivingDeathEvent event){
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        player.getCapability(BrawlerIndexProvider.BRAWLER_INDEX).ifPresent(brawlerIndex -> {
            event.setCanceled(true);

            // Store the current position
            BlockPos deathPos = player.blockPosition();

            // Optional: Apply death-like effects
            player.setInvisible(true);
            player.getAbilities().invulnerable = true;
            player.setNoGravity(true);
            Objects.requireNonNull(player.level().getServer()).getPlayerList().broadcastSystemMessage(
                    Component.literal(player.getName().getString() + " died..."), true
            );
            player.setHealth(1f);
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 5));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 10)); // Prevent damage while down

            // "Fake" death sound or animation
            player.level().playSound(null, deathPos, SoundEvents.WITHER_DEATH, SoundSource.PLAYERS, 1.0F, 1.0F);

            // Delay task
            ServerLevel serverLevel = (ServerLevel) player.level();
            serverLevel.getServer().execute(() -> {
                // Use queueServerWork with delay
                serverLevel.getServer().tell(new TickTask(serverLevel.getServer().getTickCount() + 60, () -> {
                    // Revive logic
                    player.setHealth(player.getMaxHealth());
                    player.teleportTo(deathPos.getX(), deathPos.getY() + 0.5, deathPos.getZ());
                    serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getY() + 1.0, player.getZ(), 50, 0.5, 1.0, 0.5, 0.2);
                    player.level().playSound(null, deathPos, SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);

                    Objects.requireNonNull(player.level().getServer()).getPlayerList().broadcastSystemMessage(
                            Component.literal("Or maybe he didn't....."), true
                    );

                    player.setInvisible(false);
                    player.getAbilities().invulnerable = false;
                    player.setNoGravity(false);
                    player.removeAllEffects();
                }));
            });
        });
    }
}
