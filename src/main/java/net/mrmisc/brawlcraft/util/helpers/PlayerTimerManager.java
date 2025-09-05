package net.mrmisc.brawlcraft.util.helpers;

import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.util.concurrent.*;

public class PlayerTimerManager {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Map<UUID, List<ScheduledFuture<?>>> playerDelays = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> playerTimeLimits = new ConcurrentHashMap<>();

    /**
     * Schedule a task to run after a delay (in seconds) for a player.
     */
    public static void scheduleDelay(ServerPlayer player, int delaySeconds, Runnable task) {
        ScheduledFuture<?> future = scheduler.schedule(task, delaySeconds, TimeUnit.SECONDS);
        playerDelays.computeIfAbsent(player.getUUID(), k -> new ArrayList<>()).add(future);
    }

    /**
     * Start a time limit (in seconds) for a player.
     */
    public static void startTimeLimit(ServerPlayer player, int seconds) {
        long expiryTime = System.currentTimeMillis() + (seconds * 1000L);
        playerTimeLimits.put(player.getUUID(), expiryTime);
    }

    /**
     * Check if the player's time limit is still active.
     */
    public static boolean isWithinTimeLimit(ServerPlayer player) {
        Long expiryTime = playerTimeLimits.get(player.getUUID());
        if (expiryTime == null) return false;
        return System.currentTimeMillis() <= expiryTime;
    }

    /**
     * Cancel all pending delayed tasks for a player.
     */
    public static void cancelDelays(ServerPlayer player) {
        List<ScheduledFuture<?>> futures = playerDelays.remove(player.getUUID());
        if (futures != null) {
            for (ScheduledFuture<?> future : futures) {
                future.cancel(false);
            }
        }
    }

    /**
     * Clear player's time limit.
     */
    public static void clearTimeLimit(ServerPlayer player) {
        playerTimeLimits.remove(player.getUUID());
    }
}
