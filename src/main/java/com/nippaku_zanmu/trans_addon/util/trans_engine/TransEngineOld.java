package com.nippaku_zanmu.trans_addon.util.trans_engine;

import com.nippaku_zanmu.trans_addon.util.KeyBuilder;
import com.nippaku_zanmu.trans_addon.util.NameCache;
import com.nippaku_zanmu.trans_addon.util.TransUtil;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;

public  class TransEngineOld extends AbstractTransEngine {
    KeyBuilder builder = new KeyBuilder();
    @Override
    public String getModuleNameKey(Module module) {
        String moduleName = module.name;
        builder.reset();
        String key = builder.append(TransUtil.getAddonName(module))
            .append(TransUtil.baseFormat(NameCache.category(module.category)))
            .end(TransUtil.baseFormat(moduleName));
        return key;
    }

    @Override
    public String getModuleDescriptionKey(Module module) {
        builder.reset();
        String key = builder.module(module).end("description");
        return  key;
    }

    @Override
    public String getSettingNameKey(Module module, SettingGroup group, Setting s) {
        String settingName = s.name;
        builder.reset();
        String key = builder.module(module)
            .append("setting")
            .append(NameCache.group(group))
            .end(settingName)
            ;
        return key;
    }

    @Override
    public String getSettingDesKey(Module module, SettingGroup group, Setting s) {
        String settingName = s.name;
        builder.reset();
        String key = builder.module(module)
            .append("setting")
            .append(NameCache.group(group))
            .append(settingName)
            .end("description")
            ;
        return  key;
    }

    @Override
    public String getGroupNameKey(Module module, SettingGroup group) {
        builder.reset();
        return builder.append("group")
            .appendWithFormat(NameCache.group(group))
            .end("name");
    }

    // --- HUD Element keys ---

    @Override
    public String getHudTitleKey(HudGroup group, HudElementInfo<?> info) {
        builder.reset();
        return builder.hud(group, info).end("name");
    }

    @Override
    public String getHudDescriptionKey(HudGroup group, HudElementInfo<?> info) {
        builder.reset();
        return builder.hud(group, info).end("description");
    }

    // --- Category keys ---

    @Override
    public String getCategoryNameKey(Category category) {
        builder.reset();
        return builder.category(category).end("name");
    }

    // --- Command keys ---

    @Override
    public String getCommandTitleKey(Command command) {
        builder.reset();
        return builder.command(command).end("title");
    }

    @Override
    public String getCommandDescriptionKey(Command command) {
        builder.reset();
        return builder.command(command).end("description");
    }

    // --- HUD Preset keys ---

    @Override
    public String getHudPresetTitleKey(HudElementInfo<?> info, String presetName) {
        builder.reset();
        return builder.hudPreset(info, presetName).end("name");
    }

    // --- Tab keys ---

    @Override
    public String getTabNameKey(Tab tab) {
        builder.reset();
        return builder.tab(tab.name).end("name");
    }

    // --- System SettingGroup keys ---

    @Override
    public String getSystemGroupNameKey(String systemId, SettingGroup group) {
        builder.reset();
        return builder.systemSg(systemId, NameCache.group(group)).end("name");
    }
}
