# 1단계 : Gradle을 사용해 빌드
FROM azul/zulu-openjdk:17-latest AS builder
WORKDIR /app

# jbeatda 폴더의 내용을 /app에 복사
COPY ./jbeatda/ ./

# gradlew 권한 설정
RUN chmod +x ./gradlew

# 빌드 실행
RUN ./gradlew clean build --no-daemon -x test

# 2단계 : 실행 컨테이너
FROM azul/zulu-openjdk:17-latest
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# Railway는 PORT 환경변수를 자동으로 설정
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Duser.timezone=Asia/Seoul", "-jar", "/app/app.jar"]