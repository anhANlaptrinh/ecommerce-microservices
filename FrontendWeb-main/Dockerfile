# Sử dụng NGINX làm web server
FROM nginx:alpine

# Xóa nội dung mặc định
RUN rm -rf /usr/share/nginx/html/*

# Copy toàn bộ file HTML/CSS/JS vào thư mục phục vụ
COPY . /usr/share/nginx/html

# Mở port 80
EXPOSE 80

# Khởi chạy nginx
CMD ["nginx", "-g", "daemon off;"]
