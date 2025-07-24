package net.mrmisc.brawlcraft.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.mrmisc.brawlcraft.BrawlCraftMod;
import net.mrmisc.brawlcraft.item.ModItems;

public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB, BrawlCraftMod.MOD_ID
    );

    public static final RegistryObject<CreativeModeTab> BRAWLCRAFT_TAB = CREATIVE_MODE_TAB.register("brawlcrafttab",
            ()-> CreativeModeTab.builder().
                    icon(()-> new ItemStack(ModItems.DYNAMITE.get())).
                    title(Component.translatable("creativemodetab.brawlcraft.brawlcrafttab")).
                    displayItems((itemDisplayParams, output) -> {
                            output.accept(ModItems.DYNAMITE.get());
                            output.accept(ModItems.JACKY_DRILL.get());
                    })
                    .build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TAB.register(eventBus);
    }

}
