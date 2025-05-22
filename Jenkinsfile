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

        stage('Build & Test Auth Service') {
            steps {
                dir('authentication-service') {
                    sh 'mvn clean compile'
                    sh 'mvn test'
                }
            }
        }

        stage('SonarQube - Auth Service') {
            steps {
                dir('authentication-service') {
                    withSonarQubeEnv('sonarqube') {
                        sh '''
                            $SCANNER_HOME/bin/sonar-scanner \
                            -Dsonar.projectKey=auth-service \
                            -Dsonar.projectName=auth-service \
                            -Dsonar.sources=src \
                            -Dsonar.java.binaries=target/classes
                        '''
                    }
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

        stage('SonarQube - Product Service') {
            steps {
                dir('product-service') {
                    withSonarQubeEnv('sonarqube') {
                        sh '''
                            $SCANNER_HOME/bin/sonar-scanner \
                            -Dsonar.projectKey=product-service \
                            -Dsonar.projectName=product-service \
                            -Dsonar.sources=src \
                            -Dsonar.java.binaries=target/classes
                        '''
                    }
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

        stage('SonarQube - Cart Service') {
            steps {
                dir('cart-service') {
                    withSonarQubeEnv('sonarqube') {
                        sh '''
                            $SCANNER_HOME/bin/sonar-scanner \
                            -Dsonar.projectKey=cart-service \
                            -Dsonar.projectName=cart-service \
                            -Dsonar.sources=src \
                            -Dsonar.java.binaries=target/classes
                        '''
                    }
                }
            }
        }

        stage('SonarQube - Frontend') {
            steps {
                dir('frontend') {
                    withSonarQubeEnv('sonarqube') {
                        sh '''
                            $SCANNER_HOME/bin/sonar-scanner \
                            -Dsonar.projectKey=frontend \
                            -Dsonar.projectName=frontend \
                            -Dsonar.sources=src \
                            -Dsonar.exclusions=node_modules/**,dist/** \
                            -Dsonar.javascript.lcov.reportPaths=coverage/lcov.info
                        '''
                    }
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