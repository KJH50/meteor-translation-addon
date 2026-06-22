package com.nippaku_zanmu.trans_addon.util;

import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Category;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Snapshots original (English) names before translation mutates them.
 * Translation mutates Category#name and SettingGroup#name for display,
 * which would otherwise break key generation that reads these fields.
 */
public final class NameCache {
    private static final ConcurrentHashMap<Category, String> CATEGORY_ORIGINAL = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<SettingGroup, String> GROUP_ORIGINAL = new ConcurrentHashMap<>();

    private NameCache() {}

    /** Returns original category name, recording on first observation. */
    public static String category(Category category) {
        return CATEGORY_ORIGINAL.computeIfAbsent(category, c -> c.name);
    }

    /** Returns original group name, recording on first observation. */
    public static String group(SettingGroup group) {
        return GROUP_ORIGINAL.computeIfAbsent(group, g -> g.name);
    }
}
