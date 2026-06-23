package com.nippaku_zanmu.trans_addon.mixin;

import com.nippaku_zanmu.trans_addon.util.TextReplacement;
import meteordevelopment.meteorclient.gui.themes.meteor.widgets.input.WMeteorDropdown;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Translate the selected value text rendered in the dropdown header
 * (the collapsed view showing the currently chosen option).
 */
@Mixin(value = WMeteorDropdown.class, remap = false)
public class WMeteorDropdownHeaderMixin {

    @Redirect(
        method = "onRender(Lmeteordevelopment/meteorclient/gui/renderer/GuiRenderer;DDD)V",
        at = @At(value = "INVOKE", target = "Ljava/lang/Object;toString()Ljava/lang/String;")
    )
    private String onHeaderValueToString(Object value) {
        return TextReplacement.replace(value.toString());
    }
}
