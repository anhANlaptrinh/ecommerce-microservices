pipeline {
    agent any
    tools {
        jdk 'JDK'
        maven 'maven3'
    }
    stages {
        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }
        stage('Maven Compile') {
            steps {
                dir('authentication-service') {
                    sh 'mvn clean compile'
                }
            }
        }
        stage('Maven Test') {
            steps {
                dir('authentication-service') {
                    sh 'mvn test'
                }
            }
        }
    }
}
