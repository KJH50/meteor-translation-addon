# Meteor Translation Addon — P2 翻译实施计划

> 基于 meteor-client 项目扫描结果，规划下一批翻译内容。
> 当前 P1 已完成：Module、Setting、SettingGroup、Category、HUD Element、Command 翻译，共 3113 条。

---

## 1. 目标

扩展翻译覆盖范围到以下用户可见表面：
- HUD 元素预设标题（P2-1）
- 系统级 SettingGroup 名称（P2-2）
- 顶部 Tab 名称（P2-3）

可选扩展（根据决策）：
- WindowScreen 窗口标题（P2-4）
- 界面按钮/标签文字（P2-5）
- 聊天消息（P2-6）
- HudGroup 标题（P2-7，高风险低收益，建议延后）

---

## 2. 范围决策

### 2.1 本次必做（P2-1 ~ P2-3）

| 编号 | 内容 | 数量 | 难度 | 原因 |
|------|------|------|------|------|
| P2-1 | HUD 元素预设标题 | 22 | 低 | 用户打开 HUD 编辑器添加 text 元素时直接可见；实现模式与 P1 的 `HudElementInfoAccessor` 完全一致 |
| P2-2 | 系统级 SettingGroup 名称 | ~21 | 低 | P1 只扫描了模块 SettingGroup，系统级（Hud/Config/MeteorGuiTheme）的 group 名称仍显示英文 |
| P2-3 | 顶部 Tab 名称 | 8 | 低 | 打开 GUI 后顶部栏直接可见；`Tab.name` 是 `public final String`，加 `TabAccessor` 即可 |

### 2.2 延后或可选

| 编号 | 内容 | 风险 | 原因 |
|------|------|------|------|
| P2-4 | 窗口标题 | 中 | 需要 `WidgetScreen` 渲染期 mixin，技术可行但需单独设计 |
| P2-5 | 界面按钮/标签 | 高 | 字符串分散在 30+ 文件，需要遍历控件树或大量 call-site mixin |
| P2-6 | 聊天消息 | 中 | 需要 `ChatUtils.sendMsg` 拦截，所有 `%s` 格式化参数需保留 |
| P2-7 | HudGroup "Meteor" 标题 | 高 | 该字已嵌入键路径 `meteor.hud.meteor.*`，翻译会破坏现有 30 条 HUD 键，需要 HudGroupNameCache |

---

## 3. 翻译键规范

沿用 P1 风格，统一前缀 `meteor.`：

```
# HUD 预设
meteor.hud_preset.{element_info_name}.{preset_constant}.name

# 系统 SettingGroup
meteor.system.{system_lowercase}.sg_{group_lowercase}.name

# Tab
meteor.tab.{tab_lowercase}.name
```

示例：
- `meteor.hud_preset.meteor_text.fps.name` → "FPS"
- `meteor.hud_preset.meteor_text.watermark.name` → "Watermark"
- `meteor.system.hud.sg_editor.name` → "编辑器"
- `meteor.system.config.sg_visual.name` → "视觉"
- `meteor.system.gui_theme.sg_colors.name` → "颜色"
- `meteor.tab.modules.name` → "模块"
- `meteor.tab.hud.name` → "HUD"

---

## 4. 技术实现

### 4.1 P2-1：HUD 元素预设标题

**目标类**：`meteordevelopment.meteorclient.systems.hud.HudElementInfo.Preset`

```java
public class Preset {
    public final HudElementInfo<?> info;
    public final String title;          // ← 翻译此字段
    public final Consumer<T> callback;
    ...
}
```

**Mixin**：
```java
@Mixin(HudElementInfo.Preset.class)
public interface PresetAccessor {
    @Mutable
    @Accessor("title")
    void setTitle(String title);
}
```

**翻译入口**：
- 遍历 `Hud.get().elements`，对每个 `HudElementInfo` 取 `info.presets`
- 对非空 preset 列表调用 `PresetAccessor.setTitle()`
- 仅 `MeteorTextHud` 有预设（22 个），但代码应通用

**新增引擎方法**：
```java
// AbstractTransEngine
public void transPresetTitle(HudElementInfo<?> info, HudElementInfo.Preset preset);

// KeyBuilder
public KeyBuilder hudPreset(HudElementInfo<?> info, String presetFieldName);
```

---

### 4.2 P2-2：系统级 SettingGroup 名称

**目标对象**：
- `Hud.get().settings.groups` — 包含默认、"Editor"、"Bind"
- `Config.get().settings.groups` — 包含默认、"Visual"、"Modules"、"Chat"、"Misc"
- `GuiThemes.get().settings.groups` — 包含默认、"Colors"、"Text"、"Background"、"Outline"、"Separator"、"Scrollbar"、"Slider"、"Starscript"

**实现**：
- 在 `Translation.tran()` 中新增一个循环，遍历上述三个系统的 `settings.groups`
- 复用现有的 `transSgName(SettingGroup)` 和 `KeyBuilder.sg()`
- 注意避免重复翻译已被模块循环处理过的 group（系统级 group 不会与模块 group 重叠）

