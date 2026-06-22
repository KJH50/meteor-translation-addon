package com.nippaku_zanmu.trans_addon.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads the universal translation table from the addon's classpath resources:
 * /assets/meteor-translation-addon/lang/universal_{lang}.json
 */
public class UniversalLangLoader {
    private static final Logger LOG = MeteorClient.LOG;

    public static void reload() {
        String langCode = getCurrentLanguage();
        Map<String, String> translations = load(langCode);

        if (translations.isEmpty() && !langCode.equals("zh_cn")) {
            translations = load("zh_cn");
        }

        TextReplacement.load(translations);
        LOG.info("[MeteorTranslation] Loaded {} universal translations for {}", translations.size(), langCode);
    }

    private static String getCurrentLanguage() {
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.options != null) {
            return mc.options.languageCode;
        }
        return "zh_cn";
    }

    private static Map<String, String> load(String langCode) {
        Map<String, String> result = new HashMap<>();

        String path = "/assets/meteor-translation-addon/lang/universal_" + langCode + ".json";
        try (InputStream is = UniversalLangLoader.class.getResourceAsStream(path)) {
            if (is == null) return result;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                    if (entry.getValue().isJsonPrimitive()) {
                        result.put(entry.getKey(), entry.getValue().getAsString());
                    }
                }
            }
        } catch (IOException | RuntimeException e) {
            LOG.warn("[MeteorTranslation] Failed to load universal translations for {}: {}", langCode, e.getMessage());
        }

        return result;
    }
}
