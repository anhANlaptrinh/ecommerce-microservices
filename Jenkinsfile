pipeline {
    agent any
    tools {
        jdk 'JDK21'
        maven 'maven3'
    }

    environment {
        SCANNER_HOME = tool 'sonar-scanner'
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

        stage('Sonarqube Analysis') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh '''
                        $SCANNER_HOME/bin/sonar-scanner \
                        -Dsonar.projectKey=ecommerce-microservices \
                        -Dsonar.projectName=ecommerce-microservices \
                        -Dsonar.sources=. \
                        -Dsonar.java.binaries=.
                    '''
                }
            }
        }

        stage('Quality Gate') {
            steps {
                script {
                    waitForQualityGate abortPipeline: false, credentialsId: 'sonarqube-token'
                }
            }
        }
    }
}
