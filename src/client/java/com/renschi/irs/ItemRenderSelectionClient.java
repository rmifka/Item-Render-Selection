package com.renschi.irs;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.renschi.irs.config.IRSConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.Set;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ItemRenderSelectionClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        IRSConfig.HANDLER.load();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    literal("irs")
                            .then(argument("regex", StringArgumentType.greedyString())
                                    .executes(ctx -> {
                                        String regex = StringArgumentType.getString(ctx, "regex");
                                        boolean added = RegexFilterManager.toggleRegex(regex);

                                        ctx.getSource().sendFeedback(
                                                Component.literal((added ? "✔ Added" : "✖ Removed") + " filter: ")
                                                        .withStyle(added ? ChatFormatting.GREEN : ChatFormatting.RED)
                                                        .append(Component.literal(regex).withStyle(ChatFormatting.YELLOW))
                                        );
                                        return 1;
                                    }))
                            .executes(ctx -> {
                                Set<String> filters = IRSConfig.HANDLER.instance().regexFilters;
                                if (filters.isEmpty()) {
                                    ctx.getSource().sendFeedback(
                                            Component.literal("No active filters.")
                                                    .withStyle(ChatFormatting.GRAY)
                                    );
                                } else {
                                    ctx.getSource().sendFeedback(
                                            Component.literal("§6Active Filters (" + filters.size() + "):")
                                    );
                                    for (String filter : filters) {
                                        ctx.getSource().sendFeedback(
                                                Component.literal(" - ").withStyle(ChatFormatting.GRAY)
                                                        .append(Component.literal(filter).withStyle(ChatFormatting.YELLOW))
                                        );
                                    }
                                }
                                return 1;
                            })
            );
        });
    }
}