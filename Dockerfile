FROM gradle:8.10.2-jdk21 AS build
WORKDIR /app
COPY . .
CMD ["gradle", "build", "-x", "test", "--no-daemon"]

FROM openjdk:21-jdk
WORKDIR /app
COPY --from=build /app/build/libs/*.jar ./app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
EXPOSE 8088