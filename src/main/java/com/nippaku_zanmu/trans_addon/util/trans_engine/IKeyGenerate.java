package com.nippaku_zanmu.trans_addon.util.trans_engine;

import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;

public interface IKeyGenerate {
    String getModuleNameKey(Module m);
    String getModuleDescriptionKey(Module m);
    String getSettingNameKey(Module module, SettingGroup group, Setting<?> s);
    String getSettingDesKey(Module module, SettingGroup group, Setting<?> s);
    String getGroupNameKey(Module module, SettingGroup group);

    // --- HUD Element keys ---
    String getHudTitleKey(HudGroup group, HudElementInfo<?> info);
    String getHudDescriptionKey(HudGroup group, HudElementInfo<?> info);
    String getHudPresetTitleKey(HudElementInfo<?> info, String presetName);

    // --- Category keys ---
    String getCategoryNameKey(Category category);

    // --- Command keys ---
    String getCommandTitleKey(Command command);
    String getCommandDescriptionKey(Command command);

    // --- Tab keys ---
    String getTabNameKey(Tab tab);

    // --- System SettingGroup keys ---
    String getSystemGroupNameKey(String systemId, SettingGroup group);
}
