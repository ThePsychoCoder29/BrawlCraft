package net.mrmisc.brawlcraft.item.custom;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.mrmisc.brawlcraft.entity.custom.DynamiteProjectileEntity;
import net.mrmisc.brawlcraft.networking.c2s.BrawlerIndexProvider;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class DynamiteItem extends Item implements ProjectileItem {

    public DynamiteItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @NotNull TooltipContext pContext, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pTooltipFlag) {
        if(Screen.hasShiftDown()){
            pTooltipComponents.add(Component.translatable("tooltip.brawlcraft.dynamite.tooltip"));
        }
        else {
            pTooltipComponents.add(Component.translatable("tooltip.brawlcraft.shift.tooltip"));
            super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.getCapability(BrawlerIndexProvider.BRAWLER_INDEX).ifPresent(brawlerIndex -> {
            if(brawlerIndex.getBrawlerIndex() == 2) {
                // Play throw sound
                level.playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.SNOWBALL_THROW,
                        SoundSource.NEUTRAL,
                        0.5F,
                        0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
                );

                // Spawn dynamite on server side
                if (!level.isClientSide) {
                    DynamiteProjectileEntity dynamite = new DynamiteProjectileEntity(level, player);
                    dynamite.setItem(stack);
                    dynamite.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 0.5F, 1.0F);
                    level.addFreshEntity(dynamite);
                }
                // Stats & cooldown
                player.awardStat(Stats.ITEM_USED.get(this));
                stack.consume(0, player); // note: consumes *0* items, so it's basically redundant
                player.getCooldowns().addCooldown(this, 100);
            }
            else {
                stack.consume(1, player);
            }
        });
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }



    @Override
    public @NotNull Projectile asProjectile(@NotNull Level pLevel, Position pPos, @NotNull ItemStack pStack, @NotNull Direction pDirection) {
        DynamiteProjectileEntity dynamite = new DynamiteProjectileEntity(pLevel, pPos.x(), pPos.y(), pPos.z());
        dynamite.setItem(pStack);
        return dynamite;
    }
}
