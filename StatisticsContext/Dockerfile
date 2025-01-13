FROM gradle:8.8-jdk21 as build
WORKDIR /app
COPY --chown=gradle:gradle . /app
RUN gradle :StatisticsContext:clean :StatisticsContext:build -x check --no-daemon

FROM openjdk:21-slim as builder
WORKDIR /app
COPY --from=build /app/StatisticsContext/build/libs/StatisticsContext.jar /app/Statistics.jar
RUN java -Djarmode=layertools -jar Statistics.jar extract

FROM openjdk:21-slim
WORKDIR /app
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./
ENTRYPOINT ["java", "-Dspring.profiles.active=gcloud", "org.springframework.boot.loader.launch.JarLauncher"]