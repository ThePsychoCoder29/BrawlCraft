package net.mrmisc.brawlcraft.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.mrmisc.brawlcraft.item.ModItems;
import org.jetbrains.annotations.NotNull;

public class DynamiteProjectileEntity extends Snowball {

    public DynamiteProjectileEntity(EntityType<? extends Snowball> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public DynamiteProjectileEntity(Level pLevel, LivingEntity pShooter) {
        super(pLevel, pShooter);
    }

    public DynamiteProjectileEntity(Level pLevel, double pX, double pY, double pZ) {
        super(pLevel, pX, pY, pZ);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.DYNAMITE.get();
    }

    private ParticleOptions getParticle() {
        ItemStack itemstack = this.getItem();
        return !itemstack.isEmpty() && !itemstack.is(this.getDefaultItem())
                ? new ItemParticleOption(ParticleTypes.ITEM, itemstack)
                : ParticleTypes.CRIT;
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 3) {
            ParticleOptions particleoptions = this.getParticle();

            for (int i = 0; i < 8; i++) {
                this.level().addParticle(particleoptions, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Level level = pResult.getEntity().level();
        BlockPos pos = pResult.getEntity().getOnPos();
        createExplosion(level, pos, 0);
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        BlockPos pos = pResult.getBlockPos();
        Level level = super.level();
        createExplosion(level, pos, 1);
    }

    protected void createExplosion(Level level, BlockPos pos, int yOffset){
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        if(!level.isClientSide()) {
            level.explode(this,Explosion.getDefaultDamageSource(this.level(), this), null,
                    x, y + yOffset , z, 2.0F, false, Level.ExplosionInteraction.TNT);
        }
    }
}
