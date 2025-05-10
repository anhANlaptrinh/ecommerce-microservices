# Dùng image JDK 21 nhẹ (Alpine)
FROM eclipse-temurin:21-jdk-alpine

# Tạo thư mục làm việc trong container
WORKDIR /app

# Copy file jar từ local vào container (nhớ build trước)
COPY target/api-gateway-0.0.1-SNAPSHOT.jar app.jar

# Expose port của api-gateway (nhớ khớp với application.yml)
EXPOSE 8888

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
