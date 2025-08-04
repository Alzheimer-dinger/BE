FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /workspace

# 1) 호스트에서 빌드된 jar 를 복사
COPY build/libs/*.jar app.jar

# 2) 런타임 이미지를 따로 쓸 수도 있음
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /workspace/app.jar .
ENTRYPOINT ["java", "-jar", "app.jar"]