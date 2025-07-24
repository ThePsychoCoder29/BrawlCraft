package net.mrmisc.brawlcraft.event.brawlers.beaspike;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrmisc.brawlcraft.BrawlCraftMod;
import net.mrmisc.brawlcraft.networking.c2s.BrawlerIndexProvider;

@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BeaSummonBeesOrSpikeSummonCactusEvent {

    @SubscribeEvent
    public static void summonBeesOrSummonCactus(LivingHurtEvent event){
        LivingEntity playerEntity = event.getEntity();
        if(playerEntity instanceof Player player) {
            player.getCapability(BrawlerIndexProvider.BRAWLER_INDEX).ifPresent(brawlerIndex -> {
                int index = brawlerIndex.getBrawlerIndex();
                if(index == 4) {
                    Entity entity = event.getSource().getDirectEntity();
                    if (entity instanceof LivingEntity livingEntity) {
                        Level level = entity.level();
                        if (!level.isClientSide()) {
                            BlockPos pos = player.getOnPos();
                            ServerLevel serverLevel = (ServerLevel) event.getEntity().level();
                            Bee bee = EntityType.BEE.create(serverLevel);
                            if (!(bee == null)) {
                                bee.moveTo(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, level.random.nextFloat() * 360F, 0);
                                // Set the bee to be angry at the target
                                bee.setTarget(livingEntity); // This causes attack behavior
                                bee.setPersistentAngerTarget(livingEntity.getUUID());
                                bee.setRemainingPersistentAngerTime(30);
                                level.addFreshEntity(bee);
                            }
                        }
                    }
                } 
                else if (index == 6) {
                    Entity entity = event.getSource().getDirectEntity();
                    if(entity instanceof LivingEntity livingEntity){
                        Level level = entity.level();
                        if(!level.isClientSide()){
                            BlockPos pos = livingEntity.getOnPos();
                            ServerLevel serverLevel = (ServerLevel) event.getEntity().level();
                            serverLevel.setBlock(pos.offset(1, -1, 0), Blocks.SAND.defaultBlockState(), 13);
                            serverLevel.setBlock(pos.offset(1, 0, 0), Blocks.CACTUS.defaultBlockState(), 13);
                        }
                    }
                }
            });
        }
    }
}
