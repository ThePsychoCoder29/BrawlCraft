package net.mrmisc.brawlcraft.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mrmisc.brawlcraft.BrawlCraftMod;
import net.mrmisc.brawlcraft.entity.custom.DynamiteProjectileEntity;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(
            ForgeRegistries.ENTITY_TYPES, BrawlCraftMod.MOD_ID
    );

    public static final RegistryObject<EntityType<DynamiteProjectileEntity>> DYNAMITE =
            ENTITY_TYPES.register("dynamite" , ()-> EntityType.Builder.<DynamiteProjectileEntity>of(DynamiteProjectileEntity::new , MobCategory.MISC)
                    .sized(0.35f , 0.35f).build("dynamite"));

    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }
}
