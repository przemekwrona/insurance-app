FROM openjdk:17-jdk-slim-buster
MAINTAINER pwrona
WORKDIR /app
EXPOSE 8080

ENV PROFILE=''
ENV JAVA_OPTS=''

COPY target/insurance-*.jar insurance.jar

ENTRYPOINT java $JAVA_OPTS -jar -Dspring.profiles.active=$PROFILE insurance.jar
