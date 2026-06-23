package com.nippaku_zanmu.trans_addon.mixin;

import com.nippaku_zanmu.trans_addon.util.TextReplacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * WCatppuccinLabel.set(String) bypasses WLabel.set() (doesn't call super),
 * so our WLabelMixin never sees dynamic text updates under the Catppuccin theme.
 * This mixin applies TextReplacement.replace() at the Catppuccin label level.
 */
@Mixin(targets = "me.pindour.catppuccin.gui.themes.catppuccin.widgets.WCatppuccinLabel", remap = false)
public class WCatppuccinLabelMixin {

    @ModifyVariable(method = "set(Ljava/lang/String;)V", at = @At("HEAD"), argsOnly = true)
    private String onSet(String text) {
        return TextReplacement.replace(text);
    }
}
