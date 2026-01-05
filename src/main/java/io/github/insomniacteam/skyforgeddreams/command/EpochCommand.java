package io.github.insomniacteam.skyforgeddreams.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.insomniacteam.skyforgeddreams.worldstate.EpochManager;
import io.github.insomniacteam.skyforgeddreams.worldstate.WorldEpoch;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

/**
 * Command for viewing and managing world epochs.
 * Uses the structure "/sd epoch <subcommand>" where "sd" is the main Skyforged Dreams command.
 */
public class EpochCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("sd")
                        .requires(source -> source.hasPermission(2)) // Requires OP level 2 for all subcommands
                        .then(Commands.literal("epoch")
                                .then(Commands.literal("info")
                                        .executes(EpochCommand::showEpochInfo))
                                .then(Commands.literal("next")
                                        .executes(EpochCommand::forceNextEpoch))
                                .then(Commands.literal("set")
                                        .then(Commands.argument("epoch", StringArgumentType.word())
                                                .suggests((context, builder) -> {
                                                    for (WorldEpoch epoch : WorldEpoch.values()) {
                                                        builder.suggest(epoch.getName());
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .executes(EpochCommand::setEpoch))))
        );
    }

    private static int showEpochInfo(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        EpochManager manager = EpochManager.get(level);

        WorldEpoch currentEpoch = manager.getCurrentEpoch();
        WorldEpoch nextEpoch = manager.getNextEpoch();
        int[] timeRemaining = manager.getDetailedTimeRemaining();
        int progressPercentage = manager.getProgressPercentage();

        int days = timeRemaining[0];
        int hours = timeRemaining[1];
        int minutes = timeRemaining[2];

        source.sendSuccess(() ->
                        Component.translatable("commands.skyforged_dreams.epoch.info.header"),
                false
        );

        source.sendSuccess(() ->
                        Component.translatable(
                                "commands.skyforged_dreams.epoch.info.current",
                                Component.translatable("epoch.skyforged_dreams." + currentEpoch.getName())
                        ),
                false
        );

        source.sendSuccess(() ->
                        Component.translatable(
                                "commands.skyforged_dreams.epoch.info.progress",
                                progressPercentage
                        ),
                false
        );

        source.sendSuccess(() ->
                        Component.translatable(
                                "commands.skyforged_dreams.epoch.info.remaining",
                                days, hours, minutes
                        ),
                false
        );

        if (nextEpoch != null) {
            source.sendSuccess(() ->
                            Component.translatable(
                                    "commands.skyforged_dreams.epoch.info.next",
                                    Component.translatable("epoch.skyforged_dreams." + nextEpoch.getName())
                            ),
                    false
            );
        }

        return 1;
    }

    private static int forceNextEpoch(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        EpochManager manager = EpochManager.get(level);

        WorldEpoch nextEpoch = manager.getNextEpoch();

        if (nextEpoch == null) {
            source.sendFailure(Component.translatable(
                    "commands.skyforged_dreams.epoch.next.no_next"
            ));
            return 0;
        }

        boolean success = manager.forceNextEpoch();

        if (success) {
            final WorldEpoch transitionedEpoch = nextEpoch;
            source.sendSuccess(() ->
                            Component.translatable(
                                    "commands.skyforged_dreams.epoch.next.success",
                                    Component.translatable("epoch.skyforged_dreams." + transitionedEpoch.getName())
                            ),
                    true
            );
            return 1;
        } else {
            source.sendFailure(Component.translatable(
                    "commands.skyforged_dreams.epoch.next.failed"
            ));
            return 0;
        }
    }

    private static int setEpoch(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String epochName = StringArgumentType.getString(context, "epoch");

        WorldEpoch foundEpoch = null;
        for (WorldEpoch epoch : WorldEpoch.values()) {
            if (epoch.getName().equalsIgnoreCase(epochName)) {
                foundEpoch = epoch;
                break;
            }
        }

        if (foundEpoch == null) {
            source.sendFailure(Component.translatable(
                    "commands.skyforged_dreams.epoch.set.invalid",
                    epochName
            ));
            return 0;
        }

        final WorldEpoch newEpoch = foundEpoch;
        ServerLevel level = source.getLevel();
        EpochManager manager = EpochManager.get(level);
        manager.forceEpoch(newEpoch);

        source.sendSuccess(() ->
                        Component.translatable(
                                "commands.skyforged_dreams.epoch.set.success",
                                Component.translatable("epoch.skyforged_dreams." + newEpoch.getName())
                        ),
                true
        );

        return 1;
    }
}
