# syntax=docker/dockerfile:1

FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -DskipTests clean package

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -Djava.net.preferIPv4Stack=true -Djava.net.preferIPv6Addresses=false"
EXPOSE 8080
CMD ["bash","-lc","java $JAVA_OPTS -jar /app/app.jar"]
