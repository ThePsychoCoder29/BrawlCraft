package net.mrmisc.brawlcraft.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.mrmisc.brawlcraft.util.helpers.Constants;
import net.mrmisc.brawlcraft.util.helpers.HelperMethods;

import java.util.Arrays;


public class BrawlerCommand {

    public BrawlerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("brawler")
                        // /brawler unlock <brawler> [player]
                        .then(Commands.literal("unlock")
                                .then(Commands.argument("brawler", StringArgumentType.word())
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                                Arrays.stream(Constants.Brawlers.values())
                                                        .filter(b -> b != Constants.Brawlers.NORMAL) // skip NORMAL
                                                        .map(Constants.Brawlers::getName)
                                                        .toList(),
                                                builder
                                        ))
                                        .executes(ctx -> {
                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            String brawlerName = StringArgumentType.getString(ctx, "brawler");
                                            return unlockBrawler(ctx.getSource(), player, brawlerName);
                                        })
                                        .then(Commands.argument("target", EntityArgument.player())
                                                .executes(ctx -> {
                                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                                                    String brawlerName = StringArgumentType.getString(ctx, "brawler");
                                                    return unlockBrawler(ctx.getSource(), target, brawlerName);
                                                })
                                        )
                                )
                        )
                        // /brawler remove <brawler> [player]
                        .then(Commands.literal("lock")
                                .then(Commands.argument("brawler", StringArgumentType.word())
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                                Arrays.stream(Constants.Brawlers.values())
                                                        .filter(b -> b != Constants.Brawlers.NORMAL) // skip NORMAL
                                                        .map(Constants.Brawlers::getName)
                                                        .toList(),
                                                builder
                                        ))
                                        .executes(ctx -> {
                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            String brawlerName = StringArgumentType.getString(ctx, "brawler");
                                            return removeBrawler(ctx.getSource(), player, brawlerName);
                                        })
                                        .then(Commands.argument("target", EntityArgument.player())
                                                .executes(ctx -> {
                                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                                                    String brawlerName = StringArgumentType.getString(ctx, "brawler");
                                                    return removeBrawler(ctx.getSource(), target, brawlerName);
                                                })
                                        )
                                )
                        )
                        // /brawler clear [player]
                        .then(Commands.literal("clear")
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    return clearBrawlers(ctx.getSource(), player);
                                })
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(ctx -> {
                                            ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                                            return clearBrawlers(ctx.getSource(), target);
                                        })
                                )
                        )
        );
    }

    // --- Handlers ---

    private int unlockBrawler(CommandSourceStack source, ServerPlayer target, String brawlerName) throws CommandSyntaxException {
        return runCMD(brawlerName,
                "Unlocked ",
                ()-> HelperMethods.completeQuest(brawlerName, target),
                source,
                target);
    }

    private int removeBrawler(CommandSourceStack source, ServerPlayer target, String brawlerName) throws CommandSyntaxException {
        return runCMD(brawlerName,
                "Locked ",
                ()-> HelperMethods.removeBrawler(brawlerName, target),
                source,
                target);
    }

    public static int runCMD(String brawlerName, String action, Runnable runnable, CommandSourceStack stack, ServerPlayer player) throws CommandSyntaxException {
        Constants.Brawlers brawler = Constants.Brawlers.fromName(brawlerName);
        if(brawler == Constants.Brawlers.NORMAL || brawlerName.equals(Constants.NO_BRAWLER)){
            stack.sendFailure(Component.literal("Unknown Brawler: " + brawlerName));
            return 0;
        }

        runnable.run();

        if(!stack.isPlayer() || !stack.getPlayerOrException().equals(player)){
            stack.sendSuccess(() ->
                    Component.literal(action + brawler.getName() + " for " + player.getName().getString()), true);
        }
        return 1;
    }

    private int clearBrawlers(CommandSourceStack source, ServerPlayer target) throws CommandSyntaxException {
        HelperMethods.clearBrawlers(target);

        if (!source.isPlayer() || !source.getPlayerOrException().equals(target)) {
            source.sendSuccess(() ->
                    Component.literal("Cleared all brawlers for " + target.getName().getString()), true);
        }

        return 1;
    }
}
