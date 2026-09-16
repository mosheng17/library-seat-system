# 从零开始：SourceMonitor 两次扫描完整指南

> 换了台 Windows、旧数据没了 —— 没关系，**两次都重扫一遍**就行，一共约 20 分钟。
> 关键顺序：**先扫「改进前」，再换代码扫「改进后」**，两个检查点必须在**同一个工程**里。

---

## 0. 先拿到两个代码包

| 包 | 内容 | 用途 |
|---|---|---|
| `library-seat-system-改进前-1bbf185.zip` | 56 个 .java，2400 行 | 第一次扫描 |
| `library-seat-system-改进后-ad9a18f.zip` | 58 个 .java，3068 行 | 第二次扫描 |

**获取方式**（任选一种）：

- **A. 直接点链接下载**（推荐，最省事，浏览器打开即下载）

  - 改进前（56 个文件）：
    <https://raw.githubusercontent.com/mosheng17/library-seat-system/main/metrics-src/seat-system-src-BEFORE-1bbf185.zip>
  - 改进后（58 个文件）：
    <https://raw.githubusercontent.com/mosheng17/library-seat-system/main/metrics-src/seat-system-src-AFTER-ad9a18f.zip>
  - 本指南的在线版：
    <https://raw.githubusercontent.com/mosheng17/library-seat-system/main/metrics-src/SourceMonitor-guide-zh.md>

- **B. U 盘**：把 U 盘插到 Mac 上，告诉我一声，我把两个包拷进去。

- **C. 从仓库源码自己找**：下载 commit 归档后，指向 `backend\src\main\java` 目录
  （⚠️ **不要**指向仓库根目录，那里有 76MB 的 `.m2`，会拖慢扫描）

**两个包解压后都是 `com\library\seatsystem\...` 结构。**

---

## 1. 第一次扫描：改进前

### 1.1 解压到一个固定目录

```
在 D 盘（或桌面）建两个互不干扰的文件夹：

D:\sm-before\        ← 解压「改进前」包，得到 D:\sm-before\com\...
D:\sm-after\         ← 解压「改进后」包，得到 D:\sm-after\com\...
```

> ⚠️ **两个文件夹必须分开**，不能解压到同一个目录（文件名会互相覆盖）。

**先只解压「改进前」**，改进后的先放着，第 2 节才用。

### 1.2 新建工程（同时截「建工程过程」的图）

打开 SourceMonitor → `File` → `New Project…`，向导共 **7 步**。关键的几步按下面填：

| 向导步骤 | 填什么 |
|---|---|
| **第 1 步 Select Language** | ① Project Source Code Language 选 **`Java`**；② Source File Extensions → **Include 填 `*.java`**，Exclude 留空 |
| **第 2 步 Project File** | Project File 名称填 **`library-seat-system`**；Project File 目录选一个你记得住的位置（**不要**放在 `sm-before` 里面） |
| 中间几歩 | 源文件目录 → 选 **`D:\sm-before`**（**包含 `com` 那一层**） |
| 最后一步 | 会问检查点名称 → 填 **`改进前`** |

> 📸 **截图点**：向导每一步填好参数、还没点「下一步」时截一张，这是报告要求的「① 建立工程的过程截图」。**至少留 3 张**（语言选择、工程名、源目录）。

### 1.3 执行扫描

```
Checkpoint 菜单 → Check Files
（或工具栏上带对勾 ✔ 的按钮）
```

扫描完，文件树里会出现 **56 个文件**。

### 1.4 设置雷达图区间（**这一步最关键**）

`Chart` 菜单 → `Kiviat Metrics Graph` → 在图上点右键 → `Properties` / 属性，
把 **11 个指标**的区间逐个改成下表。**下限全部填 0，只有注释率的下限是 10。**

| 指标 | 下限 | 上限 |
|---|---|---|
| Lines | **0** | **3500** |
| Statements | **0** | 2000 |
| % Branches | **0** | **35** |
| % Comments | **10** | 100 |
| Classes | **0** | 60 |
| Methods/Class | **0** | 10 |
| Avg Stmts/Method | **0** | 20 |
| Max Complexity | **0** | 7 |
| Avg Complexity | **0** | 4 |
| Max Depth | **0** | 5 |
| Avg Depth | **0** | **2.5** |

（Functions 不在雷达图上，不用管。）

**为什么要这么设**：本组阈值都是"单侧上限"，只有注释率是"下限要求"。
下限填 0 之后，**改进前这张图只会有 `% Comments` 一个点落在绿圈以内侧**，
正好对应报告表6 第 1 条；而改进后所有点都会回到绿圈内。

### 1.5 截图（改进前，5 张）

| # | 截什么 | 怎么打开 | 报告里的位置 |
|---|---|---|---|
| 1 | 建工程过程 ×3 | 向导的每一步 | §4 ① |
| 2 | **汇总报告 Summary** | 选中顶层检查点 → 切到 **`Summary`** 视图 | **图 5-1** |
| 3 | 明细报告 Files | 选中顶层检查点 → 切到 **`Files`** 视图 | §4 ③ |
| 4 | **Kiviat 雷达图** | `Chart` → `Kiviat Metrics Graph` | **图 5-2** |
| 5 | 三维柱状图 | `Chart` → `Block Histogram` | §4 ④ |

> 汇总报告那张要**完整显示 12 行**：Lines / Statements / % Branches / % Comments / Classes /
> Methods per Class / Functions / Avg Stmts per Method / Max Complexity / Avg Complexity /
> Max Depth / Avg Depth。一屏截不下就分两张。

### 1.6 自检 —— 必须是这两个数

