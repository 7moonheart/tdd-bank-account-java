pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/7moonheart/tdd-bank-account-java.git'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean verify'
            }
        }
        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar, target/surefire-reports/*'
            }
        }
    }
}