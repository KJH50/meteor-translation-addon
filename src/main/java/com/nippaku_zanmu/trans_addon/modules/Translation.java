package com.nippaku_zanmu.trans_addon.modules;


import com.nippaku_zanmu.trans_addon.MeteorTranslation;
import com.nippaku_zanmu.trans_addon.settings.StringSelectSetting;
import com.nippaku_zanmu.trans_addon.util.JsonDump;
import com.nippaku_zanmu.trans_addon.util.NameCache;
import com.nippaku_zanmu.trans_addon.util.TextReplacement;
import com.nippaku_zanmu.trans_addon.util.TransUtil;
import com.nippaku_zanmu.trans_addon.util.UniversalLangLoader;
import com.nippaku_zanmu.trans_addon.mixin.CategoryAccessor;
import com.nippaku_zanmu.trans_addon.mixin.CommandAccessor;
import com.nippaku_zanmu.trans_addon.mixin.HudElementInfoAccessor;
import com.nippaku_zanmu.trans_addon.mixin.ModuleAccessor;
import com.nippaku_zanmu.trans_addon.mixin.PresetAccessor;
import com.nippaku_zanmu.trans_addon.mixin.SettingAccessor;
import com.nippaku_zanmu.trans_addon.mixin.SettingGroupAccessor;
import com.nippaku_zanmu.trans_addon.mixin.TabAccessor;
import com.nippaku_zanmu.trans_addon.util.trans_engine.AbstractTransEngine;
import com.nippaku_zanmu.trans_addon.util.trans_engine.EngineManager;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class Translation extends Module {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    public final Setting<Boolean> bSetAutoTranslation = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-translation")
        .description("")
        .defaultValue(false)
        .build());

    public final Setting<Set<String>> translationModules = sgGeneral.add(new StringSelectSetting.Builder()
        .validValues(TransUtil.getAddonNames())
        .defaultValue(TransUtil.getAddonNames())
        .name("translation-modules")
        .build());

    public final Setting<Boolean> bSetUniversalTranslation = sgGeneral.add(new BoolSetting.Builder()
        .name("universal-translation")
        .description("Translate hardcoded widget labels, window titles and chat messages via universal text replacement.")
        .defaultValue(true)
        .build());

    private final SettingGroup sgDev = this.settings.createGroup("Dev", false);

    public final Setting<String> strSetTransEngine = sgDev.add(new StringSetting.Builder()
        .defaultValue("NEW")
        .name("translation-engine")
        .build());


    public final Setting<String> sSetDumpPath = sgDev.add(new StringSetting.Builder()
        .name("dump-path")
        .defaultValue("D:\\hack\\Misc\\meteor-translation-addon\\test\\en_us.json")
        .build());

    public final Setting<Boolean> bSetDumpText = sgDev.add(new BoolSetting.Builder()
        .name("dump-text")
        .defaultValue(false)
        .build()
    );
    public final Setting<String> strSetDumpTextEngine = sgDev.add(new StringSetting.Builder()
        .defaultValue("OLD")
        .visible(bSetDumpText::get)
        .name("dump-text-engine")
        .build());

    public final Setting<Boolean> bSetScanUnknown = sgDev.add(new BoolSetting.Builder()
        .name("scan-unknown-text")
        .description("Record English strings that have no universal translation.")
        .defaultValue(true)
        .build());

    public final Setting<String> sSetUnknownDumpPath = sgDev.add(new StringSetting.Builder()
        .name("unknown-dump-path")
        .defaultValue("C:\\Users\\KJH50\\AppData\\Local\\Temp\\opencode\\meteor-translation-addon\\unknown.json")
        .visible(bSetScanUnknown::get)
        .build());


    public Translation() {
        super(MeteorTranslation.CATEGORY, "meteor-trans", "An example module that highlights the center of the world.");
    }

    private boolean isTranslation = false;

    @Override
    public void onActivate() {
        if (bSetUniversalTranslation.get()) {
            UniversalLangLoader.reload();
            TextReplacement.setEnabled(true);
            TextReplacement.setScanUnknown(bSetScanUnknown.get());
        } else {
            TextReplacement.setEnabled(false);
            TextReplacement.setScanUnknown(false);
        }

        if (bSetAutoTranslation.get() && !isTranslation) {
            isTranslation = true;
            tran();
            // Delayed re-translation to catch commands/HUD elements registered after init
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    mc.execute(() -> {
                        if (isActive()) {
                            tran();
                        }
                    });
                }
            }, 5000);
        }

        ChatUtils.warning("流星翻译插件是开源的项目且完全免费 作者不会以任何形式对此插件进行收费");
        ChatUtils.warning("如果你购买了此插件 则说明你被骗了");
    }

    @Override
    public void onDeactivate() {
        if (bSetScanUnknown.get()) {
            dumpUnknownText();
        }

        TextReplacement.setEnabled(false);
        TextReplacement.setScanUnknown(false);
    }


    @Override
    public WWidget getWidget(GuiTheme theme) {
        WVerticalList list = theme.verticalList();

        WHorizontalList l1 = list.add(theme.horizontalList()).expandX().widget();

        WButton start = l1.add(theme.button("Translate")).expandX().widget();
        start.action = () -> {
            if (this.isActive()) {
                isTranslation = true;
                tran();
                ChatUtils.info("翻译已应用，5秒后自动重翻以确保所有内容都被翻译");
                // Delayed re-translation to catch late-registered content
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mc.execute(() -> {
                            if (isActive()) {
                                tran();
                                ChatUtils.info("延迟重翻完成");
                            }
                        });
                    }
                }, 5000);
            } else {
                ChatUtils.warning("你首先要开启此模块");
            }
        };

        if (!sgDev.sectionExpanded)
            return list;
        WHorizontalList l2 = list.add(theme.horizontalList()).expandX().widget();
        WButton dump = l2.add(theme.button("Dump")).expandX().widget();
        dump.action = () -> {
            JsonDump.getINSTANCE().write(EngineManager.getInstance().getEngine(strSetTransEngine.get()), EngineManager.getInstance().getEngine(strSetDumpTextEngine.get()));
        };

        if (bSetScanUnknown.get()) {
            WHorizontalList l3 = list.add(theme.horizontalList()).expandX().widget();
            WButton dumpUnknown = l3.add(theme.button("Dump Unknown")).expandX().widget();
            dumpUnknown.action = this::dumpUnknownText;
        }

        return list;
    }

    private void dumpUnknownText() {
        java.util.Set<String> unknown = TextReplacement.getUnknown();
        if (unknown.isEmpty()) {
            ChatUtils.info("没有发现未知英文文本");
            return;
        }

        try {
            java.io.File path = new java.io.File(sSetUnknownDumpPath.get());
            path.getParentFile().mkdirs();

            try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(
                new java.io.FileOutputStream(path, false), java.nio.charset.StandardCharsets.UTF_8))) {

                com.google.gson.Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
                writer.write(gson.toJson(unknown));
            }

            ChatUtils.info("已导出 %d 条未知文本到 %s", unknown.size(), sSetUnknownDumpPath.get());
        } catch (Exception e) {
            ChatUtils.error("导出未知文本失败: %s", e.getMessage());
        }
    }


    public void tran() {
        tran(EngineManager.getInstance().getEngine(strSetTransEngine.get()));
    }


    private void tran(AbstractTransEngine engine) {
        // --- Module + Setting translation (existing) ---
        for (Module module : Modules.get().getAll()) {
            String addonName = TransUtil.getAddonName(module);
            if (!translationModules.get().contains(addonName)) continue;
            //插件过滤

            String tranName = engine.transModuleName(module);
            // 经过翻译的名称
            ((ModuleAccessor) module).setTitle(Utils.nameToTitle(tranName));
            //把标题设为翻译之后的名称

            String tranDescry = engine.transModuleDescription(module);
            ((ModuleAccessor) module).setDescription(Utils.nameToTitle(tranDescry));
            //翻译简介

            for (SettingGroup group : module.settings.groups) {
                // First translate all settings (using original group.name for key generation)
                for (Setting<?> setting : ((SettingGroupAccessor) group).getSettings()) {
                    String tranSettName = engine.transSettingName(module, group, setting);
                    ((SettingAccessor) setting).setTitle(Utils.nameToTitle(tranSettName));

                    String tranSettDesc = engine.transSettingDes(module, group, setting);
                    ((SettingAccessor) setting).setDescription(Utils.nameToTitle(tranSettDesc));
                }

                // Then translate the group name itself
                String tranGroupName = engine.transGroupName(module, group);
                ((SettingGroupAccessor) group).setName(Utils.nameToTitle(tranGroupName));
            }

        }

        // --- HUD Element translation ---
        for (HudElementInfo<?> info : Hud.get().infos.values()) {
            String tranTitle = engine.transHudTitle(info.group, info);
            ((HudElementInfoAccessor) info).setTitle(Utils.nameToTitle(tranTitle));

            String tranDesc = engine.transHudDescription(info.group, info);
            ((HudElementInfoAccessor) info).setDescription(Utils.nameToTitle(tranDesc));
        }

        // --- Category translation ---
        for (Category category : Modules.loopCategories()) {
            String tranName = engine.transCategoryName(category);
            ((CategoryAccessor) category).setName(Utils.nameToTitle(tranName));
        }

        // --- Command translation ---
        for (Command command : Commands.COMMANDS) {
            String tranTitle = engine.transCommandTitle(command);
            ((CommandAccessor) command).setTitle(Utils.nameToTitle(tranTitle));

            String tranDesc = engine.transCommandDescription(command);
            ((CommandAccessor) command).setDescription(tranDesc);
        }

        // --- HUD Preset translation ---
        for (HudElementInfo<?> info : Hud.get().infos.values()) {
            if (!info.hasPresets()) continue;

            for (HudElementInfo.Preset preset : info.presets) {
                String presetName = TransUtil.baseFormat(preset.title);
                String tranTitle = engine.transHudPresetTitle(info, presetName);
                ((PresetAccessor) preset).setTitle(Utils.nameToTitle(tranTitle));
            }
        }

        // --- Tab translation ---
        for (Tab tab : Tabs.get()) {
            String tranName = engine.transTabName(tab);
            ((TabAccessor) tab).setName(Utils.nameToTitle(tranName));
        }

        // --- System-level SettingGroup translation ---
        transSystemGroups(engine, "hud", Hud.get().settings);
        transSystemGroups(engine, "config", Config.get().settings);
        transSystemGroups(engine, "gui_theme", GuiThemes.get().settings);
    }

    private void transSystemGroups(AbstractTransEngine engine, String systemId, Settings settings) {
        for (SettingGroup group : settings.groups) {
            String originalName = NameCache.group(group);
            if (originalName == null || originalName.isEmpty()) continue;

            String tranName = engine.transSystemGroupName(systemId, group);
            ((SettingGroupAccessor) group).setName(Utils.nameToTitle(tranName));
        }
    }
}
