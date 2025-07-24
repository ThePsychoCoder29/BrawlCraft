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
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        if(Screen.hasShiftDown()){
            pTooltipComponents.add(Component.translatable("tooltip.brawlcraft.dynamite.tooltip"));
        }
        else {
            pTooltipComponents.add(Component.translatable("tooltip.brawlcraft.shift.tooltip"));
            super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        pLevel.playSound(
                null,
                pPlayer.getX(),
                pPlayer.getY(),
                pPlayer.getZ(),
                SoundEvents.SNOWBALL_THROW,
                SoundSource.NEUTRAL,
                0.5F,
                0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F)
        );
        if (!pLevel.isClientSide) {
            DynamiteProjectileEntity dynamite = new DynamiteProjectileEntity(pLevel, pPlayer);
            dynamite.setItem(itemstack);
            dynamite.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 0.5F, 1.0F);
            pLevel.addFreshEntity(dynamite);
        }
        pPlayer.awardStat(Stats.ITEM_USED.get(this));
        pPlayer.getCapability(BrawlerIndexProvider.BRAWLER_INDEX).ifPresent(brawlerIndex -> {
            int index = brawlerIndex.getBrawlerIndex();
            if(index == 2){
                itemstack.consume(0, pPlayer);
            }
            else {
                itemstack.consume(1, pPlayer);
            }
        });
        pPlayer.getCooldowns().addCooldown(this, 100);
        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }


    @Override
    public @NotNull Projectile asProjectile(@NotNull Level pLevel, Position pPos, @NotNull ItemStack pStack, @NotNull Direction pDirection) {
        DynamiteProjectileEntity dynamite = new DynamiteProjectileEntity(pLevel, pPos.x(), pPos.y(), pPos.z());
        dynamite.setItem(pStack);
        return dynamite;
    }
}
