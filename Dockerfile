FROM openjdk:8-jdk-alpine
ARG APP_NAME
WORKDIR /home/app
ENV JAVA_OPTS="-Xmx256m -Xms64m -Xss256k"
ENV SERVER_PORT=5501
EXPOSE ${SERVER_PORT}
COPY target/${APP_NAME}.jar /home/app/app.jar

RUN touch app.jar \
    && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo 'Asia/Shanghai' >/etc/timezone \
    && mkdir logs
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -Dserver.port=$SERVER_PORT -Djava.security.egd=file:/dev/./urandom -jar /home/app/app.jar >> /home/app/logs/server.log 2>&1" ]
