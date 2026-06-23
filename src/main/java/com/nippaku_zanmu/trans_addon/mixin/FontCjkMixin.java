package com.nippaku_zanmu.trans_addon.mixin;

import org.lwjgl.BufferUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Swap the font buffer in Meteor's Font class constructor with a CJK-capable
 * system font when running on a Chinese locale. This catches BOTH the Meteor
 * CustomTextRenderer path AND Catppuccin's RichTextRenderer path, since both
 * ultimately call new Font(buffer, height).
 */
@Mixin(targets = "meteordevelopment.meteorclient.renderer.text.Font", remap = false)
public class FontCjkMixin {

    private static ByteBuffer cjkBuffer;
    private static boolean cjkTried;

    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true)
    private static ByteBuffer swapBuffer(ByteBuffer original) {
        if (cjkBuffer != null) return cjkBuffer;
        if (cjkTried) return original;
        cjkTried = true;

        String lang = System.getProperty("user.language", "");
        if (!lang.equalsIgnoreCase("zh")) return original;

        String[] paths = {
            System.getenv("SystemRoot") + "\\Fonts\\msyh.ttc",
            System.getenv("SystemRoot") + "\\Fonts\\simsun.ttc",
            System.getenv("SystemRoot") + "\\Fonts\\msyh.ttf",
            "/System/Library/Fonts/PingFang.ttc",
            "/usr/share/fonts/opentype/noto/NotoSansCJK-Regular.ttc",
        };

        for (String path : paths) {
            File f = new File(path);
            if (!f.isFile()) continue;
            try (FileChannel ch = new FileInputStream(f).getChannel()) {
                cjkBuffer = BufferUtils.createByteBuffer((int) ch.size());
                ch.read(cjkBuffer);
                cjkBuffer.flip();
                System.out.println("[MeteorTranslation] Font CJK swap: " + path);
                return cjkBuffer;
            } catch (Exception ignored) {}
        }
        return original;
    }
}
