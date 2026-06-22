package com.nippaku_zanmu.trans_addon.mixin;

import com.nippaku_zanmu.trans_addon.util.TextReplacement;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = WWidget.class, remap = false)
public class WWidgetMixin {

    @ModifyArg(
        method = "render(Lmeteordevelopment/meteorclient/gui/renderer/GuiRenderer;DDD)Z",
        at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/gui/renderer/GuiRenderer;tooltip(Ljava/lang/String;)V"),
        index = 0
    )
    private String translateTooltip(String tooltip) {
        return TextReplacement.replace(tooltip);
    }
}
