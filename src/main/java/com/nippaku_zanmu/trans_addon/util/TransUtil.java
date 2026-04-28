package com.nippaku_zanmu.trans_addon.util;

import meteordevelopment.meteorclient.addons.AddonManager;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import java.util.Set;
import java.util.stream.Collectors;

public class TransUtil {
    private static final Minecraft mc = Minecraft.getInstance();

    private static String trans(String s) {
        return Component.translatable(s).getString();
    }
    public static String trans(String key,String alternative){
        String trans = trans(key);
        //调用mc函数翻译
        if (trans.equals(key)) {
            return alternative;
        }//如果没有翻译 即翻译后的还是原本的key 则返回原名称
        return trans;
    }

    public static Set<String> getAddonNames() {
        return AddonManager.ADDONS.stream().map(addon -> addon.name).map(TransUtil::baseFormat).collect(Collectors.toSet());
    }

    public static String getAddonName(Module m) {
        return TransUtil.baseFormat(m.addon == null ? "unknow_addon" : m.addon.name);
    }

    public static String baseFormat(String s) {
        //把某些Addon作者的不规范模块命名还原
        s = s.toLowerCase();
        s = s.replace(" ", "_");
        s = s.replace("-", "_");
        s = s.replace(".", "_");
        s = s.replace("\"", "_");
        return s;
    }
    public static String formatValue(String s){
        s = s.replace("\"", "\\\"");
        return s;
    }
}
