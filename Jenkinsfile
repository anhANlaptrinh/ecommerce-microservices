pipeline {
    agent any
    tools {
        jdk 'JDK21'        // Đảm bảo JDK21 đã được cấu hình trong Jenkins
        maven 'maven3'     // Đảm bảo Maven 3 đã cài
    }
    stages {
        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }

        stage('Checkout Source Code') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test Authentication Service') {
            steps {
                dir('authentication-service') {
                    sh 'mvn clean compile'
                    sh 'mvn test'
                }
            }
        }

        stage('Build & Test Product Service') {
            steps {
                dir('product-service') {
                    sh 'mvn clean compile'
                    sh 'mvn test'
                }
            }
        }

        stage('Build & Test Cart Service') {
            steps {
                dir('cart-service') {
                    sh 'mvn clean compile'
                    sh 'mvn test'
                }
            }
        }
    }
}