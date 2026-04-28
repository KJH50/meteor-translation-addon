//package com.nippaku_zanmu.trans_addon.mixin.HudRenderer;
//
//import com.google.common.cache.CacheBuilder;
//import com.google.common.cache.CacheLoader;
//import com.google.common.cache.LoadingCache;
//import com.nippaku_zanmu.trans_addon.font_fix.FontFix;
//import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
//import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
//import meteordevelopment.meteorclient.events.meteor.CustomFontChangedEvent;
//import meteordevelopment.meteorclient.renderer.*;
//import meteordevelopment.meteorclient.renderer.text.CustomTextRenderer;
//import meteordevelopment.meteorclient.renderer.text.VanillaTextRenderer;
//import meteordevelopment.meteorclient.systems.hud.Hud;
//import meteordevelopment.meteorclient.systems.hud.HudRenderer;
//import meteordevelopment.meteorclient.utils.Utils;
//import meteordevelopment.meteorclient.utils.render.RenderUtils;
//import meteordevelopment.meteorclient.utils.render.color.Color;
//import meteordevelopment.orbit.EventHandler;
//import net.minecraft.client.gui.DrawContext;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.Identifier;
//import org.lwjgl.BufferUtils;
//import org.spongepowered.asm.mixin.*;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import java.nio.ByteBuffer;
//import java.time.Duration;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//import static meteordevelopment.meteorclient.MeteorClient.mc;
//
//@Mixin(value = HudRenderer.class,remap = false)
//public class HudRendererMixin {
//    @Shadow
//    @Final
//    private static double SCALE_TO_HEIGHT = 1.0 / 18.0;
//    @Shadow
//    @Final
//    private final Hud hud = Hud.get();
//    @Shadow
//    @Final
//    private final List<Runnable> postTasks = new ArrayList<>();
//
//    @Unique
//    private final Int2ObjectMap<FontHolder> fontsInUse_f = new Int2ObjectOpenHashMap<>();
//
//    @Unique
//    private final LoadingCache<Integer, FontHolder> fontCache_f = CacheBuilder.newBuilder()
//        .maximumSize(4)
//        .expireAfterAccess(Duration.ofMinutes(10))
//        .removalListener(notification -> {
//            if (notification.wasEvicted())
//                ((FontHolder) notification.getValue()).destroy();
//        })
//        .build(CacheLoader.from(HudRendererMixin::loadFont_f));
//    @Shadow
//    public double delta;
//
//    @Shadow
//    public DrawContext drawContext;
//
//    /**
//     * @author Nippaku_Zanmu
//     * @reason  我只能用这种方法修复他 之前尝试过Mixin Font类 但是字体会乱码
//     */
//    @Overwrite
//    public void begin(DrawContext drawContext) {
//        Renderer2D.COLOR.begin();
//
//        this.drawContext = drawContext;
//        this.delta = Utils.frameTime;
//
//        if (!hud.hasCustomFont()) {
//            VanillaTextRenderer.INSTANCE.scaleIndividually = true;
//            VanillaTextRenderer.INSTANCE.begin();
//        }
//    }
//
//    /**
//     * @author Nippaku_Zanmu
//     * @reason  我只能用这种方法修复他 之前尝试过Mixin Font类 但是字体会乱码
//     */
//    @Overwrite
//    public void end() {
//        Renderer2D.COLOR.render();
//
//        if (hud.hasCustomFont()) {
//            // Render fonts that were visited this frame and move to cache which weren't visited
//            for (Iterator<FontHolder> it = fontsInUse_f.values().iterator(); it.hasNext(); ) {
//                FontHolder fontHolder = it.next();
//
//                if (fontHolder.visited) {
//                    MeshRenderer.begin()
//                        .attachments(mc.getFramebuffer())
//                        .pipeline(MeteorRenderPipelines.UI_TEXT)
//                        .mesh(fontHolder.getMesh())
//                        .setupCallback(pass -> pass.bindSampler("u_Texture", fontHolder.font.texture.getGlTexture()))
//                        .end();
//                }
//                else {
//                    it.remove();
//                    fontCache_f.put(fontHolder.font.getHeight(), fontHolder);
//                }
//
//                fontHolder.visited = false;
//            }
//        }
//        else {
//            VanillaTextRenderer.INSTANCE.end();
//            VanillaTextRenderer.INSTANCE.scaleIndividually = false;
//        }
//
//        for (Runnable task : postTasks) task.run();
//        postTasks.clear();
//
//        this.drawContext = null;
//    }
//
//    /**
//     * @author Nippaku_Zanmu
//     * @reason  我只能用这种方法修复他 之前尝试过Mixin Font类 但是字体会乱码
//     */
//    @Overwrite
//    public void line(double x1, double y1, double x2, double y2, Color color) {
//        Renderer2D.COLOR.line(x1, y1, x2, y2, color);
//    }
//
//    /**
//     * @author Nippaku_Zanmu
//     * @reason  我只能用这种方法修复他 之前尝试过Mixin Font类 但是字体会乱码
//     */
//    @Overwrite
//    public void quad(double x, double y, double width, double height, Color color) {
//        Renderer2D.COLOR.quad(x, y, width, height, color);
//    }
//
//    /**
//     * @author Nippaku_Zanmu
//     * @reason  我只能用这种方法修复他 之前尝试过Mixin Font类 但是字体会乱码
//     */
//    @Overwrite
//    public void quad(double x, double y, double width, double height, Color cTopLeft, Color cTopRight, Color cBottomRight, Color cBottomLeft) {
//        Renderer2D.COLOR.quad(x, y, width, height, cTopLeft, cTopRight, cBottomRight, cBottomLeft);
//    }
//
//    /**
//     * @author Nippaku_Zanmu
//     * @reason  我只能用这种方法修复他 之前尝试过Mixin Font类 但是字体会乱码
//     */
//    @Overwrite
//    public void triangle(double x1, double y1, double x2, double y2, double x3, double y3, Color color) {
//        Renderer2D.COLOR.triangle(x1, y1, x2, y2, x3, y3, color);
//    }
//
//    /**
//     * @author Nippaku_Zanmu
//     * @reason  我只能用这种方法修复他 之前尝试过Mixin Font类 但是字体会乱码
//     */
//    @Overwrite
//    public void texture(Identifier id, double x, double y, double width, double height, Color color) {
//        Renderer2D.TEXTURE.begin();
//        Renderer2D.TEXTURE.texQuad(x, y, width, height, color);
//        Renderer2D.TEXTURE.render(mc.getTextureManager().getTexture(id).getGlTexture());
//    }
//
//    /**
//     * @author Nippaku_Zanmu
//     * @reason  我只能用这种方法修复他 之前尝试过Mixin Font类 但是字体会乱码
//     */
//    @Overwrite
//    public double text(String text, double x, double y, Color color, boolean shadow, double scale) {
//        if (scale == -1) scale = hud.getTextScale();
//
//        if (!hud.hasCustomFont()) {
//            VanillaTextRenderer.INSTANCE.scale = scale * 2;
//            return VanillaTextRenderer.INSTANCE.render(text, x, y, color, shadow);
//        }
//
//        FontHolder fontHolder = getFontHolder_f(scale, true);
//
//        FontFix font = fontHolder.font;
//        MeshBuilder mesh = fontHolder.getMesh();
//
//        double width;
//
//        if (shadow) {
//            int preShadowA = CustomTextRenderer.SHADOW_COLOR.a;
//            CustomTextRenderer.SHADOW_COLOR.a = (int) (color.a / 255.0 * preShadowA);
//
//            width = font.render(mesh, text, x + 1, y + 1, CustomTextRenderer.SHADOW_COLOR, scale);
//            font.render(mesh, text, x, y, color, scale);
//
//            CustomTextRenderer.SHADOW_COLOR.a = preShadowA;
//        }
//        else {
//            width = font.render(mesh, text, x, y, color, scale);
//        }
//
//        return width;
//    }
//    /**
//     * @author Nippaku_Zanmu
//     * @reason  我只能用这种方法修复他 之前尝试过Mixin Font类 但是字体会乱码
//     */
//    @Overwrite
//    public double text(String text, double x, double y, Color color, boolean shadow) {
//        return text(text, x, y, color, shadow, -1);
//    }
//    /**
//     * @author Nippaku_Zanmu
//     * @reason  我只能用这种方法修复他 之前尝试过Mixin Font类 但是字体会乱码
//     */
//    @Overwrite
//    public double textWidth(String text, boolean shadow, double scale) {
//        if (text.isEmpty()) return 0;
//
//        if (hud.hasCustomFont()) {
//            double width = getFont_f(scale).getWidth(text, text.length());
//            return (width + (shadow ? 1 : 0)) * (scale == -1 ? hud.getTextScale() : scale) + (shadow ? 1 : 0);
//        }
//
//        VanillaTextRenderer.INSTANCE.scale = (scale == -1 ? hud.getTextScale() : scale) * 2;
//        return VanillaTextRenderer.INSTANCE.getWidth(text, shadow);
//    }
//    /**
//     * @author Nippaku_Zanmu
//     * @reason  我只能用这种方法修复他 之前尝试过Mixin Font类 但是字体会乱码
//     */
//    @Overwrite
//    public double textWidth(String text, boolean shadow) {
//        return textWidth(text, shadow, -1);
//    }
//    /**
//     * @author Nippaku_Zanmu
//     * @reason  我只能用这种方法修复他 之前尝试过Mixin Font类 但是字体会乱码
//     */
//    @Overwrite
//    public double textWidth(String text, double scale) {
//        return textWidth(text, false, scale);
//    }
//    /**
//     * @author Nippaku_Zanmu
//     * @reason  我只能用这种方法修复他 之前尝试过Mixin Font类 但是字体会乱码
//     */
//    @Overwrite
//    public double textWidth(String text) {
//        return textWidth(text, false, -1);
//    }
//
//    /**
//     * @author Nippaku_Zanmu
//     * @reason  我只能用这种方法修复他 之前尝试过Mixin Font类 但是字体会乱码
//     */
//    @Overwrite
//    public double textHeight(boolean shadow, double scale) {
//        if (hud.hasCustomFont()) {
//            double height = getFont_f(scale).getHeight() + 1;
//            return (height + (shadow ? 1 : 0)) * (scale == -1 ? hud.getTextScale() : scale);
//        }
//
//        VanillaTextRenderer.INSTANCE.scale = (scale == -1 ? hud.getTextScale() : scale) * 2;
//        return VanillaTextRenderer.INSTANCE.getHeight(shadow);
//    }
//    /**
//     * @author Nippaku_Zanmu
//     * @reason  我只能用这种方法修复他 之前尝试过Mixin Font类 但是字体会乱码
//     */
//    @Overwrite
//    public double textHeight(boolean shadow) {
//        return textHeight(shadow, -1);
//    }
//    /**
//     * @author Nippaku_Zanmu
//     * @reason  我只能用这种方法修复他 之前尝试过Mixin Font类 但是字体会乱码
//     */
//    @Overwrite
//    public double textHeight() {
//        return textHeight(false, -1);
//    }
//
//    /**
//     * @author Nippaku_Zanmu
//     * @reason  我只能用这种方法修复他 之前尝试过Mixin Font类 但是字体会乱码
//     */
//    @Overwrite
//    public void post(Runnable task) {
//        postTasks.add(task);
//    }
//
//    /**
//     * @author Nippaku_Zanmu
//     * @reason  我只能用这种方法修复他 之前尝试过Mixin Font类 但是字体会乱码
//     */
//    @Overwrite
//    public void item(ItemStack itemStack, int x, int y, float scale, boolean overlay, String countOverlay) {
//        RenderUtils.drawItem(drawContext, itemStack, x, y, scale, overlay, countOverlay);
//    }
//
//    /**
//     * @author Nippaku_Zanmu
//     * @reason  我只能用这种方法修复他 之前尝试过Mixin Font类 但是字体会乱码
//     */
//    @Overwrite
//    public void item(ItemStack itemStack, int x, int y, float scale, boolean overlay) {
//        RenderUtils.drawItem(drawContext, itemStack, x, y, scale, overlay);
//    }
//
//    @Unique
//    private FontHolder getFontHolder_f(double scale, boolean render) {
//        // Calculate font height
//        if (scale == -1) scale = hud.getTextScale();
//        int height = (int) Math.round(scale / SCALE_TO_HEIGHT);
//
//        // Check fonts in use
//        FontHolder fontHolder = fontsInUse_f.get(height);
//        if (fontHolder != null) {
//            if (render) fontHolder.visited = true;
//            return fontHolder;
//        }
//
//        // Create font if not in cache otherwise remove from cache and add to fonts in use
//        if (render) {
//            fontHolder = fontCache_f.getIfPresent(height);
//            if (fontHolder == null) fontHolder = loadFont_f(height);
//            else fontCache_f.invalidate(height);
//
//            fontsInUse_f.put(height, fontHolder);
//            fontHolder.visited = true;
//
//            return fontHolder;
//        }
//
//        // Otherwise get from cache
//        return fontCache_f.getUnchecked(height);
//    }
//
//    @Unique
//    private FontFix getFont_f(double scale) {
//        return getFontHolder_f(scale, false).font;
//    }
//    /**
//     * @author Nippaku_Zanmu
//     * @reason  我只能用这种方法修复他 之前尝试过Mixin Font类 但是字体会乱码
//     */
////    @Overwrite
////    @EventHandler
////    private void onCustomFontChanged(CustomFontChangedEvent event) {
////        // Need to destroy both fonts in use and in cache because they were not evicted from the cache automatically
////        for (FontHolder fontHolder : fontsInUse_f.values()) fontHolder.destroy();
////        for (FontHolder fontHolder : fontCache_f.asMap().values()) fontHolder.destroy();
////
////        // Clear collections
////        fontsInUse_f.clear();
////        fontCache_f.invalidateAll();
////    }
//    @Inject(method = "onCustomFontChanged",at = @At("HEAD"))
//    private void onCustomFontChanged(CustomFontChangedEvent event, CallbackInfo ci){
//        // Need to destroy both fonts in use and in cache because they were not evicted from the cache automatically
//        for (FontHolder fontHolder : fontsInUse_f.values()) fontHolder.destroy();
//        for (FontHolder fontHolder : fontCache_f.asMap().values()) fontHolder.destroy();
//
//        // Clear collections
//        fontsInUse_f.clear();
//        fontCache_f.invalidateAll();
//    }
//
//    @Unique
//    private static FontHolder loadFont_f(int height) {
//        byte[] data = Utils.readBytes(Fonts.RENDERER.fontFace.toStream());
//        ByteBuffer buffer = BufferUtils.createByteBuffer(data.length).put(data).flip();
//
//        return new FontHolder(new FontFix(buffer, height));
//    }
//
//
//}
