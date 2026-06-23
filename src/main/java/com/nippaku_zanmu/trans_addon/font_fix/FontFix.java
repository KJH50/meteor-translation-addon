package com.nippaku_zanmu.trans_addon.font_fix;

import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.TextureFormat;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import meteordevelopment.meteorclient.renderer.MeshBuilder;
import meteordevelopment.meteorclient.renderer.Texture;
import meteordevelopment.meteorclient.utils.render.color.Color;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.*;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class FontFix {
    public Texture texture;
    private final int height;
    private final float scale;
    private final float ascent;
    private final Int2ObjectOpenHashMap<CharData> charMap = new Int2ObjectOpenHashMap<>();
    private final static int SIZE = 2048;

    private final ByteBuffer buffer;
    private final STBTTFontinfo fontInfo;
    private final ByteBuffer bitmap;
    private final STBTTPackContext packContext;

    private long loadTimer = 0;
    private int loadCount = 0;
    private static final int LOAD_SPEED_LIMIT = 7;

    public FontFix(ByteBuffer buffer, int height) {
        this.buffer = buffer;
        this.height = height;

        fontInfo = STBTTFontinfo.create();
        STBTruetype.stbtt_InitFont(fontInfo, buffer);

        bitmap = BufferUtils.createByteBuffer(SIZE * SIZE);
        packContext = STBTTPackContext.create();
        STBTruetype.stbtt_PackBegin(packContext, bitmap, SIZE, SIZE, 0, 1);

        texture = new Texture(SIZE, SIZE, TextureFormat.RED8, FilterMode.LINEAR, FilterMode.LINEAR);
        texture.upload(bitmap);
        scale = STBTruetype.stbtt_ScaleForPixelHeight(fontInfo, height);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer a = stack.mallocInt(1);
            STBTruetype.stbtt_GetFontVMetrics(fontInfo, a, null, null);
            this.ascent = a.get(0);
        }

        preloadAsciiCharacters();
    }

    private void preloadAsciiCharacters() {
        STBTTPackedchar.Buffer cdata = STBTTPackedchar.create(128);
        STBTTPackRange.Buffer pr = STBTTPackRange.create(1);
        pr.put(STBTTPackRange.create().set(height, 32, null, 128, cdata, (byte) 2, (byte) 2));
        pr.flip();

        STBTruetype.stbtt_PackFontRanges(packContext, buffer, 0, pr);

        for (int i = 0; i < cdata.capacity(); i++) putCharData(i + 32, cdata.get(i));
        createTexture();
    }

    private void loadCharacter(List<Integer> codePoints) {
        if (System.currentTimeMillis() - loadTimer > 100) { loadTimer = System.currentTimeMillis(); loadCount = 0; }
        if (loadCount >= LOAD_SPEED_LIMIT) return;
        for (Integer cp : codePoints) loadCharacter(cp);
        createTexture();
        loadCount++;
    }

    private void loadCharacter(int codePoint) {
        if (charMap.containsKey(codePoint)) return;

        STBTTPackedchar.Buffer cdata = STBTTPackedchar.create(1);
        STBTTPackRange.Buffer pr = STBTTPackRange.create(1);
        pr.put(STBTTPackRange.create().set(height, codePoint, null, 1, cdata, (byte) 2, (byte) 2));
        pr.flip();

        STBTruetype.stbtt_PackFontRanges(packContext, buffer, 0, pr);
        putCharData(codePoint, cdata.get(0));
    }

    private void putCharData(int codePoint, STBTTPackedchar pc) {
        float ipw = 1f / SIZE, iph = 1f / SIZE;
        charMap.put(codePoint, new CharData(
            pc.xoff(), pc.yoff(), pc.xoff2(), pc.yoff2(),
            pc.x0() * ipw, pc.y0() * iph, pc.x1() * ipw, pc.y1() * iph,
            pc.xadvance()
        ));
    }

    private void createTexture() {
        texture = new Texture(SIZE, SIZE, TextureFormat.RED8, FilterMode.LINEAR, FilterMode.LINEAR);
        texture.upload(bitmap);
    }

    public double getWidth(String string, int length) {
        double width = 0;
        if (tryLoadString(string)) return width;
        for (int i = 0; i < length; i++) {
            CharData c = charMap.get((int) string.charAt(i));
            if (c != null) width += c.xAdvance;
        }
        return width;
    }

    public int getHeight() { return height; }

    private boolean tryLoadString(String s) {
        boolean loading = false;
        List<Integer> pts = null;
        for (int i = 0; i < s.length(); i++) {
            int cp = s.charAt(i);
            if (!charMap.containsKey(cp)) {
                if (pts == null) pts = new ArrayList<>();
                pts.add(cp);
                loading = true;
            }
        }
        if (pts != null) loadCharacter(pts);
        return loading;
    }

    public double render(MeshBuilder mesh, String string, double x, double y, Color color, double s) {
        if (tryLoadString(string)) return x;
        y += ascent * this.scale * s;

        int len = string.length();
        mesh.ensureCapacity(len * 4, len * 6);
        for (int i = 0; i < len; i++) {
            CharData c = charMap.get((int) string.charAt(i));
            if (c == null) continue;
            mesh.quad(
                mesh.vec2(x + c.x0 * s, y + c.y0 * s).vec2(c.u0, c.v0).color(color).next(),
                mesh.vec2(x + c.x0 * s, y + c.y1 * s).vec2(c.u0, c.v1).color(color).next(),
                mesh.vec2(x + c.x1 * s, y + c.y1 * s).vec2(c.u1, c.v1).color(color).next(),
                mesh.vec2(x + c.x1 * s, y + c.y0 * s).vec2(c.u1, c.v0).color(color).next()
            );
            x += c.xAdvance * s;
        }
        return x;
    }

    private record CharData(float x0, float y0, float x1, float y1,
                            float u0, float v0, float u1, float v1,
                            float xAdvance) {}
}
