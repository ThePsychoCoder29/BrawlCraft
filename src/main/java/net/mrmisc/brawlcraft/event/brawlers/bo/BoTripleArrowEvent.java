package net.mrmisc.brawlcraft.event.brawlers.bo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrmisc.brawlcraft.BrawlCraftMod;
import net.mrmisc.brawlcraft.networking.c2s.BrawlerIndexProvider;

import java.util.Set;

@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BoTripleArrowEvent {
    @SubscribeEvent
    public static void onArrowLoose(ArrowLooseEvent event) {
        if (!event.getLevel().isClientSide()) {
            LivingEntity shooter = event.getEntity();
            if (shooter instanceof ServerPlayer player) {
                player.getCapability(BrawlerIndexProvider.BRAWLER_INDEX).ifPresent(brawlerIndex -> {
                    int index = brawlerIndex.getBrawlerIndex();
                    if(index == 8) {
                        ItemStack bow = event.getBow();
                        int charge = event.getCharge();
                        float velocity = BowItem.getPowerForTime(charge);

                        if (player.getRandom().nextFloat() < 0.5f) {
                            event.setCanceled(true);

                            // spawn three arrows in total
                            spawnThreeArrows(player, velocity, bow);
                        }
                    }
                });
            }
        }
    }

    protected static void spawnThreeArrows(Player player, float velocity, ItemStack bow){
        for (int i = -1; i < 2; i++) {
            Arrow arrow = new Arrow(player.level(), player.getX() + i, player.getEyeY(), player.getZ(), new ItemStack(Items.ARROW), bow);
            arrow.setOwner(player);
            arrow.shootFromRotation(player, player.getXRot() + i,
                    player.getYRot() + i,
                    0.0F, velocity * 3.0F, 1.0F);
            if (i == 0) { // main arrow
                arrow.pickup = AbstractArrow.Pickup.ALLOWED;
                if (!player.isCreative()) {
                    ItemStack ammo = player.getProjectile(bow);
                    if (!ammo.isEmpty()) {
                        ammo.shrink(1);
                        if (ammo.isEmpty()) {
                            player.getInventory().removeItem(ammo);
                        }
                    }
                }
            }
            else {
                arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
            player.level().addFreshEntity(arrow);
            player.level().playSound(null, player.blockPosition(),
                    SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
                    1.0F, 1.0F / (player.getRandom().nextFloat() * 0.4f + 1.2f) + velocity * 0.5f);
        }
    }
}
