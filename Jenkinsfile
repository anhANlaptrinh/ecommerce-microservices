pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'maven3'
    }

    environment {
        SCANNER_HOME = tool 'sonar-scanner'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10', daysToKeepStr: '7'))
        timeout(time: 15, unit: 'MINUTES')
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
        stage('Maven Compile') {
            parallel {
                stage('Compile Auth') {
                    steps {
                        dir('authentication-service') {
                            sh 'mvn clean compile'
                        }
                    }
                }
                stage('Compile Product') {
                    steps {
                        dir('product-service') {
                            sh 'mvn clean compile'
                        }
                    }
                }
                stage('Compile Cart') {
                    steps {
                        dir('cart-service') {
                            sh 'mvn clean compile'
                        }
                    }
                }
            }
        }
        stage('Test Services') {
            parallel {
                stage('Test Auth') {
                    steps {
                        dir('authentication-service') {
                            sh 'mvn test'
                        }
                    }
                }
                stage('Test Product') {
                    steps {
                        dir('product-service') {
                            sh 'mvn test'
                        }
                    }
                }
                stage('Test Cart') {
                    steps {
                        dir('cart-service') {
                            sh 'mvn test'
                        }
                    }
                }
            }
        }

        stage('SonarQube Scan') {
            parallel {
                stage('Scan Auth') {
                    steps {
                        dir('authentication-service') {
                            withSonarQubeEnv('sonarqube') {
                                sh '''
                                    $SCANNER_HOME/bin/sonar-scanner \
                                    -Dsonar.projectKey=auth-service \
                                    -Dsonar.projectName="Auth Service" \
                                    -Dsonar.sources=src \
                                    -Dsonar.java.binaries=target/classes \
                                    -Dsonar.sourceEncoding=UTF-8
                                '''
                            }
                            sh 'rm -rf target'
                        }
                    }
                }

                stage('Scan Product') {
                    steps {
                        dir('product-service') {
                            withSonarQubeEnv('sonarqube') {
                                sh '''
                                    $SCANNER_HOME/bin/sonar-scanner \
                                    -Dsonar.projectKey=product-service \
                                    -Dsonar.projectName="Product Service" \
                                    -Dsonar.sources=src \
                                    -Dsonar.java.binaries=target/classes \
                                    -Dsonar.sourceEncoding=UTF-8
                                '''
                            }
                            sh 'rm -rf target'
                        }
                    }
                }

                stage('Scan Cart') {
                    steps {
                        dir('cart-service') {
                            withSonarQubeEnv('sonarqube') {
                                sh '''
                                    $SCANNER_HOME/bin/sonar-scanner \
                                    -Dsonar.projectKey=cart-service \
                                    -Dsonar.projectName="Cart Service" \
                                    -Dsonar.sources=src \
                                    -Dsonar.java.binaries=target/classes \
                                    -Dsonar.sourceEncoding=UTF-8
                                '''
                            }
                            sh 'rm -rf target'
                        }
                    }
                }

                stage('Scan Frontend') {
                    steps {
                        dir('FrontendWeb-main') {
                            withSonarQubeEnv('sonarqube') {
                                sh '''
                                    $SCANNER_HOME/bin/sonar-scanner \
                                    -Dsonar.projectKey=frontend \
                                    -Dsonar.projectName="Frontend Web"
                                '''
                            }
                        }
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

    post {
        always {
            cleanWs()
        }
    }
}