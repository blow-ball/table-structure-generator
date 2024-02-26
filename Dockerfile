# 基于 OpenJDK 8 创建镜像
FROM  java:8
MAINTAINER zhengxiaobo<1464949391@qq.com>
# 设置工作目录
WORKDIR /app
# 设置容器的时区为上海时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo "Asia/Shanghai" > /etc/timezone
# 复制构建好的 Java 项目 jar 文件到容器中
COPY target/table-structure-generator-0.0.1-SNAPSHOT.jar .
#多个cmd命令只会运行最后一个,远程断点调试
#ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005", "-jar", "admin.jar"]
# 运行 Java 项目
#CMD ["java", "-jar", "admin.jar","--spring.config.additional-location=/home/api.yml"]
CMD ["java", "-jar", "table-structure-generator-0.0.1-SNAPSHOT.jar"]
