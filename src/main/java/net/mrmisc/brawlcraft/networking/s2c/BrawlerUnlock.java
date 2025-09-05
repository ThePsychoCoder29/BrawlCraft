
package net.mrmisc.brawlcraft.networking.s2c;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.util.HashSet;
import java.util.Set;

@AutoRegisterCapability
public class BrawlerUnlock {
    private final Set<String> brawlerUnlock = new HashSet<>();

    public Set<String> getBrawlerUnlock(){
        return brawlerUnlock;
    }
    public Set<String> getAll() {
        return this.brawlerUnlock;
    }

    public void addBrawlerUnlock(String name){
        brawlerUnlock.add(name);
    }

    public void removeBrawlerUnlock(String name){
        brawlerUnlock.remove(name);
    }
    public void clearBrawlerUnlock(){
        brawlerUnlock.clear();
    }

    public void copy(Set<String> brawlerUnlock) {
        this.brawlerUnlock.clear();
        this.brawlerUnlock.addAll(brawlerUnlock);
    }

    public void saveNBTData(CompoundTag tag) {
        ListTag listTag = new ListTag();
        for (String name : brawlerUnlock) {
            listTag.add(StringTag.valueOf(name));
        }
        tag.put("unlockedbrawlers", listTag);
    }

    public void loadNBTData(CompoundTag tag) {
        ListTag listTag = tag.getList("unlockedbrawlers", Tag.TAG_STRING);
        brawlerUnlock.clear();
        for (Tag entry : listTag) {
            brawlerUnlock.add(entry.getAsString());
        }
    }
}