**新增键示例**：
- `meteor.system.hud.sg_editor.name`
- `meteor.system.config.sg_visual.name`
- `meteor.system.gui_theme.sg_colors.name`

---

### 4.3 P2-3：顶部 Tab 名称

**目标类**：`meteordevelopment.meteorclient.gui.tabs.Tab`

```java
public abstract class Tab {
    public final String name;     // ← 翻译此字段
    ...
}
```

**Mixin**：
```java
@Mixin(Tab.class)
public interface TabAccessor {
    @Mutable
    @Accessor("name")
    void setName(String name);
}
```

**翻译入口**：
- 遍历 `Tabs.get()`（提供迭代器）
- 对非 `PathManagerTab` 的 7 个静态 tab 调用 `setName()`
- `PathManagerTab` 的名称是动态的 `PathManagers.get().getName()`，需单独判断是否需要翻译

**新增引擎方法**：
```java
public void transTabName(Tab tab);

// KeyBuilder
public KeyBuilder tab(String rawName);
```

**注意**：`WTopBar.WTopBarButton.onRender()` 每次渲染都读 `tab.name`，所以 reflection 改字段后顶部栏会立即更新。

---

## 5. 需要修改的文件

### 5.1 新增文件

| 文件 | 说明 |
|------|------|
| `src/main/java/.../mixin/PresetAccessor.java` | HUD 预设 title accessor |
| `src/main/java/.../mixin/TabAccessor.java` | Tab name accessor |

### 5.2 修改文件

| 文件 | 修改内容 |
|------|----------|
| `src/main/resources/addon-translation.mixins.json` | 注册 PresetAccessor、TabAccessor |
| `src/main/java/.../util/KeyBuilder.java` | 新增 `hudPreset()`、`tab()`、`systemSg()` 方法 |
| `src/main/java/.../util/trans_engine/AbstractTransEngine.java` | 新增 `transPresetTitle()`、`transTabName()`、`transSystemSettingGroups()` 抽象方法 |
| `src/main/java/.../util/trans_engine/TransEngineNew.java` | 实现上述方法 |
| `src/main/java/.../util/trans_engine/TransEngineOld.java` | 实现上述方法（如仍保留） |
| `src/main/java/.../modules/Translation.java` | 在 `tran()` 中新增系统 SettingGroup、Tab、Preset 的遍历循环 |
| `src/main/resources/assets/.../lang/en_us.json` | 追加新键（英文原文） |
| `src/main/resources/assets/.../lang/zh_cn.json` | 追加中文翻译 |

---

## 6. 键生成详细规则

### 6.1 HUD Preset

```java
public KeyBuilder hudPreset(HudElementInfo<?> info, String presetFieldName) {
    append("hud_preset")
        .appendWithFormat(info.name)        // e.g. "meteor_text"
        .append(presetFieldName);           // e.g. "fps"
    return this;
}
```

生成键：`meteor.hud_preset.meteor_text.fps.name`

### 6.2 System SettingGroup

```java
public KeyBuilder systemSg(String systemId, String groupName) {
    append("system")
        .append(systemId)                  // e.g. "hud"
        .append("sg")
        .append(groupName);                // e.g. "editor"
    return this;
}
```

生成键：`meteor.system.hud.sg_editor.name`

### 6.3 Tab

```java
public KeyBuilder tab(String rawName) {
    append("tab").append(rawName);         // e.g. "modules"
    return this;
}
```

生成键：`meteor.tab.modules.name`

---

## 7. 具体中文翻译草稿

### 7.1 HUD 预设（22 条）

| 键 | 英文 | 中文 |
|---|---|---|
| `meteor.hud_preset.meteor_text.empty.name` | Empty | 空 |
| `meteor.hud_preset.meteor_text.fps.name` | FPS | FPS |
| `meteor.hud_preset.meteor_text.tps.name` | TPS | TPS |
| `meteor.hud_preset.meteor_text.ping.name` | Ping | 延迟 |
| `meteor.hud_preset.meteor_text.speed.name` | Speed | 速度 |
| `meteor.hud_preset.meteor_text.game_mode.name` | Game mode | 游戏模式 |
| `meteor.hud_preset.meteor_text.durability.name` | Durability | 耐久 |
| `meteor.hud_preset.meteor_text.position.name` | Position | 坐标 |
| `meteor.hud_preset.meteor_text.opposite_position.name` | Opposite Position | 对角坐标 |
| `meteor.hud_preset.meteor_text.looking_at.name` | Looking at | 注视方块 |
| `meteor.hud_preset.meteor_text.looking_at_with_position.name` | Looking at with position | 注视方块与坐标 |
| `meteor.hud_preset.meteor_text.breaking_progress.name` | Breaking progress | 破坏进度 |
| `meteor.hud_preset.meteor_text.server.name` | Server | 服务器 |
| `meteor.hud_preset.meteor_text.weather.name` | Weather | 天气 |
| `meteor.hud_preset.meteor_text.biome.name` | Biome | 生物群系 |
| `meteor.hud_preset.meteor_text.world_time.name` | World time | 世界时间 |
| `meteor.hud_preset.meteor_text.real_time.name` | Real time | 现实时间 |
| `meteor.hud_preset.meteor_text.rotation.name` | Rotation | 朝向 |
| `meteor.hud_preset.meteor_text.module_enabled.name` | Module enabled | 模块开启 |
| `meteor.hud_preset.meteor_text.module_enabled_with_info.name` | Module enabled with info | 模块开启详情 |
| `meteor.hud_preset.meteor_text.watermark.name` | Watermark | 水印 |
| `meteor.hud_preset.meteor_text.baritone.name` | Baritone | Baritone |

