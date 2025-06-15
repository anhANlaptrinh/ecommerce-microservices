pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'maven3'
    }

    environment {
        SCANNER_HOME = tool 'sonar-scanner'
        DOCKER_CREDENTIALS = credentials('docker')
        TRIVY_CACHE_DIR = "${WORKSPACE}/.trivy-cache"
        IMAGE_TAG = "v${BUILD_NUMBER}"
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10', daysToKeepStr: '7'))
        timeout(time: 15, unit: 'MINUTES')
    }

    stages {
        stage('Check Commit Message') {
            steps {
                script {
                    def lastCommitMessage = sh(script: "git log -1 --pretty=%B", returnStdout: true).trim()
                    if (lastCommitMessage.startsWith("ci: update image tags")) {
                        echo "Detected auto-commit from Jenkins. Skipping build to avoid infinite loop."
                        currentBuild.result = 'SUCCESS'
                        error("Stopping pipeline triggered by auto-commit.")
                    }
                }
            }
        }

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
                            sh '''
                                DB_HOST=ecommerce-db.ch8mugaw6zy6.ap-southeast-2.rds.amazonaws.com \
                                DB_PORT=5432 \
                                DB_NAME=auth_service \
                                DB_USER=postgres \
                                DB_PASSWORD=Tangnhatdang2004 \
                                JWT_SECRET=MY_SUPER_SECRET_KEY_FOR_JWT_1234567890 \
                                mvn test
                            '''
                        }
                    }
                }
                stage('Test Product') {
                    steps {
                        dir('product-service') {
                            sh '''
                                DB_HOST=ecommerce-db.ch8mugaw6zy6.ap-southeast-2.rds.amazonaws.com \
                                DB_PORT=5432 \
                                DB_NAME=product_service \
                                DB_USER=postgres \
                                DB_PASSWORD=Tangnhatdang2004 \
                                mvn test
                            '''
                        }
                    }
                }
                stage('Test Cart') {
                    steps {
                        dir('cart-service') {
                            sh '''
                                DB_HOST=ecommerce-db.ch8mugaw6zy6.ap-southeast-2.rds.amazonaws.com \
                                DB_PORT=5432 \
                                DB_NAME=cart_service \
                                DB_USER=postgres \
                                DB_PASSWORD=Tangnhatdang2004 \
                                mvn test
                            '''
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

        stage('OWASP Dependency Check') {
            steps {
                dependencyCheck additionalArguments: '--scan ./ --format XML', odcInstallation: 'DP-Check'
                dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
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
                                docker rmi -f dohuynhan/auth-service:${IMAGE_TAG} || true
                                docker build --no-cache -t dohuynhan/auth-service:${IMAGE_TAG} .
                                docker push dohuynhan/auth-service:${IMAGE_TAG}
                            """
                        }
                    }
                }

                stage('Docker Image - Product') {
                    steps {
                        dir('product-service') {
                            sh """
                                docker rmi -f dohuynhan/product-service:${IMAGE_TAG} || true
                                docker build --no-cache -t dohuynhan/product-service:${IMAGE_TAG} .
                                docker push dohuynhan/product-service:${IMAGE_TAG}
                            """
                        }
                    }
                }

                stage('Docker Image - Cart') {
                    steps {
                        dir('cart-service') {
                            sh """
                                docker rmi -f dohuynhan/cart-service:${IMAGE_TAG} || true
                                docker build --no-cache -t dohuynhan/cart-service:${IMAGE_TAG} .
                                docker push dohuynhan/cart-service:${IMAGE_TAG}
                            """
                        }
                    }
                }

                stage('Docker Image - Gateway') {
                    steps {
                        dir('api-gateway') {
                            sh """
                                docker rmi -f dohuynhan/api-gateway:${IMAGE_TAG} || true
                                docker build --no-cache -t dohuynhan/api-gateway:${IMAGE_TAG} .
                                docker push dohuynhan/api-gateway:${IMAGE_TAG}
                            """
                        }
                    }
                }

                stage('Docker Image - Frontend') {
                    steps {
                        dir('FrontendWeb-main') {
                            sh """
                                docker rmi -f dohuynhan/frontend-web:${IMAGE_TAG} || true
                                docker build --no-cache -t dohuynhan/frontend-web:${IMAGE_TAG} .
                                docker push dohuynhan/frontend-web:${IMAGE_TAG}
                            """
                        }
                    }
                }
            }
        }

        stage('Trivy DB Update') {
            steps {
                sh "trivy image --download-db-only --cache-dir ${TRIVY_CACHE_DIR}"
            }
        }

        stage('Trivy Scan Docker Images') {
            parallel {
                stage('Scan Auth Image') {
                    steps {
                        sh """
                            trivy image --cache-dir ${TRIVY_CACHE_DIR} --exit-code 1 --severity HIGH,CRITICAL --ignore-unfixed dohuynhan/auth-service:${IMAGE_TAG}
                        """
                    }
                }
                stage('Scan Product Image') {
                    steps {
                        sh """
                            trivy image --cache-dir ${TRIVY_CACHE_DIR} --exit-code 1 --severity HIGH,CRITICAL --ignore-unfixed dohuynhan/product-service:${IMAGE_TAG}
                        """
                    }
                }
                stage('Scan Cart Image') {
                    steps {
                        sh """
                             trivy image --cache-dir ${TRIVY_CACHE_DIR} --exit-code 1 --severity HIGH,CRITICAL --ignore-unfixed dohuynhan/cart-service:${IMAGE_TAG}
                        """
                    }
                }
                stage('Scan Gateway Image') {
                    steps {
                        sh """
                            trivy image --cache-dir ${TRIVY_CACHE_DIR} --exit-code 1 --severity HIGH,CRITICAL --ignore-unfixed dohuynhan/api-gateway:${IMAGE_TAG}
                        """
                    }
                }
                stage('Scan Frontend Image') {
                    steps {
                        sh """
                            trivy image --cache-dir ${TRIVY_CACHE_DIR} --exit-code 1 --severity HIGH,CRITICAL --ignore-unfixed dohuynhan/frontend-web:${IMAGE_TAG}
                        """
                    }
                }
            }
        }

        stage('Commit YAML Update') {
            steps {
                withCredentials([string(credentialsId: 'github-token', variable: 'GITHUB_TOKEN')]) {
                    sh """
                        git checkout main
                        git config user.name "Jenkins CI"
                        git config user.email "jenkins@example.com"
                        git remote set-url origin https://${GITHUB_TOKEN}@github.com/anhANlaptrinh/ecommerce-microservices.git
                        git pull origin main
                        sed -i "s|image: .*/auth-service:.*|image: dohuynhan/auth-service:${IMAGE_TAG}|" k8s/manifests/auth-service/deployment.yaml
                        sed -i "s|image: .*/product-service:.*|image: dohuynhan/product-service:${IMAGE_TAG}|" k8s/manifests/product-service/deployment.yaml
                        sed -i "s|image: .*/cart-service:.*|image: dohuynhan/cart-service:${IMAGE_TAG}|" k8s/manifests/cart-service/deployment.yaml
                        sed -i "s|image: .*/api-gateway:.*|image: dohuynhan/api-gateway:${IMAGE_TAG}|" k8s/manifests/api-gateway/deployment.yaml
                        sed -i "s|image: .*/frontend-web:.*|image: dohuynhan/frontend-web:${IMAGE_TAG}|" k8s/manifests/frontend/deployment.yaml
                        git add k8s/manifests/**/deployment.yaml
                        git commit -m "ci: update image tags to ${IMAGE_TAG}" || echo "No changes to commit"
                        git push origin main
                    """
                }
            }
        }

        stage('OWASP ZAP Scan Frontend') {
            steps {
                script {
                    def targetUrl = "https://frontend.myjenkins.click"

                    sh """
                        docker run --rm -v \$PWD:/zap/wrk/:rw -t zaproxy/zap-stable zap-baseline.py \
                            -t ${targetUrl} -g gen.conf -r zap-report.html || true
                    """
                    archiveArtifacts artifacts: 'zap-report.html', allowEmptyArchive: true
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

        /*
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
        */
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