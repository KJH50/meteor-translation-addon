package com.nippaku_zanmu.trans_addon.util;

import meteordevelopment.meteorclient.addons.AddonManager;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import java.util.Set;
import java.util.stream.Collectors;

public class TransUtil {
    private static final Minecraft mc = Minecraft.getInstance();

    /**
     * Translate a key using Minecraft's language system.
     * Falls back to en_us.json content via MC's language chain.
     * If the key is not found in any language file, extracts
     * a readable short name from the key itself as last resort.
     */
    public static String trans(String key) {
        String result = Component.translatable(key).getString();
        // If translation equals the key itself, it means no translation was found
        // in any language file (including en_us). Extract a readable fallback
        // from the key rather than showing the raw key string.
        if (result.equals(key)) {
            return fallbackFromKey(key);
        }
        return result;
    }

    /**
     * Extract a readable short name from a translation key as fallback.
     * e.g. "meteor.meteor_client.combat.auto_armor.name" → "auto_armor"
     * e.g. "meteor.hud.meteor.active_modules.description" → "active_modules"
     */
    private static String fallbackFromKey(String key) {
        String[] parts = key.split("\\.");
        if (parts.length >= 2) {
            String last = parts[parts.length - 1];
            String secondLast = parts[parts.length - 2];
            // Keys always end with .name or .description
            if (last.equals("name") || last.equals("description")) {
                return secondLast;
            }
        }
        return key;
    }

    public static Set<String> getAddonNames() {
        return AddonManager.ADDONS.stream().map(addon -> addon.name).map(TransUtil::baseFormat).collect(Collectors.toSet());
    }

    public static String getAddonName(Module m) {
        return TransUtil.baseFormat(m.addon == null ? "unknow_addon" : m.addon.name);
    }

    public static String baseFormat(String s) {
        //把某些Addon作者的不规范模块命名还原
        s = s.toLowerCase();
        s = s.replace(" ", "_");
        s = s.replace("-", "_");
        s = s.replace(".", "_");
        s = s.replace("\"", "_");
        return s;
    }
    public static String formatValue(String s){
        s = s.replace("\"", "\\\"");
        return s;
    }
}
