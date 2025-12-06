FROM eclipse-temurin:17-jre
ARG JAR_FILE=target/*.jar
ENV TIME_ZONE=Asia/Shanghai
COPY ${JAR_FILE} app.jar
RUN ln -snf /usr/share/zoneinfo/${TIME_ZONE} /etc/localtime && echo ${TIME_ZONE} > /etc/timezone
ENTRYPOINT ["java","-jar","/app.jar"]