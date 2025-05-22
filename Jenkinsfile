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
        stage('Checkout Source Code') {
            steps {
                checkout scm
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
                                    -Dsonar.sources=src \
                                    -Dsonar.java.binaries=target/classes
                                '''
                            }
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
                                    -Dsonar.sources=src \
                                    -Dsonar.java.binaries=target/classes
                                '''
                            }
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
                                    -Dsonar.sources=src \
                                    -Dsonar.java.binaries=target/classes
                                '''
                            }
                        }
                    }
                }
                stage('Scan Frontend') {
                    steps {
                        dir('FrontendWeb-main') {
                            withSonarQubeEnv('sonarqube') {
                                sh '$SCANNER_HOME/bin/sonar-scanner'
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
}
