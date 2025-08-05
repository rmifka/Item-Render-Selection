package com.renschi.irs;

import com.renschi.irs.config.IRSConfig;
import net.fabricmc.api.ClientModInitializer;

public class ItemRenderSelectionClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        IRSConfig.HANDLER.load();
    }
}