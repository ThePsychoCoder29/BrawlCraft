package net.mrmisc.brawlcraft.networking.s2c;

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

@SuppressWarnings("deprecation")
public class BrawlerUnlockProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<BrawlerUnlock> BRAWLER_UNLOCK = CapabilityManager.get(new CapabilityToken<>() {
    });

    private BrawlerUnlock brawlerUnlock = null;
    private final LazyOptional<BrawlerUnlock> optional = LazyOptional.of(this::createBrawlerUnlock);

    private BrawlerUnlock createBrawlerUnlock() {
        if(this.brawlerUnlock == null){
            this.brawlerUnlock = new BrawlerUnlock();
        }
        return this.brawlerUnlock;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(BRAWLER_UNLOCK)){
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider registryAccess) {
        CompoundTag nbt = new CompoundTag();
        createBrawlerUnlock().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider registryAccess, CompoundTag nbt) {
        createBrawlerUnlock().loadNBTData(nbt);
    }
}
