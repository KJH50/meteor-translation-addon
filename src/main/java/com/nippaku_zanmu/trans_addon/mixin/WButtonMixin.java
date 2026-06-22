package com.nippaku_zanmu.trans_addon.mixin;

import com.nippaku_zanmu.trans_addon.util.TextReplacement;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = WButton.class, remap = false)
public class WButtonMixin {

    @ModifyVariable(method = "<init>(Ljava/lang/String;Lmeteordevelopment/meteorclient/gui/renderer/packer/GuiTexture;)V", at = @At("HEAD"), argsOnly = true)
    private static String onInit(String text) {
        return TextReplacement.replace(text);
    }

    @ModifyVariable(method = "set(Ljava/lang/String;)V", at = @At("HEAD"), argsOnly = true)
    private String onSet(String text) {
        return TextReplacement.replace(text);
    }
}
