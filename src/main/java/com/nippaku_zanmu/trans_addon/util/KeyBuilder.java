package com.nippaku_zanmu.trans_addon.util;

import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;

public class KeyBuilder {
    StringBuilder sb = new StringBuilder();

    public KeyBuilder(Module m) {
        append("meteor")
            .append(TransUtil.getAddonName(m))
            .append(TransUtil.baseFormat(NameCache.category(m.category)))
            .append(TransUtil.baseFormat(m.name));
    }

    public KeyBuilder() {
        append("meteor");
    }

    public KeyBuilder reset() {
        sb = new StringBuilder();
        append("meteor");
        return this;
    }

    public KeyBuilder module(Module m) {
        append(TransUtil.getAddonName(m))
            .append(TransUtil.baseFormat(NameCache.category(m.category)))
            .append(TransUtil.baseFormat(m.name));
        return this;
    }

    public KeyBuilder hud(HudGroup group, HudElementInfo<?> info) {
        append("hud")
            .appendWithFormat(group.title())
            .appendWithFormat(info.name);
        return this;
    }

    public KeyBuilder category(Category category) {
        append("category")
            .appendWithFormat(NameCache.category(category));
        return this;
    }

    public KeyBuilder command(Command command) {
        append("command")
            .append(TransUtil.baseFormat(command.getName()));
        return this;
    }

    public KeyBuilder hudPreset(HudElementInfo<?> info, String presetName) {
        append("hud_preset")
            .appendWithFormat(info.name)
            .appendWithFormat(presetName);
        return this;
    }

    public KeyBuilder tab(String rawName) {
        append("tab")
            .appendWithFormat(rawName);
        return this;
    }

    public KeyBuilder systemSg(String systemId, String groupName) {
        append("system")
            .appendWithFormat(systemId)
            .append("sg")
            .appendWithFormat(groupName);
        return this;
    }

    public KeyBuilder append(String s) {
        sb.append(s).append(".");
        return this;
    }

    public KeyBuilder appendWithFormat(String s) {
        sb.append(TransUtil.baseFormat(s)).append(".");
        return this;
    }

    public String end(String s) {
        return sb.append(s).toString();
    }
    public String endWithFormat(String s) {
        return sb.append(TransUtil.baseFormat(s)).toString();
    }
}
