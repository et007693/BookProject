FROM openjdk:17-jdk
VOLUME /tmp
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
COPY build/libs/application-API-KEY.properties application-API-KEY.properties
ENTRYPOINT ["java","-jar","/app.jar"]