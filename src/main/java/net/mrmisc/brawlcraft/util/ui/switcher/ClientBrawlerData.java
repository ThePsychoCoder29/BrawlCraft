package net.mrmisc.brawlcraft.util.ui.switcher;

import java.util.HashSet;
import java.util.Set;

public class ClientBrawlerData {
    public static final Set<String> UNLOCKED_BRAWLERS = new HashSet<>();

    public static void print() {
        System.out.println("[CLIENT] Current unlocked: " + UNLOCKED_BRAWLERS);
    }
}
