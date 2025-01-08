FROM gradle:8.8-jdk21 as build
WORKDIR /app
COPY --chown=gradle:gradle . /app
RUN gradle :CheckersContext:clean :CheckersContext:build -x test --no-daemon

FROM openjdk:21-slim as builder
WORKDIR /app
COPY --from=build /app/CheckersContext/build/libs/CheckersContext.jar /app/Checkers.jar
RUN java -Djarmode=layertools -jar Checkers.jar extract

FROM openjdk:21-slim
WORKDIR /app
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]