package com.nippaku_zanmu.trans_addon.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Universal text replacement layer.
 * Replaces hardcoded English strings with Chinese translations at display time.
 * Works for any addon/plugin that renders text through Meteor's standard widgets
 * or sends messages through ChatUtils.
 */
public class TextReplacement {
    private static final Map<String, String> MAP = new LinkedHashMap<>();
    private static final Set<String> UNKNOWN = Collections.synchronizedSet(new LinkedHashSet<>());
    private static boolean scanUnknown = false;
    private static boolean enabled = false;

    private TextReplacement() {}

    public static void setEnabled(boolean enabled) {
        TextReplacement.enabled = enabled;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setScanUnknown(boolean scan) {
        scanUnknown = scan;
    }

    public static boolean isScanningUnknown() {
        return scanUnknown;
    }

    public static void load(Map<String, String> translations) {
        MAP.clear();
        MAP.putAll(translations);
    }

    public static void clear() {
        MAP.clear();
        UNKNOWN.clear();
    }

    /** Replace a static string. */
    public static String replace(String original) {
        if (!enabled || original == null || original.isEmpty()) return original;

        if (MAP.containsKey(original)) {
            return MAP.get(original);
        }

        if (scanUnknown && looksLikeEnglish(original)) {
            UNKNOWN.add(original);
        }

        return original;
    }

    /** Only record strings with at least four consecutive ASCII letters.
     *  This skips already-translated CJK text that happens to contain
     *  format specifiers like %s / %d, single-letter labels, and short
     *  acronyms embedded in Chinese (e.g. "方块ESP").
     */
    private static boolean looksLikeEnglish(String s) {
        int consecutive = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                consecutive++;
                if (consecutive >= 4) return true;
            } else {
                consecutive = 0;
            }
        }
        return false;
    }

    /**
     * Replace a format string, then format with args.
     * Preserves %s placeholders so translated templates still work.
     */
    public static String replaceFormat(String format, Object... args) {
        if (!enabled || format == null || format.isEmpty()) {
            return args.length == 0 ? format : String.format(format, args);
        }

        String translated = MAP.getOrDefault(format, format);

        if (scanUnknown && translated == format && looksLikeEnglish(format)) {
            UNKNOWN.add(format);
        }

        return args.length == 0 ? translated : String.format(translated, args);
    }

    /** Get all unknown strings seen since last clear. */
    public static Set<String> getUnknown() {
        return new LinkedHashSet<>(UNKNOWN);
    }

    public static void clearUnknown() {
        UNKNOWN.clear();
    }

    public static Map<String, String> getMap() {
        return Collections.unmodifiableMap(MAP);
    }
}
