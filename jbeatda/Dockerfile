#Dockerfile

# 1단계 : Gradle을 사용해 빌드
FROM azul/zulu-openjdk:17-latest AS builder
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean build --no-daemon -x test

# 2단계 : 빌드된 JAR 파일만 실행 컨테이너로 복사
# base image
FROM azul/zulu-openjdk:17-latest
# set the working directory
WORKDIR /app
# copy the jar file
COPY --from=builder /app/build/libs/*.jar app.jar

# Railway는 PORT 환경변수를 자동으로 설정
EXPOSE 8080
# run the jar file
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Duser.timezone=Asia/Seoul", "-jar", "/app/app.jar"]