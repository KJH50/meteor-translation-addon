package com.nippaku_zanmu.trans_addon.mixin;

import meteordevelopment.meteorclient.commands.Command;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Command.class, remap = false)
public interface CommandAccessor {
    @Mutable
    @Accessor("title")
    void setTitle(String title);

    @Mutable
    @Accessor("description")
    void setDescription(String description);
}
