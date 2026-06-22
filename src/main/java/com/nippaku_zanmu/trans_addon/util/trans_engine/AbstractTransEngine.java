package com.nippaku_zanmu.trans_addon.util.trans_engine;

import com.nippaku_zanmu.trans_addon.util.TransUtil;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;

public abstract class AbstractTransEngine implements IKeyGenerate {

    public String transModuleName(Module module){
        return TransUtil.trans(getModuleNameKey(module));
    }
    public String transModuleDescription(Module module){
        return TransUtil.trans(getModuleDescriptionKey(module));
    }
    public String transSettingName(Module module, SettingGroup group, Setting s){
        return TransUtil.trans(getSettingNameKey(module,group,s));
    }
    public String transSettingDes(Module module, SettingGroup group, Setting s){
        return TransUtil.trans(getSettingDesKey(module,group,s));
    }
    public String transGroupName(Module module, SettingGroup group){
        return TransUtil.trans(getGroupNameKey(module,group));
    }

    // --- HUD Element translation ---

    public String transHudTitle(HudGroup group, HudElementInfo<?> info){
        return TransUtil.trans(getHudTitleKey(group, info));
    }
    public String transHudDescription(HudGroup group, HudElementInfo<?> info){
        return TransUtil.trans(getHudDescriptionKey(group, info));
    }

    // --- Category translation ---

    public String transCategoryName(Category category){
        return TransUtil.trans(getCategoryNameKey(category));
    }

    // --- Command translation ---

    public String transCommandTitle(Command command) {
        return TransUtil.trans(getCommandTitleKey(command));
    }

    public String transCommandDescription(Command command) {
        return TransUtil.trans(getCommandDescriptionKey(command));
    }

    // --- HUD Preset translation ---

    public String transHudPresetTitle(HudElementInfo<?> info, String presetName) {
        return TransUtil.trans(getHudPresetTitleKey(info, presetName));
    }

    // --- Tab translation ---

    public String transTabName(Tab tab) {
        return TransUtil.trans(getTabNameKey(tab));
    }

    // --- System SettingGroup translation ---

    public String transSystemGroupName(String systemId, SettingGroup group) {
        return TransUtil.trans(getSystemGroupNameKey(systemId, group));
    }
}
