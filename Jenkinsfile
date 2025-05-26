pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'maven3'
    }

    environment {
        SCANNER_HOME = tool 'sonar-scanner'
        DOCKERHUB_USERNAME = credentials('docker')
        DOCKERHUB_PASSWORD = credentials('docker')
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

        stage('Build Services') {
            parallel {
                stage('Build Auth') {
                    steps {
                        dir('authentication-service') {
                            sh 'mvn clean install -DskipTests=true'
                        }
                    }
                }
                stage('Build Product') {
                    steps {
                        dir('product-service') {
                            sh 'mvn clean install -DskipTests=true'
                        }
                    }
                }
                stage('Build Cart') {
                    steps {
                        dir('cart-service') {
                            sh 'mvn clean install -DskipTests=true'
                        }
                    }
                }
            }
        }

        stage("OWASP Dependency Check") {
            steps {
                dependencyCheck additionalArguments: '--scan ./ --format XML', odcInstallation: 'DP-Check'
                dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
            }
        }

        stage('SonarQube Scan') {
            steps {
                script {
                    def services = [
                        [dir: 'authentication-service', key: 'auth-service'],
                        [dir: 'product-service', key: 'product-service'],
                        [dir: 'cart-service', key: 'cart-service']
                    ]
                    for (svc in services) {
                        dir(svc.dir) {
                            withSonarQubeEnv('sonarqube') {
                                sh """
                                    $SCANNER_HOME/bin/sonar-scanner \
                                    -Dsonar.projectKey=${svc.key} \
                                    -Dsonar.projectName=${svc.key} \
                                    -Dsonar.sources=src \
                                    -Dsonar.java.binaries=target/classes \
                                    -Dsonar.sourceEncoding=UTF-8
                                """
                            }
                        }
                    }

                    dir('FrontendWeb-main') {
                        withSonarQubeEnv('sonarqube') {
                            sh """
                                $SCANNER_HOME/bin/sonar-scanner \
                                -Dsonar.projectKey=frontend-web \
                                -Dsonar.projectName=frontend-web \
                                -Dsonar.sources=. \
                                -Dsonar.sourceEncoding=UTF-8 \
                                -Dsonar.exclusions=node_modules/**,dist/**,coverage/**,**/*.spec.js,**/*.test.js \
                                -Dsonar.coverage.jacoco.reportPaths=disabled \
                                -Dsonar.projectVersion=${BUILD_NUMBER}
                            """
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

        stage('Deploy Services') {
            parallel {
                stage('Deploy Auth') {
                    steps {
                        dir('ansible') {
                            ansiblePlaybook credentialsId: 'ssh',
                                            installation: 'ansible',
                                            inventory: '/etc/ansible/hosts',
                                            playbook: 'deploy-auth.yaml',
                                            disableHostKeyChecking: true
                        }
                    }
                }

                stage('Deploy Product') {
                    steps {
                        dir('ansible') {
                            ansiblePlaybook credentialsId: 'ssh',
                                            installation: 'ansible',
                                            inventory: '/etc/ansible/hosts',
                                            playbook: 'deploy-product.yaml',
                                            disableHostKeyChecking: true
                        }
                    }
                }

                stage('Deploy Cart') {
                    steps {
                        dir('ansible') {
                            ansiblePlaybook credentialsId: 'ssh',
                                            installation: 'ansible',
                                            inventory: '/etc/ansible/hosts',
                                            playbook: 'deploy-cart.yaml',
                                            disableHostKeyChecking: true
                        }
                    }
                }

                stage('Deploy Gateway') {
                    steps {
                        dir('ansible') {
                            ansiblePlaybook credentialsId: 'ssh',
                                            installation: 'ansible',
                                            inventory: '/etc/ansible/hosts',
                                            playbook: 'deploy-gateway.yaml',
                                            disableHostKeyChecking: true
                        }
                    }
                }

                stage('Deploy Frontend') {
                    steps {
                        dir('ansible') {
                            ansiblePlaybook credentialsId: 'ssh',
                                            installation: 'ansible',
                                            inventory: '/etc/ansible/hosts',
                                            playbook: 'deploy-frontend.yaml',
                                            disableHostKeyChecking: true
                        }
                    }
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
