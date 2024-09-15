# OpenJDK 19이 설치된 slim Linux 이미지를 기반으로 설정
FROM openjdk:22-ea-19-slim
# 로컬 시스템에서 wooriarte-0.0.1-SNAPSHOT.jar 파일을 Docker 이미지 내의 /app.jar로 복사
COPY build/libs/wooriarte-0.0.1-SNAPSHOT.jar /app.jar
# 컨테이너 실행 시 자바 애플리케이션을 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]
