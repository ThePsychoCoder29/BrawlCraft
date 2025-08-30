package net.mrmisc.brawlcraft.event.brawlers.doug;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrmisc.brawlcraft.BrawlCraftMod;
import net.mrmisc.brawlcraft.networking.c2s.BrawlerIndexProvider;
import net.mrmisc.brawlcraft.util.ui.switcher.ClientTickHandler;
import net.mrmisc.brawlcraft.util.PlayerTimerManager;

import java.util.List;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID)
public class DougRevivePlayerEvent {
    public static boolean doDeath;

    @SubscribeEvent
    public static void onDougRevive(LivingDeathEvent event){
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        player.getCapability(BrawlerIndexProvider.BRAWLER_INDEX).ifPresent(brawlerIndex -> {
            Component deathMsg = event.getSource().getLocalizedDeathMessage(player);
            int index = brawlerIndex.getBrawlerIndex();
            if(index == 7) {
                if(player.getInventory().contains(new ItemStack(Items.TOTEM_OF_UNDYING)) && player.getRandom().nextFloat() <= 0.75) {
                    event.setCanceled(true);

                    // Store the current position
                    BlockPos deathPos = player.blockPosition();

                    // Optional: Apply death-like effects
                    addDeathProperties(player, true);
                    sendDeathMessage(player, deathMsg);
                    addDeathEffects(player);
                    player.setHealth(1f);

                    PlayerTimerManager.scheduleDelay(player, 3, ()-> {
                        // "Fake" death sound or animation
                        player.level().playSound(null, deathPos, SoundEvents.WITHER_DEATH, SoundSource.PLAYERS, 1.0F, 1.0F);
                        // Delay task
                        ServerLevel serverLevel = (ServerLevel) player.level();
                        // Use queueServerWork with delay
                        // Revive logic
                        ClientTickHandler.startTickEvent();
                        doPostDeathEffects(player, deathPos, serverLevel);
                    });
                }
            }
        });
    }

    public static void addDeathProperties(Player player, boolean addProperties){
        player.setInvisible(addProperties);
        player.getAbilities().invulnerable = addProperties;
        player.setNoGravity(addProperties);
    }

    public static void sendDeathMessage(Player player, Component component){
        List<ServerPlayer> players = Objects.requireNonNull(player.level().getServer()).getPlayerList().getPlayers();
        for(ServerPlayer serverPlayer : players){
            serverPlayer.sendSystemMessage(component);
        }
    }

    public static void addDeathEffects(Player player){
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 5));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 10)); // Prevent damage while down
    }

    public static void setDoDeath(boolean death) {
        doDeath = death;
    }

    public static void doPostDeathEffects(ServerPlayer player, BlockPos deathPos, ServerLevel serverLevel){
        if (doDeath) {
            player.setHealth(player.getMaxHealth());
            serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getY() + 1.0, player.getZ(), 50, 0.5, 1.0, 0.5, 0.2);
            player.level().playSound(null, deathPos, SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
            Component component = Component.literal("Or maybe he didn't.....");
            sendDeathMessage(player, component);
            addDeathProperties(player, false);
            player.removeAllEffects();
        }
    }
}
