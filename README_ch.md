# Spring-AI-Demo

## 项目概述

本应用程序展示了Spring AI与阿里云DashScope API的集成，为多个AI模型提供标准和流式聊天端点。该项目使用Spring Boot 3.4.5构建，并遵循具有配置、控制器和服务组件的分层架构。

## 运行环境

- Java 17或更高版本
- Maven 3.6+
- DashScope API密钥（来自阿里云）

## 安装和设置

1. 克隆仓库：
   ```bash
   git clone git@github.com:GwwSx/SpringAI-chat.git
   cd SpringAI-chat
   ```

2. 配置您的DashScope API密钥：
   - 创建环境变量：
     ```bash
     export DASHSCOPE_API_KEY=your_api_key_here
     ```
   - 或修改`application.yml`文件，添加您的API密钥

3. 构建项目：
   ```bash
   ./mvnw clean package
   ```

## 运行应用程序

```bash
# 使用Maven
./mvnw spring-boot:run

# 或使用构建的JAR文件
java -jar target/Spring-AI-Demo-0.0.1-SNAPSHOT.jar
```

服务器将在端口8081上启动，上下文路径为`/api`。


## 项目结构

```
src/main/java/com/zijiang/springaidemo/
├── SpringAiDemoApplication.java       # 应用程序入口点
├── config/                            # 配置类
│   └── ChatConfig.java                # 聊天客户端配置
├── controller/                        # REST控制器
│   └── AIAssistantController.java     # AI聊天端点
└── service/                           # 业务逻辑
    ├── ChatClientService.java         # 基于客户端的聊天服务
    └── ChatModelService.java          # 基于模型的聊天服务
```

## 依赖项

主要依赖项包括：
- Spring Boot 3.4.5
- Spring AI Alibaba DashScope Starter
- Spring Web
- Lombok
- JUnit 5 用于测试

## 项目相关环境依赖补充
### ubuntu安装redis stack

#### 1.下载镜像

```
# 下载镜像
docker pull redis

# 检查当前所有Docker下载的镜像
docker images
```

| 命令                  | 描述                                                         |
| :-------------------- | :----------------------------------------------------------- |
| docker pull redis     | 下载最新版Redis镜像 (其实此命令就等同于 : docker pull redis:latest ) |
| docker pull redis:xxx | 下载指定版本的Redis镜像 (xxx指具体版本号)                    |

#### 2.创建文件夹

```
mkdir -p /opt/redis/conf
mkdir -p /opt/redis/data

touch redis.conf
```

#### 3.安装命令

```dockerfile
# Docker 创建 Redis 容器命令
docker run \
--restart=always \
--log-opt max-size=100m \
--log-opt max-file=2 \
-p 6379:6379 \
--name redis \
-v /opt/myFile/redis/conf/redis.conf:/etc/redis/redis.conf  \
-v /opt/myFile/redis/data:/data \
-d redis redis-server /etc/redis/redis.conf \
--appendonly yes \
--requirepass 123456

# Docker 创建 redis stack
docker run -d --name redis-stack -p 6379:6379 -p 8001:8001 -v /opt/myFile/redis/conf/redis.conf:/etc/redis/redis.conf  -v /opt/myFile/redis/data:/data -e REDIS_ARGS="--requirepass 123456" redis/redis-stack:latest
```

| **命令**                                                     | **功能**                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| docker run                                                   | 这是 Docker 用来创建并运行一个新的容器的命令                 |
| --restart=always                                             | 如果容器退出，这个选项会使得它自动重启                       |
| --log-opt max-size=100m                                      | 这是对容器日志的设置，最大大小为 100MB                       |
| --log-opt max-file=2                                         | 这是对容器日志文件的设置，最多可以有2个日志文件              |
| -p 6379:6379                                                 | 这是端口映射的设置，将宿主机的6379端口映射到容器的6379端口   |
| --name redis                                                 | 这是给新创建的容器命名的选项，名字是 "redis"                 |
| -v /opt/myredis/redis.conf:/etc/redis/redis.conf             | 这是对容器内的文件系统的挂载设置，将宿主机上的 /opt/myredis/redis.conf 文件挂载到容器内的 /etc/redis/redis.conf 位置 |
| -v /opt/myredis/data:/data                                   | 这是另一个文件系统的挂载选项，将宿主机上的 /opt/myredis/data 目录挂载到容器内的 /data目录 |
| -d                                                           | 这是 Docker 的分离模式，新创建的进程将会在后台运行           |
| redis redis-server /etc/redis/redis.conf --appendonly yes --requirepass 123456 | 这是容器内要运行的命令，启动 Redis 服务，使用 /etc/redis/redis.conf 配置文件，设置追加写入(appendonly)为 yes，设置密码为 "123456" |

#### 4.Redis 配置文件修改

```shell
# 修改 /opt/redis/conf/redis.conf
protected-mode no
bind 0.0.0.0
```

| 命令              | 功能                                                         |
| ----------------- | ------------------------------------------------------------ |
| protected-mode no | 关闭protected-mode模式，此时外部网络可以直接访问 (docker貌似自动开启了) |
| bind 0.0.0.0      | 设置所有IP都可以访问 (docker貌似自动开启了)                  |

#### 5.RDM远程连接

```shell
# 重启redis服务
docker restart redis
```

#### 6.可视化界面地址

```
localhost:8001
```



