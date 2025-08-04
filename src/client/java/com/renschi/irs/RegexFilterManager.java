package com.renschi.irs;

import com.renschi.irs.config.IRSConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RegexFilterManager {

    private static final List<Pattern> compiledPatterns = new ArrayList<>();

    static {
        reloadCompiledPatterns();
    }

    public static void reloadCompiledPatterns() {
        compiledPatterns.clear();
        for (String regex : IRSConfig.HANDLER.instance().regexFilters) {
            try {
                compiledPatterns.add(Pattern.compile(regex));
            } catch (Exception e) {
                // Log invalid regex or ignore
                System.err.println("Invalid regex pattern: " + regex);
            }
        }
    }

    public static boolean toggleRegex(String regex) {
        boolean added = IRSConfig.HANDLER.instance().regexFilters.add(regex);
        if (!added) {
            IRSConfig.HANDLER.instance().regexFilters.remove(regex);
        }
        IRSConfig.HANDLER.save();
        reloadCompiledPatterns();  // Update compiled patterns after change
        return added;
    }

    public static boolean shouldHide(String itemName) {
        for (Pattern pattern : compiledPatterns) {
            if (pattern.matcher(itemName.toLowerCase()).find()) {
                return true;
            }
        }
        return false;
    }

}
