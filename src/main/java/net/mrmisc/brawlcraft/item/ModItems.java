package net.mrmisc.brawlcraft.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mrmisc.brawlcraft.BrawlCraftMod;
import net.mrmisc.brawlcraft.item.custom.DynamiteItem;
import net.mrmisc.brawlcraft.item.custom.JackyDrillItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(
            ForgeRegistries.ITEMS, BrawlCraftMod.MOD_ID);

    public static final RegistryObject<Item> DYNAMITE = ITEMS.register("dynamite",
            ()-> new DynamiteItem(new Item.Properties()));

    public static final RegistryObject<Item> JACKY_DRILL = ITEMS.register("jacky_drill",
            ()-> new JackyDrillItem(new Item.Properties()));
    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
