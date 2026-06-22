package com.nippaku_zanmu.trans_addon.mixin;

import meteordevelopment.meteorclient.gui.tabs.Tab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Tab.class, remap = false)
public interface TabAccessor {
    @Mutable
    @Accessor("name")
    void setName(String name);
}
