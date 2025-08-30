package net.mrmisc.brawlcraft.networking.c2s;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.util.HashSet;
import java.util.Set;

@AutoRegisterCapability
public class BrawlerIndex {
    public static Set<Integer> unlockedBrawlers = new HashSet<>();
    private static int brawlerIndex;

    public int getBrawlerIndex(){
        return brawlerIndex;
    }

    public Set<Integer> getUnlockedBrawlers(){
        return unlockedBrawlers;
    }

    public static void addUnlockedBrawlers(int brawlerIndex){
        unlockedBrawlers.add(brawlerIndex);
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
