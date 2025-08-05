package com.renschi.irs;

import com.renschi.irs.config.IRSConfig;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class RegexFilterManager {
    private static final List<Pattern> compiledPatterns = new ArrayList<>();
    private static final Set<ItemEntity> SUPPRESSED_ENTITIES = Collections.newSetFromMap(new WeakHashMap<>());
    private static final Map<String, Boolean> filterCache = new ConcurrentHashMap<>();

    static {
        reloadCompiledPatterns();
    }

    public static void reloadCompiledPatterns() {
        compiledPatterns.clear();
        filterCache.clear(); // Clear cache when patterns change
        for (String regex : IRSConfig.HANDLER.instance().regexFilters) {
            try {
                System.out.println(regex);
                compiledPatterns.add(Pattern.compile(regex));
            } catch (Exception e) {
                System.err.println("Invalid regex pattern: " + regex);
            }
        }
        onFilterSettingsChanged();
    }

    public static boolean toggleRegex(String regex) {
        boolean added = IRSConfig.HANDLER.instance().regexFilters.add(regex);
        if (!added) {
            IRSConfig.HANDLER.instance().regexFilters.remove(regex);
        }
        IRSConfig.HANDLER.save();
        reloadCompiledPatterns();
        return added;
    }

    public static boolean shouldHide(String itemName) {
        String lowerCaseName = itemName.toLowerCase();
        return filterCache.computeIfAbsent(lowerCaseName, name -> {
            for (Pattern pattern : compiledPatterns) {
                if (pattern.matcher(name).find()) {
                    return true;
                }
            }
            return false;
        });
    }

    public static boolean isSuppressed(ItemEntity entity) {
        return SUPPRESSED_ENTITIES.contains(entity);
    }

    public static void updateSuppression(ItemEntity entity) {
        String name = entity.getName().getString();
        if (shouldHide(name)) {
            SUPPRESSED_ENTITIES.add(entity);
        } else {
            SUPPRESSED_ENTITIES.remove(entity);
        }
    }

    private static void onFilterSettingsChanged() {
        Set<ItemEntity> entities = Set.copyOf(SUPPRESSED_ENTITIES);
        SUPPRESSED_ENTITIES.clear();
        for (ItemEntity entity : entities) {
            if (!entity.isRemoved()) {
                updateSuppression(entity);
            }
        }
    }
}