```
Lines   = 2400
Classes = 56
```

**对不上就是扫错代码了**，停下重新检查源目录指向的是不是 `D:\sm-before`。

---

## 2. 第二次扫描：改进后

### 2.1 把改进后的代码换进同一个源目录

**⚠️ 最关键的一步**：SourceMonitor 的检查点扫的是**「源文件夹当前的内容」**，
所以要在**同一个目录**（`D:\sm-before`）里把代码换掉。

```
① 解压「改进后」包到 D:\sm-after\（得到 D:\sm-after\com\...）
② 打开 D:\sm-before\，把里面的 com 文件夹【改名】为 com_before
   （保留着，万一要重扫改进前还能用）
③ 把 D:\sm-after\com 整个【复制】进 D:\sm-before\
```

结果：`D:\sm-before\` 里现在只有一个 `com`（改进后的），外加一个 `com_before`。

> ❗ 不要新建工程、不要换源目录、不要删工程文件（`.smproj`）。
> 源目录路径一变，SourceMonitor 会把它当成一批全新文件，两次数据就没法在同一个工程里对比了。

**自检**：`D:\sm-before\com\library\seatsystem\service\` 里现在应该是 **20 个 .java**（改进前是 16 个）。

### 2.2 新建检查点

```
Checkpoint 菜单 → New Checkpoint…
名称填：改进后
```

### 2.3 扫描

```
Checkpoint 菜单 → Check Files
```

跑完应该出现 **58 个文件**。

> 如果提示"没有文件变化"，说明 SourceMonitor 按文件时间戳判断跳过了。
> 随便改动并保存其中一个文件，或在扫描时选**强制全部重扫**（Force / Re-check all）。

### 2.4 确认区间没变

再打开 `Chart` → `Kiviat Metrics Graph`，核对还是 1.4 节那 11 个区间。
**这张图应该所有点都在绿圈里面** —— 这就是报告要的"红圈已回到绿圈以内"。

### 2.5 截图（改进后，3 张）

| # | 截什么 | 怎么打开 | 报告里的位置 |
|---|---|---|---|
| 1 | **汇总报告 Summary** | 选中 `改进后` 检查点 → `Summary` 视图 | **图 5-3** |
| 2 | **Kiviat 雷达图** | `Chart` → `Kiviat Metrics Graph` | **图 5-4** |
| 3 | 三维柱状图 | `Chart` → `Block Histogram` | §7（可选） |

### 2.6 自检 —— 必须是这三个数

```
Files        = 58      （改进前 56）
Lines        = 3068    （改进前 2400）
% Comments   ≈ 14      （改进前 4.4，必须 > 10）
```

**`Lines` 还是 2400 → 2.1 步没换成功。**
**`% Comments` 还是 4~5% → 两次扫的是同一批文件。**

---

## 3. 截图规范

| 要求 | 做法 |
|---|---|
| 工具 | `Win + Shift + S` 选「窗口截图」，或 `Alt + PrtSc` 截当前窗口 |
| ❌ 不要 | 手机拍屏幕（会有摩尔纹条纹，一眼看出） |
| ❌ 不要 | 截完再缩放/压缩（字会糊） |
| ✅ 要 | 连**窗口标题栏**一起截进去（证明是 SourceMonitor 跑出来的） |
| ✅ 要 | 图上能看到项目名 `library-seat-system` 和检查点名 |
| 格式 | PNG，每张 ≥ 800×600 |

**命名建议**：

```
图5-1-汇总报告-改进前.png
图5-2-雷达图-改进前.png
图5-2-柱状图-改进前.png
图4-明细报告-改进前.png
图4-建工程向导-1.png / -2.png / -3.png
图5-3-汇总报告-改进后.png
图5-4-雷达图-改进后.png
```

---

## 4. 全部扫完后发回给我

1. **8 张左右的截图**
2. **改进后的 12 个指标数值**（直接抄汇总报告）：
   ```
   Lines / Statements / % Branches / % Comments / Classes /
   Methods per Class / Functions / Avg Stmts per Method /
   Max Complexity / Avg Complexity / Max Depth / Avg Depth
   ```
3. 一句话确认：`Lines = 3068` ？`Classes = 58` ？`% Comments = ?`

我拿到后：把数值填进报告表5「改进后」一栏（替换现在的估算值）、插入图5-3/图5-4，终版就能交。

---

## 5. 常见问题

**Q：向导有 7 步，我记不住哪个填哪个？**
只要保证这四项对就行：① 语言 `Java`；② 文件过滤器 `*.java`；③ 源目录指向 `D:\sm-before`（含 `com` 那层）；④ 检查点名 `改进前`。其余默认即可。

**Q：不确定源目录该指哪一层？**
指向**包含 `com` 文件夹的那一层**（即 `D:\sm-before`）。如果指向 `D:\sm-before\com`，SourceMonitor 也能扫到，但路径会变，两次对比容易乱。

**Q：扫出来文件数是 0？**
文件过滤器写错了。回到工程属性把 Include 改成 `*.java`。

**Q：扫出来几百个文件？**
源目录指到了仓库根目录，把 `.m2` 也扫进去了。改成只指向 `D:\sm-before`。

**Q：改进前扫完就把 `com` 换掉了，后来发现改进前的图截错了想重来？**
把 `com_before` 改名回 `com`，`Checkpoint` → `New Checkpoint` 命名 `改进前_重扫`，`Check Files` 即可。旧检查点数据都还在工程文件里。

**Q：电脑重启后工程还能用吗？**
能。工程数据存在 `.smproj` 文件里，**别删它**。重装 SourceMonitor 后 `File → Open Project` 打开即可。