### 7.2 系统 SettingGroup

| 键 | 英文 | 中文 |
|---|---|---|
| `meteor.system.hud.sg_editor.name` | Editor | 编辑器 |
| `meteor.system.hud.sg_bind.name` | Bind | 绑定 |
| `meteor.system.config.sg_visual.name` | Visual | 视觉 |
| `meteor.system.config.sg_modules.name` | Modules | 模块 |
| `meteor.system.config.sg_chat.name` | Chat | 聊天 |
| `meteor.system.config.sg_misc.name` | Misc | 杂项 |
| `meteor.system.gui_theme.sg_colors.name` | Colors | 颜色 |
| `meteor.system.gui_theme.sg_text.name` | Text | 文本 |
| `meteor.system.gui_theme.sg_background.name` | Background | 背景 |
| `meteor.system.gui_theme.sg_outline.name` | Outline | 边框 |
| `meteor.system.gui_theme.sg_separator.name` | Separator | 分隔线 |
| `meteor.system.gui_theme.sg_scrollbar.name` | Scrollbar | 滚动条 |
| `meteor.system.gui_theme.sg_slider.name` | Slider | 滑块 |
| `meteor.system.gui_theme.sg_starscript.name` | Starscript | Starscript |

### 7.3 Tab 名称

| 键 | 英文 | 中文 |
|---|---|---|
| `meteor.tab.modules.name` | Modules | 模块 |
| `meteor.tab.config.name` | Config | 配置 |
| `meteor.tab.gui.name` | GUI | GUI |
| `meteor.tab.hud.name` | HUD | HUD |
| `meteor.tab.friends.name` | Friends | 好友 |
| `meteor.tab.macros.name` | Macros | 宏 |
| `meteor.tab.profiles.name` | Profiles | 配置方案 |

---

## 8. 实施步骤

1. **新增 Mixin**
   - 创建 `PresetAccessor.java` 和 `TabAccessor.java`
   - 更新 `addon-translation.mixins.json`

2. **扩展 KeyBuilder**
   - 添加 `hudPreset()`、`tab()`、`systemSg()`

3. **扩展 TransEngine**
   - `AbstractTransEngine` 声明新方法
   - `TransEngineNew` / `TransEngineOld` 实现

4. **扩展 Translation 模块**
   - 新增系统 SettingGroup 遍历
   - 新增 Tab 遍历
   - 新增 HUD Preset 遍历

5. **生成 en_us.json 键**
   - 运行生成脚本，追加新键
   - 确保总数从 3113 变为约 3164

6. **翻译 zh_cn.json**
   - 填入上表中文

7. **构建 + 测试**
   - `BUILD SUCCESSFUL`
   - 打开 GUI 检查顶部 Tab、HUD 编辑器预设选择、系统设置分组是否中文

---

## 9. 风险与注意事项

1. **HudGroup 标题不要碰**：`meteor.hud.meteor.*` 的 "meteor" 段来自 `HudGroup.title()`，翻译会导致所有 HUD 键失效。如确需翻译，先实现 HudGroupNameCache。

2. **PathManagerTab 动态名称**：`PathManagerTab` 使用 `PathManagers.get().getName()`，可能返回 "Baritone" 等第三方名称。P2-3 中建议跳过或单独处理。

3. **预设字段名**：`MeteorTextHud` 的预设是 `public static final Preset FPS = ...` 这类静态字段。键生成时应使用字段名（如 `fps`）而非 `title`，避免空格和大小写问题。

4. **系统 SettingGroup 与模块 SettingGroup 可能重名**：例如多个系统都有 "General"。使用 `meteor.system.{system}.sg_{group}` 前缀隔离。

5. **Tab.name 是 final**：通过 mixin accessor 修改是安全的，但应确保只在 Meteor 的 `Tabs.get()` 初始化完成后执行。

---

## 10. 可选扩展（P3）

- **P2-4 窗口标题**：Mixin `WidgetScreen` 的标题渲染，拦截 `WWindow.title`。
- **P2-5 界面按钮/标签**：写一个控件树遍历器，匹配已知英文文本并替换 `WLabel.text` / `WButton.text`。
- **P2-6 聊天消息**：Mixin `ChatUtils.sendMsg()`，保留 `String.format` 参数，只翻译消息模板。
- **P2-7 HudGroup 标题**：实现 `HudGroupNameCache`，与 `NameCache` 对称。

---

*文档版本：P2-draft-1*
*创建日期：2026-06-22*
