package com.nippaku_zanmu.trans_addon;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.nippaku_zanmu.trans_addon.modules.Translation;
import com.nippaku_zanmu.trans_addon.util.TextReplacement;
import com.nippaku_zanmu.trans_addon.util.UniversalLangLoader;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class MeteorTranslation extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("MeteorTranslation");
    @Override
    public void onInitialize() {
        LOG.info("Initializing MeteorTransaction");

        // Load universal text replacement table early so that static strings
        // created during Meteor's PostInit (e.g. ChatUtils prefix) can be translated.
        UniversalLangLoader.reload();
        TextReplacement.setEnabled(true);

        // Modules
        Modules.get().add(new Translation());

        // Always dump collected unknown text when the client stops, even if
        // Translation.onDeactivate is not reliably called during shutdown.
        Runtime.getRuntime().addShutdownHook(new Thread(this::dumpUnknownText));
    }

    private void dumpUnknownText() {
        java.util.Set<String> unknown = TextReplacement.getUnknown();
        LOG.info("Shutdown hook: {} unknown English strings collected", unknown.size());
        if (unknown.isEmpty()) return;

        try {
            File path = new File("C:\\Users\\KJH50\\AppData\\Local\\Temp\\opencode\\meteor-translation-addon\\unknown.json");
            path.getParentFile().mkdirs();

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(path, false), StandardCharsets.UTF_8))) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                writer.write(gson.toJson(unknown));
            }

            LOG.info("Dumped {} unknown English strings to {}", unknown.size(), path);
        } catch (Exception e) {
            LOG.error("Failed to dump unknown text", e);
        }
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.nippaku_zanmu.trans_addon";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("Nippaku-Zanmu", "meteor-translation-addon");
    }
}
