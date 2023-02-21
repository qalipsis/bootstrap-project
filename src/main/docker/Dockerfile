FROM openjdk:13-alpine

ARG JAR_NAME

RUN apk add --no-cache tini

RUN mkdir -p /app/logs /app/config
ADD $JAR_NAME /app/qalipsis.jar

RUN chmod -R 777 /app

WORKDIR /app

COPY entrypoint.sh /usr/local/bin
RUN chmod +x /usr/local/bin/entrypoint.sh

ENV JVM_ARGS "-Xms64m -Xmx1024m"
ENV LOG_PATH /app/logs
ENV MIN_FLAT_FILE_LOG_LEVEL ALL
ENTRYPOINT ["/sbin/tini", "--", "/usr/local/bin/entrypoint.sh"]
EXPOSE 8080 8443