pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'maven3'
    }

    environment {
        SCANNER_HOME = tool 'sonar-scanner'
        DOCKER_CREDENTIALS = credentials('docker')
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

        stage('OWASP Dependency Check') {
            steps {
                dependencyCheck additionalArguments: '--scan ./ --format XML', odcInstallation: 'DP-Check'
                dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
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
                stage('Compile Gateway') {
                    steps {
                        dir('api-gateway') {
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
                stage('Build Gateway') {
                    steps {
                        dir('api-gateway') {
                            sh 'mvn clean install -DskipTests=true'
                        }
                    }
                }
            }
        }

        stage('Build & Push Docker Images') {
            parallel {
                stage('Docker Login') {
                    steps {
                        sh """
                            echo ${DOCKER_CREDENTIALS_PSW} | docker login -u ${DOCKER_CREDENTIALS_USR} --password-stdin
                        """
                    }
                }

                stage('Docker Image - Auth') {
                    steps {
                        dir('authentication-service') {
                            sh """
                                docker rmi -f dohuynhan/auth-service:latest || true
                                docker build --no-cache -t dohuynhan/auth-service:latest .
                                docker push dohuynhan/auth-service:latest
                            """
                        }
                    }
                }

                stage('Docker Image - Product') {
                    steps {
                        dir('product-service') {
                            sh """
                                docker rmi -f dohuynhan/product-service:latest || true
                                docker build --no-cache -t dohuynhan/product-service:latest .
                                docker push dohuynhan/product-service:latest
                            """
                        }
                    }
                }

                stage('Docker Image - Cart') {
                    steps {
                        dir('cart-service') {
                            sh """
                                docker rmi -f dohuynhan/cart-service:latest || true
                                docker build --no-cache -t dohuynhan/cart-service:latest .
                                docker push dohuynhan/cart-service:latest
                            """
                        }
                    }
                }

                stage('Docker Image - Gateway') {
                    steps {
                        dir('api-gateway') {
                            sh """
                                docker rmi -f dohuynhan/api-gateway:latest || true
                                docker build --no-cache -t dohuynhan/api-gateway:latest .
                                docker push dohuynhan/api-gateway:latest
                            """
                        }
                    }
                }

                stage('Docker Image - Frontend') {
                    steps {
                        dir('FrontendWeb-main') {
                            sh """
                                docker rmi -f dohuynhan/frontend-web:latest || true
                                docker build --no-cache -t dohuynhan/frontend-web:latest .
                                docker push dohuynhan/frontend-web:latest
                            """
                        }
                    }
                }
            }
        }

        stage('Trivy Scan Docker Images') {
            parallel {
                stage('Scan Auth Image') {
                    steps {
                        sh 'trivy image -f html -o trivy-auth.html --exit-code 0 --severity HIGH,CRITICAL dohuynhan/auth-service:latest'
                        archiveArtifacts artifacts: 'trivy-auth.html', allowEmptyArchive: true
                    }
                }
                stage('Scan Product Image') {
                    steps {
                        sh 'trivy image -f html -o trivy-product.html --exit-code 0 --severity HIGH,CRITICAL dohuynhan/product-service:latest'
                        archiveArtifacts artifacts: 'trivy-product.html', allowEmptyArchive: true
                    }
                }
                stage('Scan Cart Image') {
                    steps {
                        sh 'trivy image -f html -o trivy-cart.html --exit-code 0 --severity HIGH,CRITICAL dohuynhan/cart-service:latest'
                        archiveArtifacts artifacts: 'trivy-cart.html', allowEmptyArchive: true
                    }
                }
                stage('Scan Gateway Image') {
                    steps {
                        sh 'trivy image -f html -o trivy-gateway.html --exit-code 0 --severity HIGH,CRITICAL dohuynhan/api-gateway:latest'
                        archiveArtifacts artifacts: 'trivy-gateway.html', allowEmptyArchive: true
                    }
                }
                stage('Scan Frontend Image') {
                    steps {
                        sh 'trivy image -f html -o trivy-frontend.html --exit-code 0 --severity HIGH,CRITICAL dohuynhan/frontend-web:latest'
                        archiveArtifacts artifacts: 'trivy-frontend.html', allowEmptyArchive: true
                    }
                }
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
                            ansiblePlaybook credentialsId: 'SSH',
                                            installation: 'ansible',
                                            inventory: '/etc/ansible/hosts',
                                            playbook: 'deploy-auth.yaml',
                                            extraVars: [
                                                DOCKERHUB_USERNAME: "${env.DOCKER_CREDENTIALS_USR}",
                                                DOCKERHUB_PASSWORD: "${env.DOCKER_CREDENTIALS_PSW}"
                                            ],
                                            disableHostKeyChecking: true
                        }
                    }
                }

                stage('Deploy Product') {
                    steps {
                        dir('ansible') {
                            ansiblePlaybook credentialsId: 'SSH',
                                            installation: 'ansible',
                                            inventory: '/etc/ansible/hosts',
                                            playbook: 'deploy-product.yaml',
                                            extraVars: [
                                                DOCKERHUB_USERNAME: "${env.DOCKER_CREDENTIALS_USR}",
                                                DOCKERHUB_PASSWORD: "${env.DOCKER_CREDENTIALS_PSW}"
                                            ],
                                            disableHostKeyChecking: true
                        }
                    }
                }

                stage('Deploy Cart') {
                    steps {
                        dir('ansible') {
                            ansiblePlaybook credentialsId: 'SSH',
                                            installation: 'ansible',
                                            inventory: '/etc/ansible/hosts',
                                            playbook: 'deploy-cart.yaml',
                                            extraVars: [
                                                DOCKERHUB_USERNAME: "${env.DOCKER_CREDENTIALS_USR}",
                                                DOCKERHUB_PASSWORD: "${env.DOCKER_CREDENTIALS_PSW}"
                                            ],
                                            disableHostKeyChecking: true
                        }
                    }
                }

                stage('Deploy Gateway') {
                    steps {
                        dir('ansible') {
                            ansiblePlaybook credentialsId: 'SSH',
                                            installation: 'ansible',
                                            inventory: '/etc/ansible/hosts',
                                            playbook: 'deploy-gateway.yaml',
                                            extraVars: [
                                                DOCKERHUB_USERNAME: "${env.DOCKER_CREDENTIALS_USR}",
                                                DOCKERHUB_PASSWORD: "${env.DOCKER_CREDENTIALS_PSW}"
                                            ],
                                            disableHostKeyChecking: true
                        }
                    }
                }

                stage('Deploy Frontend') {
                    steps {
                        dir('ansible') {
                            ansiblePlaybook credentialsId: 'SSH',
                                            installation: 'ansible',
                                            inventory: '/etc/ansible/hosts',
                                            playbook: 'deploy-frontend.yaml',
                                            extraVars: [
                                                DOCKERHUB_USERNAME: "${env.DOCKER_CREDENTIALS_USR}",
                                                DOCKERHUB_PASSWORD: "${env.DOCKER_CREDENTIALS_PSW}"
                                            ],
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
            sh '''
                echo "[CLEANUP] Dọn dẹp container và image không dùng..."
                docker container prune -f
                docker image prune -a -f
            '''
        }
    }
}