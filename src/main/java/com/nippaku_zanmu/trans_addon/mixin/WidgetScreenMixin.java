package com.nippaku_zanmu.trans_addon.mixin;

import com.nippaku_zanmu.trans_addon.util.TextReplacement;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = WidgetScreen.class, remap = false)
public class WidgetScreenMixin {

    @ModifyVariable(method = "<init>(Lmeteordevelopment/meteorclient/gui/GuiTheme;Ljava/lang/String;)V", at = @At("HEAD"), argsOnly = true)
    private static String onInit(String title) {
        return TextReplacement.replace(title);
    }
}
