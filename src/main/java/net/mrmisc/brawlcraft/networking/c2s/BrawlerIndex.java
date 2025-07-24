package net.mrmisc.brawlcraft.networking.c2s;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public class BrawlerIndex {
    private static int brawlerIndex;

    public int getBrawlerIndex(){
        return brawlerIndex;
    }

    public static void setBrawlerIndex(int index){
        BrawlerIndex.brawlerIndex = index;
    }

    public void saveNBT(CompoundTag nbt){
        nbt.putInt("brawlerIndex", brawlerIndex);
    }

    public void loadNBT(CompoundTag nbt){
        brawlerIndex = nbt.getInt("brawlerIndex");
    }
}
