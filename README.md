<div align="center">
<img src="/assets/logo.png" alt="meteor-translation-addon" width="225px" />

<h1>Meteor Translation Addon</h1>
<h2>流星翻译插件</h2>

[![Minecraft](https://img.shields.io/badge/Minecraft-21.1.2-blue)](https://github.com/KJH50/meteor-translation-addon)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)
</div>

## 📖 简介

将 Meteor Client 的界面、设置、HUD、下拉菜单、提示框等**全面汉化**的插件。

相比原版翻译仅覆盖模块名称，本插件通过 **通用文本替换层** 拦截渲染管线中的硬编码英文文本，
实现了对 GUI 标签、按钮、Tab 名、弹窗提示、枚举下拉值、HUD 预设名称等 **939 条字符串**的全覆盖翻译。

## ✅ 翻译覆盖

| 层级 | 内容 | 条目数 |
|---|---|---|
| P1 模块层 | 模块名、设置名、设置描述、分类、命令、HUD 元素 | zh_cn.json |
| P2 系统层 | 设置分组名、HUD 预设标题、Tab 名称 | zh_cn.json |
| 通用替换层 | GUI 按钮/标签、下拉菜单枚举值、弹窗提示、状态消息、存储方块名、字体名等 | 939 条 universal_zh_cn.json |

> 通用层采用**原文作为键**的查表方式，在文本渲染前拦截替换，无需修改 Meteor 源码即可覆盖所有硬编码英文。

## 🚀 如何使用

1. 安装插件后启动游戏
2. 翻译**默认自动启用**，无需手动操作
3. 如需手动触发：进入 `MeteorTranslation` → `Meteor Trans` 模块，点击 `翻译` 按钮
4. 退出游戏时自动导出未知文本到 `meteor-client/meteor-translation-addon/unknown.json`

> 💡 如果自定义字体导致中文显示异常，请在流星设置中选择一款中文字体（如 Microsoft YaHei、Noto Sans SC）。

## 🛠 技术架构

```
渲染管线拦截层 (Mixins)
├── WLabelMixin       → 标签文本替换
├── WButtonMixin      → 按钮文本替换
├── WWindowMixin      → 窗口标题替换
├── WidgetScreenMixin → 屏幕标题替换
├── WSectionMixin     → 分组标题替换
├── WWidgetMixin      → 通用组件 tooltip
├── WMeteorDropdownValueMixin    → 下拉展开列表汉化
├── WMeteorDropdownHeaderMixin   → 下拉折叠态汉化 (NEW)
├── WMultiLabelMixin  → 多行标签替换
├── WTextBoxMixin     → 输入框占位符替换
├── ChatUtilsMixin    → 聊天消息替换
└── ChatUtilsPrefixMixin → 聊天前缀替换
```

## 📦 构建

```bash
# 需要 Java 25
$env:JAVA_HOME = "路径\Zulu\zulu-25"
.\gradlew.bat build
```

> 从 0.7.4 起需要 **Java 25** 编译（Minecraft 26.x），推荐使用 [Zulu JDK 25](https://www.azul.com/downloads/)。

## 🤝 贡献

1. Fork 本仓库
2. 发现未翻译的英文文本后，添加到 `universal_zh_cn.json` 或 `zh_cn.json`
3. 提交 PR 并注明理由

如果有翻译错误请提交 **Issue / PR**。

## 🙏 鸣谢

- [MeteorDevelopment](https://github.com/MeteorDevelopment) - 流星端及插件模板
- [顶针](https://github.com/dingzhen-vape) - 完整的翻译文件
- [E0x72-24](https://github.com/E0x72-24) - 图标设计及文档优化

## 📄 License

MIT License - 详见 [LICENSE](LICENSE)
