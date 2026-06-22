package com.nippaku_zanmu.trans_addon.mixin;

import com.nippaku_zanmu.trans_addon.util.TextReplacement;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = ChatUtils.class, remap = false)
public class ChatUtilsMixin {

    @ModifyArg(
        method = "sendMsg(ILjava/lang/String;Lnet/minecraft/ChatFormatting;Lnet/minecraft/ChatFormatting;Ljava/lang/String;[Ljava/lang/Object;)V",
        at = @At(value = "INVOKE", target = "Ljava/lang/String;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"),
        index = 0
    )
    private static String onSendMsgFormatted(String messageContent) {
        return TextReplacement.replace(messageContent);
    }

    @ModifyArg(
        method = "sendMsg(ILjava/lang/String;Lnet/minecraft/ChatFormatting;Ljava/lang/String;Lnet/minecraft/ChatFormatting;)V",
        at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/utils/player/ChatUtils;formatMsg(Ljava/lang/String;Lnet/minecraft/ChatFormatting;)Lnet/minecraft/network/chat/MutableComponent;"),
        index = 0
    )
    private static String onSendMsgUnformatted(String messageContent) {
        return TextReplacement.replace(messageContent);
    }
}
