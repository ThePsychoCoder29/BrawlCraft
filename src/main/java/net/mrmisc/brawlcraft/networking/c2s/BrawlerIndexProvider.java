package net.mrmisc.brawlcraft.networking.c2s;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BrawlerIndexProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<BrawlerIndex> BRAWLER_INDEX = CapabilityManager.get(new CapabilityToken<BrawlerIndex>() {});

    private BrawlerIndex brawlerIndex = null;
    private final LazyOptional<BrawlerIndex> optional = LazyOptional.of(this::createBrawlerIndex);

    private BrawlerIndex createBrawlerIndex() {
        if(this.brawlerIndex == null){
            this.brawlerIndex = new BrawlerIndex();
        }
        return this.brawlerIndex;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(BRAWLER_INDEX)){
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider registryAccess) {
        CompoundTag nbt = new CompoundTag();
        createBrawlerIndex().saveNBT(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider registryAccess, CompoundTag nbt) {
        createBrawlerIndex().loadNBT(nbt);
    }
}
