package com.renschi.irs.config;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import com.renschi.irs.RegexFilterManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class IRSConfig {
    public static ConfigClassHandler<IRSConfig> HANDLER = ConfigClassHandler.createBuilder(IRSConfig.class)
            .id(ResourceLocation.parse("irs"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance()
                            .getConfigDir()
                            .resolve("irs_config.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry
    public Set<String> regexFilters = new HashSet<>();

    public Screen createConfigScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Component.literal("ItemRenderSelection Configuration"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Config"))
                        .group(ListOption.<String>createBuilder()
                                .name(Component.literal("Regex Filters"))
                                .binding(Binding.generic(
                                        new ArrayList<>(regexFilters),
                                        () -> new ArrayList<>(regexFilters),
                                        newVal -> {
                                            regexFilters.clear();
                                            regexFilters.addAll(newVal);
                                            RegexFilterManager.reloadCompiledPatterns();
                                            HANDLER.save();
                                        }
                                ))
                                .controller(StringControllerBuilder::create)
                                .initial("")
                                .build())
                        .build())
                .build()
                .generateScreen(parent);
    }
}
