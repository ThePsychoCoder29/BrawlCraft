package net.mrmisc.brawlcraft.networking.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class BrawlerDataPacket{
    private final int brawlerIndex;

    public BrawlerDataPacket(int brawlerIndex){
        this.brawlerIndex = brawlerIndex;
    }
    public BrawlerDataPacket(FriendlyByteBuf buf){
        this.brawlerIndex = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeInt(brawlerIndex);
    }

    public void handle(CustomPayloadEvent.Context context){
        context.enqueueWork(()-> BrawlerIndex.setBrawlerIndex(brawlerIndex));
        context.setPacketHandled(true);
    }

}
