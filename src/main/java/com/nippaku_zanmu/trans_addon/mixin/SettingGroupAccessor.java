package com.nippaku_zanmu.trans_addon.mixin;

import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = SettingGroup.class, remap = false)
public interface SettingGroupAccessor {
    @Accessor("settings")
    public List<Setting<?>> getSettings();

    @Mutable
    @Accessor("name")
    void setName(String name);
}
