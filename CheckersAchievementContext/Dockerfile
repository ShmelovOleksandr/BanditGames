FROM gradle:8.8-jdk21 as build
WORKDIR /app
COPY --chown=gradle:gradle . /app
RUN gradle :CheckersAchievementContext:clean :CheckersAchievementContext:build -x check --no-daemon

FROM openjdk:21-slim as builder
WORKDIR /app
COPY --from=build /app/CheckersAchievementContext/build/libs/CheckersAchievementContext.jar /app/CheckersAchievement.jar
RUN java -Djarmode=layertools -jar CheckersAchievement.jar extract

FROM openjdk:21-slim
WORKDIR /app
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./
ENTRYPOINT ["java", "-Dspring.profiles.active=gcloud", "org.springframework.boot.loader.launch.JarLauncher"]