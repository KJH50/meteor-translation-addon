package com.nippaku_zanmu.trans_addon.mixin;

import com.nippaku_zanmu.trans_addon.font_fix.FontFix;
import meteordevelopment.meteorclient.renderer.*;
import meteordevelopment.meteorclient.renderer.text.CustomTextRenderer;
import meteordevelopment.meteorclient.renderer.text.FontFace;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.ByteBuffer;

import static meteordevelopment.meteorclient.renderer.text.CustomTextRenderer.SHADOW_COLOR;

@Mixin(value = CustomTextRenderer.class)
public abstract class CustomTextRendererMixin implements TextRenderer {

    @Shadow
    @Final
    private MeshBuilder mesh = new MeshBuilder(MeteorRenderPipelines.UI_TEXT);


    @Unique
    private FontFix[] fonts_fix;
    @Unique
    private FontFix font_fix;

    @Shadow
    private boolean building;
    @Shadow
    private boolean scaleOnly;
    @Shadow
    private double fontScale = 1;
    @Shadow
    private double scale = 1;


    @Inject(method = "<init>",at = @At("RETURN"))
    public void onInit(FontFace fontFace, CallbackInfo ci) throws IOException {
        // FontCjkMixin already handles CJK buffer swap at the Font level
        ByteBuffer buffer = fontFace.readToDirectByteBuffer();
        this.fonts_fix = new FontFix[5];

        for(int i = 0; i < this.fonts_fix.length; ++i) {
            this.fonts_fix[i] = new FontFix(buffer, (int)Math.round(27.0 * ((double)i * 0.5 + 1.0)));
        }
    }


    /**
     * @author Nippaku_Zanmu
     * @reason  我只能用这种方法修复他 之前尝试过Mixin Font类 但是字体会乱码
     */
    @Overwrite
    public void begin(double scale, boolean scaleOnly, boolean big) {
        if (this.building) {
            throw new RuntimeException("CustomTextRenderer.begin() called twice");
        } else {
            if (!scaleOnly) {
                this.mesh.begin();
            }

            if (big) {
                this.font_fix = this.fonts_fix[this.fonts_fix.length - 1];
            } else {
                double scaleA = Math.floor(scale * 10.0) / 10.0;
                byte scaleI;
                if (scaleA >= 3.0) {
                    scaleI = 5;
                } else if (scaleA >= 2.5) {
                    scaleI = 4;
                } else if (scaleA >= 2.0) {
                    scaleI = 3;
                } else if (scaleA >= 1.5) {
                    scaleI = 2;
                } else {
                    scaleI = 1;
                }

                this.font_fix = this.fonts_fix[scaleI - 1];
            }

            this.building = true;
            this.scaleOnly = scaleOnly;
            this.fontScale = (double)this.font_fix.getHeight() / 27.0;
            this.scale = 1.0 + (scale - this.fontScale) / this.fontScale;
        }
    }
    /**
     * @author Nippaku_Zanmu
     * @reason  我只能用这种方法修复他 之前尝试过Mixin Font类 但是字体会乱码
     */
    @Overwrite
    public double getWidth(String text, int length, boolean shadow) {
        if (text.isEmpty()) {
            return 0.0;
        } else {
            FontFix font = this.building ? this.font_fix : this.fonts_fix[0];
            return (font.getWidth(text, length) + (double)(shadow ? 1 : 0)) * this.scale / 1.5;
        }
    }
    /**
     * @author Nippaku_Zanmu
     * @reason  我只能用这种方法修复他 之前尝试过Mixin Font类 但是字体会乱码
     */
    public double getHeight(boolean shadow) {
        FontFix font = this.building ? this.font_fix : this.fonts_fix[0];
        return (double)(font.getHeight() + 1 + (shadow ? 1 : 0)) * this.scale / 1.5;
    }
    /**
     * @author Nippaku_Zanmu
     * @reason  我只能用这种方法修复他 之前尝试过Mixin Font类 但是字体会乱码
     */
    @Overwrite
    public double render(String text, double x, double y, Color color, boolean shadow) {
        boolean wasBuilding = this.building;
        if (!wasBuilding) {
            this.begin();
        }

        double width;
        if (shadow) {
            int preShadowA = SHADOW_COLOR.a;
            SHADOW_COLOR.a = (int)((double)color.a / 255.0 * (double)preShadowA);
            width = this.font_fix.render(this.mesh, text, x + this.fontScale * this.scale / 1.5, y + this.fontScale * this.scale / 1.5, SHADOW_COLOR, this.scale / 1.5);
            this.font_fix.render(this.mesh, text, x, y, color, this.scale / 1.5);
            SHADOW_COLOR.a = preShadowA;
        } else {
            width = this.font_fix.render(this.mesh, text, x, y, color, this.scale / 1.5);
        }

        if (!wasBuilding) {
            this.end();
        }

        return width;
    }

    /**
     * @author Nippaku_Zanmu
     * @reason  我只能用这种方法修复他 之前尝试过Mixin Font类 但是字体会乱码
     */
    @Overwrite
    public void end() {
        if (!this.building) {
            throw new RuntimeException("CustomTextRenderer.end() called without calling begin()");
        } else {
            if (!this.scaleOnly) {
                this.mesh.end();
                MeshRenderer.begin().attachments(Minecraft.getInstance().getMainRenderTarget()).pipeline(MeteorRenderPipelines.UI_TEXT).mesh(this.mesh).sampler("u_Texture", this.font_fix.texture.getTextureView(), this.font_fix.texture.getSampler()).end();
            }

            this.building = false;
            this.scale = 1.0;
        }
    }

    public void destroy() {}
}
