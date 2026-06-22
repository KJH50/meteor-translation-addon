package com.nippaku_zanmu.trans_addon.mixin;

import meteordevelopment.meteorclient.systems.modules.Category;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Category.class, remap = false)
public interface CategoryAccessor {
    @Mutable
    @Accessor("name")
    void setName(String name);
}
