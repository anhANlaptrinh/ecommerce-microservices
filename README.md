# 🛒 E-commerce Microservices Platform

Hệ thống thương mại điện tử hiện đại được xây dựng theo kiến trúc microservices với CI/CD pipeline hoàn chỉnh và triển khai trên Kubernetes.

## 📋 Tổng quan dự án

**UIT Store** là một nền tảng thương mại điện tử gaming gear hoàn chỉnh bao gồm:
- **Frontend Web**: Giao diện responsive với HTML, CSS, JavaScript
- **Microservices Backend**: Kiến trúc microservices với Spring Boot + Java 21
- **API Gateway**: Spring Cloud Gateway để định tuyến requests
- **Infrastructure as Code**: Terraform + Ansible
- **CI/CD Pipeline**: Jenkins với automated testing và deployment
- **Container Orchestration**: Kubernetes + Docker
- **Security Scanning**: Trivy + OWASP ZAP

## 🏗️ Kiến trúc hệ thống

### System Architecture Diagram
![System Architecture](https://drive.google.com/uc?export=view&id=18ccgR0I4QLqbn2afZ2DZnJ9RAjDOafBK)
```
ecommerce-microservices/
├── FrontendWeb-main/              # Frontend (NGINX)
│   ├── css/, js/, images/         # Static assets
│   ├── *.html                     # Web pages
│   └── Dockerfile                 # Frontend container
├── api-gateway/                   # API Gateway (Port 8888)
│   ├── src/main/java/             # Spring Cloud Gateway
│   ├── pom.xml                    # Maven dependencies
│   └── Dockerfile                 # Gateway container
├── authentication-service/       # Auth Service (Port 8080)
│   ├── src/main/java/             # Spring Boot
│   ├── pom.xml                    # Maven config
│   └── Dockerfile                 # Multi-stage build
├── product-service/              # Product Service (Port 8081)
│   ├── src/main/java/             # Product management
│   └── Dockerfile                # Service container
├── cart-service/                 # Cart Service (Port 8082)
│   ├── src/main/java/             # Shopping cart logic
│   └── Dockerfile                # Cart container
├── k8s/manifests/                # Kubernetes deployments
│   ├── frontend/                 # Frontend K8s configs
│   ├── api-gateway/              # Gateway K8s configs
│   ├── auth-service/             # Auth K8s configs
│   ├── product-service/          # Product K8s configs
│   └── cart-service/             # Cart K8s configs
├── infra/terraform/              # Infrastructure as Code
│   ├── modules/                  # Terraform modules
│   │   ├── jenkins-master/       # Jenkins master setup
│   │   ├── jenkins-agent/        # Jenkins agent setup
│   │   └── k8s-nodes/           # Kubernetes nodes
│   └── *.tf                     # Terraform configs
└── Jenkinsfile                  # CI/CD pipeline
```

### Microservices Flow Diagram
![Microservices Flow](https://drive.google.com/uc?export=view&id=1xsG3CJKIbzQ5tmvCTr_Yzfl8Wa94w-gH)

## 💻 Tech Stack

### Frontend
- **HTML5 + CSS3/SCSS** - Responsive UI
- **Vanilla JavaScript (ES6+)** - Frontend logic
- **Bootstrap 5** - CSS framework
- **Chart.js** - Analytics dashboard
- **Owl Carousel** - Product sliders
- **Toastify** - User notifications
- **NGINX** - Web server

### Backend Microservices
- **Java 21** - Programming language
- **Spring Boot 3.4.5** - Microservices framework
- **Spring Cloud Gateway** - API Gateway
- **Maven** - Dependency management
- **Docker** - Containerization

### DevOps & Infrastructure
- **Kubernetes** - Container orchestration
- **Terraform** - Infrastructure as Code
- **Ansible** - Configuration management
- **Jenkins** - CI/CD pipeline
- **ArgoCD** - GitOps continuous deployment
- **AWS EC2** - Cloud infrastructure
- **Cloudflare** - DNS management
- **Docker Hub** - Container registry

### Security & Monitoring
- **Trivy** - Container vulnerability scanning
- **OWASP ZAP** - Web application security testing
- **JWT** - Authentication tokens

## 🚀 Tính năng chính

### 🛍️ Người dùng cuối
- **Trang chủ**: Sản phẩm nổi bật, danh mục gaming gear
- **Cửa hàng**: Duyệt sản phẩm theo danh mục (chuột, bàn phím, tai nghe, v.v.)
- **Chi tiết sản phẩm**: Xem thông tin, hình ảnh chi tiết
- **Giỏ hàng**: Thêm/xóa/cập nhật sản phẩm
- **Xác thực**: Đăng nhập/đăng ký với JWT
- **Blog**: Tin tức gaming và reviews sản phẩm
- **Tìm kiếm**: Tìm kiếm thông minh với placeholder động

### 👨‍💼 Admin Dashboard
- **Analytics**: Biểu đồ doanh số, đơn hàng, lợi nhuận
- **Quản lý sản phẩm**: CRUD operations
- **Quản lý danh mục**: Category management
- **Thống kê**: Dashboard với Chart.js

### 🔧 DevOps Features
- **Automated CI/CD**: Jenkins pipeline với multi-stage deployment
- **GitOps Deployment**: ArgoCD cho continuous deployment
- **Infrastructure Automation**: Terraform modules cho AWS
- **Security Scanning**: Automated vulnerability assessment
- **Blue-Green Deployment**: Zero-downtime deployments
- **Health Checks**: Service monitoring và auto-recovery

## 🖼️ Screenshots & UI Preview

### Homepage
![Homepage](https://drive.google.com/uc?export=view&id=1YNM_9WKdjgum-f6QUjWyi-pMlGz1Z3-O)

### Product Store
![Product Store](https://drive.google.com/uc?export=view&id=1F8S1Srndh5esJUYjGhQ65GQQpuddHWLn)

### Shopping Cart
![Shopping Cart](https://drive.google.com/uc?export=view&id=1ngzUPBebMSaKafxuTy0ZLgcl0QVyVmgp)

## 🔧 DevOps Pipeline

### Jenkins CI/CD Pipeline
![CI/CD Pipeline](https://drive.google.com/uc?export=view&id=1xQO5D6w0RHIvKqihGsJP5EE4Cmh5-4yO)

**Pipeline Stages:**
1. Build (Maven)
2. Test (Unit Test)
3. SAST (Sonarqube)
4. DP-Check (Dependency-Check)
5. Build + Push + Scan Container (Docker + Trivy)
6. Push updated YAML to Git
7. DAST (OWASP ZAP)

### Jenkins Dashboard
![Jenkins Dashboard](https://drive.google.com/uc?export=view&id=1PLh425FO3FqSBJsal0WTyTkMvC9XFkY-)

### ArgoCD GitOps Dashboard
![ArgoCD Dashboard](https://drive.google.com/uc?export=view&id=1DityzNVrw6eg96HaqaX1aOyz_wvdj0o0)

**ArgoCD Applications:**
- Frontend Application
- API Gateway
- Authentication Service
- Product Service
- Cart Service

## 🌐 URLs

- **Frontend**: https://frontend.myjenkins.click
- **API Gateway**: https://api-gateway.myjenkins.click
- **Jenkins Master**: https://master.myjenkins.click
- **Jenkins Agent**: https://agent.myjenkins.click
- **ArgoCD**: https://argocd.myjenkins.click

## 📊 API Endpoints

### Base URL: `https://api-gateway.myjenkins.click`

#### Authentication Service (Port 8080)
```
GET  /api/auth/hello     - Verify authentication
POST /api/auth/login     - User login
POST /api/auth/register  - User registration
```

#### Product Service (Port 8081)
```
GET  /api/products       - Get all products
GET  /api/products/{id}  - Get product by ID
GET  /api/categories     - Get all categories
```

#### Cart Service (Port 8082)
```
GET    /api/cart            - Get user cart
POST   /api/cart/items      - Add item to cart
DELETE /api/cart/items/{id} - Remove item from cart
DELETE /api/cart            - Clear cart
```

## 🔧 Setup & Installation

### Prerequisites
```bash
# Required tools
- Docker & Docker Compose
- Java 21+
- Maven 3.9+
- Node.js (for development)
- Terraform
- kubectl
- AWS CLI
- ArgoCD CLI
```

### 1. Clone Repository
```bash
git clone https://github.com/anhANlaptrinh/ecommerce-microservices.git
cd ecommerce-microservices
```

### 2. Infrastructure Setup
```bash
# Setup AWS infrastructure
cd infra/terraform
terraform init
terraform plan
terraform apply
```

### 3. ArgoCD Setup
```bash
# Install ArgoCD
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

# Access ArgoCD UI
kubectl port-forward svc/argocd-server -n argocd 8080:443

# Get initial admin password
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d
```

### 4. Local Development
```bash
# Build all services
./build-all.sh

# Run with Docker Compose
docker-compose up -d

# Or run individual services
cd api-gateway && mvn spring-boot:run
cd authentication-service && mvn spring-boot:run
cd product-service && mvn spring-boot:run
cd cart-service && mvn spring-boot:run
```

### 5. Frontend Development
```bash
cd FrontendWeb-main
# Serve with any HTTP server
python -m http.server 8000
# Or use live-server
live-server
```

## 🚢 Deployment

### Kubernetes Deployment
```bash
# Apply all manifests
kubectl apply -f k8s/manifests/

# Check deployments
kubectl get pods -A
kubectl get services -A
kubectl get ingress -A
```

### GitOps with ArgoCD
```bash
# Create ArgoCD application
argocd app create ecommerce-frontend \
  --repo https://github.com/anhANlaptrinh/ecommerce-microservices.git \
  --path k8s/manifests/frontend \
  --dest-server https://kubernetes.default.svc \
  --dest-namespace frontend

# Sync application
argocd app sync ecommerce-frontend
```

### CI/CD Pipeline
Pipeline tự động được trigger khi push code:

1. **Build & Test**: Maven build + unit tests
2. **Security Scan**: Trivy container scanning
3. **Docker Build**: Multi-stage builds
4. **Push Images**: Docker Hub registry
5. **Update Manifests**: GitOps workflow
6. **ArgoCD Sync**: Automatic deployment
7. **Verify**: Health checks + version verification
8. **Security Test**: OWASP ZAP scanning

## 🔒 Security Features

- **Container Scanning**: Trivy cho vulnerability assessment
- **Web App Security**: OWASP ZAP baseline scanning
- **JWT Authentication**: Stateless authentication
- **HTTPS**: SSL/TLS encryption
- **Network Security**: Kubernetes network policies
- **Secret Management**: Kubernetes secrets

## 🤝 Contributing

1. Fork repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push branch: `git push origin feature/amazing-feature`
5. Open Pull Request

### Development Workflow
```bash
# Setup development environment
make dev-setup

# Run tests
make test

# Run security checks
make security-scan

# Build and deploy
make build-deploy
```

## 👨‍💻 Authors

- **anhANlaptrinh** - *Initial work* - [GitHub](https://github.com/anhANlaptrinh)
- **tangnhatdang2810** - *Initial work* - [GitHub](https://github.com/tangnhatdang2810)

## 🙏 Acknowledgements

- [Spring Boot](https://spring.io/projects/spring-boot) - Microservices framework
- [Bootstrap](https://getbootstrap.com/) - CSS framework
- [Chart.js](https://www.chartjs.org/) - Data visualization
- [Terraform](https://www.terraform.io/) - Infrastructure as Code
- [Jenkins](https://www.jenkins.io/) - CI/CD automation
- [ArgoCD](https://argo-cd.readthedocs.io/) - GitOps continuous deployment
- [Kubernetes](https://kubernetes.io/) - Container orchestration

---

## 📊 Project Stats

![Languages](https://img.shields.io/badge/HTML-49.5%25-orange)
![Languages](https://img.shields.io/badge/Java-18.6%25-red)
![Languages](https://img.shields.io/badge/JavaScript-16.9%25-yellow)
![Languages](https://img.shields.io/badge/SCSS-13.3%25-pink)
![Languages](https://img.shields.io/badge/HCL-1.3%25-purple)
![Languages](https://img.shields.io/badge/Dockerfile-0.4%25-blue)

⭐ **Nếu project này hữu ích, hãy cho một star!** ⭐
