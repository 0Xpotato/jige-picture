# 鸡哥智能素材网项目

> 作者：[程序员鸡哥](https://gitee.com/Lucky__Style)
>


## 项目介绍

基于 Vue 3 + Spring Boot + COS + WebSocket 的 **鸡哥智能素材网平台**。


1）所有用户都可以在平台公开上传和检索图片素材，快速找到需要的图片。可用作表情包网站、设计素材网站、壁纸网站等：

![image](https://github.com/user-attachments/assets/76a981a6-d231-4b1a-87f2-340a5b2253e1)


2）管理员可以上传、审核和管理图片，并对系统内的图片进行分析：

![image](https://github.com/user-attachments/assets/b65f0cab-843f-413b-b06b-8e0e3415bb0f)


3）对于个人用户，可将图片上传至私有空间进行批量管理、检索、编辑和分析，用作个人网盘、个人相册、作品集等：

![image](https://github.com/user-attachments/assets/d368a2e1-0487-4797-ab0d-01a87e08479e)


项目架构设计图：

![image](https://github.com/user-attachments/assets/58f15cd4-0ea2-4ed0-acc6-e5fad2a364ee)


## 技术选型

### 后端

- Java Spring Boot 框架
- MySQL 数据库 + MyBatis-Plus 框架 + MyBatis X 
- Redis 分布式缓存 + Caffeine 本地缓存 
- Jsoup 数据抓取
- ⭐️ COS 对象存储
- ⭐️ Sa-Token 权限控制
- ⭐️ WebSocket 双向通信
- ⭐️ JUC 并发和异步编程
- ⭐️ AI 绘图大模型接入
- ⭐️ 多种设计模式的运用
- ⭐️ 多角度项目优化：性能、成本、安全性等



### 前端

- Vue 3 框架
- Vite 打包工具
- Ant Design Vue 组件库
- Axios 请求库
- Pinia 全局状态管理
- 其他组件：数据可视化、图片编辑等
- ⭐️ 前端工程化：ESLint + Prettier + TypeScript
- ⭐️ OpenAPI 前端代码生成
