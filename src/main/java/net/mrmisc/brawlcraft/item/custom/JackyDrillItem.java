package net.mrmisc.brawlcraft.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.mrmisc.brawlcraft.networking.c2s.BrawlerIndexProvider;
import org.jetbrains.annotations.NotNull;


public class JackyDrillItem extends Item {
    public JackyDrillItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockPos pPos, @NotNull LivingEntity pMiningEntity) {
        if(pMiningEntity instanceof Player player){
            player.getCapability(BrawlerIndexProvider.BRAWLER_INDEX).ifPresent(brawlerIndex -> {
                int index = brawlerIndex.getBrawlerIndex();
                if(index == 5){
                    destroy3x3(pLevel, pPos);
                }
            });
        }
        return super.mineBlock(pStack, pLevel, pState, pPos, pMiningEntity);
    }

    public static void destroy3x3(Level level, BlockPos centerPos) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos targetPos = centerPos.offset(dx, 0, dz);
                BlockState state = level.getBlockState(targetPos);
                if(state.getDestroySpeed(level, targetPos) < 0 || state.getDestroySpeed(level, targetPos) > 30f){
                    return;
                }
                level.destroyBlock(targetPos, targetPos == centerPos);
            }
        }
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack pStack, @NotNull BlockState pState) {
        return 3.0f;
    }
}
