FROM gradle:8.8-jdk17 AS build
WORKDIR /app
COPY build.gradle settings.gradle /app/
RUN gradle dependencies --no-daemon || true
COPY src /app/src
RUN gradle build -x test --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/app.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

