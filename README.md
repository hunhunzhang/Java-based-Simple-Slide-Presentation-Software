<div align="center">

# 📊 Java 简易幻灯片演示软件

<p align="center">
  <img src="https://img.shields.io/badge/Java-21%2B-orange?style=for-the-badge&logo=java" alt="Java Version"/>
  <img src="https://img.shields.io/badge/IDE-IntelliJ%20IDEA-blue?style=for-the-badge&logo=intellij-idea" alt="IDE"/>
  <img src="https://img.shields.io/badge/GUI-Java%20Swing-green?style=for-the-badge" alt="GUI Framework"/>
</p>

<p align="center">一款基于 Java Swing 开发的轻量级幻灯片演示软件，支持文字、形状、图片等多种元素的创建与编辑。</p>

</div>

---

## ✨ 功能特性

### 📁 文件操作
- 📄 **新建演示文稿** — 快速创建全新的幻灯片演示文稿
- 📂 **打开文件** — 加载已保存的演示文稿（基于 Java 序列化）
- 💾 **保存文件** — 持久化保存演示文稿及所有对象

### 🖼️ 幻灯片管理
- ➕ **多张幻灯片** — 在一个演示文稿中管理多张幻灯片
- 🔀 **幻灯片导航** — 左侧面板列表，轻松切换幻灯片
- ❌ **增删幻灯片** — 右键菜单快速插入或删除幻灯片

### 🎨 可绘制元素

| 元素类型 | 功能说明 |
|----------|----------|
| 📝 **文本框** | 支持自定义字体、字号、颜色、加粗、斜体、文本内容编辑 |
| ▭ **矩形** | 支持自定义边框颜色与填充颜色 |
| ⭕ **圆形** | 支持自定义边框颜色与填充颜色 |
| 🖼️ **图片** | 支持插入 JPG、PNG、GIF 格式图片 |

### 🖱️ 交互操作
- **拖拽移动** — 点击并拖动任意对象至目标位置
- **缩放调整** — 拖拽红色角柄以调整对象大小
- **单击选中** — 点击即可选中对象（高亮青色边框）
- **双击编辑** — 双击文本框进入文字编辑模式
- **右键菜单** — 删除对象或设置格式属性
- **颜色选择器** — 通过 JColorChooser 对话框自由配色

---

## 🗂️ 项目结构

```
Java-based-Simple-Slide-Presentation-Software/
├── 源代码/                     # Java 源代码目录
│   ├── Main.java               # 程序入口
│   ├── SimpleSlideMaker.java   # 主界面与核心逻辑（JFrame）
│   ├── Slide.java              # 幻灯片数据模型
│   ├── DrawableObject.java     # 可绘制对象抽象基类
│   ├── TextBox.java            # 文本框元素
│   ├── RectangleShape.java     # 矩形形状元素
│   ├── CircleShape.java        # 圆形形状元素
│   └── MyImage.java            # 图片元素
├── 可执行字节码/               # 预编译 .class 文件目录
├── 报告 1.doc                  # 详细程序说明报告
└── README.md                   # 项目说明文档
```

---

## 🚀 快速开始

### 环境要求

- **Java** 版本 `21` 或以上（开发时使用 21.0.4，更高版本亦可）
- **IDE（推荐）**：IntelliJ IDEA Community Edition 2024.2.1 或更新版本

---

### 方式一：使用 IDE 运行（推荐）

1. 打开 **IntelliJ IDEA**，新建一个 Java 项目
2. 将 `源代码/` 目录下的所有 `.java` 文件复制到项目的 `src/` 文件夹中
3. 点击运行按钮，或右键 `Main.java` 选择 **Run 'Main'**

```
项目结构示例：
MyProject/
└── src/
    ├── Main.java
    ├── SimpleSlideMaker.java
    ├── Slide.java
    ├── DrawableObject.java
    ├── TextBox.java
    ├── RectangleShape.java
    ├── CircleShape.java
    └── MyImage.java
```

---

### 方式二：使用命令行运行预编译字节码

```bash
# 1. 进入可执行字节码目录
cd "可执行字节码"

# 2. 运行程序
java Main
```

**Windows 示例：**

```cmd
D:\> cd "D:\桌面\可执行字节码"
D:\桌面\可执行字节码> java Main
```

---

### 方式三：直接编译源代码运行

```bash
# 1. 进入源代码目录
cd "源代码"

# 2. 编译所有 Java 文件
javac *.java

# 3. 运行程序
java Main
```

> ⚠️ **注意**：编译后会在当前目录生成 `.class` 字节码文件，这是正常现象。

---

## 🛠️ 技术细节

| 技术栈 | 说明 |
|--------|------|
| **语言** | Java 21.0.4（兼容 Java 21+）|
| **GUI 框架** | Java Swing (JFrame, JPanel, JSplitPane 等) |
| **持久化** | Java 对象序列化（ObjectOutputStream / ObjectInputStream）|
| **设计模式** | 多态绘制（抽象类 DrawableObject + 具体子类）|
| **文件选择** | JFileChooser（支持过滤 .jpg/.png/.gif 格式）|
| **颜色选择** | JColorChooser |
| **事件处理** | MouseListener / MouseMotionListener |

---

## 📖 使用说明

1. **启动程序** — 运行后出现主窗口，左侧为幻灯片列表，右侧为编辑画布
2. **新建/打开文稿** — 通过顶部菜单栏「文件」操作
3. **插入元素** — 点击菜单栏「插入」，选择文本框、矩形、圆形或图片
4. **编辑元素** — 单击选中（青色边框），拖拽移动，拖动红角柄缩放
5. **编辑文字** — 双击文本框，在弹出对话框中修改文字内容与格式
6. **右键操作** — 右键对象可删除或修改颜色等属性
7. **保存文稿** — 通过「文件 > 保存」持久化存储

> 📄 更多详细操作说明，请参见项目中的 **报告 1.doc** 文档。

---

## 🤝 贡献

欢迎提交 Issue 或 Pull Request 来改进本项目！

---

<div align="center">
  <sub>使用 ❤️ 和 Java Swing 构建</sub>
</div>
