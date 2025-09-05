package net.mrmisc.brawlcraft.event.quests.bea;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrmisc.brawlcraft.BrawlCraftMod;
import net.mrmisc.brawlcraft.util.helpers.HelperMethods;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.mrmisc.brawlcraft.util.helpers.Constants.BEA;

@Mod.EventBusSubscriber(modid = BrawlCraftMod.MOD_ID)
public class BeaQuestEvent{

    private static final String TAG_BEA_ROOT = "BC_BeaQuest";
    private static final String TAG_BEA_VISITED = "VisitedNests";
    private static final String TAG_BEA_DONE = "Done";
    private static final Map<UUID, Integer> beaMap = new HashMap<>();
    private static final int REQUIRED_UNIQUE_NESTS = 10;

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock evt) {
        if (evt.getLevel().isClientSide) return;
        Player player = evt.getEntity();
        if (!(player instanceof ServerPlayer sp)) return;

        InteractionHand hand = evt.getHand();
        if (hand != InteractionHand.MAIN_HAND) return;

        BlockPos pos = evt.getPos();
        BlockState state = evt.getLevel().getBlockState(pos);

        if (!state.is(Blocks.BEE_NEST)) return;

        boolean justAdded = addVisitedNest(sp, (ServerLevel) evt.getLevel(), pos);

        if (!justAdded) return;

        if (HelperMethods.manageQuestThresholdCounter(sp, beaMap, REQUIRED_UNIQUE_NESTS, "Bee Nests interacted with: ", BEA) && !isQuestDone(sp)) {
            setQuestDone(sp);
            tryUnlockBea(sp);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone evt) {
        if (evt.isWasDeath()) {
            CompoundTag oldTag = evt.getOriginal().getPersistentData();
            CompoundTag newTag = evt.getEntity().getPersistentData();
            CompoundTag oldRoot = oldTag.getCompound(TAG_BEA_ROOT);
            if (!oldRoot.isEmpty()) {
                newTag.put(TAG_BEA_ROOT, oldRoot.copy());
            }
        }
    }


    private static CompoundTag getRoot(Player p) {
        CompoundTag pd = p.getPersistentData();
        if (!pd.contains(TAG_BEA_ROOT, CompoundTag.TAG_COMPOUND)) {
            pd.put(TAG_BEA_ROOT, new CompoundTag());
        }
        return pd.getCompound(TAG_BEA_ROOT);
    }

    private static ListTag getVisitedList(Player p) {
        CompoundTag root = getRoot(p);
        if (!root.contains(TAG_BEA_VISITED, ListTag.TAG_LIST)) {
            root.put(TAG_BEA_VISITED, new ListTag());
        }
        return root.getList(TAG_BEA_VISITED, StringTag.TAG_STRING);
    }

    private static boolean isQuestDone(Player p) {
        return getRoot(p).getBoolean(TAG_BEA_DONE);
    }

    private static void setQuestDone(Player p) {
        getRoot(p).putBoolean(TAG_BEA_DONE, true);
    }

    private static boolean addVisitedNest(ServerPlayer player, ServerLevel level, BlockPos pos) {
        ListTag list = getVisitedList(player);
        String key = encode(level.dimension(), pos);

        for (int i = 0; i < list.size(); i++) {
            if (list.getString(i).equals(key)) {
                return false;
            }
        }
        list.add(StringTag.valueOf(key));
        getRoot(player).put(TAG_BEA_VISITED, list);
        return true;
    }

    private static String encode(ResourceKey<Level> dim, BlockPos pos) {
        return dim.location() + "|" + pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }


    private static void tryUnlockBea(ServerPlayer player) {
        HelperMethods.completeQuest(BEA, player);
    }
}
