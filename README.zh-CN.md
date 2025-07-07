[English](README.md) | [简体中文](README.zh-CN.md)

<p align="center">
<a href="https://www.gogoscrum.com">
  <img src="https://gogoscrum.oss-cn-hangzhou.aliyuncs.com/materials/gogoscrum-full-logo.png" width="360">
  </a>
</p>

<p align="center">
  <a href="https://www.apache.org/licenses/LICENSE-2.0"><img src="https://img.shields.io/badge/License-Apache 2.0-orange" alt="License"></a>
  <img src="https://img.shields.io/badge/Java-21-brightgreen.svg" alt="Java Version" />
  <a href="https://www.gogoscrum.com"><img src="https://img.shields.io/badge/Website-www.gogoscrum.com-blue" alt="gogoscrum Website"></a>
</p>

<br/>

# gogoscrum开源轻量级敏捷项目管理工具

gogoscrum是轻量级敏捷项目管理工具，遵循Scrum敏捷实践，为敏捷软件开发团队量身打造。可以轻松管理从代办事项列表、迭代任务看板、故事点估算、Bug跟踪、项目文件管理和燃尽图报表的所有环节。

## 主要功能

- **迭代看板**：以看板为团队协作核心，领取任务、拖动更新状态、开展每日站会，团队成员随时保持信息同步。
- **任务管理**：任务故事点估算、添加标签、快捷上传截图或附件、关联到其它任务、添加评论、任务变更历史记录一目了然。
- **Bug追踪**：提交Bug，快捷上传截图，设置优先级，并可关联到需求和任务，按功能模块统计。
- **代办列表**：按照优先级和故事点数自动排序，轻松梳理项目需求和任务。
- **文件管理**：创建文件夹并上传多种格式的文件，统一管理项目中的各类文档资料。
- **项目资料**：记录项目中的重要信息，以便团队成员随时查阅。
- **统计图表**：迭代燃尽图、速率图和累积流图，一眼便知项目进展是否符合预期。

## 页面截图

经典风格看板

<img src="https://gogoscrum.oss-cn-hangzhou.aliyuncs.com/materials/white-board-classic.png" width="680">

彩色贴纸风格看板

<img src="https://gogoscrum.oss-cn-hangzhou.aliyuncs.com/materials/white-board-sticky-notes.png" width="680">

黑暗模式看板

<img src="https://gogoscrum.oss-cn-hangzhou.aliyuncs.com/materials/dark-board.png" width="680">

任务列表

<img src="https://gogoscrum.oss-cn-hangzhou.aliyuncs.com/materials/issue-list.png" width="680">

文件列表

<img src="https://gogoscrum.oss-cn-hangzhou.aliyuncs.com/materials/file-list.png" width="680">

燃尽图

<img src="https://gogoscrum.oss-cn-hangzhou.aliyuncs.com/materials/burn-down-chart.png" width="680">

任务详情

<img src="https://gogoscrum.oss-cn-hangzhou.aliyuncs.com/materials/issue-edit.png" width="480">

## 技术栈

**gogoscrum后端服务基于以下技术栈构建**：

- Java 21
- Spring Boot 3.4
- Spring Security
- JPA + Hibernate
- MySQL 8.0

**gogoscrum前端应用基于以下技术栈构建**：
- Vue 3.5
- Element Plus
- Echarts

## 体验站

- [https://demo.gogoscrum.com/]
- 用户名：`demo`
- 密码：`demo1234`

## 快速开始

### 通过docker运行gogoscrum

- 下载压缩包：[https://gogoscrum.oss-cn-hangzhou.aliyuncs.com/downloads/gogoscrum-docker.zip]
- 解压缩到 `gogoscrum-docker`目录
- 进入目录，运行命令 `docker compose up --build`
- 访问 [http://localhost] 开始体验gogoscrum

### 通过源代码运行gogoscrum

#### 启动后端服务

- 克隆 `gsf-core-service` 仓库并安装 `shimi-gsf-core.jar` 至本地Maven仓库（具体步骤参考 `gsf-core-service` 项目内README文档）
- 克隆 `gogoscrum-rest-service` 仓库至本地目录，并导入至IDE开发工具
- 复制 `dist.application-dev.properties` 文件为 `application-dev.properties`
- 修改 `application-dev.properties` 中的数据库连接及其它配置信息
  - 预先手动创建好数据库，服务启动时会自动创建所有的表
  - 检查本地Redis服务是否已安装并已启动
  - 创建lfs目录用于保存上传的文件，并修改 `file.local.dir.base` 配置指向该目录
- 运行 `GogoscrumApplication.java` 启动服务，查看服务端日志，确认服务启动正常
- 访问 [http://localhost:8080/health] 验证服务已正常运行

#### 启动前端应用

- 克隆 `gogoscrum-web-client` 仓库至本地目录，并导入至IDE开发工具
- 在项目根目录下新建文件 `.env.local` 并添加以下内容：
```
NODE_ENV=development
VITE_FILE_PATH = '指向到后端Java服务中所配置的lfs路径'
```
- 运行命令 `npm install` 安装依赖
- 运行命令 `npm run dev` 启动前端项目
- 访问 [http://localhost:8081] 开始使用gogoscrum
- 首次访问请先注册新用户，注册后即可登录使用

## Bug提交

加入“gogoscrum社区版”项目，提交bug：[https://www.gogoscrum.com/invitations/n6DDaxwqMndM]

## 交流社区

微信扫码加入gogoscrum开发者交流群：\
<img src="https://gogoscrum.oss-cn-hangzhou.aliyuncs.com/materials/wechat-qr-dev-group.png" width="100">

## 授权协议

本项目基于 **Apache License 2.0** 进行授权，并附加以下条款。

您可以自由地：

- 出于**非商业目的**使用、修改和分发本软件（需保留logo和版权声明）；
- 学习源代码并为项目做出贡献。

但您**不得将本软件用于商业目的**，包括但不限于作为产品或服务进行销售、收费使用或提供给第三方。

如需将本软件用于商业用途（例如 SaaS 服务、企业内部系统或付费解决方案），请联系我们获取**商业授权**。